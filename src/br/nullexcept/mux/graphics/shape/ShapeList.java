package br.nullexcept.mux.graphics.shape;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ShapeList {
    private final ArrayList<Shape> shapes = new ArrayList<>();

    public ShapeList(Shape... shapes) {
        this.shapes.addAll(Arrays.asList(shapes));
    }

    public List<Shape> asArray() {
        return new ArrayList<>(shapes);
    }

    public int getLength() {
        return shapes.size();
    }
}
