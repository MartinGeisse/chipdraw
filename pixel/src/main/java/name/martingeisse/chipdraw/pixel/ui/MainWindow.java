package name.martingeisse.chipdraw.pixel.ui;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import name.martingeisse.chipdraw.pixel.About;
import name.martingeisse.chipdraw.pixel.Editor;
import name.martingeisse.chipdraw.pixel.Workbench;
import name.martingeisse.chipdraw.pixel.design.*;
import name.martingeisse.chipdraw.pixel.drc.PositionedViolation;
import name.martingeisse.chipdraw.pixel.drc.Violation;
import name.martingeisse.chipdraw.pixel.global_tools.Autocropper;
import name.martingeisse.chipdraw.pixel.global_tools.Enlarger;
import name.martingeisse.chipdraw.pixel.scmos.magic.MagicExportDialog;
import name.martingeisse.chipdraw.pixel.global_tools.stdcell.StandardCellExtender;
import name.martingeisse.chipdraw.pixel.global_tools.stdcell.StandardCellPruner;
import name.martingeisse.chipdraw.pixel.global_tools.stdcell.StandardCellTemplateGeneratorBase;
import name.martingeisse.chipdraw.pixel.icons.Icons;
import name.martingeisse.chipdraw.pixel.scmos.ScmosConcept;
import name.martingeisse.chipdraw.pixel.operation.DesignOperation;
import name.martingeisse.chipdraw.pixel.operation.OutOfPlaceDesignOperation;
import name.martingeisse.chipdraw.pixel.operation.mouse.DrawTool;
import name.martingeisse.chipdraw.pixel.operation.mouse.MouseTool;
import name.martingeisse.chipdraw.pixel.operation.mouse.RectangleTool;
import name.martingeisse.chipdraw.pixel.operation.mouse.RowTool;
import name.martingeisse.chipdraw.pixel.operation.scmos.ScmosContactTool;
import name.martingeisse.chipdraw.pixel.operation.scmos.ScmosContactType;
import name.martingeisse.chipdraw.pixel.operation.scmos.ScmosTransistorTool;
import name.martingeisse.chipdraw.pixel.operation.scmos.meta_transistor.TransistorToolFactory;
import name.martingeisse.chipdraw.pixel.ui.util.*;
import name.martingeisse.chipdraw.pixel.util.Point;
import name.martingeisse.chipdraw.pixel.util.UserVisibleMessageException;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

public class MainWindow extends JFrame implements Editor.Ui {

    public static final int MIN_PIXEL_SIZE = 2;
    public static final int MAX_PIXEL_SIZE = 32;

    private final Workbench workbench;
    private final JPanel sideBar;
    private final JPanel mainPanel;
    private final LoadAndSaveDialogs loadAndSaveDialogs;
    private final JButton drcButton;
    private final MaterialUiState materialUiState;
    private final Editor editor;
    private final JLabel bottomLine;

    private int zoom;
    private MouseTool mouseTool;

    private int mousePixelX, mousePixelY;
    private Map<name.martingeisse.chipdraw.pixel.util.Point, String> positionedDrcViolations = ImmutableMap.of();

    private boolean isShiftDown;

