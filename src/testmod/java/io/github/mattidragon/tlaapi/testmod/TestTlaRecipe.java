package io.github.mattidragon.tlaapi.testmod;

import io.github.mattidragon.tlaapi.api.gui.GuiBuilder;
import io.github.mattidragon.tlaapi.api.gui.TextureConfig;
import io.github.mattidragon.tlaapi.api.recipe.TlaCategory;
import io.github.mattidragon.tlaapi.api.recipe.TlaIngredient;
import io.github.mattidragon.tlaapi.api.recipe.TlaRecipe;
import io.github.mattidragon.tlaapi.api.recipe.TlaStack;
import io.github.mattidragon.tlaapi.testmod.content.TestRecipe;
import net.minecraft.recipe.RecipeEntry;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

import java.util.List;

public record TestTlaRecipe(RecipeEntry<TestRecipe> entry) implements TlaRecipe {
    @Override
    public TlaCategory getCategory() {
        return TestPlugin.TEST_CATEGORY;
    }

    @Override
    public Identifier getId() {
        return entry.id();
    }

    @Override
    public List<TlaIngredient> getInputs() {
        return List.of(TlaIngredient.ofIngredient(entry.value().input()));
    }

    @Override
    public List<TlaStack> getOutputs() {
        return List.of(TlaStack.of(entry.value().output()));
    }

    @Override
    public List<TlaIngredient> getCatalysts() {
        return List.of();
    }

    @Override
    public void buildGui(GuiBuilder builder) {
        var dirtTexture = TextureConfig.builder()
                .texture(Identifier.ofVanilla("textures/block/dirt.png"))
                .fullSize(16, 16)
                .build();

        builder.addSlot(TlaIngredient.ofIngredient(entry.value().input()), 0, 4).addTooltip(Text.literal("hi")).markInput();
        builder.addTexture(dirtTexture, 18, 0);
        builder.addArrow(24, 4, false).addTooltip(Text.literal("am arrow"));
        builder.addArrow(24, 24, true).addTooltip(Text.literal("am full arrow"));
        builder.addSlot(TlaStack.of(entry.value().output()).withChance(0.5), 46, 0).markOutput().makeLarge().addTooltip(Text.literal("am large"));
        builder.addCustomWidget(0, 22, 16, 16, (context, mouseX, mouseY, delta) -> {
            context.drawTexture(Identifier.ofVanilla("textures/block/cobblestone.png"), 0, 0, 0, 0, 16, 16, 16, 16);
        }).addTooltip(Text.literal("am custom widget"));
    }
}
