package name.martingeisse.chipdraw;

import com.google.common.collect.ImmutableList;
import name.martingeisse.chipdraw.technology.*;

import javax.swing.*;

public class Main {

    public static void main(String[] args) {

        SimpleTechnologyRepository technologyRepository = new SimpleTechnologyRepository();

        String defaultTechnologyId = "default";
        PlaneSchema well = new PlaneSchema("well", ImmutableList.of("nwell", "pwell"));
        PlaneSchema diff = new PlaneSchema("diff", ImmutableList.of("ndiff", "pdiff"));
        PlaneSchema poly = new PlaneSchema("poly");
        PlaneSchema contact = new PlaneSchema("contact");
        PlaneSchema metal1 = new PlaneSchema("metal1");
        ImmutableList<PlaneSchema> layerSchemas = ImmutableList.of(well, diff, poly, contact, metal1);
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
