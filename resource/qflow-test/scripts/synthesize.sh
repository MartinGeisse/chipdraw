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

#---------------------------------------------------------------------
# Determine hierarchy by running yosys with a simple script to check
# hierarchy.  Add files until yosys no longer reports an error.
# Any error not related to a missing source file causes the script
# to rerun yosys and dump error information into the log file, and
# exit.
#---------------------------------------------------------------------

cd ${sourcedir}

set uniquedeplist = ""
set yerrcnt = 2

while ($yerrcnt > 1)

# Note:  While the use of read_liberty to allow structural verilog only
# works in yosys 0.3.1 and newer, the following line works for the
# purpose of querying the hierarchy in all versions.

set vext = "v"
set svopt = ""

if ( !( -f sevenseg.${vext} )) then
   set vext = "sv"
   set svopt = "-sv"
   if ( !( -f sevenseg.${vext} )) then
      echo "Error:  Verilog source file sevenseg.v (or .sv) cannot be found!" |& tee -a ${synthlog}
   endif
endif

cat > sevenseg.ys << EOF
# Synthesis script for yosys created by qflow
read_liberty -lib -ignore_miss_dir -setattr blackbox ${libertypath}
read_verilog ${svopt} sevenseg.${vext}
EOF

foreach subname ( $uniquedeplist )
    if ( !( -f ${subname}.${vext} )) then
	echo "Error:  Verilog source file ${subname}.${vext} cannot be found!" |& tee -a ${synthlog}
    endif
    echo "read_verilog ${svopt} ${subname}.${vext}" >> sevenseg.ys
end

cat >> sevenseg.ys << EOF
# Hierarchy check
hierarchy -check
EOF

set yerrors = `eval ${bindir}/yosys -s sevenseg.ys |& sed -e "/\\/s#\\#/#g" | grep ERROR`
set yerrcnt = `echo $yerrors | wc -c`

if ($yerrcnt > 1) then
   set yvalid = `echo $yerrors | grep "referenced in module" | wc -c`
   if ($yvalid > 1) then
      set newdep = `echo $yerrors | cut -d " " -f 3 | cut -c3- | cut -d "'" -f 1`
      set uniquedeplist = "${uniquedeplist} ${newdep}"
   else
      ${bindir}/yosys -s sevenseg.ys >& ${synthlog}
      echo "Errors detected in verilog source, need to be corrected." |& tee -a ${synthlog}
      echo "See file ${synthlog} for error output."
      echo "Synthesis flow stopped due to error condition." >> ${synthlog}
      exit 1
   endif
endif

# end while ($yerrcnt > 1)
end

#---------------------------------------------------------------------
# Generate the main yosys script
#---------------------------------------------------------------------

set blif_opts = ""

# Set option for generating buffers
set blif_opts = "${blif_opts} -buf ${bufcell} ${bufpin_in} ${bufpin_out}"

# Set option for generating only the flattened top-level cell
# set blif_opts = "${blif_opts} sevenseg"

# Determine version of yosys
set versionstring = `${bindir}/yosys -V | cut -d' ' -f2`
set major = `echo $versionstring | cut -d. -f1`
set minor = `echo $versionstring | cut -d. -f2`

# Sigh. . .  versioning doesn't follow any fixed standard
set minortest = `echo $minor | cut -d+ -f2`
set minor = `echo $minor | cut -d+ -f1`
if ( ${minortest} == "" ) then

   set revisionstring = `echo $versionstring | cut -d. -f3`
   if ( ${revisionstring} == "" ) set revisionstring = 0
   set revision = `echo $revisionstring | cut -d+ -f1`
   set subrevision = `echo $revisionstring | cut -d+ -f2`
   if ( ${subrevision} == "" ) set subrevision = 0

else
   set revision = 0
   set subrevision = ${minortest}

endif
set minor = `echo ${minor} | sed 's/+//'`
      
cat > sevenseg.ys << EOF
# Synthesis script for yosys created by qflow
EOF

# From yosys version 3.0.0+514, structural verilog using cells from the
# the same standard cell set that is mapped by abc is supported.
if (( ${major} == 0 && ${minor} == 3 && ${revision} == 0 && ${subrevision} >= 514) || \
    ( ${major} == 0 && ${minor} == 3 && ${revision} > 0 ) || \
    ( ${major} == 0 && ${minor} > 3 ) || \
    ( ${major} > 0) ) then
