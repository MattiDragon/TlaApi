package io.github.mattidragon.tlaapi.api.plugin;

import io.github.mattidragon.tlaapi.api.StackDragHandler;
import io.github.mattidragon.tlaapi.api.gui.TlaBounds;
import io.github.mattidragon.tlaapi.api.recipe.TlaCategory;
import io.github.mattidragon.tlaapi.api.recipe.TlaRecipe;
import io.github.mattidragon.tlaapi.impl.ImplementationsExtend;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.recipe.Recipe;
import net.minecraft.recipe.RecipeEntry;
import net.minecraft.recipe.RecipeType;

import java.util.List;
import java.util.function.Function;

/**
 * The main way plugins interact with the API.
 * Provided by implementations to the plugin entrypoint.
 * @see TlaApiPlugin
 */
@ImplementationsExtend
public interface PluginContext {
    /**
     * Adds a category to the recipe viewer.
     * Categories are groups of recipes that work the same way and share a tab.
     * Usually corresponds to recipe types.
     * @see TlaCategory
     */
    void addCategory(TlaCategory category);

    /**
     * Adds a recipe generator that create recipe entries in the recipe viewer for all recipes of a given type.
     * @see TlaRecipe
     * @see #addGenerator
     */
    <T extends Recipe<?>> void addRecipeGenerator(RecipeType<T> type, Function<RecipeEntry<T>, TlaRecipe> generator);

    /**
     * Adds a recipe generator that can create recipe entries in the recipe viewer from any source.
     * The generator is provided the client instance for convenience.
     * @see TlaRecipe
     * @see #addRecipeGenerator
     */
    void addGenerator(Function<MinecraftClient, List<TlaRecipe>> generator);

    /**
     * Adds a stack drag handler for a given screen type.
     * Stack drag handlers are used to handle dragging stacks from the recipe viewer to a screen,
     * usually for setting filters or similar things.
     */
    <T extends Screen> void addStackDragHandler(Class<T> clazz, StackDragHandler<T> handler);

    /**
     * Adds a screen size provider for a given screen type.
     * This is used by REI to position itself.
     * With EMI this adds an exclusion zone instead preventing EMI from extending onto the screen.
     * Usually calling this is unnecessary as the default implementations based on handled screens are sufficient.
     * @see #addExclusionZoneProvider
     */
    default <T extends Screen> void addScreenSizeProvider(Class<T> clazz, Function<T, TlaBounds> provider) {
        addExclusionZoneProvider(clazz, screen -> List.of(provider.apply(screen)));
    }

    /**
     * Adds an exclusion zone provider for a given screen type.
     * This prevents recipe viewers from extending onto areas of the screen.
     */
    <T extends Screen> void addExclusionZoneProvider(Class<T> clazz, Function<T, ? extends Iterable<TlaBounds>> provider);
}
