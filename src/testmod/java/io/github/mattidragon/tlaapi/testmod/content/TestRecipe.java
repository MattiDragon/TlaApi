package io.github.mattidragon.tlaapi.testmod.content;

import com.google.gson.JsonObject;
import net.minecraft.inventory.RecipeInputInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.recipe.Ingredient;
import net.minecraft.recipe.Recipe;
import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.recipe.RecipeType;
import net.minecraft.recipe.ShapedRecipe;
import net.minecraft.registry.DynamicRegistryManager;
import net.minecraft.util.Identifier;
import net.minecraft.util.JsonHelper;
import net.minecraft.world.World;

public record TestRecipe(Identifier id, Ingredient input, ItemStack output) implements Recipe<RecipeInputInventory> {

    @Override
    public Identifier getId() {
        return id;
    }

    @Override
    public boolean matches(RecipeInputInventory inventory, World world) {
        return input.test(inventory.getStack(0));
    }

    @Override
    public ItemStack craft(RecipeInputInventory inventory, DynamicRegistryManager lookup) {
        return output.copy();
    }

    @Override
    public boolean fits(int width, int height) {
        return true;
    }

    @Override
    public ItemStack getOutput(DynamicRegistryManager lookup) {
        return output;
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return TestMod.RECIPE_SERIALIZER;
    }

    @Override
    public RecipeType<?> getType() {
        return TestMod.RECIPE_TYPE;
    }

    public static class Serializer implements RecipeSerializer<TestRecipe> {
        @Override
        public TestRecipe read(Identifier id, JsonObject json) {
            return new TestRecipe(id,
                    Ingredient.fromJson(JsonHelper.getObject(json, "input")),
                    ShapedRecipe.outputFromJson(JsonHelper.getObject(json, "output"))
            );
        }

        @Override
        public TestRecipe read(Identifier id, PacketByteBuf buf) {
            return new TestRecipe(id,
                    Ingredient.fromPacket(buf),
                    buf.readItemStack()
            );
        }

        @Override
        public void write(PacketByteBuf buf, TestRecipe recipe) {
            recipe.input().write(buf);
            buf.writeItemStack(recipe.output());
        }
    }
}
