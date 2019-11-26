package name.martingeisse.chipdraw.pnr.ui;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import name.martingeisse.chipdraw.pnr.About;
import name.martingeisse.chipdraw.pnr.Editor;
import name.martingeisse.chipdraw.pnr.Workbench;
import name.martingeisse.chipdraw.pnr.cell.NoSuchCellLibraryException;
import name.martingeisse.chipdraw.pnr.drc.PositionedViolation;
import name.martingeisse.chipdraw.pnr.drc.Violation;
import name.martingeisse.chipdraw.pnr.global_tools.Autocropper;
import name.martingeisse.chipdraw.pnr.global_tools.ConnectivityExtractor;
import name.martingeisse.chipdraw.pnr.global_tools.CornerStitchingExtrator;
import name.martingeisse.chipdraw.pnr.global_tools.Enlarger;
import name.martingeisse.chipdraw.pnr.global_tools.magic.MagicExportDialog;
import name.martingeisse.chipdraw.pnr.icons.Icons;
import name.martingeisse.chipdraw.pnr.operation.DesignOperation;
import name.martingeisse.chipdraw.pnr.operation.OutOfPlaceDesignOperation;
import name.martingeisse.chipdraw.pnr.operation.library.DrawPoints;
import name.martingeisse.chipdraw.pnr.operation.library.ErasePoints;
import name.martingeisse.chipdraw.pnr.design.NoSuchTechnologyException;
import name.martingeisse.chipdraw.pnr.ui.util.DesignPixelPanel;
import name.martingeisse.chipdraw.pnr.ui.util.MenuBarBuilder;
import name.martingeisse.chipdraw.pnr.ui.util.UiRunnable;
import name.martingeisse.chipdraw.pnr.util.Point;
import name.martingeisse.chipdraw.pnr.util.UserVisibleMessageException;
import name.martingeisse.chipdraw.pnr.ui.util.SingleIconBooleanCellRenderer;
import name.martingeisse.chipdraw.pnr.design.Design;
import name.martingeisse.chipdraw.pnr.design.PixelPlane;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.HashMap;
import java.util.Map;

public class MainWindow extends JFrame implements Editor.Ui {

    public static final int MIN_PIXEL_SIZE = 2;
    public static final int MAX_PIXEL_SIZE = 32;

    private final Workbench workbench;
    private final JPanel sideBar;
    private final JPanel mainPanel;
    private final LoadAndSaveDialogs loadAndSaveDialogs;
    private final JButton drcButton;
    private final PlaneUiState planeUiState;
    private final Editor editor;
    private final JLabel bottomLine;

    private boolean drawing, erasing, firstPixelOfStroke;
    private int pixelSize;

    private int mousePixelX, mousePixelY;
    private Map<name.martingeisse.chipdraw.pnr.util.Point, String> positionedDrcViolations = ImmutableMap.of();

