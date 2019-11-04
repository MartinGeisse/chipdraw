package name.martingeisse.chipdraw;

import com.google.common.collect.ImmutableList;
import name.martingeisse.chipdraw.technology.*;

import javax.swing.*;

public class Main {

    public static void main(String[] args) {

        SimpleTechnologyRepository technologyRepository = new SimpleTechnologyRepository();

        String defaultTechnologyId = "default";
        LayerSchema well = new LayerSchema("well", ImmutableList.of("nwell", "pwell"));
        LayerSchema diff = new LayerSchema("diff", ImmutableList.of("ndiff", "pdiff"));
        LayerSchema poly = new LayerSchema("poly");
        LayerSchema contact = new LayerSchema("contact");
        LayerSchema metal1 = new LayerSchema("metal1");
        ImmutableList<LayerSchema> layerSchemas = ImmutableList.of(well, diff, poly, contact, metal1);
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
