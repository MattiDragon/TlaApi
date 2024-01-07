package io.github.mattidragon.tlaapi.api.recipe;

import io.github.mattidragon.tlaapi.api.gui.TextureConfig;
import io.github.mattidragon.tlaapi.impl.ImplementationOnly;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant;
import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant;
import net.minecraft.fluid.Fluid;
import net.minecraft.item.ItemConvertible;

/**
 * Represents an icon for a recipe category.
 * Icons are either normal {@link TlaStack stacks} or {@link TextureConfig textures}.
 * This class is sealed and cannot be extended.
 */
public sealed interface CategoryIcon {
    /**
     * Creates a new icon from the provided stack.
     */
    static CategoryIcon stack(TlaStack stack) {
        return new StackIcon(stack);
    }

    /**
     * Creates a new icon from the provided item variant.
     */
    static CategoryIcon item(ItemVariant item) {
        return stack(TlaStack.of(item));
    }

    /**
     * Creates a new icon from the provided item.
     */
    static CategoryIcon item(ItemConvertible item) {
        return stack(TlaStack.of(item));
    }

    /**
     * Creates a new icon from the provided fluid variant.
     */
    static CategoryIcon fluid(FluidVariant fluid) {
        return stack(TlaStack.bucketOf(fluid));
    }

    /**
     * Creates a new icon from the provided fluid.
     */
    static CategoryIcon fluid(Fluid fluid) {
        return stack(TlaStack.bucketOf(fluid));
    }

    /**
     * Creates a new icon from the provided texture config.
     */
    static CategoryIcon texture(TextureConfig texture) {
        return new TextureIcon(texture);
    }

    @ImplementationOnly
    final class StackIcon implements CategoryIcon {
        private final TlaStack stack;

        private StackIcon(TlaStack stack) {
            this.stack = stack;
        }

        public TlaStack stack() {
            return stack;
        }
    }

    @ImplementationOnly
    final class TextureIcon implements CategoryIcon {
        private final TextureConfig texture;

        private TextureIcon(TextureConfig texture) {
            this.texture = texture;
        }

        public TextureConfig texture() {
            return texture;
        }
    }
}
