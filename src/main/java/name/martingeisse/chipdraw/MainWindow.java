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
    private final LayerUiState layerUiState = new LayerUiState();

    private Design design;
    private Technology technology;
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
                    if (rowIndex < 0 || rowIndex >= 3) {
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
        }
        sideBar.add(new JLabel(Icons.get("eye.png")), BorderLayout.CENTER); // TODO test remove
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
                Graphics2D g = (Graphics2D)_g;
                g.setColor(Color.black);
                g.fillRect(0, 0, getWidth(), getHeight());
                for (int x = 0; x < MainWindow.this.design.getWidth(); x++) {
                    for (int y = 0; y < MainWindow.this.design.getHeight(); y++) {
                        boolean l0 = MainWindow.this.design.getLayers().get(0).getCell(x, y);
                        boolean l1 = MainWindow.this.design.getLayers().get(1).getCell(x, y);
                        boolean l2 = MainWindow.this.design.getLayers().get(2).getCell(x, y);

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
                        layerUiState.setEditing(0);
                        break;

                    case '2':
                        layerUiState.setEditing(1);
                        break;

                    case '3':
                        layerUiState.setEditing(2);
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
            builder.addMenu("Test");
            builder.add("Corner Stitching Extractor", () -> CornerStitchingExtrator.extract(design));
            builder.add("Connectivity Extractor", () -> ConnectivityExtractor.extract(design));
            builder.addMenu("Help");
            builder.addExternalLink("Contents", "https://github.com/MartinGeisse/chipdraw/blob/master/doc/index.md"); // TODO link to commit for this version
            builder.add("About", () -> JOptionPane.showMessageDialog(MainWindow.this, About.ABOUT_TEXT));
            setJMenuBar(builder.build());
        }

        resetUi();
        pack();
        drcAgent.setDesign(design);
    }

    private void resetUi() {
        layerUiState.setEditing(0);
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
        drcAgent.setDesign(design);
        resetUi();
    }

}
