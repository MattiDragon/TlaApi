package io.github.mattidragon.tlaapi.impl.emi;

import dev.emi.emi.api.widget.Bounds;
import dev.emi.emi.api.widget.Widget;
import io.github.mattidragon.tlaapi.api.gui.CustomTlaWidget;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.util.math.MatrixStack;

public class EmiCustomWidget extends Widget {
    private final CustomTlaWidget widget;

    public EmiCustomWidget(CustomTlaWidget widget) {
        this.widget = widget;
    }

    @Override
    public Bounds getBounds() {
        var tlaBounds = widget.getBounds();
        return new Bounds(tlaBounds.x(), tlaBounds.y(), tlaBounds.width(), tlaBounds.height());
    }

    @Override
    public void render(DrawContext draw, int mouseX, int mouseY, float delta) {
        var matrices = draw.getMatrices();
        matrices.push();
        matrices.translate(getBounds().x(), getBounds().y(), 0);
        widget.render(draw, mouseX, mouseY, delta);
        matrices.pop();
    }
}
