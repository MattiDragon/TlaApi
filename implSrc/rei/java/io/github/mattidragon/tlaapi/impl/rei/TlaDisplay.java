package io.github.mattidragon.tlaapi.impl.rei;

import io.github.mattidragon.tlaapi.api.recipe.TlaRecipe;
import me.shedaniel.rei.api.common.category.CategoryIdentifier;
import me.shedaniel.rei.api.common.display.Display;
import me.shedaniel.rei.api.common.entry.EntryIngredient;
import net.minecraft.util.Identifier;

import java.util.List;
import java.util.Optional;

public class TlaDisplay implements Display {
    private final CategoryIdentifier<?> category;
    private final TlaRecipe recipe;

    public TlaDisplay(CategoryIdentifier<?> category, TlaRecipe recipe) {
        this.category = category;
        this.recipe = recipe;
    }

    @Override
    public Optional<Identifier> getDisplayLocation() {
        return Optional.ofNullable(recipe.getId());
    }

    @Override
    public List<EntryIngredient> getInputEntries() {
        return recipe.getInputs().stream().map(ReiUtil::convertIngredient).toList();
    }

    @Override
    public List<EntryIngredient> getOutputEntries() {
        return recipe.getOutputs().stream().map(ReiUtil::convertStack).map(EntryIngredient::of).toList();
    }

    @Override
    public CategoryIdentifier<?> getCategoryIdentifier() {
        return category;
    }

    public TlaRecipe getRecipe() {
        return recipe;
    }
}
