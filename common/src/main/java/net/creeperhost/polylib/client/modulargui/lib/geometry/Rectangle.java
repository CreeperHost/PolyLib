package net.creeperhost.polylib.client.modulargui.lib.geometry;

/**
 * Created by brandon3055 on 14/08/2023
 */
public record Rectangle(Position position, double width, double height) {

    public double x() {
        return position.x();
    }

    public double y() {
        return position.y();
    }

    public double maxX() {
        return x() + width;
    }

    public double maxY() {
        return y() + height;
    }

    public Rectangle translate(double xAmount, double yAmount) {
        return create(x() + xAmount, y() + yAmount, width, height);
    }

    public Rectangle move(double newX, double newY) {
        return create(newX, newY, width, height);
    }

    public Rectangle setSize(double width, double height) {
        return create(position, width, height);
    }

    public Rectangle adjustSize(double xAmount, double yAmount) {
        return create(position, width() + xAmount, height() + yAmount);
    }

    /**
     * Returns a new rectangle that represents the intersection area between the two inputs
     */
    public static Rectangle intersect(Rectangle rectA, Rectangle rectB) {
        double x = Math.max(rectA.x(), rectB.x());
        double y = Math.max(rectA.y(), rectB.y());
        double width = Math.max(0, Math.min(rectA.maxX(), rectB.maxX()) - rectA.x());
        double height = Math.max(0, Math.min(rectA.maxY(), rectB.maxY()) - rectA.y());
        return create(x, y, width, height);
    }

    /**
     * Returns a new rectangle, the bounds of which enclose all the input rectangles.
     *
     * @param rect        Starting rectangle
     * @param combineWith Rectangles to combine with the start rectangle
     */
    public static Rectangle combine(Rectangle rect, Rectangle... combineWith) {
        double x = rect.x();
        double y = rect.y();
        double maxX = rect.maxX();
        double maxY = rect.maxY();
        for (Rectangle other : combineWith) {
            x = Math.min(x, other.x());
            y = Math.min(y, other.y());
            maxX = Math.max(maxX, other.maxX());
            maxY = Math.max(maxY, other.maxY());
        }
        return create(x, y, maxX - x, maxY - y);
    }

    public boolean contains(double x, double y) {
        return x >= this.x() && x <= this.x() + this.width && y >= this.y() && y <= this.y() + this.height;
    }

    public static Rectangle create(Position position, double width, double height) {
        return new Rectangle(position, width, height);
    }

    public static Rectangle create(double x, double y, double width, double height) {
        return new Rectangle(Position.create(x, y), width, height);
    }
}
