package name.martingeisse.chipdraw.ui;

import com.google.common.collect.ImmutableList;
import name.martingeisse.chipdraw.About;
import name.martingeisse.chipdraw.Editor;
import name.martingeisse.chipdraw.Workbench;
import name.martingeisse.chipdraw.design.Design;
import name.martingeisse.chipdraw.design.DesignOperation;
import name.martingeisse.chipdraw.design.Plane;
import name.martingeisse.chipdraw.drc.Violation;
import name.martingeisse.chipdraw.global_tools.Autocropper;
import name.martingeisse.chipdraw.global_tools.ConnectivityExtractor;
import name.martingeisse.chipdraw.global_tools.CornerStitchingExtrator;
import name.martingeisse.chipdraw.global_tools.Enlarger;
import name.martingeisse.chipdraw.global_tools.magic.MagicExportDialog;
import name.martingeisse.chipdraw.icons.Icons;
import name.martingeisse.chipdraw.technology.NoSuchTechnologyException;
import name.martingeisse.chipdraw.ui.util.DesignPixelPanel;
import name.martingeisse.chipdraw.ui.util.MenuBarBuilder;
import name.martingeisse.chipdraw.ui.util.SingleIconBooleanCellRenderer;
import name.martingeisse.chipdraw.util.UserVisibleMessageException;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class MainWindow extends JFrame implements Editor.Ui {

    public static final int MIN_CELL_SIZE = 2;
    public static final int MAX_CELL_SIZE = 32;

    private final Workbench workbench;
    private final JPanel sideBar;
    private final JPanel mainPanel;
    private final LoadAndSaveDialogs loadAndSaveDialogs;
    private final JButton drcButton;
    private final MaterialUiState materialUiState;
    private final Editor editor;

    private boolean drawing;
    private boolean erasing;
    private int cellSize;
    private int cursorSize = 1;

    public MainWindow(Workbench _workbench, Design _design) {
        super("Chipdraw");
        this.workbench = _workbench;
        this.materialUiState = new MaterialUiState(_design.getTechnology());
        this.editor = new Editor(this);

        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        setSize(800, 600);
        setResizable(true);

        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosed(WindowEvent e) {
                editor.dispose();
            }
        });

        sideBar = new JPanel();
        sideBar.setLayout(new BoxLayout(sideBar, BoxLayout.Y_AXIS));
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
                    if (!editor.getDesign().getTechnology().isGlobalMaterialIndexValid(rowIndex)) {
                        return;
                    }
                    if (columnIndex == 1) {
                        materialUiState.toggleMaterialVisible(rowIndex);
                        table.repaint();
                    } else {
                        materialUiState.setEditingGlobalMaterialIndex(rowIndex);
                        table.repaint();
                    }
                }
            });
            table.setRowSelectionAllowed(false);
            table.setFillsViewportHeight(true);
            table.setFocusable(false);
            JScrollPane scrollPane = new JScrollPane(table);
            sideBar.add(scrollPane);

            materialUiState.getSidebarTableModel().addTableModelListener(event -> {
                MainWindow.this.repaint();
            });
        }
        {
            JPanel cursorSizeButtonPanel = new JPanel();
            cursorSizeButtonPanel.setLayout(new BoxLayout(cursorSizeButtonPanel, BoxLayout.X_AXIS));
            JButton button1x1 = new JButton(Icons.get("cursor_1x1.png"));
            button1x1.setFocusable(false);
            button1x1.addActionListener(event -> cursorSize = 1);
            cursorSizeButtonPanel.add(button1x1);
            JButton button2x2 = new JButton(Icons.get("cursor_2x2.png"));
            button2x2.setFocusable(false);
            button2x2.addActionListener(event -> cursorSize = 2);
            cursorSizeButtonPanel.add(button2x2);
            JButton button3x3 = new JButton(Icons.get("cursor_3x3.png"));
            button3x3.setFocusable(false);
            button3x3.addActionListener(event -> cursorSize = 3);
            cursorSizeButtonPanel.add(button3x3);
            sideBar.add(cursorSizeButtonPanel);
        }
        sideBar.add(Box.createGlue());
        {
            drcButton = new JButton("DRC");
            drcButton.setFocusable(false);
            sideBar.add(drcButton);
            consumeDrcResult(null);
            drcButton.addActionListener(event -> {
                ImmutableList<Violation> violationsToShow = editor.getDrcViolations();
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
            drcButton.setMaximumSize(new Dimension(Integer.MAX_VALUE, drcButton.getPreferredSize().height));
            drcButton.setAlignmentX(0.5f);

        }

        mainPanel = new DesignPixelPanel(this) {

            private final Paint nwellPaint = createHatching(0x0000ff);
            private final Paint pwellPaint = createHatching(0xff0000);
            private final Paint ndiffPaint = new Color(0x0000ff);
            private final Paint pdiffPaint = new Color(0xff0000);
            private final Paint polyPaint = new Color(0, 128, 0);
            private final Paint contactPaint = Color.GRAY;
            private final Paint metal1Paint = Color.LIGHT_GRAY;
            private final Paint via12Paint = new Color(0x008080);
            private final Paint metal2Paint = new Color(0x00c0c0);
            private final Paint padPaint = new Color(0xff00ff);

            private int getPixel(int planeIndex, int x, int y) {
                if (materialUiState.isPlaneVisible(planeIndex)) {
                    return editor.getDesign().getPlanes().get(planeIndex).getCell(x, y);
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
                int padPlane = getPixel(5, cellX, cellY);

                // select paint
                if (padPlane != Plane.EMPTY_CELL) {
                    g.setPaint(padPaint);
                } else if (metal2Plane != Plane.EMPTY_CELL) {
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
                    performOperation(design -> {
                        int x = e.getX() / cellSize;
                        int y = e.getY() / cellSize;
                        int globalMaterialIndex = materialUiState.getEditingGlobalMaterialIndex();
                        int localMaterialIndex = design.getTechnology().getLocalMaterialIndexForGlobalMaterialIndex(globalMaterialIndex);
                        int planeIndex = design.getTechnology().getPlaneIndexForGlobalMaterialIndex(globalMaterialIndex);
                        Plane plane = design.getPlanes().get(planeIndex);
                        if (plane.isValidPosition(x, y)) {
                            int offset = (cursorSize - 1) / 2;
                            plane.drawRectangleAutoclip(x - offset, y - offset, cursorSize, cursorSize, drawing ? localMaterialIndex : Plane.EMPTY_CELL);
                        }
                        return new DesignOperation.Result();
                    });
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
                        if (editor.getDesign().getTechnology().isGlobalMaterialIndexValid(globalMaterialIndex)) {
                            materialUiState.setEditingGlobalMaterialIndex(globalMaterialIndex);
                        }
                        break;
                    }

                    case '0':
                        if (editor.getDesign().getTechnology().isGlobalMaterialIndexValid(9)) {
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
            builder.add("New", () -> new MainWindow(workbench, new Design(editor.getDesign().getTechnology(), 200, 200)).setVisible(true));
            builder.add("Load", this::showLoadDialog);
            builder.add("Save", this::showSaveDialog);
            builder.addSeparator();
            builder.add("Quit", () -> System.exit(0));
            builder.addMenu("Edit");
            builder.add("Undo", () -> editor.undo());
            builder.addMenu("Test");
            builder.add("Corner Stitching Extractor", () -> new CornerStitchingExtrator.Test().extract(editor.getDesign()));
            builder.add("Connectivity Extractor", () -> new ConnectivityExtractor.Test().extract(editor.getDesign()));
            builder.add("Magic Export", () -> MagicExportDialog.showExportDialog(this, editor.getDesign()));
            builder.add("Enlarge", () -> performOperation(design -> new DesignOperation.Result(design, new Enlarger(design).enlarge())));
            builder.add("Autocrop", () -> performOperation(design -> new DesignOperation.Result(design, new Autocropper(design).autocrop())));
            builder.addMenu("Help");
            builder.addExternalLink("Contents", "https://github.com/MartinGeisse/chipdraw/blob/master/doc/index.md"); // TODO link to commit for this version
            builder.add("About", () -> JOptionPane.showMessageDialog(MainWindow.this, About.ABOUT_TEXT));
            setJMenuBar(builder.build());
        }

        loadAndSaveDialogs = new LoadAndSaveDialogs(workbench.getTechnologyRepository());
        editor.restart(_design);
    }

    public Design getCurrentDesign() {
        return editor.getDesign();
    }

    public int getCurrentCellSize() {
        return cellSize;
    }

    private void updateMainPanelSize() {
        Dimension size = new Dimension(editor.getDesign().getWidth() * cellSize, editor.getDesign().getHeight() * cellSize);
        mainPanel.setPreferredSize(size);
        mainPanel.setSize(size);
        mainPanel.repaint();
    }

    private void showSaveDialog() {
        loadAndSaveDialogs.showSaveDialog(this, editor.getDesign());
    }

    private void showLoadDialog() {
        Design design;
        try {
            design = loadAndSaveDialogs.showLoadDialog(this);
        } catch (NoSuchTechnologyException exception) {
            JOptionPane.showMessageDialog(this, exception.getMessage());
            return;
        }
        if (design != null) {
            editor.restart(design);
        }
    }

    public void performOperation(DesignOperation operation) {
        try {
            editor.performOperation(operation);
        } catch (UserVisibleMessageException e) {
            JOptionPane.showMessageDialog(this, e.getMessage());
        }
    }

    @Override
    public void onRestart() {
        materialUiState.setTechnology(editor.getDesign().getTechnology());
        materialUiState.setEditingGlobalMaterialIndex(0);
        drawing = false;
        erasing = false;
        cellSize = 16;
        updateMainPanelSize();
    }

    @Override
    public void onDesignObjectReplaced() {
        materialUiState.setTechnology(editor.getDesign().getTechnology());
        updateMainPanelSize();
    }

    @Override
    public void onDesignModified() {
        mainPanel.repaint();
    }

    @Override
    public void consumeDrcResult(ImmutableList<Violation> violations) {
        if (violations == null) {
            drcButton.setForeground(new Color(128, 128, 0));
        } else if (violations.isEmpty()) {
            drcButton.setForeground(new Color(0, 128, 0));
        } else {
            drcButton.setForeground(new Color(128, 0, 0));
        }
    }

}