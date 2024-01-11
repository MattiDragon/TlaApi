package io.github.mattidragon.tlaapi.api.gui;

import net.minecraft.util.Identifier;

import java.util.Objects;

/**
 * Represents a texture configuration.
 * @see Builder
 */
public final class TextureConfig {
    private final Identifier lightTexture;
    private final Identifier darkTexture;
    private final int width;
    private final int height;
    private final int u;
    private final int v;
    private final int regionWidth;
    private final int regionHeight;
    private final int textureWidth;
    private final int textureHeight;

    private TextureConfig(Identifier lightTexture, Identifier darkTexture, int width, int height, int u, int v, int regionWidth, int regionHeight, int textureWidth, int textureHeight) {
        this.lightTexture = lightTexture;
        this.darkTexture = darkTexture;
        this.width = width;
        this.height = height;
        this.u = u;
        this.v = v;
        this.regionWidth = regionWidth;
        this.regionHeight = regionHeight;
        this.textureWidth = textureWidth;
        this.textureHeight = textureHeight;
    }

    /**
     * @deprecated Use builder instead.
     */
    @Deprecated
    public TextureConfig(Identifier texture, int width, int height, int u, int v, int regionWidth, int regionHeight, int textureWidth, int textureHeight) {
        this(texture, texture, width, height, u, v, regionWidth, regionHeight, textureWidth, textureHeight);
    }

    /**
     * Creates a texture config with a 1 to 1 scaling and a texture size of 256x256.
     *
     * @deprecated Use {@link #builder()} instead.
     */
    @Deprecated
    public static TextureConfig of(Identifier texture, int width, int height, int u, int v) {
        return builder().texture(texture).size(width, height).uv(u, v).build();
    }

    /**
     * Creates a texture config with a 1 to 1 scaling.
     *
     * @deprecated Use {@link #builder()} instead.
     */
    @Deprecated
    public static TextureConfig of(Identifier texture, int width, int height, int u, int v, int textureWidth, int textureHeight) {
        return builder().texture(texture).size(width, height).uv(u, v).textureSize(textureWidth, textureHeight).build();
    }

    /**
     * Creates a texture config that captures a whole texture file.
     *
     * @deprecated Use {@link #builder()} instead.
     */
    @Deprecated
    public static TextureConfig of(Identifier texture, int width, int height) {
        return builder().texture(texture).fullSize(width, height).build();
    }

    /**
     * Creates a new texture config builder.
     *
     * @see Builder
     */
    public static Builder builder() {
        return new Builder();
    }

    /**
     * @deprecated Use {@link #lightTexture()} or {@link #darkTexture()} instead.
     */
    @Deprecated
    public Identifier texture() {
        return lightTexture();
    }

    public Identifier lightTexture() {
        return lightTexture;
    }

    public Identifier darkTexture() {
        return darkTexture;
    }

    public int width() {
        return width;
    }

    public int height() {
        return height;
    }

    public int u() {
        return u;
    }

    public int v() {
        return v;
    }

    public int regionWidth() {
        return regionWidth;
    }

    public int regionHeight() {
        return regionHeight;
    }

    public int textureWidth() {
        return textureWidth;
    }

