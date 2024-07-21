package br.nullexcept.mux.graphics;

import java.util.ArrayList;

/**
 * CODE TO CONVERT SVG TO Path
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

    public void scale(float x, float y) {
        for (Segment seg: segments) {
            seg.begin[0] *= x;
            seg.begin[1] *= y;
            for (float[] point: seg.points) {
                for (int i = 0; i < point.length; i+= 2) {
                    point[i] *= x;
                    point[i+1] *= y;
                }
            }
        }
    }

    public static final class Segment {
        private final float[] begin = new float[2];
        private final boolean closed;
        private final float[][] points;

        public Segment(float x, float y, boolean closed, float[][] points) {
            begin[0] = x;
            begin[1] = y;

            this.closed = closed;
            this.points = points;
        }

        public boolean closed() {
            return closed;
        }

        public float beginX() {
            return begin[0];
        }

        public float beginY(){
            return begin[1];
        }

        public float[] part(int index) {
            return points[index];
        }

        public int partCount(){
            return points.length;
        }
    }
}
