package name.martingeisse.chipdraw;

import name.martingeisse.chipdraw.drc.DrcAgent;
import name.martingeisse.chipdraw.icons.Icons;
import name.martingeisse.chipdraw.technology.NoSuchTechnologyException;
import name.martingeisse.chipdraw.technology.Technology;
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

    private Design design;
    private LayerUiState layerUiState;
    private boolean drawing;
    private boolean erasing;
    private int cellSize;

    public MainWindow(Workbench workbench, Design design) throws NoSuchTechnologyException {
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
                    if (!design.getTechnology().isLayerIndexValid(rowIndex)) {
                        return;
                    }
                    if (columnIndex == 0) {
                        layerUiState.setEditing(rowIndex);
                        table.repaint();
                    } else if (columnIndex == 1) {
                        layerUiState.toggleVisible(rowIndex);
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
            JButton button = new JButton("DRC");
            button.setFocusable(false);
            sideBar.add(button, BorderLayout.PAGE_END);
        }

        Paint layer0Paint;
        {
            BufferedImage image = new BufferedImage(3, 3, BufferedImage.TYPE_INT_RGB);
            image.setRGB(0, 0, 0xff0000);
            image.setRGB(1, 1, 0xff0000);
            image.setRGB(2, 2, 0xff0000);
            layer0Paint = new TexturePaint(image, new Rectangle2D.Float(0, 0, 3, 3));
        }

        mainPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics _g) {
                Graphics2D g = (Graphics2D) _g;

                int panelWidth = mainPanel.getWidth();
                int panelHeight = mainPanel.getHeight();
                int designDisplayWidth = design.getWidth() * cellSize;
                int designDisplayHeight = design.getHeight() * cellSize;
                if (designDisplayWidth < panelWidth || designDisplayHeight < panelHeight) {
                    g.setColor(Color.LIGHT_GRAY);
                    g.fillRect(0, 0, panelWidth, panelHeight);
                }

                g.setColor(Color.BLACK);
                g.fillRect(0, 0, designDisplayWidth, designDisplayHeight);
                for (int x = 0; x < MainWindow.this.design.getWidth(); x++) {
                    for (int y = 0; y < MainWindow.this.design.getHeight(); y++) {
                        boolean l0 = MainWindow.this.design.getLayers().get(0).getCell(x, y) && layerUiState.getVisible(0);
                        boolean l1 = MainWindow.this.design.getLayers().get(1).getCell(x, y) && layerUiState.getVisible(1);
                        boolean l2 = MainWindow.this.design.getLayers().get(2).getCell(x, y) && layerUiState.getVisible(2);

                        if (l1 || l2) {
                            g.setColor(new Color(0, l1 ? 255 : 0, l2 ? 255 : 0));
                            g.fillRect(x * cellSize, y * cellSize, cellSize, cellSize);
                        } else if (l0) {
                            g.setPaint(layer0Paint);
                            g.fillRect(x * cellSize, y * cellSize, cellSize, cellSize);
                        }
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
                    if (MainWindow.this.design.getLayers().get(layerUiState.getEditing()).isValidPosition(x, y)) {
                        MainWindow.this.design.getLayers().get(layerUiState.getEditing()).setCell(x, y, drawing);
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
                        int layer = event.getKeyChar() - '1';
                        if (design.getTechnology().isLayerIndexValid(layer)) {
                            layerUiState.setEditing(layer);
                        }
                        break;
                    }

                    case '0':
                        if (design.getTechnology().isLayerIndexValid(9)) {
                            layerUiState.setEditing(9);
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

    private void resetUi() {
        layerUiState.setEditing(0);
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
        drcAgent.setDesign(design);
        resetUi();
    }

}
