package name.martingeisse.chipdraw;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class MainWindow extends JFrame {

    public static final int CELL_SIZE = 20;

    private final Design design = new Design(20, 10);
    private int drawLayerIndex = 0;

    public MainWindow() {
        super("Chipdraw");
        setLayout(null);
        setPreferredSize(new Dimension(design.getWidth() * CELL_SIZE, design.getHeight() * CELL_SIZE));
        setSize(new Dimension(design.getWidth() * CELL_SIZE, design.getHeight() * CELL_SIZE));
        getContentPane().setBackground(new Color(64, 64, 64));
        pack();
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setResizable(false);

        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                handleMouseEvent(e);
            }
        });
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

                }
            }
        });

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
                g.fillRect(x * CELL_SIZE, y * CELL_SIZE, CELL_SIZE, CELL_SIZE);
            }
        }
    }

    private void handleMouseEvent(MouseEvent e) {
        int button = e.getButton();
        if (button == MouseEvent.BUTTON1 || button == MouseEvent.BUTTON3) {
            int x = e.getX() / CELL_SIZE;
            int y = e.getY() / CELL_SIZE;
            if (design.getLayers().get(drawLayerIndex).isValidPosition(x, y)) {
                design.getLayers().get(drawLayerIndex).setCell(x, y, button == MouseEvent.BUTTON1);
                repaint();
            }
        }
    }

}
