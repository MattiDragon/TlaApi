package io.github.mattidragon.tlaapi.testmod.content;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.item.ItemStack;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.recipe.Ingredient;
import net.minecraft.recipe.Recipe;
import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.recipe.RecipeType;
import net.minecraft.recipe.input.CraftingRecipeInput;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.world.World;

public record TestRecipe(Ingredient input, ItemStack output) implements Recipe<CraftingRecipeInput> {
    @Override
    public boolean matches(CraftingRecipeInput inventory, World world) {
        return input.test(inventory.getStackInSlot(0));
    }

    @Override
    public ItemStack craft(CraftingRecipeInput inventory, RegistryWrapper.WrapperLookup lookup) {
        return output.copy();
    }

    @Override
    public boolean fits(int width, int height) {
        return true;
    }

    @Override
    public ItemStack getResult(RegistryWrapper.WrapperLookup lookup) {
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
        private static final MapCodec<TestRecipe> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            Ingredient.DISALLOW_EMPTY_CODEC.fieldOf("input").forGetter(TestRecipe::input),
            ItemStack.CODEC.fieldOf("output").forGetter(TestRecipe::output)
        ).apply(instance, TestRecipe::new));
        private static final PacketCodec<RegistryByteBuf, TestRecipe> PACKET_CODEC = PacketCodec.tuple(
                Ingredient.PACKET_CODEC, TestRecipe::input,
                ItemStack.PACKET_CODEC, TestRecipe::output,
                TestRecipe::new);

        @Override
        public MapCodec<TestRecipe> codec() {
            return CODEC;
        }

        @Override
        public PacketCodec<RegistryByteBuf, TestRecipe> packetCodec() {
            return PACKET_CODEC;
        }
    }
}
