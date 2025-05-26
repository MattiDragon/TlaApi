package io.github.mattidragon.tlaapi.impl.rei;

import java.util.List;
import java.util.Objects;
import java.util.function.Function;
import java.util.function.Supplier;

import org.jetbrains.annotations.Nullable;

import com.google.common.base.Suppliers;

import io.github.mattidragon.tlaapi.api.gui.GuiBuilder;
import io.github.mattidragon.tlaapi.api.recipe.TlaCategory;
import io.github.mattidragon.tlaapi.api.recipe.TlaIngredient;
import io.github.mattidragon.tlaapi.api.recipe.TlaRecipe;
import io.github.mattidragon.tlaapi.api.recipe.TlaStack;
import me.shedaniel.rei.api.common.category.CategoryIdentifier;
import me.shedaniel.rei.api.common.display.Display;
import net.minecraft.util.Identifier;

class ReiTlaRecipe implements TlaRecipe {
    static TlaRecipe of(Function<CategoryIdentifier<?>, TlaCategory> context, Display display) {
        if (display instanceof TlaDisplay tla) {
            return tla.getRecipe();
        }
        return new ReiTlaRecipe(context, display);
    }

    private final Display display;
    private final TlaCategory category;
    private final Supplier<List<TlaIngredient>> inputs;
    private final Supplier<List<TlaStack>> outputs;

    private ReiTlaRecipe(Function<CategoryIdentifier<?>, TlaCategory> context, Display display) {
        this.display = display;
        this.category = context.apply(display.getCategoryIdentifier());
        this.inputs = Suppliers.memoize(() -> display.getInputEntries().stream().map(ReiUtil::convertIngredient).toList());
        this.outputs = Suppliers.memoize(() -> display.getOutputEntries().stream().flatMap(List::stream).map(ReiUtil::convertStack).toList());
    }

    @Override
    public TlaCategory getCategory() {
        return category;
    }

    @Override
    public @Nullable Identifier getId() {
        return display.getDisplayLocation().orElse(null);
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
        return List.of();
    }

    @Override
    public void buildGui(GuiBuilder builder) {
        // Not implemented. This is only for filtering
    }

    @Override
    public boolean equals(Object o) {
        return o == this
            || (o instanceof ReiTlaRecipe recipe && Objects.equals(display, recipe.display))
            || (o instanceof TlaDisplay display && this == display.getRecipe())
            || (o instanceof Display display && Objects.equals(this.display, display));
    }

    @Override
    public int hashCode() {
        return display.hashCode();
    }
}
