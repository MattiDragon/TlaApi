package io.github.mattidragon.tlaapi.api.gui;

import io.github.mattidragon.tlaapi.api.recipe.TlaIngredient;
import io.github.mattidragon.tlaapi.impl.PluginsExtend;
import net.minecraft.text.Text;

import java.util.Arrays;
import java.util.List;

/**
 * Configuration options for a slot widget in a recipe gui.
 * You should almost always set a slot type with {@link #markInput()}, {@link #markOutput()}, or {@link #markCatalyst()} so that recipe viewers can display the slot correctly.
 * Some options might not do anything in some recipe viewers. For example {@link #markInput()} doesn't do anything on EMI as slots default to being inputs.
 * @see GuiBuilder#addSlot(TlaIngredient, int, int)
 */
@SuppressWarnings("UnusedReturnValue")
@PluginsExtend
public interface SlotConfig extends WidgetConfig {
    /**
     * Marks a slot as an output slot. This is important for certain features of EMI to function properly.
     */
    SlotConfig markOutput();

    /**
     * Marks a slot as an input slot. This does something on REI. Not sure what.
     */
    SlotConfig markInput();

    /**
     * Marks a slot as a catalyst slot. This adds a catalyst indicator on EMI and does nothing for REI.
     * It's safe to not call this method if you are sure you don't want a catalyst indicator.
     */
    SlotConfig markCatalyst();

    /**
     * Disables the background of a slot. This is usually paired with a custom background texture of some sort.
     */
    SlotConfig disableBackground();

    /**
     * Makes a slot large, increasing its size from the default 18x18 to 26x26.
     * The top left corner of the slot will remain at the same position so positioning code will need to be tweaked.
     */
    SlotConfig makeLarge();

    // Overrides to change return type

    @Override
    SlotConfig addTooltip(List<Text> tooltip);

    @Override
    default SlotConfig addTooltip(Text... tooltip) {
        return addTooltip(Arrays.asList(tooltip));
    }
}