    public MainWindow(Workbench _workbench, Design _design) {
        super("Chipdraw");
        this.workbench = _workbench;
        this.planeUiState = new PlaneUiState(_design.getTechnology());
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
            JTable table = new JTable(planeUiState.getSidebarTableModel()) {
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
                    planeUiState.onClick(rowIndex, columnIndex);
                    table.repaint();
                }
            });
            table.setRowSelectionAllowed(false);
            table.setFillsViewportHeight(true);
            table.setFocusable(false);
            JScrollPane scrollPane = new JScrollPane(table);
            sideBar.add(scrollPane);

            planeUiState.getSidebarTableModel().addTableModelListener(event -> {
                MainWindow.this.repaint();
            });
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

        // TODO from here

        mainPanel = new DesignPixelPanel(this) {

            private Material getPixel(PlaneSchema planeSchema, int x, int y) {
                PixelPlane plane = editor.getDesign().getPlane(planeSchema);
                if (planeUiState.isPlaneVisible(plane.getSchema())) {
                    return plane.getPixel(x, y);
                } else {
                    return Material.NONE;
                }
            }

            @Override
            protected void drawPixel(Graphics2D g, int pixelX, int pixelY, int screenX, int screenY, int screenSize) {

                // read pixel per plane
                Material wellPlane = getPixel(Technologies.Concept.PLANE_WELL, pixelX, pixelY);
                Material diffPlane = getPixel(Technologies.Concept.PLANE_DIFF, pixelX, pixelY);
                Material polyPlane = getPixel(Technologies.Concept.PLANE_POLY, pixelX, pixelY);
                Material metal1Plane = getPixel(Technologies.Concept.PLANE_METAL1, pixelX, pixelY);
                Material metal2Plane = getPixel(Technologies.Concept.PLANE_METAL2, pixelX, pixelY);
                Material padPlane = getPixel(Technologies.Concept.PLANE_PAD, pixelX, pixelY);

                if (wellPlane != Material.NONE) {
                    g.setPaint(wellPlane == Technologies.Concept.MATERIAL_NWELL ? getHatching(0x0000ff, 0) : getHatching(0xff0000, 0));
                    g.fillRect(screenX, screenY, screenSize, screenSize);
                }
                if (diffPlane != Material.NONE) {
                    g.setPaint(diffPlane == Technologies.Concept.MATERIAL_NDIFF ? new Color(0x0000ff) : new Color(0xff0000));
                    g.fillRect(screenX, screenY, screenSize, screenSize);
                }
                if (polyPlane != Material.NONE) {
                    if (planeUiState.isPlaneVisible(Technologies.Concept.PLANE_METAL1)) {
                        g.setPaint(new Color(0, 128, 0));
                    } else {
                        g.setPaint(getHatching(0x008000, 2, true));
                    }
                    g.fillRect(screenX, screenY, screenSize, screenSize);
                }
                if (metal1Plane != Material.NONE) {
                    if (metal1Plane == Technologies.Concept.MATERIAL_CONTACT) {
                        g.setPaint(Color.GRAY);
                    } else if (planeUiState.isPlaneVisible(Technologies.Concept.PLANE_METAL2)) {
                        g.setPaint(Color.LIGHT_GRAY);
                    } else {
                        g.setPaint(getHatching(0xc0c0c0, 4));
                    }
                    g.fillRect(screenX, screenY, screenSize, screenSize);
                }
                if (metal2Plane != Material.NONE) {
                    if (metal2Plane == Technologies.Concept.MATERIAL_VIA12) {
                        g.setPaint(new Color(0x008080));
                    } else if (planeUiState.isPlaneVisible(Technologies.Concept.PLANE_PAD)) {
                        g.setPaint(new Color(0x00c0c0));
                    } else {
                        g.setPaint(getHatching(0x00c0c0, 0, true));
                    }
                    g.fillRect(screenX, screenY, screenSize, screenSize);
                }
                if (padPlane != Material.NONE) {
                    g.setPaint(new Color(0xff00ff));
                    g.fillRect(screenX, screenY, screenSize, screenSize);
                }

                if (positionedDrcViolations.get(new name.martingeisse.chipdraw.pnr.util.Point(pixelX, pixelY)) != null) {
                    int centerX = screenX + screenSize / 2 - 2;
                    int centerY = screenY + screenSize / 2 - 2;
                    g.setColor(Color.RED);
                    g.fillOval(centerX, centerY, 4, 4);
                    g.setColor(Color.BLACK);
                    g.drawOval(centerX, centerY, 4, 4);
                }

            }
        };
        MouseAdapter mouseAdapter = new MouseAdapter() {

            @Override
            public void mousePressed(MouseEvent e) {
                drawing = (e.getButton() == MouseEvent.BUTTON1);
                erasing = (e.getButton() == MouseEvent.BUTTON3);
                firstPixelOfStroke = true;
                mouseMoved(e);
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                drawing = erasing = false;
            }

            @Override
            public void mouseMoved(MouseEvent event) {
                mousePixelX = event.getX() / pixelSize;
                mousePixelY = event.getY() / pixelSize;
                if (drawing || erasing) {
                    Material material = planeUiState.getEditingMaterial();
                    if (MainWindow.this.drawing) {
                        performOperation(new DrawPoints(mousePixelX, mousePixelY, 1, 1, material), !firstPixelOfStroke);
                    } else {
                        performOperation(new ErasePoints(mousePixelX, mousePixelY, 1, 1, material.getPlaneSchema()), !firstPixelOfStroke);
                    }
                    firstPixelOfStroke = false;
                }
                updateBottomLine();
            }

            @Override
            public void mouseDragged(MouseEvent e) {
                mouseMoved(e);
            }

        };
        // TODO till here
        mainPanel.addMouseListener(mouseAdapter);
        mainPanel.addMouseMotionListener(mouseAdapter);
        {
            InputMap inputMap = mainPanel.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW);
            inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_Z, Toolkit.getDefaultToolkit().getMenuShortcutKeyMaskEx()), ActionName.UNDO);
            inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_Y, Toolkit.getDefaultToolkit().getMenuShortcutKeyMaskEx()), ActionName.REDO);
            inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_Z, KeyEvent.SHIFT_DOWN_MASK | Toolkit.getDefaultToolkit().getMenuShortcutKeyMaskEx()), ActionName.REDO);
            inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_Q, 0), ActionName.VISIBILITY_UP);
            inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_A, 0), ActionName.VISIBILITY_DOWN);
        }
        mainPanel.getActionMap().put(ActionName.UNDO, new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                run(editor::undo);
                repaint();
            }
        });
        mainPanel.getActionMap().put(ActionName.REDO, new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                run(editor::redo);
                repaint();
            }
        });
        mainPanel.getActionMap().put(ActionName.VISIBILITY_UP, new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                run(planeUiState::moveVisibilityUp);
                repaint();
            }
        });
        mainPanel.getActionMap().put(ActionName.VISIBILITY_DOWN, new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                run(planeUiState::moveVisibilityDown);
                repaint();
            }
        });
        mainPanel.addKeyListener(new KeyAdapter() {
            @Override
            public void keyTyped(KeyEvent event) {
                boolean controlOrCommand = (event.getModifiersEx() & Toolkit.getDefaultToolkit().getMenuShortcutKeyMaskEx()) != 0;
                switch (event.getKeyChar()) {

                    case '1':
                    case '2':
                    case '3':
                    case '4':
                    case '5':
                    case '6':
                    case '7':
                    case '8':
                    case '9':
                        planeUiState.onClick(event.getKeyChar() - '1', 0);
                        break;

                    case '0':
                        planeUiState.onClick(9, 0);
                        break;

                    case '+':
                        if (pixelSize < MAX_PIXEL_SIZE) {
                            pixelSize *= 2;
                            updateMainPanelSize();
                        }
                        break;

                    case '-':
                        if (pixelSize > MIN_PIXEL_SIZE) {
                            pixelSize /= 2;
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

                    case 'z':
                        if (controlOrCommand) {
                            System.out.println("undo");
                        }
                        break;

                    case 'y':
                    case 'Z':
                        if (controlOrCommand) {
                            System.out.println("redo");
                        }
                        break;

                }
            }
        });
        mainPanel.setFocusable(true);
        JScrollPane mainPanelScrollPane = new JScrollPane(mainPanel, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
        add(mainPanelScrollPane);
        mainPanel.grabFocus();

        // TODO from here
        {
            MenuBarBuilder builder = new MenuBarBuilder();
            builder.addMenu("File");
            builder.add("New", () -> new MainWindow(workbench, new Design(editor.getDesign().getTechnology(), 200, 200)).setVisible(true));
            builder.add("Load", this::showLoadDialog);
            builder.add("Save", this::showSaveDialog);
            builder.addSeparator();
            builder.add("Quit", () -> System.exit(0));
            builder.addMenu("Edit");
            builder.add("Undo", editor::undo);
            builder.add("Redo", editor::redo);
            builder.addMenu("View");
            builder.add("Up", planeUiState::moveVisibilityUp);
            builder.add("Down", planeUiState::moveVisibilityDown);
            builder.addMenu("Test");
            builder.add("Corner Stitching Extractor", () -> new CornerStitchingExtrator.Test().extract(editor.getDesign()));
            builder.add("Connectivity Extractor", () -> new ConnectivityExtractor.Test().extract(editor.getDesign()));
            builder.add("Magic Export", () -> MagicExportDialog.showExportDialog(this, editor.getDesign()));
            builder.add("Enlarge", () -> performOperation(new OutOfPlaceDesignOperation() {
                @Override
                protected Design createNewDesign(Design oldDesign) throws UserVisibleMessageException {
                    return new Enlarger(oldDesign).enlarge();
                }
            }));
            builder.add("Autocrop", () -> performOperation(new OutOfPlaceDesignOperation() {
                @Override
                protected Design createNewDesign(Design oldDesign) throws UserVisibleMessageException {
                    return new Autocropper(oldDesign).autocrop();
                }
            }));
            builder.addMenu("Help");
            builder.addExternalLink("Contents", "https://github.com/MartinGeisse/chipdraw/blob/master/doc/index.md"); // TODO link to commit for this version
            builder.add("About", () -> JOptionPane.showMessageDialog(MainWindow.this, About.ABOUT_TEXT));
            setJMenuBar(builder.build());
        }
        // TODO till here

        bottomLine = new JLabel(" ");
        add(bottomLine, BorderLayout.PAGE_END);

        loadAndSaveDialogs = new LoadAndSaveDialogs(workbench.getTechnologyRepository());
        editor.restart(_design);
    }

    public Design getCurrentDesign() {
        return editor.getDesign();
    }

    public int getCurrentPixelSize() {
        return pixelSize;
    }

    private void updateMainPanelSize() {
        Dimension size = new Dimension(editor.getDesign().getWidth() * pixelSize, editor.getDesign().getHeight() * pixelSize);
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
        } catch (NoSuchCellLibraryException exception) {
            JOptionPane.showMessageDialog(this, exception.getMessage());
            return;
        }
        if (design != null) {
            editor.restart(design);
        }
    }

    public void performOperation(DesignOperation operation) {
        performOperation(operation, false);
    }

    public void performOperation(DesignOperation operation, boolean merge) {
        try {
            editor.performOperation(operation, merge);
        } catch (UserVisibleMessageException e) {
            JOptionPane.showMessageDialog(this, e.getMessage());
        }
    }

    // TODO from here
    @Override
    public void onRestart() {
        planeUiState.setTechnology(editor.getDesign().getTechnology());
        planeUiState.onClick(0, 0);
        drawing = false;
        erasing = false;
        pixelSize = 16;
        updateMainPanelSize();
    }

    @Override
    public void onDesignObjectReplaced() {
        planeUiState.setTechnology(editor.getDesign().getTechnology());
        updateMainPanelSize();
    }
    // TODO till here

    @Override
    public void onDesignModified() {
        mainPanel.repaint();
    }

    @Override
    public void consumeDrcResult(ImmutableList<Violation> violations) {

        // update DRC button color
        if (violations == null) {
            drcButton.setForeground(new Color(128, 128, 0));
        } else if (violations.isEmpty()) {
            drcButton.setForeground(new Color(0, 128, 0));
        } else {
            drcButton.setForeground(new Color(128, 0, 0));
        }

        // Update map of positioned violations so we can show them on mouse-over. Leave old messages while DRC is running.
        if (violations != null) {
            positionedDrcViolations = new HashMap<>();
            for (Violation violation : violations) {
                if (violation instanceof PositionedViolation) {
                    PositionedViolation positionedViolation = (PositionedViolation)violation;
                    positionedDrcViolations.put(positionedViolation.getPoint(), positionedViolation.getMessage());
                }
            }
            updateBottomLine();
            repaint();
        }

    }

    private void updateBottomLine() {
        String message = positionedDrcViolations.get(new Point(mousePixelX, mousePixelY));
        if (message == null) {
            message = "";
        } else {
            message = " / " + message;
        }
        bottomLine.setText("  " + mousePixelX + ", " + mousePixelY + message);
    }

    private void run(UiRunnable runnable) {
        try {
            runnable.run();
        } catch (UserVisibleMessageException e) {
            JOptionPane.showMessageDialog(this, e.getMessage());
        }
    }

}
