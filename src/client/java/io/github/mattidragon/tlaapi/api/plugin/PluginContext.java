package io.github.mattidragon.tlaapi.api.plugin;

import io.github.mattidragon.tlaapi.api.StackDragHandler;
import io.github.mattidragon.tlaapi.api.BuiltInRecipeCategory;
import io.github.mattidragon.tlaapi.api.gui.TlaBounds;
import io.github.mattidragon.tlaapi.api.recipe.TlaCategory;
import io.github.mattidragon.tlaapi.api.recipe.TlaIngredient;
import io.github.mattidragon.tlaapi.api.recipe.TlaRecipe;
import io.github.mattidragon.tlaapi.api.recipe.TlaStack;
import io.github.mattidragon.tlaapi.impl.ImplementationsExtend;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.fluid.Fluid;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemConvertible;
import net.minecraft.recipe.Recipe;
import net.minecraft.recipe.RecipeEntry;
import net.minecraft.recipe.RecipeType;
import net.minecraft.util.Identifier;

import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Predicate;

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
     * Gets a vanilla category for a specified id
     * @param id
     * @see BuiltInRecipeCategory
     */
    Optional<TlaCategory> getVanillaCategory(BuiltInRecipeCategory type);

    /**
     * Adds a block as the workstation for a recipe category.
     *
     * @param category The recipe category to assign to this workstation.
     * @param workstations Ingredients matching the blocks or items that make up this workstation.
     */
    void addWorkstation(TlaCategory category, TlaIngredient... workstations);

    /**
     * Adds a recipe generator that create recipe entries in the recipe viewer for all recipes of a given type.
     * @see TlaRecipe
     * @see #addGenerator
     */
    <I extends Inventory, T extends Recipe<I>> void addRecipeGenerator(RecipeType<T> type, Function<RecipeEntry<T>, TlaRecipe> generator);

    /**
     * Adds a recipe generator that can create recipe entries in the recipe viewer from any source.
     * The generator is provided the client instance for convenience.
     * @see TlaRecipe
     * @see #addRecipeGenerator
     */
    void addGenerator(Function<MinecraftClient, List<TlaRecipe>> generator);

    /**
     * Adds an area which can be clicked to open a category if the recipe viewer supports it.
     * The bounds are based on screen coordinates. To use handled screen coordinates use {@link #addScreenHandlerClickArea}.
     * It should also be preferred whenever possible due to better compatibility.
     * @param clazz The class of the screen for which this applies.
     * @param category The category which should be opened.
     * @param boundsFunction The function that supplies the bounds.
     * @see #addScreenHandlerClickArea(Class, TlaCategory, Function)
     * @implNote EMI does not provide this functionality, as such this method is a no-op when running through it.
     * <br>JEI doesn't provide the ability to add click areas to non-handled screens, as such this method won't work there.
     */
    <T extends Screen> void addClickArea(Class<T> clazz, TlaCategory category, Function<T, TlaBounds> boundsFunction);

    /**
     * Adds an area which can be clicked to open a category if the recipe viewer supports it.
     * The bounds are based on handled screen coordinates based on the reported screen size. For non-handled screens, see {@link #addClickArea}.
     * @param clazz The class of the screen for which this applies.
     * @param category The category which should be opened.
     * @param boundsFunction The function that supplies the bounds.
     * @see #addClickArea(Class, TlaCategory, Function)
     * @implNote EMI does not provide this functionality, as such this method is a no-op when running through it.
     */
    <T extends HandledScreen<?>> void addScreenHandlerClickArea(Class<T> clazz, TlaCategory category, Function<T, TlaBounds> boundsFunction);

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

    /**
     * Adds a predicate to run on all current and future TlaStacks to prevent certain ones from being added to the sidebar.
     */
    void removeStacks(Predicate<TlaStack> predicate);

    /**
     * Adds a predicate to run on all current and future TlaStacks to prevent matching ones from being added to the sidebar.
     */
    default void removeStacks(TlaStack stack) {
        removeStacks(s -> s.equals(stack));
    }

    /**
     * Adds a predicate to run on all current and future recipes to prevent certain ones from being added.
     */
    void removeRecipes(Predicate<TlaRecipe> predicate);

    /**
     * Adds a predicate to run on all current and future recipes to prevent certain ones with the given identifier from being added.
     */
    default void removeRecipes(Identifier id) {
        removeRecipes(r -> id.equals(r.getId()));
    }

    /**
     * Gets a registry used for registering the default comparisons for items and blocks.
     *
     * @see TlaStackComparison
     */
    Comparisons<ItemConvertible> getItemComparisons();

    /**
     * Gets a registry used for registering the default comparisons for fluids.
     *
     * @see TlaStackComparison
     */
    Comparisons<Fluid> getFluidComparisons();

    /**
     * Provides users of TLA-api knowledge of which recipe viewer invoked the plugin.
     */
    RecipeViewer getActiveViewer();
}
