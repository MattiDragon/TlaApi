package io.github.mattidragon.tlaapi.impl.rei.util;

import com.mojang.blaze3d.systems.RenderSystem;
import me.shedaniel.clothconfig2.api.animator.NumberAnimator;
import me.shedaniel.clothconfig2.api.animator.ValueAnimator;
import me.shedaniel.math.Rectangle;
import me.shedaniel.rei.api.client.REIRuntime;
import me.shedaniel.rei.api.client.gui.widgets.WidgetWithBounds;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.Element;
import net.minecraft.util.Identifier;

import java.util.Collections;
import java.util.List;
import java.util.function.DoubleSupplier;

// hopefully working combination of EMI and REI code
public final class AnimatedTextureWidget extends WidgetWithBounds {
    private final Rectangle bounds;
    private final DoubleSupplier progressSupplier;
    private final NumberAnimator<Float> darkBackgroundAlpha = ValueAnimator.ofFloat()
            .withConvention(() -> REIRuntime.getInstance().isDarkThemeEnabled() ? 1.0F : 0.0F, ValueAnimator.typicalTransitionTime())
            .asFloat();

    private final Identifier darkTexture;
    private final Identifier lightTexture;
    private final int u;
    private final int v;
    private final int regionWidth;
    private final int regionHeight;
    private final int textureWidth;
    private final int textureHeight;
    private final boolean horizontal, endToStart, fullToEmpty;

    public AnimatedTextureWidget(Rectangle bounds, DoubleSupplier progressSupplier, Identifier darkTexture, Identifier lightTexture, int u, int v, int regionWidth, int regionHeight, int textureWidth, int textureHeight, boolean horizontal, boolean endToStart, boolean fullToEmpty) {
        this.bounds = bounds;
        this.progressSupplier = progressSupplier;
        this.darkTexture = darkTexture;
        this.lightTexture = lightTexture;
        this.u = u;
        this.v = v;
        this.regionWidth = regionWidth;
        this.regionHeight = regionHeight;
        this.textureWidth = textureWidth;
        this.textureHeight = textureHeight;
        this.horizontal = horizontal;
        this.endToStart = endToStart;
        this.fullToEmpty = fullToEmpty;
    }

    @Override
    public Rectangle getBounds() {
        return bounds;
    }
    
    @Override
    public void render(DrawContext graphics, int mouseX, int mouseY, float delta) {
        this.darkBackgroundAlpha.update(delta);
        renderBackground(graphics, false, 1.0F);
        if (darkBackgroundAlpha.value() > 0) {
            renderBackground(graphics, true, this.darkBackgroundAlpha.value());
        }
    }
    
    public void renderBackground(DrawContext graphics, boolean dark, float alpha) {
        var x = bounds.x;
        var y = bounds.y;
        var width = bounds.width;
        var height = bounds.height;

        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, alpha);
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();

        var texture = dark ? darkTexture : lightTexture;

        var progress = progressSupplier.getAsDouble();
        if (endToStart ^ fullToEmpty) {
            progress = 1 - progress;
        }

        int mx = x, my = y;
        int mw = width, mh = height;
        int mu = u, mv = v;
        int mrw = regionWidth, mrh = regionHeight;
        if (horizontal) {
            if (endToStart) {
                mx = (int) (x + width * progress);
                mu = (int) (u + regionWidth * progress);
                mw = width - (mx - x);
                mrw = regionWidth - (mu - u);
            } else {
                mw = (int) (width * progress);
                mrw = (int) (regionWidth * progress);
            }
        } else {
            if (endToStart) {
                my = (int) (y + height * progress);
                mv = (int) (v + regionHeight * progress);
                mh = height - (my - y);
                mrh = regionHeight - (mv - v);
            } else {
                mh = (int) (height * progress);
                mrh = (int) (regionHeight * progress);
            }
        }

        graphics.drawTexture(texture, mx, my, mw, mh, mu, mv, mrw, mrh, textureWidth, textureHeight);

        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
    }
    
    @Override
    public List<? extends Element> children() {
        return Collections.emptyList();
    }

    public static class Builder {
        private final Rectangle bounds;
        private DoubleSupplier progressSupplier;
        private Identifier darkTexture;
        private Identifier lightTexture;
        private int u = 0;
        private int v = 0;
        private int regionWidth;
        private int regionHeight;
        private int textureWidth = 256;
        private int textureHeight = 256;
        private boolean horizontal;
        private boolean endToStart;
        private boolean fullToEmpty;

        public Builder(Rectangle bounds) {
            this.bounds = bounds;
            regionWidth = bounds.width;
            regionHeight = bounds.height;
        }

        public Builder(int x, int y, int width, int height) {
            this(new Rectangle(x, y, width, height));
        }

        public Builder animationDuration(double animationDuration) {
            if (animationDuration <= 0) throw new IllegalArgumentException("Animation duration must be positive");
            this.progressSupplier = () -> (System.currentTimeMillis() % (long) animationDuration) / animationDuration;
            return this;
        }

        public Builder progress(DoubleSupplier progressSupplier) {
            this.progressSupplier = progressSupplier;
            return this;
        }

        public Builder texture(Identifier texture) {
            this.darkTexture = texture;
            this.lightTexture = texture;
            return this;
        }

        public Builder texture(Identifier darkTexture, Identifier lightTexture) {
            this.darkTexture = darkTexture;
            this.lightTexture = lightTexture;
            return this;
        }

        public Builder defaultTexture() {
            darkTexture = REIRuntime.getInstance().getDefaultDisplayTexture(true);
            lightTexture = REIRuntime.getInstance().getDefaultDisplayTexture(false);
            return this;
        }

        public Builder uv(int u, int v) {
            this.u = u;
            this.v = v;
            return this;
        }

        public Builder regionSize(int regionWidth, int regionHeight) {
            this.regionWidth = regionWidth;
            this.regionHeight = regionHeight;
            return this;
        }

        public Builder textureSize(int textureWidth, int textureHeight) {
            this.textureWidth = textureWidth;
            this.textureHeight = textureHeight;
            return this;
        }

        public Builder horizontal() {
            this.horizontal = true;
            return this;
        }

        public Builder endToStart() {
            this.endToStart = true;
            return this;
        }

        public Builder fullToEmpty() {
            this.fullToEmpty = true;
            return this;
        }

        public Builder flags(boolean horizontal, boolean endToStart, boolean fullToEmpty) {
            this.horizontal = horizontal;
            this.endToStart = endToStart;
            this.fullToEmpty = fullToEmpty;
            return this;
        }

        public AnimatedTextureWidget build() {
            if (progressSupplier == null) throw new IllegalStateException("Progress must be set");
            if (darkTexture == null || lightTexture == null) throw new IllegalStateException("Texture must be set");
            return new AnimatedTextureWidget(bounds, progressSupplier, darkTexture, lightTexture, u, v, regionWidth, regionHeight, textureWidth, textureHeight, horizontal, endToStart, fullToEmpty);
        }
    }
}
