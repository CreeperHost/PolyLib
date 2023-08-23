package net.creeperhost.polylib.client.modulargui.lib.geometry;

/**
 * Created by brandon3055 on 14/08/2023
 */
public class Rectangle {
    private double xPos;
    private double yPos;
    private double width;
    private double height;

    public Rectangle(double xPos, double yPos, double width, double height) {
        this.xPos = xPos;
        this.yPos = yPos;
        this.width = width;
        this.height = height;
    }

    public double getX() {
        return xPos;
    }

    public double getY() {
        return yPos;
    }

    public double getWidth() {
        return width;
    }

    public double getHeight() {
        return height;
    }

    public double getMaxX() {
        return getX() + getWidth();
    }

    public double getMaxY() {
        return getY() + getHeight();
    }

    public Rectangle setX(double xPos) {
        this.xPos = xPos;
        return this;
    }

    public Rectangle setY(double yPos) {
        this.yPos = yPos;
        return this;
    }

    public Rectangle setWidth(double width) {
        this.width = width;
        return this;
    }

    public Rectangle setHeight(double height) {
        this.height = height;
        return this;
    }

    public Rectangle setPos(double xPos, double yPos) {
        this.xPos = xPos;
        this.yPos = yPos;
        return this;
    }

    public Rectangle setSize(double width, double height) {
        this.width = width;
        this.height = height;
        return this;
    }

    /**
     * Modifies this rectangle to match the intersection area between itself and the specified rectangle.
     * @param other The other rectangle
     */
    public Rectangle intersect(Rectangle other) {
        this.xPos = Math.max(this.xPos, other.getX());
        this.yPos = Math.max(this.yPos, other.getY());
        this.width = Math.max(0, Math.min(this.getMaxX(), other.getMaxX()) - this.xPos);
        this.height = Math.max(0, Math.min(this.getMaxY(), other.getMaxY()) - this.yPos);
        return this;
    }

    /**
     * Expands this the bounds of this rectangle to include the given other rectangle.
     * @param other The other rectangle
     */
    public Rectangle combine(Rectangle other) {
        this.xPos = Math.min(this.xPos, other.getX());
        this.yPos = Math.min(this.yPos, other.getY());
        this.width = Math.max(this.getMaxX(), other.getMaxX()) - this.xPos;
        this.height = Math.max(this.getMaxY(), other.getMaxY()) - this.yPos;
        return this;
    }

    public boolean contains(double x, double y) {
        return x >= this.xPos && x <= this.xPos + this.width && y >= this.yPos && y <= this.yPos + this.height;
    }
}
