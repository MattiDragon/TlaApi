package io.github.mattidragon.tlaapi.testmod;

import io.github.mattidragon.tlaapi.api.gui.GuiBuilder;
import io.github.mattidragon.tlaapi.api.recipe.TlaCategory;
import io.github.mattidragon.tlaapi.api.recipe.TlaIngredient;
import io.github.mattidragon.tlaapi.api.recipe.TlaRecipe;
import io.github.mattidragon.tlaapi.api.recipe.TlaStack;
import net.minecraft.item.Items;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class TestCustomDisplay implements TlaRecipe {
    @Override
    public TlaCategory getCategory() {
        return TestPlugin.TEST_CATEGORY;
    }

    @Override
    public @Nullable Identifier getId() {
        return null;
    }

    @Override
    public List<TlaIngredient> getInputs() {
        return List.of(TlaIngredient.ofStacks(TlaStack.of(Items.DIAMOND).withAmount(4)));
    }

    @Override
    public List<TlaStack> getOutputs() {
        return List.of(TlaStack.of(Items.EMERALD).withAmount(3));
    }

    @Override
    public List<TlaIngredient> getCatalysts() {
        return List.of(TlaIngredient.ofStacks(TlaStack.of(Items.NETHER_STAR)));
    }

    @Override
    public void buildGui(GuiBuilder builder) {
        builder.addSlot(getInputs().get(0), 0, 0).markInput();
        builder.addSlot(getOutputs().get(0), 54, 0).markOutput();
        builder.addAnimatedArrow(24, 0, 1000).addTooltip(Text.literal("1s"));
        builder.addSlot(getCatalysts().get(0), 27, 20).markCatalyst();
        builder.addFlame(0, 18);
        builder.addAnimatedFlame(54, 18, 1000);
    }
}