    public MainWindow(Workbench _workbench, Design _design) {
        super("Chipdraw");
        this.workbench = _workbench;
        this.materialUiState = new MaterialUiState(_design.getTechnology());
        this.editor = new Editor(this);

        setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
        setSize(800, 600);
        setResizable(true);

        addWindowListener(new WindowAdapter() {

            @Override
            public void windowClosing(WindowEvent e) {
                // this is far from perfect, but at least it helps when you mis-click the close button, which I did...
                int answer = JOptionPane.showConfirmDialog(MainWindow.this, "Really close?", "Close Window", JOptionPane.YES_NO_OPTION);
                if (answer == JOptionPane.YES_OPTION) {
                    dispose();
                }
            }

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
                    materialUiState.onClick(rowIndex, columnIndex);
                    table.repaint();
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
		// note: we use several tool panels because BoxLayout is too stupid to break lines
		{
            ToolbarBuilder builder = new ToolbarBuilder();
            builder.add("cursor_1x1.png", event -> mouseTool = new DrawTool(materialUiState::getEditingMaterial, 1));
            builder.add("cursor_2x2.png", event -> mouseTool = new DrawTool(materialUiState::getEditingMaterial, 2));
            builder.add("cursor_3x3.png", event -> mouseTool = new DrawTool(materialUiState::getEditingMaterial, 3));
			sideBar.add(builder.build());
		}
		{
            ToolbarBuilder builder = new ToolbarBuilder();
            builder.add("row.png", event -> mouseTool = new RowTool());
            builder.add("rectangle.png", event -> mouseTool = new RectangleTool(materialUiState::getEditingMaterial));
            sideBar.add(builder.build());
		}
        {
            ToolbarBuilder builder = new ToolbarBuilder();
            builder.add("contact_n.png", event -> mouseTool = new ScmosContactTool(ScmosContactType.NDIFF));
            builder.add("contact_p.png", event -> mouseTool = new ScmosContactTool(ScmosContactType.PDIFF));
            builder.add("contact_g.png", event -> mouseTool = new ScmosContactTool(ScmosContactType.POLY));
            sideBar.add(builder.build());
        }
        {
            ToolbarBuilder builder = new ToolbarBuilder();
            builder.add("transistor_n.png", event -> mouseTool = new ScmosTransistorTool(ConceptSchemas.MATERIAL_NDIFF));
            builder.add("transistor_p.png", event -> mouseTool = new ScmosTransistorTool(ConceptSchemas.MATERIAL_PDIFF));
            builder.add("transistor_meta.png", event -> invokeMetaTransistorTool());
            sideBar.add(builder.build());
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

        mainPanel = new JPanel() {

            private final DesignPainter designPainter = new DesignPainter() {

                private Material getPixel(PlaneSchema planeSchema, int x, int y) {
                    Plane plane = editor.getDesign().getPlane(planeSchema);
                    if (materialUiState.isPlaneVisible(plane.getSchema())) {
                        return plane.getPixel(x, y);
                    } else {
                        return Material.NONE;
                    }
                }

                @Override
                protected void drawPixel(Graphics2D g, int pixelX, int pixelY, int screenX, int screenY, int screenSize) {

                    // read pixel per plane
                    Material wellPlane = getPixel(ConceptSchemas.PLANE_WELL, pixelX, pixelY);
                    Material diffPlane = getPixel(ConceptSchemas.PLANE_DIFF, pixelX, pixelY);
                    Material polyPlane = getPixel(ConceptSchemas.PLANE_POLY, pixelX, pixelY);
                    Material metal1Plane = getPixel(ConceptSchemas.PLANE_METAL1, pixelX, pixelY);
                    Material metal2Plane = getPixel(ConceptSchemas.PLANE_METAL2, pixelX, pixelY);
                    Material padPlane = getPixel(ConceptSchemas.PLANE_PAD, pixelX, pixelY);

                    if (wellPlane != Material.NONE) {
                        g.setPaint(wellPlane == ConceptSchemas.MATERIAL_NWELL ? getHatching(0x0000ff, 0) : getHatching(0xff0000, 0));
                        g.fillRect(screenX, screenY, screenSize, screenSize);
                    }
                    if (diffPlane != Material.NONE) {
                        g.setPaint(diffPlane == ConceptSchemas.MATERIAL_NDIFF ? new Color(0x0000ff) : new Color(0xff0000));
                        g.fillRect(screenX, screenY, screenSize, screenSize);
                    }
                    if (polyPlane != Material.NONE) {
                        if (materialUiState.isPlaneVisible(ConceptSchemas.PLANE_METAL1)) {
                            g.setPaint(new Color(0, 128, 0));
                        } else {
                            g.setPaint(getHatching(0x008000, 2, true));
                        }
                        g.fillRect(screenX, screenY, screenSize, screenSize);
                    }
                    if (metal1Plane != Material.NONE) {
                        if (metal1Plane == ConceptSchemas.MATERIAL_CONTACT) {
                            g.setPaint(Color.GRAY);
                        } else if (materialUiState.isPlaneVisible(ConceptSchemas.PLANE_METAL2)) {
                            g.setPaint(Color.LIGHT_GRAY);
                        } else {
                            g.setPaint(getHatching(0xc0c0c0, 4));
                        }
                        g.fillRect(screenX, screenY, screenSize, screenSize);
                    }
                    if (metal2Plane != Material.NONE) {
                        if (metal2Plane == ConceptSchemas.MATERIAL_VIA12) {
                            g.setPaint(new Color(0x008080));
                        } else if (materialUiState.isPlaneVisible(ConceptSchemas.PLANE_PAD)) {
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

                    if (positionedDrcViolations.get(new name.martingeisse.chipdraw.pixel.util.Point(pixelX, pixelY)) != null) {
                        int centerX = screenX + screenSize / 2 - 2;
                        int centerY = screenY + screenSize / 2 - 2;
                        g.setColor(Color.RED);
                        g.fillOval(centerX, centerY, 4, 4);
                        g.setColor(Color.BLACK);
                        g.drawOval(centerX, centerY, 4, 4);
                    }

                }

            };

            @Override
            protected void paintComponent(Graphics _g) {
                Graphics2D g = (Graphics2D) _g;
                Design design = getCurrentDesign();

                // get width / height / pixel size
                int zoom = getZoom();
                int panelWidth = getWidth();
                int panelHeight = getHeight();
                int designDisplayWidth = design.getWidth() * zoom;
                int designDisplayHeight = design.getHeight() * zoom;

                // draw background outside the design area if this panel is larger than that
                if (designDisplayWidth < panelWidth || designDisplayHeight < panelHeight) {
                    g.setColor(Color.LIGHT_GRAY);
                    g.fillRect(0, 0, panelWidth, panelHeight);
                }

                // draw design
                designPainter.paintDesign(g, design, zoom);

                // draw mouse tool
                if (mouseTool != null) {
                    mouseTool.draw(g, zoom, isShiftDown);
                }

            }

        };
        MouseAdapter mouseAdapter = new MouseAdapter() {

            @Override
            public void mousePressed(MouseEvent event) {
                MouseTool.MouseButton button;
                if (event.getButton() == MouseEvent.BUTTON1) {
                    button = MouseTool.MouseButton.LEFT;
                } else if (event.getButton() == MouseEvent.BUTTON3) {
                    button = MouseTool.MouseButton.RIGHT;
                } else {
                    button = MouseTool.MouseButton.MIDDLE;
                }
                boolean shift = (event.getModifiersEx() & KeyEvent.SHIFT_DOWN_MASK) != 0;
                handle(event, () -> mouseTool.onMousePressed(getCurrentDesign(), mousePixelX, mousePixelY, button, shift));
            }

            @Override
            public void mouseReleased(MouseEvent event) {
                handle(event, () -> mouseTool.onMouseReleased(getCurrentDesign()));
            }

            @Override
            public void mouseMoved(MouseEvent event) {
                int oldMousePixelX = mousePixelX;
                int oldMousePixelY = mousePixelY;
                handle(event, () -> {
                    if (mousePixelX != oldMousePixelX || mousePixelY != oldMousePixelY) {
                        return mouseTool.onMouseMoved(getCurrentDesign(), mousePixelX, mousePixelY);
                    } else {
                        return null;
                    }
                });
            }

            @Override
            public void mouseDragged(MouseEvent e) {
                mouseMoved(e);
            }

            private void handle(MouseEvent event, Supplier<MouseTool.Result> body) {
                mousePixelX = event.getX() / zoom;
                mousePixelY = event.getY() / zoom;
                if (mouseTool != null) {
                    MouseTool.Result result = body.get();
                    if (result != null) {
                        performOperation(result.getOperation(), result.isMerge());
                    }
                }
                updateBottomLine();
                repaint();
            }

        };
        mainPanel.addMouseListener(mouseAdapter);
        mainPanel.addMouseMotionListener(mouseAdapter);
        mainPanel.addKeyListener(new KeyAdapter() {

            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_SHIFT) {
                    isShiftDown = true;
                    MainWindow.this.repaint();
                }
            }

            @Override
            public void keyReleased(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_SHIFT) {
                    isShiftDown = false;
                    MainWindow.this.repaint();
                }
            }

        });
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
                run(materialUiState::moveVisibilityUp);
                repaint();
            }
        });
        mainPanel.getActionMap().put(ActionName.VISIBILITY_DOWN, new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                run(materialUiState::moveVisibilityDown);
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
                        materialUiState.onClick(event.getKeyChar() - '1', 0);
                        break;

                    case '0':
                        materialUiState.onClick(9, 0);
                        break;

                    case '+':
                        if (zoom < MAX_PIXEL_SIZE) {
                            zoom *= 2;
                            updateMainPanelSize();
                        }
                        break;

                    case '-':
                        if (zoom > MIN_PIXEL_SIZE) {
                            zoom /= 2;
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

                    case 't':
                        invokeMetaTransistorTool();
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
            builder.add("Load...", this::showLoadDialog);
            builder.add("Save As...", this::showSaveDialog);
            builder.add("Magic Export As...", () -> MagicExportDialog.showExportDialog(this, editor.getDesign()));
            builder.addSeparator();
            builder.add("Quit", () -> System.exit(0));
            builder.addMenu("Edit");
            builder.add("Undo", editor::undo);
            builder.add("Redo", editor::redo);
            builder.addMenu("View");
            builder.add("Up", materialUiState::moveVisibilityUp);
            builder.add("Down", materialUiState::moveVisibilityDown);
            builder.addMenu("Tools");
            builder.add("Enlarge", () -> performOperation(new OutOfPlaceDesignOperation() {
                @Override
                protected Design createNewDesign(Design oldDesign) throws UserVisibleMessageException {
                    return new Enlarger(oldDesign, 10).enlarge();
                }
            }));
            builder.add("X-Enlarge", () -> performOperation(new OutOfPlaceDesignOperation() {
                @Override
                protected Design createNewDesign(Design oldDesign) throws UserVisibleMessageException {
                    return new Enlarger(oldDesign, 10, true, false).enlarge();
                }
            }));
            builder.add("Autocrop", () -> performOperation(new OutOfPlaceDesignOperation() {
                @Override
                protected Design createNewDesign(Design oldDesign) throws UserVisibleMessageException {
                    return new Autocropper(oldDesign).autocrop();
                }
            }));
            builder.add("X-Autocrop", () -> performOperation(new OutOfPlaceDesignOperation() {
                @Override
                protected Design createNewDesign(Design oldDesign) throws UserVisibleMessageException {
                    return new Autocropper(oldDesign, true, false).autocrop();
                }
            }));
            builder.add("New StdCell", () -> new MainWindow(workbench,
                    new StandardCellTemplateGeneratorBase().generate(ScmosConcept.TECHNOLOGY)).setVisible(true));
            builder.add("Prune StdCell", () -> performOperation(new OutOfPlaceDesignOperation() {
                @Override
                protected Design createNewDesign(Design oldDesign) throws UserVisibleMessageException {
                    return new StandardCellPruner().prune(editor.getDesign());
                }
            }));
            builder.add("Repair Rails", () -> performOperation(new OutOfPlaceDesignOperation() {
                @Override
                protected Design createNewDesign(Design oldDesign) {
                    return new StandardCellExtender(0, false).extend(oldDesign);
                }
            }));
            builder.add("Extend Cell (7)", () -> performOperation(new OutOfPlaceDesignOperation() {
                @Override
                protected Design createNewDesign(Design oldDesign) {
                    return new StandardCellExtender(7, false).extend(oldDesign);
                }
            }));
            builder.add("Extend Cell (14)", () -> performOperation(new OutOfPlaceDesignOperation() {
                @Override
                protected Design createNewDesign(Design oldDesign) {
                    return new StandardCellExtender(14, false).extend(oldDesign);
                }
            }));
            builder.add("Extend Cell Left (7)", () -> performOperation(new OutOfPlaceDesignOperation() {
                @Override
                protected Design createNewDesign(Design oldDesign) {
                    return new StandardCellExtender(7, true).extend(oldDesign);
                }
            }));
            builder.addMenu("Help");
            builder.addExternalLink("Contents", "https://github.com/MartinGeisse/chipdraw/blob/master/doc/index.md"); // TODO link to commit for this version
            builder.add("About", () -> JOptionPane.showMessageDialog(MainWindow.this, About.ABOUT_TEXT));
            setJMenuBar(builder.build());
        }

        bottomLine = new JLabel(" ");
        add(bottomLine, BorderLayout.PAGE_END);

        loadAndSaveDialogs = new LoadAndSaveDialogs(workbench.getTechnologyRepository());
        editor.restart(_design);
    }

