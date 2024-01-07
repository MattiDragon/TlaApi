package io.github.mattidragon.tlaapi.impl.rei.util;

import me.shedaniel.math.Dimension;
import me.shedaniel.math.Point;
import me.shedaniel.math.Rectangle;

public class TlaWidgets {
    public static AnimatedTextureWidget animatedFlame(int x, int y, int animationDurationMs) {
        return new AnimatedTextureWidget.Builder(x, y, 14, 14)
                .animationDuration(animationDurationMs)
                .fullToEmpty()
                .endToStart()
                .defaultTexture()
                .uv(82, 77)
                .build();
    }

    public static TextureWidget reiTexture(Point point, int u, int v, int width, int height) {
        return new TextureWidget.Builder(new Rectangle(point, new Dimension(width, height)))
                .defaultTexture()
                .uv(u, v)
                .build();
    }
}
