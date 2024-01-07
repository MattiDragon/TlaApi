package io.github.mattidragon.tlaapi.api.gui;

import io.github.mattidragon.tlaapi.impl.PluginsExtend;
import net.minecraft.text.Text;

import java.util.Arrays;
import java.util.List;

/**
 * Provides configuration options available for all widgets.
 * @see GuiBuilder
 */
@PluginsExtend
public interface WidgetConfig {
    /**
     * Adds a tooltip to the widget.
     * @apiNote The behaviour of this method is undefined if the bounds of the widget change after calling.
     * Sometimes the tooltip may follow the widget, sometimes it may not.
     * @see #addTooltip(Text...)
     */
    WidgetConfig addTooltip(List<Text> tooltip);

    /**
     * Adds a tooltip to the widget.
     * @apiNote The behaviour of this method is undefined if the bounds of the widget change after calling.
     * Sometimes the tooltip may follow the widget, sometimes it may not.
     * @see #addTooltip(List)
     */
    default WidgetConfig addTooltip(Text... tooltip) {
        return addTooltip(Arrays.asList(tooltip));
    }

    /**
     * Provides the current bounds of the widget.
     * Some actions on widgets may change their bounds, such as changing alignment on a text widget.
     */
    TlaBounds getBounds();
}
