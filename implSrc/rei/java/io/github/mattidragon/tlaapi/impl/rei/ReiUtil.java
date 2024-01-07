package io.github.mattidragon.tlaapi.impl.rei;

import dev.architectury.fluid.FluidStack;
import io.github.mattidragon.tlaapi.api.recipe.CategoryIcon;
import io.github.mattidragon.tlaapi.api.recipe.TlaIngredient;
import io.github.mattidragon.tlaapi.api.recipe.TlaStack;
import io.github.mattidragon.tlaapi.impl.rei.util.TextureWidget;
import me.shedaniel.rei.api.client.gui.Renderer;
import me.shedaniel.rei.api.common.entry.EntryIngredient;
import me.shedaniel.rei.api.common.entry.EntryStack;
import me.shedaniel.rei.api.common.util.EntryStacks;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant;
import net.minecraft.item.ItemStack;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ReiUtil {
    public static final Logger LOGGER = LoggerFactory.getLogger("TLA-Api/REI");

    public static EntryStack<?> convertStack(TlaStack stack) {
        if (stack instanceof TlaStack.TlaFluidStack fluidStack) {
            var archFluidStack = FluidStack.create(fluidStack.getFluid(), fluidStack.getAmount());
            archFluidStack.setTag(fluidStack.getFluidVariant().getNbt());
            return EntryStacks.of(archFluidStack);
        } else if (stack instanceof TlaStack.TlaItemStack itemStack) {
            return EntryStacks.of(itemStack.toStack());
        } else {
            throw new IllegalArgumentException("Unknown stack type: " + stack.getClass().getName());
        }
    }

    public static EntryIngredient convertIngredient(TlaIngredient ingredient) {
        var stacks = ingredient.getStacks();
        if (stacks.isEmpty()) return EntryIngredient.empty();
        if (stacks.size() == 1) return EntryIngredient.of(convertStack(stacks.iterator().next()));

        return EntryIngredient.of(stacks.stream().map(ReiUtil::convertStack).toList());
    }

    public static TlaStack convertStack(EntryStack<?> stack) {
        if (stack.getValue() instanceof FluidStack fluidStack) {
            return TlaStack.of(FluidVariant.of(fluidStack.getFluid(), fluidStack.getTag()), fluidStack.getAmount());
        } else if (stack.getValue() instanceof ItemStack itemStack) {
            return TlaStack.of(itemStack);
        } else {
            return TlaStack.empty();
        }
    }

    public static TlaIngredient convertIngredient(EntryIngredient ingredient) {
        if (ingredient.isEmpty()) return TlaIngredient.EMPTY;
        if (ingredient.size() == 1) return TlaIngredient.ofStacks(convertStack(ingredient.iterator().next()));

        return TlaIngredient.ofStacks(ingredient.stream().map(ReiUtil::convertStack).toList());
    }

    public static Renderer iconToRenderer(CategoryIcon icon) {
        if (icon instanceof CategoryIcon.StackIcon stackIcon) {
            return convertStack(stackIcon.stack());
        } else if (icon instanceof CategoryIcon.TextureIcon textureIcon) {
            return new TextureWidget(0, 0, textureIcon.texture());
        } else {
            throw new IllegalArgumentException("Unknown icon type: " + icon.getClass().getName());
        }
    }
}
