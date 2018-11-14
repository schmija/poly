package renderer;

import model.Edge;
import model.Point;
import view.Raster;

import java.util.ArrayList;
import java.util.List;

public class Renderer {

    private Raster raster;

    public Renderer(Raster raster) {
        this.raster = raster;
    }

    public void drawDDA(int x1, int y1, int x2, int y2, int color) {
        int dx = x2 - x1;
        int dy = y2 - y1;
        float x, y, k, h, g;

        k = dy / (float) dx;
        if (Math.abs(k) < 1) {
            // řídící osa X
            g = 1;
            h = k;
            if (x1 > x2) {
                x1 = x2;
                y1 = y2;
            }
        } else {
            // řídící osa Y
            g = 1 / k;
            h = 1;
            if (y1 > y2) {
                x1 = x2;
                y1 = y2;
            }
        }

        x = x1;
        y = y1;

        for (int i = 0; i <= Math.max(Math.abs(dx), Math.abs(dy)); i++) {
            raster.drawPixel(Math.round(x), Math.round(y), color);
            x += g;
            y += h;
        }
    }

    public void drawPolygon(List<Point> points, int color) {
        for (int i = 0; i < points.size() - 1; i++) {
            drawDDA(points.get(i).x,
                    points.get(i).y,
                    points.get(i + 1).x,
                    points.get(i + 1).y,
                    color
            );
        }
        // spoj poslední a první
        drawDDA(points.get(0).x,
                points.get(0).y,
                points.get(points.size() - 1).x,
                points.get(points.size() - 1).y,
                color
        );
    }

    public List<Point> clip(List<Point> polygon, List<Point> clipPolygon)
    {

        if (polygon.size() < 2){
            return polygon;
        }

        List<Point> in = polygon;
        Point p1 = clipPolygon.get(clipPolygon.size() - 1);

        for (Point p2 : clipPolygon)
        {
            List<Point> out = new ArrayList();
            out.clear();
            Point v1 = in.get(in.size() - 1);
            Edge edge = new Edge(p1, p2);

            for (Point v2 : in)
            {
                if (edge.inside(v2))
                {
                    if (!edge.inside(v1)) {
                        out.add(edge.getIntersection(v1, v2));
                    }
                    out.add(v2);
                }
                else if (edge.inside(v1))
                {
                    out.add(edge.getIntersection(v1, v2));
                }
                v1 = v2;
                in = out;
                p1 = p2;
            }
        }
        return in;
    }
}
