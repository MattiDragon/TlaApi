package io.github.mattidragon.tlaapi.api.gui;

/**
 * Represents a bounding box.
 * Typically used for widgets, but can be used for other things as well.
 * For widgets the position is relative to the top left corner of the recipe.
 * In most other cases the position is relative to the top left corner of the screen.
 */
public record TlaBounds(int x, int y, int width, int height) {
    /**
     * Calculates the center x coordinates of the bounding box.
     */
    public int centerX() {
        return x + width / 2;
    }

    /**
     * Calculates the center y coordinates of the bounding box.
     */
    public int centerY() {
        return y + height / 2;
    }

    /**
     * Returns the x coordinate of the left edge of the bounding box.
     */
    public int left() {
        return x;
    }

    /**
     * Returns the x coordinate of the right edge of the bounding box.
     */
    public int right() {
        return x + width;
    }

    /**
     * Returns the y coordinate of the top edge of the bounding box.
     */
    public int top() {
        return y;
    }

    /**
     * Returns the y coordinate of the bottom edge of the bounding box.
     */
    public int bottom() {
        return y + height;
    }

    /**
     * Checks if the given coordinates are inside the bounding box.
     * Make sure that they are in the same coordinate space as the bounding box.
     */
    public boolean contains(int x, int y) {
        return x >= left() && x < right() && y >= top() && y < bottom();
    }
}
