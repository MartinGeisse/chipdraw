#!/usr/bin/tcsh -f

set projectpath=$argv[1]

#---------------------------------------------------------------------
# This script is called with the first argument <project_path>, which should
# have file "qflow_vars.sh".  Get all of our standard variable definitions
# from the qflow_vars.sh file.
#---------------------------------------------------------------------

source /home/martin/git-repos/chipdraw/resource/qflow-test/qflow_vars.sh
source techdir/osu050.sh
cd /home/martin/git-repos/chipdraw/resource/qflow-test
source project_vars.sh

# Reset the logfile
rm -f ${synthlog} >& /dev/null
touch ${synthlog}

#
set libertypath=techdir/osu05_stdcells.lib
set spicepath=techdir/osu050_stdcells.sp
set lefpath=techdir/osu050_stdcells.lef

#
cd ${sourcedir}


#---------------------------------------------------------------------
# Generate the main yosys script
#---------------------------------------------------------------------

cat > sevenseg.ys << EOF

read_liberty -lib -ignore_miss_dir -setattr blackbox ${libertypath}

read_verilog sevenseg.v
# TODO add other verilog files

# High-level synthesis
synth -top sevenseg

# Map register flops
dfflibmap -liberty ${libertypath}
opt

# Map combinatorial cells, standard script
abc -exe ${bindir}/yosys-abc -liberty ${libertypath} -script +strash;scorr;ifraig;retime,{D};strash;dch,-f;map,-M,1,{D}
flatten

# Purge buffering of internal net name aliases.  Option "debug"
# retains all internal names by buffering them, resulting in a
# larger layout (especially for layouts derived from hierarchical
# source), but one in which all signal names from the source can
# be probed.
clean -purge

# Output buffering
iopadmap -outpad ${bufcell} ${bufpin_in}:${bufpin_out} -bits

# Cleanup
opt
clean
rename -enumerate
write_blif -buf ${bufcell} ${bufpin_in} ${bufpin_out} sevenseg_mapped.blif

EOF

#---------------------------------------------------------------------
# Yosys synthesis
#---------------------------------------------------------------------

# If there is a file sevenseg_mapped.blif, move it to a temporary
# place so we can see if yosys generates a new one or not.

if ( -f sevenseg_mapped.blif ) then
   mv sevenseg_mapped.blif sevenseg_mapped_orig.blif
endif

echo "Running yosys for verilog parsing and synthesis" |& tee -a ${synthlog}
eval ${bindir}/yosys "" -s sevenseg.ys |& tee -a ${synthlog}

#---------------------------------------------------------------------
# Spot check:  Did yosys produce file sevenseg_mapped.blif?
#---------------------------------------------------------------------

if ( !( -f sevenseg_mapped.blif )) then
   echo "outputprep failure:  No file sevenseg_mapped.blif." |& tee -a ${synthlog}
   echo "Premature exit." |& tee -a ${synthlog}
   echo "Synthesis flow stopped due to error condition." >> ${synthlog}
   # Replace the old blif file, if we had moved it
   if ( -f sevenseg_mapped_orig.blif ) then
      mv sevenseg_mapped_orig.blif sevenseg_mapped.blif
   endif
   exit 1
else
   # Remove the old blif file, if we had moved it
   if ( -f sevenseg_mapped_orig.blif ) then
      rm sevenseg_mapped_orig.blif
   endif
endif

echo "Cleaning up output syntax" |& tee -a ${synthlog}
${scriptdir}/ypostproc.tcl sevenseg_mapped.blif sevenseg techdir/osu050.sh

# Buffers already handled within yosys
set final_blif = "sevenseg_mapped_tmp.blif"

#---------------------------------------------------------------------
# The following definitions will replace "LOGIC0" and "LOGIC1"
# with buffers from gnd and vdd, respectively.  This takes care
# of technologies where tie-low and tie-high cells are not
# defined.
#---------------------------------------------------------------------

echo "Cleaning Up blif file syntax" |& tee -a ${synthlog}

set subs0a="/LOGIC0/s/O=/${bufpin_in}=gnd ${bufpin_out}=/"
set subs0b="/LOGIC0/s/LOGIC0/${bufcell}/"
set subs1a="/LOGIC1/s/O=/${bufpin_in}=vdd ${bufpin_out}=/"
set subs1b="/LOGIC1/s/LOGIC1/${bufcell}/"

