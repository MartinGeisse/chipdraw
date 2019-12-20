package name.martingeisse.chipdraw.pnr.ui;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import name.martingeisse.chipdraw.pnr.About;
import name.martingeisse.chipdraw.pnr.Editor;
import name.martingeisse.chipdraw.pnr.Workbench;
import name.martingeisse.chipdraw.pnr.cell.*;
import name.martingeisse.chipdraw.pnr.design.CellInstance;
import name.martingeisse.chipdraw.pnr.design.Design;
import name.martingeisse.chipdraw.pnr.design.RoutingPlane;
import name.martingeisse.chipdraw.pnr.design.RoutingTile;
import name.martingeisse.chipdraw.pnr.drc.PositionedViolation;
import name.martingeisse.chipdraw.pnr.drc.Violation;
import name.martingeisse.chipdraw.pnr.icons.Icons;
import name.martingeisse.chipdraw.pnr.operation.DesignOperation;
import name.martingeisse.chipdraw.pnr.ui.util.MenuBarBuilder;
import name.martingeisse.chipdraw.pnr.ui.util.SingleIconBooleanCellRenderer;
import name.martingeisse.chipdraw.pnr.ui.util.UiRunnable;
import name.martingeisse.chipdraw.pnr.util.Point;
import name.martingeisse.chipdraw.pnr.util.UserVisibleMessageException;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.HashMap;
import java.util.Map;

public class MainWindow extends JFrame implements Editor.Ui {

    public static final int PLACING_VIA_MINIMUM_MOUSE_DISTANCE = 10;

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
    private final JList<String> cellTemplateList;

    private boolean drawing, erasing, firstPixelOfStroke, placingVia, removingVia;
    private int placingViaOriginalScreenY, placingViaOriginalPixelX, placingViaOriginalPixelY;
    private CellInstance pickedUpCellInstance;
    private boolean pickedUpCellInstanceCollides;
    private int pixelSize;

    private int mousePixelX, mousePixelY;
    private int previousMousePixelX, previousMousePixelY;
    private Map<name.martingeisse.chipdraw.pnr.util.Point, String> positionedDrcViolations = ImmutableMap.of();

