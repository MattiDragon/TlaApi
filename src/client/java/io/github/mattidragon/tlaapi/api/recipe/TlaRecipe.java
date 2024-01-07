package io.github.mattidragon.tlaapi.api.recipe;

import io.github.mattidragon.tlaapi.api.gui.GuiBuilder;
import io.github.mattidragon.tlaapi.impl.PluginsExtend;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;

import java.util.List;

/**
 * Represents a single entry in a category.
 * Usually a recipe represents a recipe in minecrafts recipe system,
 * but a recipe can represent anything.
 */
@PluginsExtend
public interface TlaRecipe {
    /**
     * Provides the category this recipe belongs to.
     */
    TlaCategory getCategory();

    /**
     * Provides the identifier of this recipe.
     * This may return {@code null} if the recipe doesn't have an identifier and one can't be generated,
     * but that will result in certain features not working properly.
     * For recipes that aren't vanilla recipes it's recommended to provide an identifier with a single slash at the start of the path to distinguish it.
     * @implNote Only used by the EMI implementation.
     */
    @Nullable Identifier getId();

    /**
     * Returns all the input ingredients of this recipe.
     */
    List<TlaIngredient> getInputs();

    /**
     * Returns all the output stacks of this recipe.
     */
    List<TlaStack> getOutputs();

    /**
     * Returns all the catalysts of this recipe.
     * Catalysts are ingredients that aren't consumed by the recipe but still need to be present.
     * Usually corresponds to the catalyst slot in the recipe gui.
     * @implNote Only used by the EMI implementation.
     */
    List<TlaIngredient> getCatalysts();

    /**
     * Builds the gui for this recipe.
     * @see GuiBuilder
     */
    void buildGui(GuiBuilder builder);
}
