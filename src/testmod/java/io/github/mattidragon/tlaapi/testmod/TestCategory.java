package io.github.mattidragon.tlaapi.testmod;

import io.github.mattidragon.tlaapi.api.gui.TextureConfig;
import io.github.mattidragon.tlaapi.api.recipe.CategoryIcon;
import io.github.mattidragon.tlaapi.api.recipe.TlaCategory;
import net.minecraft.item.Items;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

public class TestCategory implements TlaCategory {
    @Override
    public Identifier getId() {
        return Identifier.of("testmod", "test_recipe");
    }

    @Override
    public Text getName() {
        return Text.literal("Test Category");
    }

    @Override
    public int getDisplayHeight() {
        return 40;
    }

    @Override
    public int getDisplayWidth() {
        return 72;
    }

    @Override
    public CategoryIcon getIcon() {
        return CategoryIcon.item(Items.ACACIA_LOG);
    }

    @Override
    public CategoryIcon getSimpleIcon() {
        return CategoryIcon.texture(TextureConfig.builder().texture(Identifier.ofVanilla("textures/block/stone.png")).fullSize(16, 16).build());
    }
}