    public MainWindow(Workbench _workbench, Design _design) {
        super("Chipdraw");
        this.workbench = _workbench;
        this.planeUiState = new PlaneUiState(1); // dummy value, will be replaced later
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
                    MainWindow.this.repaint();
                }
            });
            table.setRowSelectionAllowed(false);
            table.setFocusable(false);
            sideBar.add(table);
            planeUiState.getSidebarTableModel().addTableModelListener(event -> {
                MainWindow.this.repaint();
            });
        }
        sideBar.add(Box.createVerticalStrut(30));
        {
            cellTemplateList = new JList<>();
            cellTemplateList.setFocusable(false);
            cellTemplateList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
            cellTemplateList.setSelectedIndex(0);
            JScrollPane scrollPane = new JScrollPane(cellTemplateList);
            scrollPane.setPreferredSize(new Dimension(0, 1_000_000));
            sideBar.add(scrollPane);
        }
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

        mainPanel = new DesignTilePanel(this) {

            private void drawPlane(Graphics2D g, int planeIndex, int pixelX, int pixelY, int screenX, int screenY, int screenSize,
                                   Color color, Color viaDownColor, Color viaUpColor) {
                RoutingPlane plane = editor.getDesign().getRoutingPlanes().get(planeIndex);
                if (!planeUiState.isPlaneVisible(planeIndex)) {
                    return;
                }
                RoutingTile tile = plane.getTile(pixelX, pixelY);
                g.setPaint(color);

                int centerX = screenX + screenSize / 2;
                int centerY = screenY + screenSize / 2;
                if (tile.isEastConnected()) {
                    g.drawLine(centerX, centerY, centerX + screenSize, centerY);
                }
                if (tile.isSouthConnected()) {
                    g.drawLine(centerX, centerY, centerX, centerY + screenSize);
                }

                int viaX = screenX + screenSize / 4;
                int viaY = screenY + screenSize / 4;
                int viaSize = screenSize / 2;
                if (tile.isDownConnected() && viaDownColor != null) {
                    g.setPaint(new GradientPaint(viaX, viaY, color, viaX, viaY + viaSize, viaDownColor));
                    g.fillRect(viaX, viaY, viaSize, viaSize);
                }
                RoutingPlane planeAbove = plane.getRoutingPlaneAbove();
                if (planeAbove != null && planeAbove.getTile(pixelX, pixelY).isDownConnected() && viaUpColor != null) {
                    g.setPaint(new GradientPaint(viaX, viaY, viaUpColor, viaX, viaY + viaSize, color));
                    g.fillRect(viaX, viaY, viaSize, viaSize);
                }
            }

            @Override
            protected void drawTile(Graphics2D g, int pixelX, int pixelY, int screenX, int screenY, int screenSize) {
                drawPlane(g, 0, pixelX, pixelY, screenX, screenY, screenSize, Color.RED, Color.GREEN, null);
                drawPlane(g, 1, pixelX, pixelY, screenX, screenY, screenSize, Color.GREEN, Color.BLUE, Color.RED);
                drawPlane(g, 2, pixelX, pixelY, screenX, screenY, screenSize, Color.BLUE, Color.LIGHT_GRAY, Color.GREEN);
                if (positionedDrcViolations.get(new name.martingeisse.chipdraw.pnr.util.Point(pixelX, pixelY)) != null) {
                    int centerX = screenX + screenSize / 2 - 2;
                    int centerY = screenY + screenSize / 2 - 2;
                    g.setColor(Color.RED);
                    g.fillOval(centerX, centerY, 4, 4);
                    g.setColor(Color.BLACK);
                    g.drawOval(centerX, centerY, 4, 4);
                }
            }

            @Override
            protected void drawCells(Graphics2D g, int pixelSize) {
                if (planeUiState.isPlaneVisible(getCurrentDesign().getTotalPlaneCount() - 1)) {
                    for (CellInstance instance : getCurrentDesign().getCellPlane().getCellInstances()) {
                        drawCell(g, pixelSize, instance, false);
                    }
                    if (pickedUpCellInstance != null) {
                        drawCell(g, pixelSize, pickedUpCellInstance, true);
                    }
                }
            }

            private void drawCell(Graphics2D g, int pixelSize, CellInstance cellInstance, boolean isPickedUp) {

                // determine cell extents on screen
                CellTemplate template = cellInstance.getTemplate();
                int cellScreenX = cellInstance.getX() * pixelSize;
                int cellScreenY = cellInstance.getY() * pixelSize;
                int cellScreenWidth = template.getWidth() * pixelSize;
                int cellScreenHeight = template.getHeight() * pixelSize;

                // determine symbol extends on screen
                int symbolScreenSize, symbolScreenX, symbolScreenY;
                if (cellScreenWidth < cellScreenHeight) {
                    symbolScreenSize = cellScreenWidth;
                    symbolScreenX = cellScreenX;
                    symbolScreenY = cellScreenY + (cellScreenHeight - symbolScreenSize) / 2;
                } else {
                    symbolScreenSize = cellScreenHeight;
                    symbolScreenX = cellScreenX + (cellScreenWidth - symbolScreenSize) / 2;
                    symbolScreenY = cellScreenY;
                }

                // draw background
                if (isPickedUp) {
                    g.setColor(pickedUpCellInstanceCollides ? Color.RED : Color.GREEN);
                } else {
                    g.setColor(Color.LIGHT_GRAY);
                    g.fillRect(cellScreenX, cellScreenY, cellScreenWidth, cellScreenHeight);
                    g.setColor(Color.DARK_GRAY);
                }
                g.drawRect(cellScreenX, cellScreenY, cellScreenWidth - 1, cellScreenHeight - 1);

                // draw symbol
                template.getSymbol().draw(new CellSymbol.DrawContext() {

                    /*
                        This draws each symbol in a 100x100 coordinate system, then scales it uniformly to fit the cell.
                        The downside is that it is near impossible to have the symbol match the location of the ports.
                        For now, we just accept that and we draw symbols that don't try to match the ports.
                     */

                    private int transformX(int x) {
                        return symbolScreenX + x * symbolScreenSize / 100;
                    }

                    private int transformY(int y) {
                        return symbolScreenY + y * symbolScreenSize / 100;
                    }

                    private int transformDelta(int delta) {
                        return delta * symbolScreenSize / 100;
                    }

                    @Override
                    public void drawLine(int x1, int y1, int x2, int y2) {
                        g.drawLine(transformX(x1), transformY(y1), transformX(x2), transformY(y2));
                    }

                    @Override
                    public void drawCircle(int x, int y, int radius) {
                        int size = transformDelta(2 * radius);
                        g.drawOval(transformX(x - radius), transformY(y - radius), size, size);
                    }

                });

                // draw ports
                for (Port port : template.getPorts()) {
                    g.fillRect(
                            // draw the port slightly towards the bottom left from the center
                            cellScreenX + port.getX() * pixelSize + pixelSize / 4,
                            cellScreenY + port.getY() * pixelSize + pixelSize / 2,
                            pixelSize / 4,
                            pixelSize / 4);
                }

            }

        };
        MouseAdapter mouseAdapter = new MouseAdapter() {

            @Override
            public void mousePressed(MouseEvent e) {
                if (planeUiState.isEditingCellPlane()) {

                    if (e.getButton() == MouseEvent.BUTTON1) {
                        // TODO wrap in operation
                        if (pickedUpCellInstance == null) {
                            pickedUpCellInstance = getCurrentDesign().getCellPlane().findAndRemoveInstanceForPosition(mousePixelX, mousePixelY);
                            if (pickedUpCellInstance != null) {
                                updatePickedUpCellInstancePosition();
                            }
                        } else {
                            if (!pickedUpCellInstanceCollides) {
                                getCurrentDesign().getCellPlane().add(pickedUpCellInstance);
                                pickedUpCellInstance = null;
                            }
                        }
                    } else {
                        pickedUpCellInstance = null;
                    }
                    repaint();
                } else {
                    drawing = (e.getButton() == MouseEvent.BUTTON1 && (e.getModifiersEx() & InputEvent.SHIFT_DOWN_MASK) == 0);
                    erasing = (e.getButton() == MouseEvent.BUTTON3 && (e.getModifiersEx() & InputEvent.SHIFT_DOWN_MASK) == 0);
                    placingVia = (e.getButton() == MouseEvent.BUTTON1 && (e.getModifiersEx() & InputEvent.SHIFT_DOWN_MASK) != 0);
                    removingVia = (e.getButton() == MouseEvent.BUTTON3 && (e.getModifiersEx() & InputEvent.SHIFT_DOWN_MASK) != 0);
                    firstPixelOfStroke = true;
                    if (placingVia || removingVia) {
                        placingViaOriginalScreenY = e.getY();
                        placingViaOriginalPixelX = mousePixelX;
                        placingViaOriginalPixelY = mousePixelY;
                    }
                    mouseMoved(e);
                }
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                if (!planeUiState.isEditingCellPlane()) {
                    drawing = erasing = placingVia = removingVia = false;
                }
            }

            @Override
            public void mouseMoved(MouseEvent event) {
                previousMousePixelX = mousePixelX;
                previousMousePixelY = mousePixelY;
                mousePixelX = event.getX() / pixelSize;
                mousePixelY = event.getY() / pixelSize;
                if (planeUiState.isEditingCellPlane()) {
                    updatePickedUpCellInstancePosition();
                } else {
                    if ((drawing || erasing) && (mousePixelX != previousMousePixelX || mousePixelY != previousMousePixelY)) {
                        RoutingPlane plane = getCurrentDesign().getRoutingPlanes().get(planeUiState.getEditingPlane());
                        if (mousePixelX == previousMousePixelX - 1 && mousePixelY == previousMousePixelY) {
                            plane.setEast(mousePixelX, mousePixelY, drawing);
                            firstPixelOfStroke = false;
                        }
                        if (mousePixelX == previousMousePixelX + 1 && mousePixelY == previousMousePixelY) {
                            plane.setEast(previousMousePixelX, mousePixelY, drawing);
                            firstPixelOfStroke = false;
                        }
                        if (mousePixelX == previousMousePixelX && mousePixelY == previousMousePixelY - 1) {
                            plane.setSouth(mousePixelX, mousePixelY, drawing);
                            firstPixelOfStroke = false;
                        }
                        if (mousePixelX == previousMousePixelX && mousePixelY == previousMousePixelY + 1) {
                            plane.setSouth(mousePixelX, previousMousePixelY, drawing);
                            firstPixelOfStroke = false;
                        }
                        repaint();

                        /*
                        TODO wrap in operation
                        Material material = planeUiState.getEditingMaterial();
                        if (MainWindow.this.drawing) {
                            performOperation(new DrawPoints(mousePixelX, mousePixelY, 1, 1, material), !firstPixelOfStroke);
                        } else {
                            performOperation(new ErasePoints(mousePixelX, mousePixelY, 1, 1, material.getPlaneSchema()), !firstPixelOfStroke);
                        }
                         */
                    }
                    if (placingVia || removingVia) {
                        RoutingPlane plane = getCurrentDesign().getRoutingPlanes().get(planeUiState.getEditingPlane());
                        if (event.getY() < placingViaOriginalScreenY - PLACING_VIA_MINIMUM_MOUSE_DISTANCE) {
                            RoutingPlane planeAbove = plane.getRoutingPlaneAbove();
                            if (planeAbove != null) {
                                planeAbove.setDown(placingViaOriginalPixelX, placingViaOriginalPixelY, placingVia);
                                repaint();
                            }
                        } else if (event.getY() > placingViaOriginalScreenY + PLACING_VIA_MINIMUM_MOUSE_DISTANCE) {
                            plane.setDown(placingViaOriginalPixelX, placingViaOriginalPixelY, placingVia);
                            repaint();
                        }
                    }
                }
                updateBottomLine();
            }

            @Override
            public void mouseDragged(MouseEvent e) {
                mouseMoved(e);
            }

        };
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

                    case ' ':
                        if (planeUiState.isEditingCellPlane() && pickedUpCellInstance == null) {
                            int index = cellTemplateList.getMinSelectionIndex();
                            if (index != -1) {
                                String id = cellTemplateList.getModel().getElementAt(index);
                                try {
                                    pickedUpCellInstance = new CellInstance(getCurrentDesign().getCellLibrary().getCellTemplate(id), -1, -1);
                                    updatePickedUpCellInstancePosition();
                                } catch (NoSuchCellException e) {
                                    JOptionPane.showMessageDialog(MainWindow.this, e.getMessage());
                                }
                            }
                        }
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
            builder.add("New", () -> new MainWindow(workbench, new Design(editor.getDesign().getCellLibrary(), 200, 200)).setVisible(true));
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
            // TODO re-add enlarge and autocrop
