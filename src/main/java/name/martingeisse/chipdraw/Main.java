package name.martingeisse.chipdraw;

import com.google.common.collect.ImmutableList;
import name.martingeisse.chipdraw.technology.*;

import javax.swing.*;

public class Main {

    public static void main(String[] args) {

        SimpleTechnologyRepository technologyRepository = new SimpleTechnologyRepository();

        String defaultTechnologyId = "default";
        LayerSchema nwell = new LayerSchema("nwell");
        LayerSchema pwell = new LayerSchema("pwell");
        LayerSchema ndiff = new LayerSchema("ndiff");
        LayerSchema pdiff = new LayerSchema("pdiff");
        LayerSchema poly = new LayerSchema("poly");
        LayerSchema metal1 = new LayerSchema("metal1");
        LayerSchema diffToPolyVia = new LayerSchema("diffToPolyVia");
        LayerSchema polyToMetal1Via = new LayerSchema("polyToMetal1Via");
        ImmutableList<LayerSchema> layerSchemas = ImmutableList.of(nwell, pwell, ndiff, pdiff, poly, metal1,
                diffToPolyVia, polyToMetal1Via);
        Technology technology = new Technology(defaultTechnologyId, layerSchemas);
        technologyRepository.add(technology);

        Workbench workbench = new Workbench(technologyRepository);

        Design design = new Design(technology, 20, 10);

        try {
            new MainWindow(workbench, design).setVisible(true);
        } catch (NoSuchTechnologyException e) {
            JOptionPane.showMessageDialog(null, e.getMessage());
        }

    }

}
