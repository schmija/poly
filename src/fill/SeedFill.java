package fill;

import view.Raster;

public class SeedFill implements Filler {

    private Raster raster;
    private int x, y, color, background;

    private int[][] pattern = new int[][]  {{0x000000, 0x006675, 0x006675, 0x006675, 0x006675},
                                            {0x006675, 0x000000, 0x006675, 0x006675, 0x006675},
                                            {0x006675, 0x006675, 0x000000, 0x006675, 0x006675},
                                            {0x006675, 0x006675, 0x006675, 0x000000, 0x006675},
                                            {0x006675   , 0x006675, 0x006675, 0x006675, 0x000000}};

    public void init(int x, int y, int color) {
        this.x = x;
        this.y = y;
        this.color = color;
        background = raster.getPixel(x, y);
    }

    @Override
    public void setRaster(Raster raster) {
        this.raster = raster;
    }

    @Override
    public void fill(boolean pattern) {
        if (pattern){
            patternFill(x, y);
        }else {
            seed(x, y);
        }
    }

    private void seed(int ax, int ay) {
        if (ax >= 0 && ay >= 0 && ax < Raster.WIDTH && ay < Raster.HEIGHT) {
            if (background == raster.getPixel(ax, ay)) {
                raster.drawPixel(ax, ay, color);
                seed(ax + 1, ay);
                seed(ax - 1, ay);
                seed(ax, ay + 1);
                seed(ax, ay - 1);
            }
        }
    }

    private void patternFill(int ax, int ay){
        if (ax >= 0 && ay >= 0 && ax < Raster.WIDTH && ay < Raster.HEIGHT) {
            if (background == raster.getPixel(ax, ay)) {
                int p = pattern[ax%3][ay%5];
                raster.drawPixel(ax, ay, p);
                patternFill(ax + 1, ay);
                patternFill(ax - 1, ay);
                patternFill(ax, ay + 1);
                patternFill(ax, ay - 1);
            }
        }
    }
}
