package io.github.mattidragon.tlaapi.testmod.content;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.recipe.Ingredient;
import net.minecraft.recipe.Recipe;
import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.recipe.RecipeType;
import net.minecraft.registry.DynamicRegistryManager;
import net.minecraft.world.World;

public record TestRecipe(Ingredient input, ItemStack output) implements Recipe<SimpleInventory> {
    @Override
    public boolean matches(SimpleInventory inventory, World world) {
        return input.test(inventory.getStack(0));
    }

    @Override
    public ItemStack craft(SimpleInventory inventory, DynamicRegistryManager registryManager) {
        return output.copy();
    }

    @Override
    public boolean fits(int width, int height) {
        return true;
    }

    @Override
    public ItemStack getResult(DynamicRegistryManager registryManager) {
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
        private static final Codec<TestRecipe> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Ingredient.DISALLOW_EMPTY_CODEC.fieldOf("input").forGetter(TestRecipe::input),
            ItemStack.CODEC.fieldOf("output").forGetter(TestRecipe::output)
        ).apply(instance, TestRecipe::new));

        @Override
        public Codec<TestRecipe> codec() {
            return CODEC;
        }

        @Override
        public TestRecipe read(PacketByteBuf buf) {
            var input = Ingredient.fromPacket(buf);
            var output = buf.readItemStack();
            return new TestRecipe(input, output);
        }

        @Override
        public void write(PacketByteBuf buf, TestRecipe recipe) {
            recipe.input.write(buf);
            buf.writeItemStack(recipe.output);
        }
    }
}
