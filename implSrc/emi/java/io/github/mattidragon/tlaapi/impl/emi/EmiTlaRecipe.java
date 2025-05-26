package io.github.mattidragon.tlaapi.impl.emi;

import java.util.List;
import java.util.function.Function;
import java.util.function.Supplier;

import org.jetbrains.annotations.Nullable;

import com.google.common.base.Suppliers;

import dev.emi.emi.api.recipe.EmiRecipe;
import dev.emi.emi.api.recipe.EmiRecipeCategory;
import io.github.mattidragon.tlaapi.api.gui.GuiBuilder;
import io.github.mattidragon.tlaapi.api.recipe.TlaCategory;
import io.github.mattidragon.tlaapi.api.recipe.TlaIngredient;
import io.github.mattidragon.tlaapi.api.recipe.TlaRecipe;
import io.github.mattidragon.tlaapi.api.recipe.TlaStack;
import net.minecraft.util.Identifier;

final class EmiTlaRecipe implements TlaRecipe {

    static TlaRecipe of(Function<EmiRecipeCategory, TlaCategory> context, EmiRecipe recipe) {
        if (recipe instanceof TlaEmiRecipe r) {
            return r.getUnderlying();
        }
        return new EmiTlaRecipe(context, recipe);
    }

    private final EmiRecipe recipe;

    private final Supplier<List<TlaIngredient>> inputs;
    private final Supplier<List<TlaStack>> outputs;
    private final Supplier<List<TlaIngredient>> catalysts;
    private final TlaCategory category;

    private EmiTlaRecipe(Function<EmiRecipeCategory, TlaCategory> context, EmiRecipe recipe) {
        this.recipe = recipe;
        this.inputs = Suppliers.memoize(() -> recipe.getInputs().stream().map(EmiUtils::convertIngredient).toList());
        this.outputs = Suppliers.memoize(() -> recipe.getOutputs().stream().map(EmiUtils::convertStack).toList());
        this.catalysts = Suppliers.memoize(() -> recipe.getCatalysts().stream().map(EmiUtils::convertIngredient).toList());
        this.category = context.apply(recipe.getCategory());
    }

    @Override
    public TlaCategory getCategory() {
        return category;
    }

    @Override
    public @Nullable Identifier getId() {
        return recipe.getId();
    }

    @Override
    public List<TlaIngredient> getInputs() {
        return inputs.get();
    }

    @Override
    public List<TlaStack> getOutputs() {
        return outputs.get();
    }

    @Override
    public List<TlaIngredient> getCatalysts() {
        return catalysts.get();
    }

    @Override
    public void buildGui(GuiBuilder builder) {
        // not supported. This is just for comparison
    }

    @Override
    public boolean equals(Object o) {
        return o == this
            || (o instanceof EmiTlaRecipe wrapped && wrapped.recipe.equals(recipe))
            || (o instanceof EmiRecipe emiRecipe && emiRecipe.equals(recipe));
    }

    @Override
    public int hashCode() {
        return recipe.hashCode();
    }
}
