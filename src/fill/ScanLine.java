package fill;

import model.Edge;
import model.Point;
import renderer.Renderer;
import view.Raster;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ScanLine implements Filler {

    private Raster raster;
    private Renderer renderer;
    private List<Point> points;
    private int fillColor, edgeColor;

    @Override
    public void setRaster(Raster raster) {
        this.raster = raster;
    }

    @Override
    public void fill(boolean pattern) {
        scanline();
    }



    public void init(List<Point> points, int fillColor, int edgeColor, Renderer renderer){
        this.points = points;
        this.fillColor = fillColor;
        this.edgeColor = edgeColor;
        this.renderer = renderer;
    }

    private void scanline() {

        List<Edge> edges = new ArrayList<>();
        // projet všechny body a vytvořit z nich hrany (jako polygon)
        // 0. a 1. bod budou první hrana; 1. a 2. bod budou druhá hrana
        // ...; poslední a 0. bod budou poslední hrana
        // ignorovat vodorovné hrany
        // vyvtořené hrany zorientovat a přidat do seznamu

        int minY = points.get(0).y;
        int maxY = minY;

        for (int i = 0; i < points.size() - 1; i++) {
            int y1 = points.get(i).y;
            int y2 = points.get(i + 1).y;

            if (minY > y1){
                minY = y1;
            }else if (minY > y2){
                minY = y2;
            }

            if (maxY < y1){
                maxY = y1;
            }else if (maxY < y2){
                maxY = y2;
            }

            Point p1 = points.get(i);
            Point p2 = points.get(i + 1);
            Edge edge = new Edge(p1, p2);
            edge.orientate();
            if (!edge.isHorizontal()){
                edges.add(edge);
            }
        }

        Point p1 = points.get(0);
        Point p2 = points.get(points.size() - 1);
        Edge edge = new Edge(p1, p2);
        edge.orientate();
        if (!edge.isHorizontal()){
            edges.add(edge);
        }

        for (int y = minY; y <= maxY; y++) {

            List<Integer> intersections = new ArrayList<>();
            for (Edge edge1 : edges){
                if (edge1.intersectionExists(y)){
                    Point v1 = new Point(0, y);
                    Point v2 = new Point(raster.getWidth(), y);
                    intersections.add(edge1.getIntersection(v1, v2).x);
                }
            }

            Collections.sort(intersections);

            for (int i = 0; i < intersections.size() - 1; i += 2) {
                renderer.drawDDA(intersections.get(i), y, intersections.get(i + 1), y, fillColor);
            }
        }
    }
}
