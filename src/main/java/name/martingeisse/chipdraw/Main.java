package name.martingeisse.chipdraw;

import name.martingeisse.chipdraw.technology.*;

import javax.swing.*;

public class Main {

    public static void main(String[] args) {

        String defaultTechnologyId = "default";

        SimpleTechnologyRepository technologyRepository = new SimpleTechnologyRepository();
        technologyRepository.add(new Technology(defaultTechnologyId, 3));

        Workbench workbench = new Workbench(technologyRepository);

        Design design = new Design(defaultTechnologyId, 20, 10);

        try {
            new MainWindow(workbench, design).setVisible(true);
        } catch (NoSuchTechnologyException e) {
            JOptionPane.showMessageDialog(null, e.getMessage());
        }

    }

}
