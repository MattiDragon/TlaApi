package io.github.mattidragon.tlaapi.testmod;

import io.github.mattidragon.tlaapi.api.gui.TextureConfig;
import io.github.mattidragon.tlaapi.api.recipe.CategoryIcon;
import io.github.mattidragon.tlaapi.api.recipe.TlaCategory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

public class TestCategory implements TlaCategory {
    @Override
    public Identifier getId() {
        return new Identifier("testmod:test_recipe");
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
        return 18 * 4;
    }

    @Override
    public CategoryIcon getIcon() {
        return CategoryIcon.item(Items.ACACIA_LOG);
    }

    @Override
    public CategoryIcon getSimpleIcon() {
        return CategoryIcon.texture(TextureConfig.of(new Identifier("minecraft:textures/block/stone.png"), 16, 16));
    }
}
