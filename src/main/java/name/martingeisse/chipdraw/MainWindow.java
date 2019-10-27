package name.martingeisse.chipdraw;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;

public class MainWindow extends JFrame {

    private Design design = new Design(20, 10);
    private int drawLayerIndex;
    private boolean drawing;
    private boolean erasing;
    private int cellSize;

    public MainWindow() {
        super("Chipdraw");
        setLayout(null);
        getContentPane().setBackground(new Color(64, 64, 64));
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setResizable(false);
        resetUi();

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
                        repaint();
                    }
                }
            }

            @Override
            public void mouseDragged(MouseEvent e) {
                mouseMoved(e);
            }

        };
        addMouseListener(mouseAdapter);
        addMouseMotionListener(mouseAdapter);
        addKeyListener(new KeyAdapter() {
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
                        cellSize *= 2;
                        // TODO does not work correctly because the title bar is part of the window size. Use a nested canvas instead!
                        updateWindowSize();
                        break;

                    case '-':
                        cellSize /= 2;
                        updateWindowSize();
                        break;

                    case 's':
                    case 'S':
                        LoadAndSaveDialogs.showSaveDialog(MainWindow.this, design);
                        break;

                    case 'l':
                    case 'L':
                    {
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
    }

    private void resetUi() {
        drawLayerIndex = 0;
        drawing = false;
        erasing = false;
        cellSize = 16;
        updateWindowSize();
    }

    private void updateWindowSize() {
        setPreferredSize(new Dimension(design.getWidth() * cellSize, design.getHeight() * cellSize));
        setSize(new Dimension(design.getWidth() * cellSize, design.getHeight() * cellSize));
        repaint();
    }

    @Override
    public void paint(Graphics g) {
        super.paint(g);
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

}