cat > sevenseg.ys << EOF
read_liberty -lib -ignore_miss_dir -setattr blackbox ${libertypath}
EOF
endif

cat > sevenseg.ys << EOF
read_liberty -lib -ignore_miss_dir -setattr blackbox ${libertypath}
read_verilog ${svopt} sevenseg.${vext}
EOF

foreach subname ( $uniquedeplist )
    echo "read_verilog ${svopt} ${subname}.${vext}" >> sevenseg.ys
end

# Will not support yosys 0.0.x syntax; flag a warning instead

if ( ${major} == 0 && ${minor} == 0 ) then
   echo "Warning: yosys 0.0.x unsupported.  Please update!"
   echo "Output is likely to be incompatible with qflow."
endif

if ( ${major} == 0 && ${minor} < 5 ) then

cat >> sevenseg.ys << EOF
# High-level synthesis
hierarchy -top sevenseg
EOF

endif

if ( ${?yosys_script} ) then
   if ( -f ${yosys_script} ) then
      cat ${yosys_script} >> sevenseg.ys
   else
      echo "Error: yosys script ${yosys_script} specified but not found"
   endif
else if ( ${major} != 0 || ${minor} >= 5 ) then

   cat >> sevenseg.ys << EOF

# High-level synthesis
synth -top sevenseg
EOF

else

   cat >> sevenseg.ys << EOF

# High-level synthesis
proc; memory; opt; fsm; opt

# Map to internal cell library
techmap; opt
EOF

endif

cat >> sevenseg.ys << EOF
# Map register flops
dfflibmap -liberty ${libertypath}
opt

EOF

if ( ${?abc_script} ) then
   if ( ${abc_script} != "" ) then
      cat >> sevenseg.ys << EOF
abc -exe ${bindir}/yosys-abc -liberty ${libertypath} -script ${abc_script}
flatten

EOF
   else
      echo "Warning: no abc script ${abc_script}, using default, no script" |& tee -a ${synthlog}
      cat >> sevenseg.ys << EOF
abc -exe ${bindir}/yosys-abc -liberty ${libertypath}
flatten

EOF
   endif
else
   cat >> sevenseg.ys << EOF
# Map combinatorial cells, standard script
abc -exe ${bindir}/yosys-abc -liberty ${libertypath} -script +strash;scorr;ifraig;retime,{D};strash;dch,-f;map,-M,1,{D}
flatten

EOF
endif

# Purge buffering of internal net name aliases.  Option "debug"
# retains all internal names by buffering them, resulting in a
# larger layout (especially for layouts derived from hierarchical
# source), but one in which all signal names from the source can
# be probed.

if ( ! ${?yosys_debug} ) then
   cat >> sevenseg.ys << EOF
clean -purge
EOF
endif

# Map tiehi and tielo, if they are defined

if ( ${?tiehi} && ${?tiehipin_out} ) then
   if ( "${tiehi}" != "" ) then
      echo "hilomap -hicell $tiehi $tiehipin_out" >> sevenseg.ys  
   endif
endif

if ( ${?tielo} && ${?tielopin_out} ) then
   if ( "${tielo}" != "" ) then
      echo "hilomap -locell $tielo $tielopin_out" >> sevenseg.ys  
   endif
endif

# Output buffering, if not specifically prevented
if ( ${major} > 0 || ${minor} > 1 ) then
   if (!($?nobuffers)) then
       cat >> sevenseg.ys << EOF
# Output buffering
iopadmap -outpad ${bufcell} ${bufpin_in}:${bufpin_out} -bits
EOF
   endif
endif

cat >> sevenseg.ys << EOF
# Cleanup
opt
clean
rename -enumerate
write_blif ${blif_opts} sevenseg_mapped.blif
EOF

#---------------------------------------------------------------------
# Yosys synthesis
#---------------------------------------------------------------------

if ( ! ${?yosys_options} ) then
   set yosys_options = ""
endif

# Check if "yosys_options" specifies a script to use for yosys.
# If not, call yosys with the default script.
set usescript = `echo ${yosys_options} | grep -- -s | wc -l`

# If there is a file sevenseg_mapped.blif, move it to a temporary
# place so we can see if yosys generates a new one or not.

if ( -f sevenseg_mapped.blif ) then
   mv sevenseg_mapped.blif sevenseg_mapped_orig.blif
endif

