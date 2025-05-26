package io.github.mattidragon.tlaapi.impl.jei;

import io.github.mattidragon.tlaapi.api.gui.TextureConfig;
import mezz.jei.api.gui.drawable.IDrawableStatic;
import net.minecraft.client.gui.DrawContext;

public class TextureDrawable implements IDrawableStatic {
    private final TextureConfig config;

    public TextureDrawable(TextureConfig config) {
        this.config = config;
    }

    @Override
    public int getWidth() {
        return config.width();
    }

    @Override
    public int getHeight() {
        return config.height();
    }

    @Override
    public void draw(DrawContext guiGraphics, int xOffset, int yOffset) {
        draw(guiGraphics, xOffset, yOffset, 0, 0, 0, 0);
    }

    @Override
    public void draw(DrawContext graphics, int xOffset, int yOffset, int maskTop, int maskBottom, int maskLeft, int maskRight) {
        var uScale = config.regionWidth() / (float) config.width();
        var vScale = config.regionHeight() / (float) config.height();
        graphics.drawTexture(config.lightTexture(),
                xOffset + maskLeft,
                yOffset + maskTop,
                config.width() - maskLeft - maskRight,
                config.height() - maskTop - maskBottom,
                 config.u() + maskLeft * uScale,
                config.v() + maskTop * vScale,
                (int) (config.regionWidth() - (maskLeft + maskRight) * uScale),
                (int) (config.regionHeight() - (maskTop + maskBottom) * vScale),
                config.textureWidth(),
                config.textureHeight());
    }
}
