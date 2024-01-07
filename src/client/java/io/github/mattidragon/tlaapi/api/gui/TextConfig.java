package io.github.mattidragon.tlaapi.api.gui;

import io.github.mattidragon.tlaapi.impl.PluginsExtend;
import net.minecraft.text.Text;

import java.util.Arrays;
import java.util.List;

/**
 * Provides configuration options for text widgets.
 * @see GuiBuilder#addText
 * @apiNote Currently, this class only provides horizontal alignment options in addition to the default widget configuration options.
 * This is due to poor configuration options for text widget in EMI. In the future more options may be added.
 */
@SuppressWarnings("UnusedReturnValue")
@PluginsExtend
public interface TextConfig extends WidgetConfig {
    /**
     * Aligns the text to the left, meaning that the left edge of the text will be at the widgets position.
     * Note that changing alignment changes the bounding box of the widget. This is the default alignment.
     */
    TextConfig alignLeft();

    /**
     * Aligns the text to the right, meaning that the right edge of the text will be at the widgets position.
     * Note that changing alignment changes the bounding box of the widget.
     */
    TextConfig alignRight();

    /**
     * Aligns the text to the center, meaning that the center of the text will be at the widgets position.
     * Note that changing alignment changes the bounding box of the widget.
     */
    TextConfig alignCenter();

    // Overrides to change return type

    @Override
    TextConfig addTooltip(List<Text> tooltip);

    @Override
    default TextConfig addTooltip(Text... tooltip) {
        return addTooltip(Arrays.asList(tooltip));
    }
}
