package io.github.mattidragon.tlaapi.impl.rei;

import io.github.mattidragon.tlaapi.api.StackDragHandler;
import io.github.mattidragon.tlaapi.api.gui.TlaBounds;
import io.github.mattidragon.tlaapi.api.plugin.PluginContext;
import io.github.mattidragon.tlaapi.api.plugin.PluginLoader;
import io.github.mattidragon.tlaapi.api.recipe.TlaCategory;
import io.github.mattidragon.tlaapi.api.recipe.TlaRecipe;
import io.github.mattidragon.tlaapi.impl.rei.util.TlaDragHandler;
import io.github.mattidragon.tlaapi.impl.rei.util.TlaExclusionZoneProvider;
import io.github.mattidragon.tlaapi.impl.rei.util.TlaScreenSizeProvider;
import me.shedaniel.rei.api.client.plugins.REIClientPlugin;
import me.shedaniel.rei.api.client.registry.category.CategoryRegistry;
import me.shedaniel.rei.api.client.registry.display.DisplayRegistry;
import me.shedaniel.rei.api.client.registry.screen.ExclusionZones;
import me.shedaniel.rei.api.client.registry.screen.ScreenRegistry;
import me.shedaniel.rei.api.common.plugins.PluginManager;
import me.shedaniel.rei.api.common.registry.ReloadStage;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.recipe.Recipe;
import net.minecraft.recipe.RecipeEntry;
import net.minecraft.recipe.RecipeType;

import java.util.*;
import java.util.function.Function;

public class TlaApiReiPlugin implements REIClientPlugin, PluginContext {
    private final Map<TlaCategory, TlaDisplayCategory> categories = new HashMap<>();
    private final List<RecipeGenerator<?>> recipeGenerators = new ArrayList<>();
    private final List<Function<MinecraftClient, List<TlaRecipe>>> customGenerators = new ArrayList<>();
    private final List<TlaDragHandler<?>> stackDragHandlers = new ArrayList<>();
    private final List<TlaScreenSizeProvider<?>> screenSizeProviders = new ArrayList<>();
    private final List<TlaExclusionZoneProvider<?>> exclusionZoneProviders = new ArrayList<>();

    @Override
    public void preStage(PluginManager<REIClientPlugin> manager, ReloadStage stage) {
        // REI doesn't have a good reload start event, so we have to do this
        if (stage == ReloadStage.START && manager == PluginManager.getClientInstance()) {
            categories.clear();
            recipeGenerators.clear();
            customGenerators.clear();
            stackDragHandlers.clear();
            screenSizeProviders.clear();
            exclusionZoneProviders.clear();
            PluginLoader.loadPlugins(this);
        }
    }

    @Override
    public void registerCategories(CategoryRegistry registry) {
        registry.add(Collections.unmodifiableCollection(categories.values()));
    }

    @Override
    public void registerDisplays(DisplayRegistry registry) {
        // This is generic soup due to javas type system limitations
        // We need an unsafe cast so that we can use the generator function after checking the recipe type
        registry.registerFiller(RecipeEntry.class,
                entry -> recipeGenerators.stream().anyMatch(generator -> generator.type == entry.value().getType()),
                entry -> recipeGenerators.stream()
                        .filter(generator -> generator.type == entry.value().getType())
                        .findFirst()
                        .map(generator -> generator.generator.apply(unsafeCast(entry)))
                        .map(this::mapRecipe)
                        .orElse(null));

        for (var generator : customGenerators) {
            for (var tlaRecipe : generator.apply(MinecraftClient.getInstance())) {
                registry.add(mapRecipe(tlaRecipe));
            }
        }
    }

    private TlaDisplay mapRecipe(TlaRecipe recipe) {
        return new TlaDisplay(categories.get(recipe.getCategory()).getCategoryIdentifier(), recipe);
    }

    @Override
    public void registerScreens(ScreenRegistry registry) {
        stackDragHandlers.forEach(registry::registerDraggableStackVisitor);
        screenSizeProviders.forEach(registry::registerDecider);
    }

    @Override
    public void registerExclusionZones(ExclusionZones zones) {
        exclusionZoneProviders.forEach(provider -> zones.register(provider.clazz, provider));
    }

    @Override
    public void addCategory(TlaCategory category) {
        categories.put(category, new TlaDisplayCategory(category));
    }

    @Override
    public <T extends Recipe<?>> void addRecipeGenerator(RecipeType<T> type, Function<RecipeEntry<T>, TlaRecipe> generator) {
        recipeGenerators.add(new RecipeGenerator<>(type, generator));
    }

    @Override
    public void addGenerator(Function<MinecraftClient, List<TlaRecipe>> generator) {
        customGenerators.add(generator);
    }

    @Override
    public <T extends Screen> void addStackDragHandler(Class<T> clazz, StackDragHandler<T> handler) {
        stackDragHandlers.add(new TlaDragHandler<>(handler, clazz));
    }

    @Override
    public <T extends Screen> void addScreenSizeProvider(Class<T> clazz, Function<T, TlaBounds> provider) {
        screenSizeProviders.add(new TlaScreenSizeProvider<>(clazz, provider));
    }

    @Override
    public <T extends Screen> void addExclusionZoneProvider(Class<T> clazz, Function<T, ? extends Iterable<TlaBounds>> provider) {
        exclusionZoneProviders.add(new TlaExclusionZoneProvider<>(clazz, provider));
    }

    private record RecipeGenerator<T extends Recipe<?>>(RecipeType<T> type, Function<RecipeEntry<T>, TlaRecipe> generator) {}

    @SuppressWarnings("unchecked")
    private <T> T unsafeCast(Object o) {
        return (T) o;
    }
}
