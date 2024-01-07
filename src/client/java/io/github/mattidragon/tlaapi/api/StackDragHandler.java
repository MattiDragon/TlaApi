package io.github.mattidragon.tlaapi.api;

import io.github.mattidragon.tlaapi.api.gui.TlaBounds;
import io.github.mattidragon.tlaapi.api.recipe.TlaIngredient;
import io.github.mattidragon.tlaapi.api.recipe.TlaStack;
import it.unimi.dsi.fastutil.objects.Object2BooleanFunction;
import net.minecraft.client.gui.screen.Screen;

import java.util.Collection;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Stream;

/**
 * A handler for dragging ghost stacks onto a screen.
 * Used to set filters or similar things.
 */
public interface StackDragHandler<T extends Screen> {
    /**
     * Checks if this handler applies to the given screen.
     * If this returns false, {@link #getDropTargets} will not be called.
     */
    default boolean appliesTo(T screen) {
        return true;
    }

    /**
     * Gets the drop targets for the given screen.
     * The ingredient consumer is called when the ingredient is dropped on the target.
     * If the consumer returns true, the ingredient is consumed.
     * If the consumer returns false, the ingredient is not consumed and will possibly return to the recipe viewer.
     * @see DropTarget
     */
    Collection<DropTarget> getDropTargets(T screen);

    /**
     * A location on a screen where a stack can be dropped.
     * @param bounds The area in which the stack can be dropped. In screen coordinates.
     * @param ingredientConsumer This consumer is called when the ingredient is dropped on the target.
     */
    record DropTarget(TlaBounds bounds, Function<TlaStack, Boolean> ingredientConsumer) { }
}
