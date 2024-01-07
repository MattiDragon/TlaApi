package io.github.mattidragon.tlaapi.impl.rei.util;

import io.github.mattidragon.tlaapi.api.gui.CustomTlaWidget;
import me.shedaniel.math.Rectangle;
import me.shedaniel.rei.api.client.gui.widgets.WidgetWithBounds;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.Element;

import java.util.List;

public class ReiCustomWidget extends WidgetWithBounds {
    private final CustomTlaWidget widget;
    private final Rectangle recipeBounds;

    public ReiCustomWidget(CustomTlaWidget widget, Rectangle recipeBounds) {
        this.widget = widget;
        this.recipeBounds = recipeBounds;
    }

    @Override
    public Rectangle getBounds() {
        var widgetBounds = widget.getBounds();
        return new Rectangle(recipeBounds.x + widgetBounds.x(), recipeBounds.y + widgetBounds.y(), widgetBounds.width(), widgetBounds.height());
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        var matrices = context.getMatrices();
        matrices.push();
        matrices.translate(getBounds().x, getBounds().y, 0);
        widget.render(context, mouseX, mouseY, delta);
        matrices.pop();
    }

    @Override
    public List<? extends Element> children() {
        return List.of();
    }
}
