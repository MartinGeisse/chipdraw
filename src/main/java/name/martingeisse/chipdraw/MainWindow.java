package name.martingeisse.chipdraw;

import name.martingeisse.chipdraw.drc.DrcAgent;
import name.martingeisse.chipdraw.technology.NoSuchTechnologyException;
import name.martingeisse.chipdraw.technology.Technology;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.net.URI;

public class MainWindow extends JFrame {

    public static final int MIN_CELL_SIZE = 2;
    public static final int MAX_CELL_SIZE = 32;

    private final Workbench workbench;
    private final JPanel mainPanel;
    private final DrcAgent drcAgent;

    private Design design;
    private Technology technology;
    private int drawLayerIndex;
    private boolean drawing;
    private boolean erasing;
    private int cellSize;

    public MainWindow(Workbench workbench, Design design) throws NoSuchTechnologyException {
        super("Chipdraw");
        this.workbench = workbench;
        this.drcAgent = new DrcAgent();
        this.design = design;
        this.technology = workbench.getTechnologyRepository().getTechnology(design.getTechnologyId());
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setResizable(false);

        mainPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                for (int x = 0; x < MainWindow.this.design.getWidth(); x++) {
                    for (int y = 0; y < MainWindow.this.design.getHeight(); y++) {
                        g.setColor(new Color(
                                MainWindow.this.design.getLayers().get(0).getCell(x, y) ? 255 : 0,
                                MainWindow.this.design.getLayers().get(1).getCell(x, y) ? 255 : 0,
                                MainWindow.this.design.getLayers().get(2).getCell(x, y) ? 255 : 0
                        ));
                        g.fillRect(x * cellSize, y * cellSize, cellSize, cellSize);
                    }
                }
            }
        };
        MouseAdapter mouseAdapter = new MouseAdapter() {

            @Override
            public void mousePressed(MouseEvent e) {
                drawing = (e.getButton() == MouseEvent.BUTTON1);
                erasing = (e.getButton() == MouseEvent.BUTTON3);
                mouseMoved(e);
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                drawing = erasing = false;
            }

            @Override
            public void mouseMoved(MouseEvent e) {
                if (drawing || erasing) {
                    int x = e.getX() / cellSize;
                    int y = e.getY() / cellSize;
                    if (MainWindow.this.design.getLayers().get(drawLayerIndex).isValidPosition(x, y)) {
                        MainWindow.this.design.getLayers().get(drawLayerIndex).setCell(x, y, drawing);
                        mainPanel.repaint();
                        drcAgent.trigger();
                    }
                }
            }

            @Override
            public void mouseDragged(MouseEvent e) {
                mouseMoved(e);
            }

        };
        mainPanel.addMouseListener(mouseAdapter);
        mainPanel.addMouseMotionListener(mouseAdapter);
        mainPanel.addKeyListener(new KeyAdapter() {
            @Override
            public void keyTyped(KeyEvent event) {
                switch (event.getKeyChar()) {

                    case '1':
                        drawLayerIndex = 0;
                        break;

                    case '2':
                        drawLayerIndex = 1;
                        break;

                    case '3':
                        drawLayerIndex = 2;
                        break;

                    case '+':
                        if (cellSize < MAX_CELL_SIZE) {
                            cellSize *= 2;
                            updateWindowSize();
                        }
                        break;

                    case '-':
                        if (cellSize > MIN_CELL_SIZE) {
                            cellSize /= 2;
                            updateWindowSize();
                        }
                        break;

                    case 's':
                    case 'S':
                        showSaveDialog();
                        break;

                    case 'l':
                    case 'L':
                        showLoadDialog();
                        break;

                }
            }
        });
        mainPanel.setFocusable(true);
        mainPanel.grabFocus();
        add(mainPanel);

        {
            MenuBarBuilder builder = new MenuBarBuilder();
            builder.addMenu("File");
            builder.add("Load", this::showLoadDialog);
            builder.add("Save", this::showSaveDialog);
            builder.addSeparator();
            builder.add("Quit", () -> System.exit(0));
            builder.addMenu("Help");
            builder.addExternalLink("Contents", "https://github.com/MartinGeisse/chipdraw/blob/master/doc/index.md"); // TODO link to commit for this version
            builder.add("About", () -> JOptionPane.showMessageDialog(MainWindow.this, "Chipdraw by Martin Geisse"));
            setJMenuBar(builder.build());
        }

        resetUi();
        pack();
        drcAgent.setDesign(design);
    }

    private void resetUi() {
        drawLayerIndex = 0;
        drawing = false;
        erasing = false;
        cellSize = 16;
        updateWindowSize();
    }

    private void updateWindowSize() {
        mainPanel.setPreferredSize(new Dimension(design.getWidth() * cellSize, design.getHeight() * cellSize));
        mainPanel.setSize(new Dimension(design.getWidth() * cellSize, design.getHeight() * cellSize));
        mainPanel.repaint();
        pack();
    }

    private void showSaveDialog() {
        LoadAndSaveDialogs.showSaveDialog(MainWindow.this, MainWindow.this.design);
    }

    private void showLoadDialog() {
        Design design = LoadAndSaveDialogs.showLoadDialog(MainWindow.this);
        if (design == null) {
            return;
        }
        Technology technology;
        try {
            technology = MainWindow.this.workbench.getTechnologyRepository().getTechnology(design.getTechnologyId());
        } catch (NoSuchTechnologyException exception) {
            JOptionPane.showMessageDialog(MainWindow.this, exception.getMessage());
            return;
        }
        this.design = design;
        this.technology = technology;
        resetUi();
        drcAgent.setDesign(design);
    }

}
