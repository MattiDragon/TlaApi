package io.github.mattidragon.tlaapi.api.gui;

import net.minecraft.util.Identifier;

/**
 * Represents a texture configuration.
 * @param texture The identifier of the texture.
 * @param width The width of the texture when rendered.
 * @param height The height of the texture when rendered.
 * @param u The x coordinate of texture in the file.
 * @param v The y coordinate of texture in the file.
 * @param regionWidth The width of the texture in the file.
 * @param regionHeight The height of the texture in the file.
 * @param textureWidth The width of the texture file.
 * @param textureHeight The height of the texture file.
 */
public record TextureConfig(Identifier texture, int width, int height, int u, int v, int regionWidth, int regionHeight, int textureWidth, int textureHeight) {
    /**
     * Creates a texture config with a 1 to 1 scaling and a texture size of 256x256.
     */
    public static TextureConfig of(Identifier texture, int width, int height, int u, int v) {
        return new TextureConfig(texture, width, height, u, v, width, height, 256, 256);
    }

    /**
     * Creates a texture config with a 1 to 1 scaling.
     */
    public static TextureConfig of(Identifier texture, int width, int height, int u, int v, int textureWidth, int textureHeight) {
        return new TextureConfig(texture, width, height, u, v, width, height, textureWidth, textureHeight);
    }

    /**
     * Creates a texture config that captures a whole texture file.
     */
    public static TextureConfig of(Identifier texture, int width, int height) {
        return new TextureConfig(texture, width, height, 0, 0, width, height, width, height);
    }
}

