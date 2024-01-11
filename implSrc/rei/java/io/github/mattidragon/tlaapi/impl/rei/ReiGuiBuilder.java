package io.github.mattidragon.tlaapi.impl.rei;

import io.github.mattidragon.tlaapi.api.gui.*;
import io.github.mattidragon.tlaapi.api.recipe.TlaIngredient;
import io.github.mattidragon.tlaapi.impl.rei.util.AnimatedTextureWidget;
import io.github.mattidragon.tlaapi.impl.rei.util.ReiCustomWidget;
import io.github.mattidragon.tlaapi.impl.rei.util.TextureWidget;
import io.github.mattidragon.tlaapi.impl.rei.util.TlaWidgets;
import me.shedaniel.math.Dimension;
import me.shedaniel.math.Point;
import me.shedaniel.math.Rectangle;
import me.shedaniel.rei.api.client.gui.widgets.*;
import net.minecraft.text.Text;

import java.util.ArrayList;
import java.util.List;
import java.util.function.DoubleSupplier;

public class ReiGuiBuilder implements GuiBuilder {
    private final List<Widget> widgets = new ArrayList<>();
    private final Rectangle bounds;

    public ReiGuiBuilder(Rectangle bounds) {
        this.bounds = bounds.clone();
        this.bounds.grow(-5, -5);
        widgets.add(Widgets.createRecipeBase(bounds));
    }

    @Override
    public SlotConfig addSlot(TlaIngredient ingredient, int x, int y) {
        var widget = Widgets.createSlot(getPoint(x + 1, y + 1));
        widget.entries(ReiUtil.convertIngredient(ingredient));
        widgets.add(widget);
        return new ReiSlotConfig(widget);
    }

    @Override
    public WidgetConfig addTexture(TextureConfig config, int x, int y) {
        var widget = new TextureWidget(x + bounds.x, y + bounds.y, config);
        widgets.add(widget);
        return new ReiWidgetConfig<>(widget);
    }

    @Override
    public WidgetConfig addAnimatedTexture(TextureConfig config, int x, int y, int duration, boolean horizontal, boolean endToStart, boolean fullToEmpty) {
        var widget = new AnimatedTextureWidget.Builder(x + bounds.x, y + bounds.y, config.width(), config.height())
                .animationDuration(duration)
                .texture(config.darkTexture(), config.lightTexture())
                .flags(horizontal, endToStart, fullToEmpty)
                .uv(config.u(), config.v())
                .regionSize(config.regionWidth(), config.regionHeight())
                .textureSize(config.textureWidth(), config.textureHeight())
                .build();
        widgets.add(widget);
        return new ReiWidgetConfig<>(widget);
    }

    @Override
    public WidgetConfig addProgressingTexture(TextureConfig config, int x, int y, DoubleSupplier progress, boolean horizontal, boolean endToStart, boolean fullToEmpty) {
        var widget = new AnimatedTextureWidget.Builder(x + bounds.x, y + bounds.y, config.width(), config.height())
                .progress(progress)
                .texture(config.darkTexture(), config.lightTexture())
                .flags(horizontal, endToStart, fullToEmpty)
                .uv(config.u(), config.v())
                .regionSize(config.regionWidth(), config.regionHeight())
                .textureSize(config.textureWidth(), config.textureHeight())
                .build();
        widgets.add(widget);
        return new ReiWidgetConfig<>(widget);
    }

    @Override
    public WidgetConfig addArrow(int x, int y, boolean full) {
        var widget = TlaWidgets.reiTexture(getPoint(x, y), full ? 82 : 106, 91, 24, 17);
        widgets.add(widget);
        return new ReiWidgetConfig<>(widget);
    }

    @Override
    public WidgetConfig addAnimatedArrow(int x, int y, int duration) {
        var widget = Widgets.createArrow(getPoint(x, y)).animationDurationMS(duration);
        widgets.add(widget);
        return new ReiWidgetConfig<>(widget);
    }

    @Override
    public WidgetConfig addFlame(int x, int y) {
        var widget = TlaWidgets.reiTexture(getPoint(x, y), 82, 77, 14, 14);
        widgets.add(widget);
        return new ReiWidgetConfig<>(widget);
    }