    public int textureHeight() {
        return textureHeight;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) return true;
        if (obj == null || obj.getClass() != this.getClass()) return false;
        var that = (TextureConfig) obj;
        return Objects.equals(this.lightTexture, that.lightTexture) &&
               Objects.equals(this.darkTexture, that.darkTexture) &&
               this.width == that.width &&
               this.height == that.height &&
               this.u == that.u &&
               this.v == that.v &&
               this.regionWidth == that.regionWidth &&
               this.regionHeight == that.regionHeight &&
               this.textureWidth == that.textureWidth &&
               this.textureHeight == that.textureHeight;
    }

    @Override
    public int hashCode() {
        return Objects.hash(lightTexture, darkTexture, width, height, u, v, regionWidth, regionHeight, textureWidth, textureHeight);
    }

    @Override
    public String toString() {
        return "TextureConfig[" +
               "lightTexture=" + lightTexture + ", " +
               "darkTexture=" + darkTexture + ", " +
               "width=" + width + ", " +
               "height=" + height + ", " +
               "u=" + u + ", " +
               "v=" + v + ", " +
               "regionWidth=" + regionWidth + ", " +
               "regionHeight=" + regionHeight + ", " +
               "textureWidth=" + textureWidth + ", " +
               "textureHeight=" + textureHeight + ']';
    }


    /**
     * A builder for texture configs.
     * The builder begins with most values uninitialized, requiring you to set them,
     * but some are already set:
     * <ul>
     *     <li>Texture size is set tp 256x256</li>
     *     <li>UV is set to 0, 0</li>
     * </ul>
     */
    public static class Builder {
        private Identifier lightTexture;
        private Identifier darkTexture;
        private int width = -1;
        private int height = -1;
        private int u = 0;
        private int v = 0;
        private int regionWidth = -1;
        private int regionHeight = -1;
        private int textureWidth = 256;
        private int textureHeight = 256;

        private Builder() {
        }

        /**
         * Configures the texture to use the same texture for light and dark mode.
         */
        public Builder texture(Identifier texture) {
            Objects.requireNonNull(texture, "Texture cannot be null");
            this.lightTexture = texture;
            this.darkTexture = texture;
            return this;
        }

        /**
         * Configures the texture to use different textures for light and dark mode.
         * Dark mode here refers to the dark theme of REI. You should match its color palette when applicable.
         */
        public Builder texture(Identifier lightTexture, Identifier darkTexture) {
            this.lightTexture = Objects.requireNonNull(lightTexture, "Light texture cannot be null");
            this.darkTexture = Objects.requireNonNull(darkTexture, "Dark texture cannot be null");
            return this;
        }

        /**
         * Sets the UV coordinates of the texture in the file.
         * The default is 0, 0.
         */
        public Builder uv(int u, int v) {
            if (u < 0 || v < 0)
                throw new IllegalArgumentException("UV coordinates must be non-negative");
            this.u = u;
            this.v = v;
            return this;
        }

        /**
         * Sets the size of the texture in the file.
         * If the region size is not set, this will also set the region size.
         */
        public Builder size(int width, int height) {
            if (width <= 0 || height <= 0)
                throw new IllegalArgumentException("Size must be positive");
            this.width = width;
            this.height = height;
            if (regionWidth == -1)
                this.regionWidth = width;
            if (regionHeight == -1)
                this.regionHeight = height;
            return this;
        }

        /**
         * Sets the size of the texture such that it takes up a whole file.
         */
        public Builder fullSize(int width, int height) {
            size(width, height);
            textureSize(width, height);
            return this;
        }

        /**
         * Sets the size of the texture in the file.
         */
        public Builder regionSize(int regionWidth, int regionHeight) {
            if (regionWidth <= 0 || regionHeight <= 0)
                throw new IllegalArgumentException("Region size must be positive");
            this.regionWidth = regionWidth;
            this.regionHeight = regionHeight;
            return this;
        }

        /**
         * Sets the size of the texture file.
         * The default is 256x256.
         */
        public Builder textureSize(int textureWidth, int textureHeight) {
            if (textureWidth <= 0 || textureHeight <= 0)
                throw new IllegalArgumentException("Texture size must be positive");
            this.textureWidth = textureWidth;
            this.textureHeight = textureHeight;
            return this;
        }

        /**
         * Builds the texture config.
         *
         * @throws IllegalStateException If the size, region size, or texture is not set.
         */
        public TextureConfig build() {
            if (width == -1 || height == -1)
                throw new IllegalStateException("Size not set");
            if (regionWidth == -1 || regionHeight == -1)
                throw new IllegalStateException("Region size not set");
            if (lightTexture == null || darkTexture == null)
                throw new IllegalStateException("Texture not set");
            return new TextureConfig(lightTexture, darkTexture, width, height, u, v, regionWidth, regionHeight, textureWidth, textureHeight);
        }
    }
}

