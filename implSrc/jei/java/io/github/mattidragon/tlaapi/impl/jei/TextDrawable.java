package io.github.mattidragon.tlaapi.impl.jei;

import mezz.jei.api.gui.drawable.IDrawable;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.text.Text;

public class TextDrawable implements IDrawable {
    private final Text text;
    private final int color;
    private final TextRenderer textRenderer = MinecraftClient.getInstance().textRenderer;
    private final boolean shadow;
    private Alignment alignment = Alignment.LEFT;

    public TextDrawable(Text text, int color, boolean shadow) {
        this.text = text;
        this.color = color;
        this.shadow = shadow;
    }

    public void setAlignment(Alignment alignment) {
        this.alignment = alignment;
    }

    @Override
    public int getWidth() {
        return textRenderer.getWidth(text);
    }

    @Override
    public int getHeight() {
        return 9;
    }

    public int getXPos(int xOffset) {
        return switch (alignment) {
            case LEFT -> xOffset;
            case RIGHT -> xOffset - getWidth();
            case CENTER -> xOffset - getWidth() / 2;
        };
    }

    @Override
    public void draw(DrawContext guiGraphics, int xOffset, int yOffset) {
        var x = getXPos(xOffset);
        
        guiGraphics.drawText(textRenderer, text, x, yOffset, color, shadow);
    }

    public enum Alignment {
        LEFT, RIGHT, CENTER
    }
}
