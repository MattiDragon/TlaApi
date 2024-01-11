package io.github.mattidragon.tlaapi.impl.emi;

import dev.emi.emi.api.widget.TextureWidget;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.util.Identifier;

import java.util.function.DoubleSupplier;

// Adapted from emi code to support double progress suppliers
public class ProgressingTextureWidget extends TextureWidget {
    protected final boolean horizontal, endToStart, fullToEmpty;
    private final DoubleSupplier progress;

    public ProgressingTextureWidget(Identifier texture, int x, int y, int width, int height, int u, int v,
                                 int regionWidth, int regionHeight, int textureWidth, int textureHeight, DoubleSupplier progress,
                                 boolean horizontal, boolean endToStart, boolean fullToEmpty) {
        super(texture, x, y, width, height, u, v, regionWidth, regionHeight, textureWidth, textureHeight);
        this.progress = progress;
        this.horizontal = horizontal;
        this.endToStart = endToStart;
        this.fullToEmpty = fullToEmpty;
    }

    @Override
    public void render(DrawContext draw, int mouseX, int mouseY, float delta) {
        var currentProgress = progress.getAsDouble();
        if (endToStart ^ fullToEmpty) {
            currentProgress = 1 - currentProgress;
        }

        int mx = x, my = y;
        int mw = width, mh = height;
        int mu = u, mv = v;
        int mrw = regionWidth, mrh = regionHeight;
        if (horizontal) {
            if (endToStart) {
                mx = (int) (x + width * currentProgress);
                mu = (int) (u + regionWidth * currentProgress);
                mw = width - (mx - x);
                mrw = regionWidth - (mu - u);
            } else {
                mw = (int) (width * currentProgress);
                mrw = (int) (regionWidth * currentProgress);
            }
        } else {
            if (endToStart) {
                my = (int) (y + height * currentProgress);
                mv = (int) (v + regionHeight * currentProgress);
                mh = height - (my - y);
                mrh = regionHeight - (mv - v);
            } else {
                mh = (int) (height * currentProgress);
                mrh = (int) (regionHeight * currentProgress);
            }
        }
        draw.drawTexture(this.texture, mx, my, mw, mh, mu, mv, mrw, mrh, textureWidth, textureHeight);
    }
}
