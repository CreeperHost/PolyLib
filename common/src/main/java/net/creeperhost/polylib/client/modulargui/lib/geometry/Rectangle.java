package net.creeperhost.polylib.client.modulargui.lib.geometry;

/**
 * Created by brandon3055 on 14/08/2023
 */
public interface Rectangle {

    Position pos();

    default double x() {
        return pos().x();
    }

    default double y() {
        return pos().y();
    }

    double width();

    double height();

    default double xMax() {
        return x() + width();
    }

    default double yMax() {
        return y() + height();
    }

    /**
     * Returns a new rectangle with this operation applied
     * */
    default Rectangle translate(double xAmount, double yAmount) {
        return create(x() + xAmount, y() + yAmount, width(), height());
    }

    /**
     * Returns a new rectangle with this operation applied
     * */
    default Rectangle move(double newX, double newY) {
        return create(newX, newY, width(), height());
    }

    /**
     * Returns a new rectangle with this operation applied
     * */
    default Rectangle setSize(double width, double height) {
        return create(x(), y(), width, height);
    }

    /**
     * Returns a new rectangle with this operation applied
     * */
    default Rectangle adjustSize(double xAmount, double yAmount) {
        return create(x(), y(), width() + xAmount, height() + yAmount);
    }

    /**
     * Returns a new rectangle that represents the intersection area between the two inputs
     */
    static Rectangle intersect(Rectangle rectA, Rectangle rectB) {
        double x = Math.max(rectA.x(), rectB.x());
        double y = Math.max(rectA.y(), rectB.y());
        double width = Math.max(0, Math.min(rectA.xMax(), rectB.xMax()) - rectA.x());
        double height = Math.max(0, Math.min(rectA.yMax(), rectB.yMax()) - rectA.y());
        return create(x, y, width, height);
    }

    /**
     * Returns a new rectangle, the bounds of which enclose all the input rectangles.
     *
     * @param rect        Starting rectangle
     * @param combineWith Rectangles to combine with the start rectangle
     */
    static Rectangle combine(Rectangle rect, Rectangle... combineWith) {
        double x = rect.x();
        double y = rect.y();
        double maxX = rect.xMax();
        double maxY = rect.yMax();
        for (Rectangle other : combineWith) {
            x = Math.min(x, other.x());
            y = Math.min(y, other.y());
            maxX = Math.max(maxX, other.xMax());
            maxY = Math.max(maxY, other.yMax());
        }
        return create(x, y, maxX - x, maxY - y);
    }

    default boolean contains(double x, double y) {
        return x >= x() && x <= x() + width() && y >= y() && y <= y() + height();
    }

    static Rectangle create(Position position, double width, double height) {
        return new Immutable(position, width, height);
    }

    static Rectangle create(double x, double y, double width, double height) {
        return new Immutable(Position.create(x, y), width, height);
    }

    /**
     * Returns a new rectangle bound to the specified parent's geometry.
     * */
    static Rectangle create(GuiParent<?> parent) {
        return new Bound(Position.create(parent), parent);
    }

    /**Should not be created directly*/
    record Immutable(Position position, double xSize, double ySize) implements Rectangle {
        @Override
        public Position pos() {
            return position;
        }

        @Override
        public double width() {
            return xSize;
        }

        @Override
        public double height() {
            return ySize;
        }
    }

    /**Should not be created directly*/
    record Bound(Position position, GuiParent<?> parent) implements Rectangle {
        @Override
        public Position pos() {
            return position;
        }

        @Override
        public double width() {
            return parent.xSize();
        }

        @Override
        public double height() {
            return parent.ySize();
        }
    }
}
