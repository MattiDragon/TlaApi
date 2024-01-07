package io.github.mattidragon.tlaapi.impl.emi;

import dev.emi.emi.api.recipe.EmiRecipe;
import dev.emi.emi.api.recipe.EmiRecipeCategory;
import dev.emi.emi.api.recipe.EmiRecipeSorting;
import io.github.mattidragon.tlaapi.api.recipe.TlaCategory;
import net.minecraft.text.Text;

import java.util.Comparator;

public class TlaEmiRecipeCategory extends EmiRecipeCategory {
    private final TlaCategory category;

    public TlaEmiRecipeCategory(TlaCategory category) {
        super(category.getId(),
                EmiUtils.iconToRenderable(category.getIcon()),
                EmiUtils.iconToRenderable(category.getSimpleIcon()),
                getComparator(category));
        this.category = category;
    }

    private static Comparator<EmiRecipe> getComparator(TlaCategory category) {
        if (category.getRecipeComparator() == null) return EmiRecipeSorting.none();
        return Comparator.comparing(recipe -> {
            if (recipe instanceof TlaEmiRecipe tlaRecipe) {
                return tlaRecipe.getUnderlying();
            }
            return null;
        }, Comparator.nullsLast(category.getRecipeComparator()));
    }

    @Override
    public Text getName() {
        return category.getName();
    }
}
