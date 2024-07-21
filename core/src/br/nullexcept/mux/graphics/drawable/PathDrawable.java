package br.nullexcept.mux.graphics.drawable;

import br.nullexcept.mux.graphics.*;

public class PathDrawable extends Drawable {
    private Path path;
    private final Point viewport = new Point();

    public PathDrawable(Path path, int width, int height) {
        viewport.set(width, height);
        this.path = path;
    }

    public void setColor(int color) {
        paint.setColor(color);
    }

    @Override
    public void draw(Canvas canvas) {
        Rect bounds = getBounds();
        path.scale(bounds.width()/(float)viewport.x, bounds.height()/(float)viewport.y);
        canvas.drawPath(path, bounds.left,bounds.top, paint);
        path.scale((float)viewport.x/bounds.width(), (float)viewport.y/bounds.height());
    }

    @Override
    public int getWidth() {
        return viewport.x;
    }

    @Override
    public int getHeight() {
        return viewport.y;
    }
}