package io.github.mattidragon.tlaapi.impl.rei.util;

import com.mojang.blaze3d.systems.RenderSystem;
import io.github.mattidragon.tlaapi.api.gui.TextureConfig;
import me.shedaniel.clothconfig2.api.animator.NumberAnimator;
import me.shedaniel.clothconfig2.api.animator.ValueAnimator;
import me.shedaniel.math.Rectangle;
import me.shedaniel.rei.api.client.REIRuntime;
import me.shedaniel.rei.api.client.gui.Renderer;
import me.shedaniel.rei.api.client.gui.widgets.WidgetWithBounds;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.Element;
import net.minecraft.util.Identifier;

import java.util.Collections;
import java.util.List;
import java.util.Objects;

public final class TextureWidget extends WidgetWithBounds {
    private final Rectangle bounds;
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

    public TextureWidget(Rectangle bounds, Identifier darkTexture, Identifier lightTexture, int u, int v, int regionWidth, int regionHeight, int textureWidth, int textureHeight) {
        this.bounds = new Rectangle(Objects.requireNonNull(bounds));
        this.darkTexture = darkTexture;
        this.lightTexture = lightTexture;
        this.u = u;
        this.v = v;
        this.regionWidth = regionWidth;
        this.regionHeight = regionHeight;
        this.textureWidth = textureWidth;
        this.textureHeight = textureHeight;
    }

    public TextureWidget(int x, int y, TextureConfig config) {
        this(new Rectangle(x, y, config.width(), config.height()),
                config.texture(), config.texture(),
                config.u(), config.v(),
                config.regionWidth(), config.regionHeight(),
                config.textureWidth(), config.textureHeight());
    }
    
    @Override
    public Rectangle getBounds() {
        return bounds;
    }
    
    @Override
    public void render(DrawContext graphics, int mouseX, int mouseY, float delta) {
        this.darkBackgroundAlpha.update(delta);
        renderTexture(graphics, false, 1.0F, bounds);
        if (darkBackgroundAlpha.value() > 0) {
            renderTexture(graphics, true, this.darkBackgroundAlpha.value(), bounds);
        }
    }

    private void renderTexture(DrawContext graphics, boolean dark, float alpha, Rectangle bounds) {
        var x = bounds.x;
        var y = bounds.y;
        var width = bounds.width;
        var height = bounds.height;

        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, alpha);
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();

        var texture = dark ? darkTexture : lightTexture;
        graphics.drawTexture(texture, x, y, width, height, u, v, regionWidth, regionHeight, textureWidth, textureHeight);

        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
    }
    
    @Override
    public List<? extends Element> children() {
        return Collections.emptyList();
    }

    public static class Builder {
        private final Rectangle bounds;
        private Identifier darkTexture;
        private Identifier lightTexture;
        private int u = 0;
        private int v = 0;
        private int regionWidth;
        private int regionHeight;
        private int textureWidth = 256;
        private int textureHeight = 256;

        public Builder(Rectangle bounds) {
            this.bounds = bounds;
            regionWidth = bounds.width;
            regionHeight = bounds.height;
        }

        public Builder(int x, int y, int width, int height) {
            this(new Rectangle(x, y, width, height));
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

        public TextureWidget build() {
            if (darkTexture == null || lightTexture == null) throw new IllegalStateException("Texture must be set");
            return new TextureWidget(bounds, darkTexture, lightTexture, u, v, regionWidth, regionHeight, textureWidth, textureHeight);
        }
    }
}
