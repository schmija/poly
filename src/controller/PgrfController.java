package controller;

import fill.ScanLine;
import fill.SeedFill;
import model.Point;
import renderer.Renderer;
import view.PgrfWindow;
import view.Raster;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.List;

public class PgrfController {

    private Raster raster;
    private Renderer renderer;
    private SeedFill seedFill;
    private ScanLine scanLine;
    private TextField text;
    private final List<Point> polygonPoints = new ArrayList<>();
    private final List<Point> clipPoints = new ArrayList<>();

    public PgrfController(PgrfWindow window) {
        initObjects(window);
        initListeners();
    }

    private void initObjects(PgrfWindow window) {
        raster = new Raster();
        raster.setFocusable(true);
        raster.grabFocus();
        window.add(raster);

        text = new TextField();
        text.setFocusable(false);
        text.setBackground(Color.BLACK);
        text.setForeground(Color.WHITE);
        text.setText("LMB = draw polygon, RMB = edit clipper, C = clear, MMB = seed fill, CTRL + MMB = pattern");
        window.add(text, BorderLayout.SOUTH);

        renderer = new Renderer(raster);

        seedFill = new SeedFill();
        seedFill.setRaster(raster);

        scanLine = new ScanLine();
        scanLine.setRaster(raster);

        clipPoints.add(0, new Point(50, 50));
        clipPoints.add(1, new Point(100, 400));
        clipPoints.add(2, new Point(680, 550));
        renderer.drawPolygon(clipPoints, 0xffffff);
    }

    private void initListeners() {

        raster.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (SwingUtilities.isMiddleMouseButton(e)){
                    if (e.isControlDown()){
                        seedFill.init(e.getX(), e.getY(), 0xff);
                        seedFill.fill(true);
                    }else {
                        seedFill.init(e.getX(), e.getY(), 0x006666);
                        seedFill.fill(false);
                    }
                }
            }

            @Override
            public void mousePressed(MouseEvent e) {
                if (SwingUtilities.isLeftMouseButton(e)) {
                    polygonPoints.add(new Point(e.getX(), e.getY()));
                    if (polygonPoints.size() == 1) {
                        polygonPoints.add(new Point(e.getX(), e.getY()));
                    }
                } else if (SwingUtilities.isRightMouseButton(e)) {
                    clipPoints.add(new Point(e.getX(), e.getY()));
                }
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                if (SwingUtilities.isLeftMouseButton(e)){
                    if (polygonPoints.size() > 2){
                        renderer.drawPolygon(polygonPoints, 0xff0000);
                        clip();
                    }
                }else if (SwingUtilities.isRightMouseButton(e)){
                    if (polygonPoints.size() > 2){
                        renderer.drawPolygon(polygonPoints, 0xff0000);
                        clip();
                    }
                    if (isConvex(e.getX(),e.getY())){
                        renderer.drawPolygon(clipPoints, 0xffffff);
                        clip();
                    }else {
                        clipPoints.remove(clipPoints.size() - 1);
                    }
                }
            }
        });
        raster.addMouseMotionListener(new MouseAdapter() {
            @Override
            public void mouseDragged(MouseEvent e) {
                if (SwingUtilities.isLeftMouseButton(e)) {
                    polygonPoints.get(polygonPoints.size() - 1).x = e.getX();
                    polygonPoints.get(polygonPoints.size() - 1).y = e.getY();
                    update();
                }else
                    if (SwingUtilities.isRightMouseButton(e)){
                        clipPoints.get(clipPoints.size() - 1).x = e.getX();
                        clipPoints.get(clipPoints.size() - 1).y = e.getY();

                        if (isConvex(e.getX(), e.getY())){
                            update();
                        }else{
                            raster.clear();
                            renderer.drawPolygon(clipPoints, 0xffffff);
                            renderer.drawDDA(clipPoints.get(0).x, clipPoints.get(0).y, e.getX(), e.getY(), 0xff0000);
                            renderer.drawDDA(clipPoints.get(clipPoints.size() - 2).x, clipPoints.get(clipPoints.size() - 2).y,
                                    e.getX(), e.getY(), 0xff0000);
                        }
                    }
                }

        });
        raster.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                // na klávesu C vymazat plátno
                if (e.getKeyCode() == KeyEvent.VK_C) {
                    raster.clear();
                    polygonPoints.clear();
                    clipPoints.clear();

                    clipPoints.add(0, new Point(50, 50));
                    clipPoints.add(1, new Point(100, 400));
                    clipPoints.add(2, new Point(680, 550));
                    renderer.drawPolygon(clipPoints, 0xffffff);
                }
            }
        });
    }

    private boolean isConvex(int px, int py) {
        double angle1;
        double angle2;
        Point p0 = clipPoints.get(0);
        Point last = clipPoints.get(clipPoints.size() - 2);

        angle1 = Math.toDegrees(Math.acos(Math.atan2(p0.x*py - p0.y*px, p0.x*px + p0.y*py)));
        angle2 = Math.toDegrees(Math.acos(Math.atan2(last.x*py - last.y*px, last.x*px + last.y*py)));

        return (angle1 > 100 && angle2 > 100);
    }

    private void clip(){
        if (polygonPoints.size() > 2){
            List<Point> out = renderer.clip(polygonPoints, clipPoints);

            scanLine.init(out,0x006600, 0x0000ff,renderer);
            scanLine.fill(false);
            renderer.drawPolygon(polygonPoints, 0xffff00);
            renderer.drawPolygon(out, 0x0000ff);
        }
    }

    private void update() {
        raster.clear();
        if (polygonPoints.size() > 0){
            renderer.drawPolygon(polygonPoints, 0xff0000);
        }
        renderer.drawPolygon(clipPoints, 0xffffff);
    }
}
