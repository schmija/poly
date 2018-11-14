package view;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.Timer;
import java.util.TimerTask;

// za bonus implementace návrhového vzoru Singleton
public class Raster extends JPanel {

    private static Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
    public static final int WIDTH = screenSize.width;
    public static final int HEIGHT = screenSize.height;
    private static final int FPS = 1000 / 30;
    private BufferedImage bi;

    public Raster() {
        // inicializace image, nastavení rozměrů
        bi = new BufferedImage(WIDTH, HEIGHT, BufferedImage.TYPE_INT_RGB);
        setLoop();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        g.drawImage(bi, 0, 0, null);
    }

    private void setLoop() {
        // časovač, který 30 krát za vteřinu obnoví obsah plátna aktuálním img
        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                repaint();
            }
        }, 0, FPS);
    }

    public void clear() {
        Graphics g = bi.getGraphics();
        g.setColor(Color.BLACK);
        g.clearRect(0, 0, WIDTH, HEIGHT);
    }

    public void drawPixel(int x, int y, int color) {
        if ((x >= 0) && (x < bi.getWidth()) && (y >= 0) && (y < bi.getHeight())) {
            bi.setRGB(x, y, color);
        }
    }

    public int getPixel(int x, int y) {
        return bi.getRGB(x, y);
    }

}
