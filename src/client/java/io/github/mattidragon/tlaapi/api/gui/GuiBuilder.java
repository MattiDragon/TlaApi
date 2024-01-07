package io.github.mattidragon.tlaapi.api.gui;

import io.github.mattidragon.tlaapi.api.recipe.TlaIngredient;
import io.github.mattidragon.tlaapi.api.recipe.TlaStack;
import io.github.mattidragon.tlaapi.impl.ImplementationsExtend;
import io.github.mattidragon.tlaapi.impl.SimpleCustomTlaWidget;
import net.minecraft.client.gui.Drawable;
import net.minecraft.text.Text;

import java.util.Arrays;
import java.util.List;

/**
 * Recipe viewer agnostic GUI builder.
 * Provides many common widgets using the defaults available in the current recipe viewer if possible,
 * as well as the ability to add custom widgets.
 * Built-in widgets should be preferred when available as they will match the current recipe viewer's style and theme.
 */
@ImplementationsExtend
@SuppressWarnings({"unused", "UnusedReturnValue"})
public interface GuiBuilder {
    /**
     * Adds a slot displaying the passed ingredient to the GUI. Make sure to configure the slot type with the returned config object.
     * @see SlotConfig
     * @see #addSlot(TlaStack, int, int)
     */
    SlotConfig addSlot(TlaIngredient ingredient, int x, int y);

    /**
     * Adds a slot displaying the passed stack to the GUI. Make sure to configure the slot type with the returned config object.
     * @see SlotConfig
     * @see #addSlot(TlaIngredient, int, int)
     */
    default SlotConfig addSlot(TlaStack stack, int x, int y) {
        return addSlot(TlaIngredient.ofStacks(stack), x, y);
    }

    /**
     * Adds a simple texture to the GUI.
     * @see TextureConfig
     * @see #addArrow
     * @see #addAnimatedArrow
     * @see #addFlame
     * @see #addAnimatedFlame
     */
    WidgetConfig addTexture(TextureConfig config, int x, int y);

    /**
     * Utility for adding an arrow texture to the GUI.
     * Should have dimensions of around 24x17, can vary by a few pixels.
     * @see #addTexture
     * @see #addAnimatedArrow
     */
    WidgetConfig addArrow(int x, int y, boolean full);
    /**
     * Utility for adding an animated arrow texture to the GUI.
     * Should have dimensions of around 24x17, can vary by a few pixels.
     * @param duration The duration of the animation in milliseconds.
     * @see #addTexture
     * @see #addArrow
     */
    WidgetConfig addAnimatedArrow(int x, int y, int duration);

    /**
     * Utility for adding a flame texture to the GUI.
     * Should have dimensions of around 14x14, can vary by a few pixels.
     * @see #addTexture
     * @see #addAnimatedFlame
     */
    WidgetConfig addFlame(int x, int y);
    /**
     * Utility for adding an animated flame texture to the GUI.
     * Should have dimensions of around 14x14, can vary by a few pixels.
     * @param duration The duration of the animation in milliseconds.
     * @see #addTexture
     * @see #addFlame
     */
    WidgetConfig addAnimatedFlame(int x, int y, int duration);

    /**
     * Adds a text to the GUI using the recipe viewer's default text color.
     * The text alignment can be tweaked in the returned config object.
     * @see TextConfig
     * @see #addText(Text, int, int, int, boolean)
     */
    default TextConfig addText(Text text, int x, int y, boolean shadow) {
        return addText(text, x, y, 0xffffffff, shadow);
    }

    /**
     * Adds a text to the GUI using a custom color.
     * The text alignment can be tweaked in the returned config object.
     * @see TextConfig
     * @see #addText(Text, int, int, boolean)
     */
    TextConfig addText(Text text, int x, int y, int color, boolean shadow);

    /**
     * Adds a custom widget to the GUI. Primarily used for adding widgets that are not available.
     * @see CustomTlaWidget
     * @see #addCustomWidget(int, int, int, int, Drawable)
     */
    WidgetConfig addCustomWidget(CustomTlaWidget widget);

    /**
     * A helper to easily add a widget using a drawable.
     * The drawable can be used as a lambda for inline widgets, or you can pass in a vanilla widget.
     * @see CustomTlaWidget
     * @see #addCustomWidget(CustomTlaWidget)
     */
    default WidgetConfig addCustomWidget(int x, int y, int width, int height, Drawable widget) {
        return addCustomWidget(new SimpleCustomTlaWidget(widget, new TlaBounds(x, y, width, height)));
    }

    /**
     * Adds a tooltip to the GUI. The tooltip will be displayed when the mouse is hovered over the specified area.
     * You should prefer to add tooltips to widgets directly using their config objects if possible.
     * @see WidgetConfig#addTooltip(List)
     * @see WidgetConfig#addTooltip(Text...)
     * @see #addTooltip(int, int, int, int, List)
     */
    void addTooltip(int x, int y, int width, int height, List<Text> tooltip);

    /**
     * Adds a tooltip to the GUI. The tooltip will be displayed when the mouse is hovered over the specified area.
     * You should prefer to add tooltips to widgets directly using their config objects if possible.
     * @see WidgetConfig#addTooltip(List)
     * @see WidgetConfig#addTooltip(Text...)
     * @see #addTooltip(int, int, int, int, List)
     */
    default void addTooltip(int x, int y, int width, int height, Text... tooltip) {
        addTooltip(x, y, width, height, Arrays.asList(tooltip));
    }

    /**
     * Gets the bounds of the GUI. Primarily useful for finding center points and such.
     * {@code x} and {@code y} will always be 0. The width and height will be taken from the category of the recipe.
     */
    TlaBounds getBounds();
}