    @Override
    public WidgetConfig addAnimatedFlame(int x, int y, int duration) {
        widgets.add(TlaWidgets.reiTexture(getPoint(x, y), 1, 74, 14, 14));
        var widget = TlaWidgets.animatedFlame(x + bounds.x, y + bounds.y, 1000);
        widgets.add(widget);
        return new ReiWidgetConfig<>(widget);
    }

    @Override
    public TextConfig addText(Text text, int x, int y, boolean shadow) {
        var label = Widgets.createLabel(getPoint(x, y), text);
        widgets.add(label.shadow(shadow));
        return new ReiTextConfig(label);
    }

    @Override
    public TextConfig addText(Text text, int x, int y, int color, boolean shadow) {
        var label = Widgets.createLabel(getPoint(x, y), text);
        widgets.add(label.shadow(shadow).color(color));
        return new ReiTextConfig(label);
    }

    @Override
    public WidgetConfig addCustomWidget(CustomTlaWidget widget) {
        var reiWidget = new ReiCustomWidget(widget, bounds);
        widgets.add(reiWidget);
        return new ReiWidgetConfig<>(reiWidget);
    }

    @Override
    public void addTooltip(int x, int y, int width, int height, List<Text> tooltip) {
        widgets.add(Widgets.createTooltip(new Rectangle(getPoint(x, y), new Dimension(width, height)), tooltip));
    }

    @Override
    public TlaBounds getBounds() {
        return new TlaBounds(0, 0, bounds.width, bounds.height);
    }

    public List<Widget> getWidgets() {
        return widgets;
    }

    private Point getPoint(int x, int y) {
        return new Point(bounds.x + x, bounds.y + y);
    }

    private class ReiWidgetConfig<T extends ReiWidgetConfig<T>> implements WidgetConfig {
        private final WidgetWithBounds widget;

        private ReiWidgetConfig(WidgetWithBounds widget) {
            this.widget = widget;
        }

        @Override
        public T addTooltip(List<Text> tooltip) {
            widgets.add(Widgets.createTooltip(widget::getBounds, tooltip));
            return getThis();
        }

        @Override
        public TlaBounds getBounds() {
            var reiBounds = widget.getBounds();
            return new TlaBounds(reiBounds.x + bounds.x, reiBounds.y + bounds.y, reiBounds.width, reiBounds.height);
        }

        @SuppressWarnings("unchecked")
        protected T getThis() {
            return (T) this;
        }
    }

    private class ReiTextConfig extends ReiWidgetConfig<ReiTextConfig> implements TextConfig {
        private final Label label;

        private ReiTextConfig(Label label) {
            super(label);
            this.label = label;
            alignLeft();
        }

        @Override
        public TextConfig alignLeft() {
            label.leftAligned();
            return this;
        }

        @Override
        public TextConfig alignRight() {
            label.rightAligned();
            return this;
        }

        @Override
        public TextConfig alignCenter() {
            label.centered();
            return this;
        }

        @Override
        public ReiTextConfig addTooltip(List<Text> tooltip) {
            label.setTooltip(tooltip.toArray(Text[]::new));
            return this;
        }
    }

    private class ReiSlotConfig extends ReiWidgetConfig<ReiSlotConfig> implements SlotConfig {
        private final Slot widget;

        public ReiSlotConfig(Slot widget) {
            super(widget);
            this.widget = widget;
        }

        @Override
        public SlotConfig markOutput() {
            widget.markOutput();
            return this;
        }

        @Override
        public SlotConfig markInput() {
            widget.markInput();
            return this;
        }

        @Override
        public SlotConfig markCatalyst() {
            return this;
        }

        @Override
        public SlotConfig disableBackground() {
            widget.disableBackground();
            return this;
        }

        @Override
        public SlotConfig makeLarge() {
            disableBackground();
            widget.getBounds().translate(4, 4);
            var backgroundLocation = widget.getBounds().getLocation();
            backgroundLocation.translate(1, 1);
            widgets.add(1, Widgets.createResultSlotBackground(backgroundLocation));
            return this;
        }

        @Override
        public ReiSlotConfig addTooltip(List<Text> tooltip) {
            widget.getEntries().forEach(entry -> entry.tooltip(tooltip));
            return this;
        }
    }
}
