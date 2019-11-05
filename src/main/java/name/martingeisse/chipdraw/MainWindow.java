package name.martingeisse.chipdraw;

import com.google.common.collect.ImmutableList;
import name.martingeisse.chipdraw.drc.DrcAgent;
import name.martingeisse.chipdraw.drc.Violation;
import name.martingeisse.chipdraw.icons.Icons;
import name.martingeisse.chipdraw.technology.NoSuchTechnologyException;
import name.martingeisse.chipdraw.ui.DesignPixelPanel;
import name.martingeisse.chipdraw.ui.MenuBarBuilder;
import name.martingeisse.chipdraw.ui.SingleIconBooleanCellRenderer;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;

public class MainWindow extends JFrame {

    public static final int MIN_CELL_SIZE = 2;
    public static final int MAX_CELL_SIZE = 32;

    private final Workbench workbench;
    private final JPanel sideBar;
    private final JPanel mainPanel;
    private final DrcAgent drcAgent;
    private final LoadAndSaveDialogs loadAndSaveDialogs;
    private final JButton drcButton;

    private Design design;
    private LayerUiState layerUiState;
    private boolean drawing;
    private boolean erasing;
    private int cellSize;
    private ImmutableList<Violation> drcViolations;

    public MainWindow(Workbench workbench, Design design) {
        super("Chipdraw");
        this.workbench = workbench;
        this.drcAgent = new DrcAgent();
        this.design = design;
        this.layerUiState = new LayerUiState(design.getTechnology());
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setSize(800, 600);
        setResizable(true);

        sideBar = new JPanel();
        sideBar.setLayout(new BorderLayout());
        sideBar.setPreferredSize(new Dimension(150, 0));
        add(sideBar, BorderLayout.LINE_START);
        {
            JTable table = new JTable(layerUiState.getSidebarTableModel()) {
                @Override
                protected void configureEnclosingScrollPane() {
                }
            };
            table.setShowGrid(false);
            table.setIntercellSpacing(new Dimension(0, 0));
            table.getColumnModel().getColumn(0).setCellRenderer(new SingleIconBooleanCellRenderer(Icons.get("pencil.png")));
            table.getColumnModel().getColumn(0).setMinWidth(20);
            table.getColumnModel().getColumn(1).setCellRenderer(new SingleIconBooleanCellRenderer(Icons.get("eye.png")));
            table.getColumnModel().getColumn(1).setMinWidth(20);
            table.getColumnModel().getColumn(2).setPreferredWidth(10_000);
            table.addMouseListener(new MouseAdapter() {
                @Override
                public void mousePressed(MouseEvent e) {
                    if (e.getButton() != MouseEvent.BUTTON1) {
                        return;
                    }
                    int rowIndex = table.rowAtPoint(e.getPoint());
                    int columnIndex = table.columnAtPoint(e.getPoint());
                    if (!design.getTechnology().isGlobalMaterialIndexValid(rowIndex)) {
                        return;
                    }
                    if (columnIndex == 0) {
                        layerUiState.setEditingGlobalMaterialIndex(rowIndex);
                        table.repaint();
                    } else if (columnIndex == 1) {
                        layerUiState.toggleMaterialVisible(rowIndex);
                        table.repaint();
                    }
                }
            });
            table.setRowSelectionAllowed(false);
            table.setFillsViewportHeight(true);
            table.setFocusable(false);
            JScrollPane scrollPane = new JScrollPane(table);
            sideBar.add(scrollPane, BorderLayout.PAGE_START);

            layerUiState.getSidebarTableModel().addTableModelListener(event -> {
                MainWindow.this.repaint();
            });
        }
        {
            drcButton = new JButton("DRC");
            drcButton.setFocusable(false);
            sideBar.add(drcButton, BorderLayout.PAGE_END);
            consumeDrcResult(null);
            drcAgent.addResultListener(this::consumeDrcResult);
            drcButton.addActionListener(event -> {
                ImmutableList<Violation> violationsToShow = drcViolations;
                if (violationsToShow.isEmpty()) {
                    JOptionPane.showMessageDialog(MainWindow.this, "DRC OK");
                    return;
                }
                if (violationsToShow.size() > 5) {
                    violationsToShow = violationsToShow.subList(0, 5);
                }
                StringBuilder builder = new StringBuilder("DRC violations (first 5): ");
                for (Violation violation : violationsToShow) {
                    builder.append("\n").append(violation.getFullText());
                }
                JOptionPane.showMessageDialog(MainWindow.this, builder.toString());
            });
        }

        // TODO
        Paint layer0Paint;
        {
            BufferedImage image = new BufferedImage(3, 3, BufferedImage.TYPE_INT_RGB);
            image.setRGB(0, 0, 0xff0000);
            image.setRGB(1, 1, 0xff0000);
            image.setRGB(2, 2, 0xff0000);
            layer0Paint = new TexturePaint(image, new Rectangle2D.Float(0, 0, 3, 3));
        }

        mainPanel = new DesignPixelPanel(this) {
            @Override
            protected void drawPixel(int cellX, int cellY, int screenX, int screenY, int screenSize) {
                boolean l0 = MainWindow.this.design.getPlanes().get(0).getCell(x, y) && layerUiState.getVisible(0);
                boolean l1 = MainWindow.this.design.getPlanes().get(1).getCell(x, y) && layerUiState.getVisible(1);
                boolean l2 = MainWindow.this.design.getPlanes().get(2).getCell(x, y) && layerUiState.getVisible(2);

                if (l1 || l2) {
                    g.setColor(new Color(0, l1 ? 255 : 0, l2 ? 255 : 0));
                    g.fillRect(x * cellSize, y * cellSize, cellSize, cellSize);
                } else if (l0) {
                    g.setPaint(layer0Paint);
                    g.fillRect(x * cellSize, y * cellSize, cellSize, cellSize);
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
                    int globalMaterialIndex = layerUiState.getEditingGlobalMaterialIndex();
                    int localMaterialIndex = design.getTechnology().getLocalMaterialIndexForGlobalMaterialIndex(globalMaterialIndex);
                    int planeIndex = design.getTechnology().getPlaneIndexForGlobalMaterialIndex(globalMaterialIndex);
                    Plane plane = design.getPlanes().get(planeIndex);
                    if (plane.isValidPosition(x, y)) {
                        plane.setCell(x, y, localMaterialIndex);
                        consumeDrcResult(null);
                        drcAgent.trigger();
                        mainPanel.repaint();
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
                    case '2':
                    case '3':
                    case '4':
                    case '5':
                    case '6':
                    case '7':
                    case '8':
                    case '9': {
                        int globalMaterialIndex = event.getKeyChar() - '1';
                        if (design.getTechnology().isGlobalMaterialIndexValid(globalMaterialIndex)) {
                            layerUiState.setEditingGlobalMaterialIndex(globalMaterialIndex);
                        }
                        break;
                    }

                    case '0':
                        if (design.getTechnology().isGlobalMaterialIndexValid(9)) {
                            layerUiState.setEditingGlobalMaterialIndex(9);
                        }
                        break;

                    case '+':
                        if (cellSize < MAX_CELL_SIZE) {
                            cellSize *= 2;
                            updateMainPanelSize();
                        }
                        break;

                    case '-':
                        if (cellSize > MIN_CELL_SIZE) {
                            cellSize /= 2;
                            updateMainPanelSize();
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
        JScrollPane mainPanelScrollPane = new JScrollPane(mainPanel, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
        add(mainPanelScrollPane);
        mainPanel.grabFocus();

        {
            MenuBarBuilder builder = new MenuBarBuilder();
            builder.addMenu("File");
            builder.add("Load", this::showLoadDialog);
            builder.add("Save", this::showSaveDialog);
            builder.addSeparator();
            builder.add("Quit", () -> System.exit(0));
            builder.addMenu("Test");
            builder.add("Corner Stitching Extractor", () -> CornerStitchingExtrator.extract(design));
            builder.add("Connectivity Extractor", () -> ConnectivityExtractor.extract(design));
            builder.addMenu("Help");
            builder.addExternalLink("Contents", "https://github.com/MartinGeisse/chipdraw/blob/master/doc/index.md"); // TODO link to commit for this version
            builder.add("About", () -> JOptionPane.showMessageDialog(MainWindow.this, About.ABOUT_TEXT));
            setJMenuBar(builder.build());
        }

        resetUi();
        drcAgent.setDesign(design);
        loadAndSaveDialogs = new LoadAndSaveDialogs(workbench.getTechnologyRepository());
    }

    public Design getCurrentDesign() {
        return design;
    }

    public int getCurrentCellSize() {
        return cellSize;
    }

    private void resetUi() {
        layerUiState.setEditingGlobalMaterialIndex(0);
        drawing = false;
        erasing = false;
        cellSize = 16;
        updateMainPanelSize();
    }

    private void updateMainPanelSize() {
        Dimension size = new Dimension(design.getWidth() * cellSize, design.getHeight() * cellSize);
        mainPanel.setPreferredSize(size);
        mainPanel.setSize(size);
        mainPanel.repaint();
    }

    private void showSaveDialog() {
        loadAndSaveDialogs.showSaveDialog(MainWindow.this, MainWindow.this.design);
    }

    private void showLoadDialog() {
        Design design;
        try {
            design = loadAndSaveDialogs.showLoadDialog(MainWindow.this);
        } catch (NoSuchTechnologyException exception) {
            JOptionPane.showMessageDialog(MainWindow.this, exception.getMessage());
            return;
        }
        if (design == null) {
            return;
        }
        this.design = design;
        this.layerUiState = new LayerUiState(design.getTechnology());
        consumeDrcResult(null);
        drcAgent.setDesign(design);
        resetUi();
    }

    private void consumeDrcResult(ImmutableList<Violation> violations) {
        this.drcViolations = violations;
        if (violations == null) {
            drcButton.setForeground(new Color(128, 128, 0));
        } else if (violations.isEmpty()) {
            drcButton.setForeground(new Color(0, 128, 0));
        } else {
            drcButton.setForeground(new Color(128, 0, 0));
        }
    }

}
