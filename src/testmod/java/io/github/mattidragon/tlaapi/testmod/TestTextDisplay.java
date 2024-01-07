package io.github.mattidragon.tlaapi.testmod;

import io.github.mattidragon.tlaapi.api.gui.GuiBuilder;
import io.github.mattidragon.tlaapi.api.recipe.TlaCategory;
import io.github.mattidragon.tlaapi.api.recipe.TlaIngredient;
import io.github.mattidragon.tlaapi.api.recipe.TlaRecipe;
import io.github.mattidragon.tlaapi.api.recipe.TlaStack;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class TestTextDisplay implements TlaRecipe {
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
        return List.of();
    }

    @Override
    public List<TlaStack> getOutputs() {
        return List.of();
    }

    @Override
    public List<TlaIngredient> getCatalysts() {
        return List.of();
    }

    @Override
    public void buildGui(GuiBuilder builder) {
        var center = builder.getBounds().centerX();
        builder.addText(Text.literal("default"), center, 0, false);
        builder.addText(Text.literal("left"), center, 9, false).alignLeft();
        builder.addText(Text.literal("center"), center, 18, false).alignCenter().addTooltip(Text.literal("Text tooltip"));
        builder.addText(Text.literal("right"), center, 27, false).alignRight();
    }
}