#---------------------------------------------------------------------
# Remove backslashes, references to "$techmap", and
# make local input nodes of the form $0node<a:b><c> into the
# form node<c>_FF_INPUT
#---------------------------------------------------------------------

cat ${final_blif} | sed \
	-e "$subs0a" -e "$subs0b" -e "$subs1a" -e "$subs1b" \
	-e 's/\\\([^$]\)/\1/g' \
	-e 's/$techmap//g' \
	-e 's/$0\([^ \t<]*\)<[0-9]*:[0-9]*>\([^ \t]*\)/\1\2_FF_INPUT/g' \
	> ${synthdir}/sevenseg.blif

# Switch to synthdir for processing of the BDNET netlist
cd ${synthdir}

# BlifFanout could run here, but I don't have that on my system. It would be used to insert higher-strength gates
# in high-fanout cases.

echo "" >> ${synthlog}
echo "Generating RTL verilog and SPICE netlist file in directory" |& tee -a ${synthlog}
echo "	 ${synthdir}" |& tee -a ${synthlog}
echo "Files:" |& tee -a ${synthlog}
echo "   Verilog: ${synthdir}/sevenseg.rtl.v" |& tee -a ${synthlog}
echo "   Verilog: ${synthdir}/sevenseg.rtlnopwr.v" |& tee -a ${synthlog}
echo "   Spice:   ${synthdir}/sevenseg.spc" |& tee -a ${synthlog}
echo "" >> ${synthlog}

echo "Running blif2Verilog." |& tee -a ${synthlog}
${bindir}/blif2Verilog -c -v ${vddnet} -g ${gndnet} sevenseg.blif > sevenseg.rtl.v

${bindir}/blif2Verilog -c -p -v ${vddnet} -g ${gndnet} sevenseg.blif > sevenseg.rtlnopwr.v

#---------------------------------------------------------------------
# Spot check:  Did blif2Verilog exit with an error?
# Note that these files are not critical to the main synthesis flow,
# so if they are missing, we flag a warning but do not exit.
#---------------------------------------------------------------------

if ( !( -f sevenseg.rtl.v || \
        ( -M sevenseg.rtl.v < -M sevenseg.blif ))) then
   echo "blif2Verilog failure:  No file sevenseg.rtl.v created." \
                |& tee -a ${synthlog}
endif

if ( !( -f sevenseg.rtlnopwr.v || \
        ( -M sevenseg.rtlnopwr.v < -M sevenseg.blif ))) then
   echo "blif2Verilog failure:  No file sevenseg.rtlnopwr.v created." \
                |& tee -a ${synthlog}
endif

#---------------------------------------------------------------------

echo "Running blif2BSpice." |& tee -a ${synthlog}
if ("x${spicefile}" == "x") then
    set spiceopt=""
else
    set spiceopt="-l ${spicepath}"
endif
${bindir}/blif2BSpice -i -p ${vddnet} -g ${gndnet} ${spiceopt} \
	sevenseg.blif > sevenseg.spc

#---------------------------------------------------------------------
# Spot check:  Did blif2BSpice exit with an error?
# Note that these files are not critical to the main synthesis flow,
# so if they are missing, we flag a warning but do not exit.
#---------------------------------------------------------------------

if ( !( -f sevenseg.spc || \
        ( -M sevenseg.spc < -M sevenseg.blif ))) then
   echo "blif2BSpice failure:  No file sevenseg.spc created." \
                |& tee -a ${synthlog}
else

   echo "Running spi2xspice.py" |& tee -a ${synthlog}
   if ("x${spicefile}" == "x") then
       set spiceopt=""
   else
       set spiceopt="-l ${spicepath}"
   endif
   ${scriptdir}/spi2xspice.py ${libertypath} sevenseg.spc \
		sevenseg.xspice
endif

if ( !( -f sevenseg.xspice || \
	( -M sevenseg.xspice < -M sevenseg.spc ))) then
   echo "spi2xspice.py failure:  No file sevenseg.xspice created." \
		|& tee -a ${synthlog}
endif