echo "Running yosys for verilog parsing and synthesis" |& tee -a ${synthlog}
if ( ${usescript} == 1 ) then
   eval ${bindir}/yosys ${yosys_options} |& tee -a ${synthlog}
else
   eval ${bindir}/yosys ${yosys_options} -s sevenseg.ys |& tee -a ${synthlog}
endif

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

#----------------------------------------------------------------------
# Add buffers in front of all outputs (for yosys versions before 0.2.0)
#----------------------------------------------------------------------

if ( ${major} == 0 && ${minor} < 2 ) then
   if ($?nobuffers) then
      set final_blif = "sevenseg_mapped_tmp.blif"
   else
      echo "Adding output buffers"
      ${scriptdir}/ybuffer.tcl sevenseg_mapped_tmp.blif sevenseg_mapped_buf.blif techdir/osu050.sh
      set final_blif = "sevenseg_mapped_buf.blif"
   endif
else
   # Buffers already handled within yosys
   set final_blif = "sevenseg_mapped_tmp.blif"
endif

#---------------------------------------------------------------------
# The following definitions will replace "LOGIC0" and "LOGIC1"
# with buffers from gnd and vdd, respectively.  This takes care
# of technologies where tie-low and tie-high cells are not
# defined.
#---------------------------------------------------------------------

echo "Cleaning Up blif file syntax" |& tee -a ${synthlog}

if ( "$tielo" == "") then
   set subs0a="/LOGIC0/s/O=/${bufpin_in}=gnd ${bufpin_out}=/"
   set subs0b="/LOGIC0/s/LOGIC0/${bufcell}/"
else
   set subs0a=""
   set subs0b=""
endif

if ( "$tiehi" == "") then
   set subs1a="/LOGIC1/s/O=/${bufpin_in}=vdd ${bufpin_out}=/"
   set subs1b="/LOGIC1/s/LOGIC1/${bufcell}/"
else
   set subs1a=""
   set subs1b=""
endif

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

#---------------------------------------------------------------------
# If "nofanout" is set, then don't run blifFanout.
#---------------------------------------------------------------------

if ($?nofanout) then
   set nchanged=0
else

#---------------------------------------------------------------------
# Make a copy of the original blif file, as this will be overwritten
# by the fanout handling process
#---------------------------------------------------------------------

   cp sevenseg.blif sevenseg_bak.blif

#---------------------------------------------------------------------
# Check all gates for fanout load, and adjust gate strengths as
# necessary.  Iterate this step until all gates satisfy drive
# requirements.
#
# Use option "-c value" in fanout_options to force a value for the
# (maximum expected) output load, in fF (default is 30fF)
# Use option "-l value" in fanout_options to force a value for the
# maximum latency, in ps (default is 1000ps)
#---------------------------------------------------------------------

   rm -f sevenseg_nofanout
   touch sevenseg_nofanout
   if ($?gndnet) then
      echo $gndnet >> sevenseg_nofanout
   endif
   if ($?vddnet) then
      echo $vddnet >> sevenseg_nofanout
   endif

   if (! $?fanout_options) then
      set fanout_options=""
   endif

   echo "Running blifFanout (iterative)" |& tee -a ${synthlog}
   echo "" >> ${synthlog}
   if (-f ${libertypath} && -f ${bindir}/blifFanout ) then
      set nchanged=1000
      while ($nchanged > 0)
         mv sevenseg.blif tmp.blif
         if ("x${separator}" == "x") then
	    set sepoption=""
         else
	    set sepoption="-s ${separator}"
         endif
         if ("x${bufcell}" == "x") then
	    set bufoption=""
         else
	    set bufoption="-b ${bufcell} -i ${bufpin_in} -o ${bufpin_out}"
         endif
         ${bindir}/blifFanout ${fanout_options} -I sevenseg_nofanout -p ${libertypath} ${sepoption} ${bufoption} tmp.blif sevenseg.blif >>& ${synthlog}
         set nchanged=$status
         echo "gates resized: $nchanged" |& tee -a ${synthlog}
      end
   else
      set nchanged=0
   endif
endif

#---------------------------------------------------------------------
# Spot check:  Did blifFanout produce an error?
#---------------------------------------------------------------------

if ( $nchanged < 0 ) then
   echo "blifFanout failure.  See file ${synthlog} for error messages." |& tee -a ${synthlog}
   echo "Premature exit." |& tee -a ${synthlog}
   echo "Synthesis flow stopped due to error condition." >> ${synthlog}
   exit 1
endif

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
