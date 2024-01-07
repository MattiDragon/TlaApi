package io.github.mattidragon.tlaapi.impl.emi;

import dev.emi.emi.api.render.EmiRenderable;
import dev.emi.emi.api.render.EmiTexture;
import dev.emi.emi.api.stack.EmiIngredient;
import dev.emi.emi.api.stack.EmiStack;
import io.github.mattidragon.tlaapi.api.recipe.CategoryIcon;
import io.github.mattidragon.tlaapi.api.recipe.TlaIngredient;
import io.github.mattidragon.tlaapi.api.recipe.TlaStack;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant;
import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant;
import net.minecraft.fluid.Fluid;
import net.minecraft.item.Item;

import java.util.OptionalDouble;

public class EmiUtils {
    public static EmiStack convertStack(TlaStack stack) {
        EmiStack emiStack;
        if (stack instanceof TlaStack.TlaFluidStack fluidStack) {
            emiStack = EmiStack.of(fluidStack.getFluid(), fluidStack.getFluidVariant().getNbt(), fluidStack.getAmount());
        } else if (stack instanceof TlaStack.TlaItemStack itemStack) {
            emiStack = EmiStack.of(itemStack.toStack(), itemStack.getAmount());
        } else {
            throw new IllegalArgumentException("Unknown stack type: " + stack.getClass().getName());
        }
        stack.getChance().ifPresent(chance -> emiStack.setChance((float) chance));
        return emiStack;
    }

    public static EmiIngredient convertIngredient(TlaIngredient ingredient) {
        var stacks = ingredient.getStacks();
        if (stacks.isEmpty()) return EmiStack.EMPTY;
        if (stacks.size() == 1) return convertStack(stacks.iterator().next());

        // Emi ingredients store the chance in the ingredient itself, not in the stacks
        // We calculate the average chance of all stacks and set it in the ingredient
        // As emi doesn't differentiate between 100% and a lack of chance, we set it to 1 if there is no chance
        var chance = stacks.stream().map(TlaStack::getChance).flatMapToDouble(OptionalDouble::stream).average().orElse(1);
        return EmiIngredient.of(stacks.stream().map(EmiUtils::convertStack).toList()).setChance((float) chance);
    }

    public static TlaStack convertStack(EmiStack stack) {
        if (stack.getKey() instanceof Fluid fluid) {
            var out = TlaStack.of(FluidVariant.of(fluid, stack.getNbt()), stack.getAmount());
            if (stack.getChance() != 1) out = out.withChance(stack.getChance());
            return out;
        } else if (stack.getKey() instanceof Item item) {
            var out = TlaStack.of(ItemVariant.of(item, stack.getNbt()), stack.getAmount()).withChance(stack.getChance());
            if (stack.getChance() != 1) out = out.withChance(stack.getChance());
            return out;
        }
        return TlaStack.empty();
    }

    public static TlaIngredient convertIngredient(EmiIngredient stack) {
        if (stack instanceof EmiStack emiStack) {
            return TlaIngredient.ofStacks(convertStack(emiStack));
        } else {
            return TlaIngredient.ofStacks(stack.getEmiStacks().stream().map(EmiUtils::convertStack).toList());
        }
    }

    public static EmiRenderable iconToRenderable(CategoryIcon icon) {
        if (icon instanceof CategoryIcon.StackIcon stackIcon) {
            return convertStack(stackIcon.stack());
        } else if (icon instanceof CategoryIcon.TextureIcon textureIcon) {
            var config = textureIcon.texture();
            return new EmiTexture(config.texture(),
                    config.width(), config.height(),
                    config.u(), config.v(),
                    config.regionWidth(), config.regionHeight(),
                    config.textureWidth(), config.textureHeight());
        } else {
            throw new IllegalArgumentException("Unknown icon type: " + icon.getClass().getName());
        }
    }
}
