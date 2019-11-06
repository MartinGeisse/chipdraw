package name.martingeisse.chipdraw;

import com.google.common.collect.ImmutableList;
import name.martingeisse.chipdraw.drc.DrcAgent;
import name.martingeisse.chipdraw.drc.Violation;
import name.martingeisse.chipdraw.extractor.ConnectivityExtractor;
import name.martingeisse.chipdraw.extractor.CornerStitchingExtrator;
import name.martingeisse.chipdraw.icons.Icons;
import name.martingeisse.chipdraw.magic.MagicExportDialog;
import name.martingeisse.chipdraw.technology.NoSuchTechnologyException;
import name.martingeisse.chipdraw.ui.DesignPixelPanel;
import name.martingeisse.chipdraw.ui.MenuBarBuilder;
import name.martingeisse.chipdraw.ui.SingleIconBooleanCellRenderer;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

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
    private MaterialUiState materialUiState;
    private boolean drawing;
    private boolean erasing;
    private int cellSize;
    private ImmutableList<Violation> drcViolations;

    public MainWindow(Workbench _workbench, Design _design) {
        super("Chipdraw");
        this.workbench = _workbench;
        this.drcAgent = new DrcAgent();
        this.design = _design;
        this.materialUiState = new MaterialUiState(design.getTechnology());
        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        setSize(800, 600);
        setResizable(true);

        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosed(WindowEvent e) {
                drcAgent.dispose();
            }
        });

        sideBar = new JPanel();
        sideBar.setLayout(new BorderLayout());
        sideBar.setPreferredSize(new Dimension(150, 0));
        add(sideBar, BorderLayout.LINE_START);
        {
            JTable table = new JTable(materialUiState.getSidebarTableModel()) {
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
                        materialUiState.setEditingGlobalMaterialIndex(rowIndex);
                        table.repaint();
                    } else if (columnIndex == 1) {
                        materialUiState.toggleMaterialVisible(rowIndex);
                        table.repaint();
                    }
                }
            });
            table.setRowSelectionAllowed(false);
            table.setFillsViewportHeight(true);
            table.setFocusable(false);
            JScrollPane scrollPane = new JScrollPane(table);
            sideBar.add(scrollPane, BorderLayout.PAGE_START);

            materialUiState.getSidebarTableModel().addTableModelListener(event -> {
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

        mainPanel = new DesignPixelPanel(this) {

            private final Paint nwellPaint = createHatching(0x0000ff);
            private final Paint pwellPaint = createHatching(0xff0000);
            private final Paint ndiffPaint = createCrossHatching(0x0000ff);
            private final Paint pdiffPaint = createCrossHatching(0xff0000);
            private final Paint polyPaint = new Color(0, 128, 0);
            private final Paint contactPaint = Color.GRAY;
            private final Paint metal1Paint = Color.LIGHT_GRAY;
            private final Paint via12Paint = new Color(0x008080);
            private final Paint metal2Paint = new Color(0x00c0c0);

            private int getPixel(int planeIndex, int x, int y) {
                if (materialUiState.isPlaneVisible(planeIndex)) {
                    return design.getPlanes().get(planeIndex).getCell(x, y);
                } else {
                    return Plane.EMPTY_CELL;
                }
            }

            @Override
            protected void drawPixel(Graphics2D g, int cellX, int cellY, int screenX, int screenY, int screenSize) {

                // read pixel per plane
                int wellPlane = getPixel(0, cellX, cellY);
                int diffPlane = getPixel(1, cellX, cellY);
                int polyPlane = getPixel(2, cellX, cellY);
                int metal1Plane = getPixel(3, cellX, cellY);
                int metal2Plane = getPixel(4, cellX, cellY);

                // select paint
                if (metal2Plane != Plane.EMPTY_CELL) {
                    g.setPaint(metal2Plane == 0 ? via12Paint : metal2Paint);
                } else if (metal1Plane != Plane.EMPTY_CELL) {
                    g.setPaint(metal1Plane == 0 ? contactPaint : metal1Paint);
                } else if (polyPlane != Plane.EMPTY_CELL) {
                    g.setPaint(polyPaint);
                } else if (diffPlane != Plane.EMPTY_CELL) {
                    g.setPaint(diffPlane == 0 ? ndiffPaint : pdiffPaint);
                } else if (wellPlane != Plane.EMPTY_CELL) {
                    g.setPaint(wellPlane == 0 ? nwellPaint : pwellPaint);
                } else {
                    return;
                }

                // draw the pixel
                g.fillRect(screenX, screenY, screenSize, screenSize);

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
                    int globalMaterialIndex = materialUiState.getEditingGlobalMaterialIndex();
                    int localMaterialIndex = design.getTechnology().getLocalMaterialIndexForGlobalMaterialIndex(globalMaterialIndex);
                    int planeIndex = design.getTechnology().getPlaneIndexForGlobalMaterialIndex(globalMaterialIndex);
                    Plane plane = design.getPlanes().get(planeIndex);
                    if (plane.isValidPosition(x, y)) {
                        plane.setCell(x, y, drawing ? localMaterialIndex : Plane.EMPTY_CELL);
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
                            materialUiState.setEditingGlobalMaterialIndex(globalMaterialIndex);
                        }
                        break;
                    }

                    case '0':
                        if (design.getTechnology().isGlobalMaterialIndexValid(9)) {
                            materialUiState.setEditingGlobalMaterialIndex(9);
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
            builder.add("New", () -> new MainWindow(workbench, new Design(design.getTechnology(), 20, 10)).setVisible(true));
            builder.add("Load", this::showLoadDialog);
            builder.add("Save", this::showSaveDialog);
            builder.addSeparator();
            builder.add("Quit", () -> System.exit(0));
            builder.addMenu("Test");
            builder.add("Corner Stitching Extractor", () -> new CornerStitchingExtrator.Test().extract(design));
            builder.add("Connectivity Extractor", () -> new ConnectivityExtractor.Test().extract(design));
            builder.add("Magic Export", () -> MagicExportDialog.showExportDialog(this, design));
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
        materialUiState.setEditingGlobalMaterialIndex(0);
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
        loadAndSaveDialogs.showSaveDialog(this, design);
    }

    private void showLoadDialog() {
        Design design;
        try {
            design = loadAndSaveDialogs.showLoadDialog(this);
        } catch (NoSuchTechnologyException exception) {
            JOptionPane.showMessageDialog(this, exception.getMessage());
            return;
        }
        if (design == null) {
            return;
        }
        this.design = design;
        this.materialUiState = new MaterialUiState(design.getTechnology());
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
