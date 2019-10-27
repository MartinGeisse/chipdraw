package name.martingeisse.chipdraw;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class MainWindow extends JFrame {

    public static final int MIN_CELL_SIZE = 2;
    public static final int MAX_CELL_SIZE = 32;

    private final JPanel mainPanel;

    private Design design = new Design(20, 10);
    private int drawLayerIndex;
    private boolean drawing;
    private boolean erasing;
    private int cellSize;

    public MainWindow() {
        super("Chipdraw");
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setResizable(false);

        mainPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                for (int x = 0; x < design.getWidth(); x++) {
                    for (int y = 0; y < design.getHeight(); y++) {
                        g.setColor(new Color(
                                design.getLayers().get(0).getCell(x, y) ? 255 : 0,
                                design.getLayers().get(1).getCell(x, y) ? 255 : 0,
                                design.getLayers().get(2).getCell(x, y) ? 255 : 0
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
                    if (design.getLayers().get(drawLayerIndex).isValidPosition(x, y)) {
                        design.getLayers().get(drawLayerIndex).setCell(x, y, drawing);
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
            public void keyTyped(KeyEvent e) {
                switch (e.getKeyChar()) {

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
                        LoadAndSaveDialogs.showSaveDialog(MainWindow.this, design);
                        break;

                    case 'l':
                    case 'L': {
                        Design design = LoadAndSaveDialogs.showLoadDialog(MainWindow.this);
                        if (design != null) {
                            MainWindow.this.design = design;
                            resetUi();
                        }
                        break;
                    }

                }
            }
        });
        mainPanel.setFocusable(true);
        mainPanel.grabFocus();
        add(mainPanel);
        resetUi();
        pack();
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

}