//            builder.add("Enlarge", () -> performOperation(new OutOfPlaceDesignOperation() {
//                @Override
//                protected Design createNewDesign(Design oldDesign) throws UserVisibleMessageException {
//                    return new Enlarger(oldDesign).enlarge();
//                }
//            }));
//            builder.add("Autocrop", () -> performOperation(new OutOfPlaceDesignOperation() {
//                @Override
//                protected Design createNewDesign(Design oldDesign) throws UserVisibleMessageException {
//                    return new Autocropper(oldDesign).autocrop();
//                }
//            }));
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

    public int getCurrentPixelSize() {
        return pixelSize;
    }

    private void updatePickedUpCellInstancePosition() {
        if (pickedUpCellInstance != null) {
            CellTemplate template = pickedUpCellInstance.getTemplate();
            int x = mousePixelX - template.getWidth() / 2;
            int y = mousePixelY - template.getHeight() / 2;
            if (x != pickedUpCellInstance.getX() || y != pickedUpCellInstance.getY()) {
                pickedUpCellInstance = new CellInstance(template, x, y);
                pickedUpCellInstanceCollides = getCurrentDesign().getCellPlane().collides(pickedUpCellInstance);
                repaint();
            }
        }
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
        } catch (NoSuchCellLibraryException|NoSuchCellException exception) {
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
        planeUiState.setTotalPlaneCount(editor.getDesign().getTotalPlaneCount());
        planeUiState.onClick(0, 0);
        cellTemplateList.setListData(editor.getDesign().getCellLibrary().getAllIds().toArray(new String[0]));
        cellTemplateList.setSelectedIndex(0);
        drawing = false;
        erasing = false;
        placingVia = false;
        removingVia = false;
        pixelSize = 16;
        updateMainPanelSize();
    }

    @Override
    public void onDesignObjectReplaced() {
        planeUiState.setTotalPlaneCount(editor.getDesign().getTotalPlaneCount());
        planeUiState.onClick(0, 0);
        cellTemplateList.setListData(editor.getDesign().getCellLibrary().getAllIds().toArray(new String[0]));
        cellTemplateList.setSelectedIndex(0);
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
