package br.nullexcept.mux.graphics;

import java.util.ArrayList;

/**
 * CODE TO CONVERT SVG TO Path
 NSVGImage img = NanoSVG.nsvgParseFromFile(__DIR__,"px",0.0f);
 NSVGShape shape = img.shapes();
 NSVGPath _path = shape.paths();
 ArrayList<NSVGPath> paths = new ArrayList<>();
 while (_path != null){
 paths.add(_path);
 try {
 _path = _path.next();
 } catch (Exception e){
 break;
 }
 }


 Path p = new Path();

 ArrayList<float[]> points = new ArrayList<>();
 for (NSVGPath path: paths) {
 FloatBuffer buffer = path.pts();
 float bx = buffer.get(0);
 float by = buffer.get(1);

 for (int x = 0; x < path.npts() - 1; x += 3) {
 int i = x * 2;
 points.add(new float[]{buffer.get(i+2), buffer.get(i+3),
 buffer.get(i+4), buffer.get(i+5),
 buffer.get(i+6), buffer.get(i+7)
 });
 }

 p.add(bx, by, path.closed() == 1, points.toArray(new float[0][]));
 points.clear();
 }
 */

public class Path {
    private final ArrayList<Segment> segments = new ArrayList<>();

    public Path() {}

    public Segment segment(int index) {
        return segments.get(index);
    }

    public int length() {
        return segments.size();
    }

    public void add(float beginX, float beginY, boolean closed, float[]... points) {
        segments.add(new Segment(beginX, beginY, closed, points));
    }

    public static final class Segment {
        private final float x,y;
        private final boolean closed;
        private float[][] points;

        public Segment(float x, float y, boolean closed, float[][] points) {
            this.x = x;
            this.y = y;
            this.closed = closed;
            this.points = points;
        }

        public boolean closed() {
            return closed;
        }

        public float beginX() {
            return x;
        }

        public float beginY(){
            return y;
        }

        public float[] part(int index) {
            return points[index];
        }

        public int partCount(){
            return points.length;
        }
    }
}
