package io.github.mattidragon.tlaapi.impl.emi;

import dev.emi.emi.api.render.EmiTexture;
import dev.emi.emi.api.widget.SlotWidget;
import dev.emi.emi.api.widget.TextWidget;
import dev.emi.emi.api.widget.Widget;
import dev.emi.emi.api.widget.WidgetHolder;
import io.github.mattidragon.tlaapi.api.gui.*;
import io.github.mattidragon.tlaapi.api.recipe.TlaIngredient;
import net.minecraft.text.Text;

import java.util.List;

public class EmiGuiBuilder implements GuiBuilder {
    private final TlaEmiRecipe recipe;
    private final WidgetHolder widgetHolder;

    public EmiGuiBuilder(TlaEmiRecipe recipe, WidgetHolder widgetHolder) {
        this.recipe = recipe;
        this.widgetHolder = widgetHolder;
    }

    @Override
    public SlotConfig addSlot(TlaIngredient ingredient, int x, int y) {
        return new EmiSlotConfig(widgetHolder.addSlot(EmiUtils.convertIngredient(ingredient), x, y));
    }

    @Override
    public WidgetConfig addTexture(TextureConfig config, int x, int y) {
        var widget = widgetHolder.addTexture(config.texture(),
                x, y,
                config.width(), config.height(),
                config.u(), config.v(),
                config.regionWidth(), config.regionHeight(),
                config.textureWidth(), config.textureHeight());
        return new EmiWidgetConfig<>(widget);
    }

    @Override
    public WidgetConfig addArrow(int x, int y, boolean full) {
        var widget = widgetHolder.addTexture(full ? EmiTexture.FULL_ARROW : EmiTexture.EMPTY_ARROW, x, y);
        return new EmiWidgetConfig<>(widget);
    }

    @Override
    public WidgetConfig addAnimatedArrow(int x, int y, int duration) {
        var widget = widgetHolder.addFillingArrow(x, y, duration);
        return new EmiWidgetConfig<>(widget);
    }

    @Override
    public WidgetConfig addFlame(int x, int y) {
        var widget = widgetHolder.addTexture(EmiTexture.FULL_FLAME, x, y);
        return new EmiWidgetConfig<>(widget);
    }

    @Override
    public WidgetConfig addAnimatedFlame(int x, int y, int duration) {
        widgetHolder.addTexture(EmiTexture.EMPTY_FLAME, x, y);
        widgetHolder.addAnimatedTexture(EmiTexture.FULL_FLAME, x, y, duration, false, true, true);
        return null;
    }

    @Override
    public TextConfig addText(Text text, int x, int y, int color, boolean shadow) {
        var widget = widgetHolder.addText(text, x, y, color, shadow);
        return new EmiTextConfig(widget);
    }

    @Override
    public WidgetConfig addCustomWidget(CustomTlaWidget widget) {
        var emiWidget = widgetHolder.add(new EmiCustomWidget(widget));
        return new EmiWidgetConfig<>(emiWidget);
    }

    @Override
    public void addTooltip(int x, int y, int width, int height, List<Text> tooltip) {
        widgetHolder.addTooltipText(tooltip, x, y, width, height);
    }

    @Override
    public TlaBounds getBounds() {
        return new TlaBounds(0, 0, widgetHolder.getWidth(), widgetHolder.getHeight());
    }

    private class EmiWidgetConfig<T extends EmiWidgetConfig<T>> implements WidgetConfig {
        private final Widget widget;

        private EmiWidgetConfig(Widget widget) {
            this.widget = widget;
        }

        @Override
        public T addTooltip(List<Text> tooltip) {
            var bounds = widget.getBounds();
            widgetHolder.addTooltipText(tooltip, bounds.x(), bounds.y(), bounds.width(), bounds.height());
            return getThis();
        }

        @Override
        public TlaBounds getBounds() {
            var emiBounds = widget.getBounds();
            return new TlaBounds(emiBounds.x(), emiBounds.y(), emiBounds.width(), emiBounds.height());
        }

        @SuppressWarnings("unchecked")
        protected T getThis() {
            return (T) this;
        }
    }

    private class EmiTextConfig extends EmiWidgetConfig<EmiTextConfig> implements TextConfig {
        private final TextWidget widget;

        public EmiTextConfig(TextWidget widget) {
            super(widget);
            this.widget = widget;
        }

        @Override
        public TextConfig alignLeft() {
            widget.horizontalAlign(TextWidget.Alignment.START);
            return this;
        }

        @Override
        public TextConfig alignRight() {
            widget.horizontalAlign(TextWidget.Alignment.END);
            return this;
        }

        @Override
        public TextConfig alignCenter() {
            widget.horizontalAlign(TextWidget.Alignment.CENTER);
            return this;
        }
    }

    private class EmiSlotConfig extends EmiWidgetConfig<EmiSlotConfig> implements SlotConfig {
        private final SlotWidget widget;

        private EmiSlotConfig(SlotWidget widget) {
            super(widget);
            this.widget = widget;
        }

        @Override
        public SlotConfig markOutput() {
            widget.recipeContext(recipe);
            return this;
        }

        @Override
        public SlotConfig markInput() {
            return this;
        }

        @Override
        public SlotConfig markCatalyst() {
            widget.catalyst(true);
            return this;
        }

        @Override
        public SlotConfig disableBackground() {
            widget.drawBack(false);
            return this;
        }

        @Override
        public SlotConfig makeLarge() {
            widget.large(true);
            return this;
        }

        @Override
        public EmiSlotConfig addTooltip(List<Text> tooltip) {
            tooltip.forEach(widget::appendTooltip);
            return this;
        }
    }
}
