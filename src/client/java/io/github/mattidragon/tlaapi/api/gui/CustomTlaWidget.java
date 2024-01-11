package io.github.mattidragon.tlaapi.api.gui;

import io.github.mattidragon.tlaapi.impl.PluginsExtend;
import net.minecraft.client.gui.Drawable;

/**
 * A custom widget that can be added to a GUI.
 * Useful for any applications where the built-in widgets are not sufficient.
 */
@PluginsExtend
public interface CustomTlaWidget extends Drawable {
    /**
     * Returns the bounds of this widget.
     * Used for tooltips among other things.
     */
    TlaBounds getBounds();

    /**
     * Called once when the widget is added to the GUI
     * and every time the theme changes.
     * Override this is you want your widget to change appearance based on the theme.
     */
    default void setTheme(boolean dark) {
    }
}
