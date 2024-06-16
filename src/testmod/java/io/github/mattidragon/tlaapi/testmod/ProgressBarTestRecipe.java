package io.github.mattidragon.tlaapi.testmod;

import io.github.mattidragon.tlaapi.api.gui.GuiBuilder;
import io.github.mattidragon.tlaapi.api.gui.TextureConfig;
import io.github.mattidragon.tlaapi.api.recipe.TlaCategory;
import io.github.mattidragon.tlaapi.api.recipe.TlaIngredient;
import io.github.mattidragon.tlaapi.api.recipe.TlaRecipe;
import io.github.mattidragon.tlaapi.api.recipe.TlaStack;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.function.DoubleSupplier;

public class ProgressBarTestRecipe implements TlaRecipe {
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
        var texture = TextureConfig.builder()
                .texture(Identifier.ofVanilla("textures/block/dirt.png"), Identifier.ofVanilla("textures/block/stone.png"))
                .size(35, 10)
                .regionSize(16, 16)
                .textureSize(16, 16)
                .build();
        DoubleSupplier manualProgress = () -> {
            var time = System.currentTimeMillis() % 1000;
            return time > 500 ? 1 - (time - 500) / 500.0 : time / 500.0;
        };

        builder.addAnimatedTexture(texture, 0, 0, 1000, true, false, false)
                .addTooltip(Text.literal("1000ms, horizontal, start to end, empty to full"));
        builder.addAnimatedTexture(texture, 0, 10, 1000, true, true, false)
                .addTooltip(Text.literal("1000ms, horizontal, end to start, empty to full"));
        builder.addAnimatedTexture(texture, 0, 20, 1000, true, false, true)
                .addTooltip(Text.literal("1000ms, horizontal, start to end, full to empty"));
        builder.addAnimatedTexture(texture, 0, 30, 1000, true, true, true)
                .addTooltip(Text.literal("1000ms, horizontal, end to start, full to empty"));

        builder.addProgressingTexture(texture, 36, 0, manualProgress, true, false, false)
                .addTooltip(Text.literal("custom, horizontal, start to end, empty to full"));
        builder.addProgressingTexture(texture, 36, 10, 0.25, true, true, false)
                .addTooltip(Text.literal("fixed, horizontal, end to start, empty to full"));
        builder.addProgressingTexture(texture, 36, 20, manualProgress, true, false, true)
                .addTooltip(Text.literal("custom, horizontal, start to end, full to empty"));
        builder.addProgressingTexture(texture, 36, 30, 0.25, true, true, true)
                .addTooltip(Text.literal("fixed, horizontal, end to start, full to empty"));
    }
}
