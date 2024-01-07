package io.github.mattidragon.tlaapi.impl.emi;

import dev.emi.emi.api.recipe.EmiRecipe;
import dev.emi.emi.api.recipe.EmiRecipeCategory;
import dev.emi.emi.api.stack.EmiIngredient;
import dev.emi.emi.api.stack.EmiStack;
import dev.emi.emi.api.widget.WidgetHolder;
import io.github.mattidragon.tlaapi.api.recipe.TlaRecipe;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class TlaEmiRecipe implements EmiRecipe {
    private final TlaRecipe recipe;
    private final EmiRecipeCategory category;

    public TlaEmiRecipe(TlaRecipe recipe, EmiRecipeCategory category) {
        this.recipe = recipe;
        this.category = category;
    }

    public TlaRecipe getUnderlying() {
        return recipe;
    }

    @Override
    public EmiRecipeCategory getCategory() {
        return category;
    }

    @Override
    public @Nullable Identifier getId() {
        return recipe.getId();
    }

    @Override
    public List<EmiIngredient> getInputs() {
        return recipe.getInputs().stream().map(EmiUtils::convertIngredient).toList();
    }

    @Override
    public List<EmiIngredient> getCatalysts() {
        return recipe.getCatalysts().stream().map(EmiUtils::convertIngredient).toList();
    }

    @Override
    public List<EmiStack> getOutputs() {
        return recipe.getOutputs().stream().map(EmiUtils::convertStack).toList();
    }

    @Override
    public int getDisplayWidth() {
        return recipe.getCategory().getDisplayWidth();
    }

    @Override
    public int getDisplayHeight() {
        return recipe.getCategory().getDisplayHeight();
    }

    @Override
    public void addWidgets(WidgetHolder widgets) {
        recipe.buildGui(new EmiGuiBuilder(this, widgets));
    }
}