    public Design getCurrentDesign() {
        return editor.getDesign();
    }

    public int getZoom() {
        return zoom;
    }

    private void updateMainPanelSize() {
        Dimension size = new Dimension(editor.getDesign().getWidth() * zoom, editor.getDesign().getHeight() * zoom);
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
        } catch (NoSuchTechnologyException|UserVisibleMessageException exception) {
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

    @Override
    public void onRestart() {
        materialUiState.setTechnology(editor.getDesign().getTechnology());
        materialUiState.onClick(0, 0);
        mouseTool = new DrawTool(materialUiState::getEditingMaterial, 1);
        zoom = 16;
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
        Design design = getCurrentDesign();
        String message = positionedDrcViolations.get(new Point(mousePixelX, mousePixelY));
        if (message == null) {
            message = "";
        } else {
            message = " / " + message;
        }
        bottomLine.setText("  " + design.getWidth() + " x " + design.getHeight() + " | " +
            mousePixelX + ", " + mousePixelY + message);
    }

    private void run(UiRunnable runnable) {
        try {
            runnable.run();
        } catch (UserVisibleMessageException e) {
            JOptionPane.showMessageDialog(this, e.getMessage());
        }
    }

    private void invokeMetaTransistorTool() {
        run(() -> {
            String formula = JOptionPane.showInputDialog("Please enter the transistor formula");
            if (formula != null) {
                mouseTool = TransistorToolFactory.create(formula);
            }
        });
    }
}
