package name.martingeisse.chipdraw;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class MainWindow extends JFrame {

    public static final int CELL_SIZE = 20;

    private final Layer layer = new Layer(20, 10);

    public MainWindow() {
        super("Chipdraw");
        setLayout(null);
        setPreferredSize(new Dimension(layer.getWidth() * CELL_SIZE, layer.getHeight() * CELL_SIZE));
        setSize(new Dimension(layer.getWidth() * CELL_SIZE, layer.getHeight() * CELL_SIZE));
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

    }

    @Override
    public void paint(Graphics g) {
        super.paint(g);
        g.setColor(Color.white);
        for (int x = 0; x < layer.getWidth(); x++) {
            for (int y = 0; y < layer.getHeight(); y++) {
                if (layer.getCell(x, y)) {
                    g.fillRect(x * CELL_SIZE, y * CELL_SIZE, CELL_SIZE, CELL_SIZE);
                }
            }
        }
    }

    private void handleMouseEvent(MouseEvent e) {
        int button = e.getButton();
        if (button == MouseEvent.BUTTON1 || button == MouseEvent.BUTTON3) {
            int x = e.getX() / CELL_SIZE;
            int y = e.getY() / CELL_SIZE;
            if (layer.isValidPosition(x, y)) {
                layer.setCell(x, y, button == MouseEvent.BUTTON1);
                repaint();
            }
        }
    }

}
