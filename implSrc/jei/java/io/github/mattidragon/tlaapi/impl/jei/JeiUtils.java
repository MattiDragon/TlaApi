package io.github.mattidragon.tlaapi.impl.jei;

import io.github.mattidragon.tlaapi.api.recipe.CategoryIcon;
import io.github.mattidragon.tlaapi.api.recipe.TlaIngredient;
import io.github.mattidragon.tlaapi.api.recipe.TlaStack;
import mezz.jei.api.constants.ModIds;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.fabric.constants.FabricTypes;
import mezz.jei.api.fabric.ingredients.fluids.IJeiFluidIngredient;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.helpers.IJeiHelpers;
import mezz.jei.api.helpers.IPlatformFluidHelper;
import mezz.jei.api.ingredients.ITypedIngredient;
import mezz.jei.api.runtime.IIngredientManager;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;

import java.util.List;
import java.util.Optional;

public class JeiUtils {
    public static final Identifier VANILLA_GUI_TEXTURE = Identifier.of(ModIds.JEI_ID, "textures/jei/gui/gui_vanilla.png");

    public static Optional<? extends ITypedIngredient<?>> convertStack(IJeiHelpers helpers, TlaStack stack) {
        var manager = helpers.getIngredientManager();
        if (stack instanceof TlaStack.TlaFluidStack fluidStack) {
            return createFluidIngredient(manager, helpers.getPlatformFluidHelper(), fluidStack);
        } else if (stack instanceof TlaStack.TlaItemStack itemStack) {
            return manager.createTypedIngredient(VanillaTypes.ITEM_STACK, itemStack.toStack());
        } else {
            return Optional.empty();
        }
    }

    public static List<ITypedIngredient<?>> convertIngredient(IJeiHelpers helpers, TlaIngredient ingredient) {
        return ingredient.getStacks()
                .stream()
                .map(stack -> convertStack(helpers, stack))
                .<ITypedIngredient<?>>flatMap(Optional::stream)
                .toList();
    }

    public static TlaStack convertStack(ITypedIngredient<?> stack) {
        if (stack.getType() == VanillaTypes.ITEM_STACK) {
            return TlaStack.of(((ItemStack) stack.getIngredient()));
        } else if (stack.getType() == FabricTypes.FLUID_STACK) {
            var ingredient = (IJeiFluidIngredient) stack.getIngredient();
            return TlaStack.of(FluidVariant.of(ingredient.getFluid(), ingredient.getTag().orElse(null)), ingredient.getAmount());
        } else {
            return TlaStack.empty();
        }
    }

    public static TlaIngredient convertIngredient(List<ITypedIngredient<?>> stacks) {
        return TlaIngredient.ofStacks(stacks.stream().map(JeiUtils::convertStack).toList());
    }

    public static IDrawable iconToDrawable(IJeiHelpers helpers, CategoryIcon icon) {
        if (icon instanceof CategoryIcon.StackIcon stackIcon) {
            return convertStack(helpers, stackIcon.stack())
                    .map(it -> getDrawableIngredient(helpers.getGuiHelper(), it))
                    .orElseGet(() -> helpers.getGuiHelper().createBlankDrawable(16, 16));
        } else if (icon instanceof CategoryIcon.TextureIcon textureIcon) {
            return new TextureDrawable(textureIcon.texture());
        } else {
            throw new RuntimeException("Not an acceptable category icon");
        }
    }

    private static <T> IDrawable getDrawableIngredient(IGuiHelper guiHelper, ITypedIngredient<T> converted) {
        return guiHelper.createDrawableIngredient(converted.getType(), converted.getIngredient());
    }

    private static <F> Optional<ITypedIngredient<F>> createFluidIngredient(IIngredientManager manager, IPlatformFluidHelper<F> helper, TlaStack.TlaFluidStack stack) {
        var variant = stack.getFluidVariant();
        return manager.createTypedIngredient(
                helper.getFluidIngredientType(),
                helper.create(variant.getFluid(), stack.getAmount(), variant.getNbt())
        );
    }
}
