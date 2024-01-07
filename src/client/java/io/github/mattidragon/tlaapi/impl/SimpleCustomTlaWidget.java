package io.github.mattidragon.tlaapi.impl;

import io.github.mattidragon.tlaapi.api.gui.CustomTlaWidget;
import io.github.mattidragon.tlaapi.api.gui.TlaBounds;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.Drawable;

public class SimpleCustomTlaWidget implements CustomTlaWidget {
    private final Drawable widget;
    private final TlaBounds bounds;

    public SimpleCustomTlaWidget(Drawable widget, TlaBounds bounds) {
        this.widget = widget;
        this.bounds = bounds;
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        widget.render(context, mouseX, mouseY, delta);
    }

    @Override
    public TlaBounds getBounds() {
        return bounds;
    }
}
