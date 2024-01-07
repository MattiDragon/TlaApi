package io.github.mattidragon.tlaapi.api.recipe;

import io.github.mattidragon.tlaapi.impl.PluginsExtend;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;

import java.util.Comparator;

/**
 * Represents a recipe category. They appear as tabs in recipe viewers.
 */
@PluginsExtend
public interface TlaCategory {
    /**
     * Provides a unique identifier for this category.
     * The identifier should usually match the represented recipe type.
     */
    Identifier getId();

    /**
     * Provides the name of this category.
     * @apiNote The default implementation returns a translatable text component with the translation key
     * {@code tla.category.<namespace>.<path>}.
     */
    default Text getName() {
        return Text.translatable(getId().toTranslationKey("tla.category"));
    }

    /**
     * Provides the height of recipes in this category.
     * @implNote This method exists because REI requires categories to define the size of recipes.
     */
    int getDisplayHeight();

    /**
     * Provides the width of recipes in this category.
     * @implNote This method exists because REI requires categories to define the size of recipes.
     */
    int getDisplayWidth();

    /**
     * Returns the primary icon of this category. This icon is used in the recipe viewer tab.
     * It's recommended to use the primary crafting block for the recipe here.
     */
    CategoryIcon getIcon();

    /**
     * Returns a simplified icon for this category. This icon is used in the EMI recipe tree.
     * It should ideally be easy to differentiate from an item as it shows up next to them without separation in the tree.
     * It's recommended to use a 16x16 white on transparent line art of the primary crafting block for the recipe here.
     * You can look at the default icons in EMI for inspiration.
     */
    CategoryIcon getSimpleIcon();

    /**
     * Returns a comparator for ordering recipes in this category.
     * Returning null will fall back to the default ordering.
     * @implNote This is only respected by the EMI implementation as REI doesn't seem to support custom ordering.
     */
    default @Nullable Comparator<TlaRecipe> getRecipeComparator() {
        return null;
    }
}
