package io.github.mattidragon.tlaapi.impl.rei;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;

import dev.architectury.event.EventResult;
import dev.architectury.fluid.FluidStack;
import io.github.mattidragon.tlaapi.api.BuiltInRecipeCategory;
import io.github.mattidragon.tlaapi.api.StackDragHandler;
import io.github.mattidragon.tlaapi.api.gui.TlaBounds;
import io.github.mattidragon.tlaapi.api.plugin.Comparisons;
import io.github.mattidragon.tlaapi.api.plugin.PluginContext;
import io.github.mattidragon.tlaapi.api.plugin.PluginLoader;
import io.github.mattidragon.tlaapi.api.plugin.RecipeViewer;
import io.github.mattidragon.tlaapi.api.recipe.CategoryIcon;
import io.github.mattidragon.tlaapi.api.recipe.TlaCategory;
import io.github.mattidragon.tlaapi.api.recipe.TlaIngredient;
import io.github.mattidragon.tlaapi.api.recipe.TlaRecipe;
import io.github.mattidragon.tlaapi.api.recipe.TlaStack;
import io.github.mattidragon.tlaapi.api.recipe.TlaStackComparison;
import io.github.mattidragon.tlaapi.impl.rei.util.TlaDragHandler;
import io.github.mattidragon.tlaapi.impl.rei.util.TlaExclusionZoneProvider;
import io.github.mattidragon.tlaapi.impl.rei.util.TlaScreenSizeProvider;
import me.shedaniel.math.Rectangle;
import me.shedaniel.rei.api.client.plugins.REIClientPlugin;
import me.shedaniel.rei.api.client.registry.category.CategoryRegistry;
import me.shedaniel.rei.api.client.registry.display.DisplayCategory;
import me.shedaniel.rei.api.client.registry.display.DisplayRegistry;
import me.shedaniel.rei.api.client.registry.entry.EntryRegistry;
import me.shedaniel.rei.api.client.registry.screen.ExclusionZones;
import me.shedaniel.rei.api.client.registry.screen.ScreenRegistry;
import me.shedaniel.rei.api.client.registry.screen.SimpleClickArea;
import me.shedaniel.rei.api.common.entry.comparison.EntryComparator;
import me.shedaniel.rei.api.common.entry.comparison.FluidComparatorRegistry;
import me.shedaniel.rei.api.common.entry.comparison.ItemComparatorRegistry;
import me.shedaniel.rei.api.common.category.CategoryIdentifier;
import me.shedaniel.rei.api.common.display.Display;
import me.shedaniel.rei.api.common.plugins.PluginManager;
import me.shedaniel.rei.api.common.registry.ReloadStage;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.fluid.Fluid;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemConvertible;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.Recipe;
import net.minecraft.recipe.RecipeEntry;
import net.minecraft.recipe.RecipeType;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.util.Identifier;

import java.util.*;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Function;
import java.util.function.Predicate;

public class TlaApiReiPlugin implements REIClientPlugin, PluginContext {
    private final Map<CategoryIdentifier<?>, BuiltInCategory> builtInCategories = new HashMap<>();
    private final Map<TlaCategory, TlaDisplayCategory> categories = new HashMap<>();
    private final Multimap<TlaCategory, TlaIngredient> workstations = HashMultimap.create();
    private final List<RecipeGenerator<?>> recipeGenerators = new ArrayList<>();
    private final List<Function<MinecraftClient, List<TlaRecipe>>> customGenerators = new ArrayList<>();
    private final List<TlaDragHandler<?>> stackDragHandlers = new ArrayList<>();
    private final List<TlaScreenSizeProvider<?>> screenSizeProviders = new ArrayList<>();
    private final List<TlaExclusionZoneProvider<?>> exclusionZoneProviders = new ArrayList<>();
    private final List<ClickAreaTuple<?>> clickAreas = new ArrayList<>();
    private final List<Comparator<Item, ItemStack>> itemComparators = new ArrayList<>();
    private final List<Comparator<Fluid, FluidStack>> fluidComparators = new ArrayList<>();

    private Predicate<TlaStack> hidePredicate = stack -> false;
    private Predicate<TlaRecipe> recipeHidePredicate = recipe -> false;

    private final Comparisons<ItemConvertible> itemComparisons = new Comparisons<>() {
        @Override
        public void register(ItemConvertible item, TlaStackComparison comparison) {
            itemComparators.add(new Comparator<>(item.asItem(), (context, stack) -> {
                return comparison.hashFunction().hash(TlaStack.of(stack));
            }));
        }

    };
    private final Comparisons<Fluid> fluidComparisons = new Comparisons<>() {
        @Override
        public void register(Fluid fluid, TlaStackComparison comparison) {
            fluidComparators.add(new Comparator<>(fluid, (context, stack) -> {
                return comparison.hashFunction().hash(ReiUtil.convertStack(stack));
            }));
        }
    };

    @Override
    public void preStage(PluginManager<REIClientPlugin> manager, ReloadStage stage) {
        // REI doesn't have a good reload start event, so we have to do this
        if (stage == ReloadStage.START && manager == PluginManager.getClientInstance()) {
            hidePredicate = stack -> false;
            recipeHidePredicate = recipe -> false;
            builtInCategories.clear();
            categories.clear();
            recipeGenerators.clear();
            customGenerators.clear();
            stackDragHandlers.clear();
            screenSizeProviders.clear();
            exclusionZoneProviders.clear();
            clickAreas.clear();
            PluginLoader.loadPlugins(this);
        }
    }

    @Override
    public void registerCategories(CategoryRegistry registry) {
        registry.add(Collections.unmodifiableCollection(categories.values()));
        workstations.forEach((category, workstation) -> registry.addWorkstations(getCategoryIdentifier(category), ReiUtil.convertIngredient(workstation)));
        builtInCategories.forEach((id, category) -> {
            category.category().set(registry.get(id).getCategory());
        });
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

        builtInCategories.forEach((id, category) -> {
            List<? extends Display> displays = registry.get(id);
            if (!displays.isEmpty()) {
                category.display().set(displays.get(0));
            }
        });

        registry.registerVisibilityPredicate((category, display) -> {
            var recipe = ReiTlaRecipe.of(categoryId -> {
                TlaDisplayCategory tla = categories.getOrDefault(categoryId, null);
                if (tla != null) {
                    return tla.category;
                }
                TlaCategory builtIn = builtInCategories.getOrDefault(categoryId, null);
                return builtIn == null ? new BuiltInCategory(categoryId, new int[] {0, }) : builtIn;
            }, display);

            if (recipeHidePredicate.test(recipe)) {
                return EventResult.interruptFalse();
            }

            return EventResult.pass();
        });
    }

    @Override
    public void registerEntries(EntryRegistry registry) {
        registry.removeEntryIf(entry -> hidePredicate.test(ReiUtil.convertStack(entry)));
    }

    private TlaDisplay mapRecipe(TlaRecipe recipe) {
        return new TlaDisplay(getCategoryIdentifier(recipe.getCategory()), recipe);
    }

    @Override
    public void registerScreens(ScreenRegistry registry) {
        stackDragHandlers.forEach(registry::registerDraggableStackVisitor);
        screenSizeProviders.forEach(registry::registerDecider);
        clickAreas.forEach(tuple -> registerClickArea(registry, tuple));
    }

    @SuppressWarnings("unchecked")
    private <T extends Screen> void registerClickArea(ScreenRegistry registry, ClickAreaTuple<T> tuple) {
        SimpleClickArea<T> clickArea = screen -> {
            var bounds = tuple.boundsFunction.apply(screen);
            return new Rectangle(bounds.x(), bounds.y(), bounds.width(), bounds.height());
        };
        if (tuple.handledScreenCoords()) {
            if (!HandledScreen.class.isAssignableFrom(tuple.clazz)) {
                throw new IllegalArgumentException("Can't use screen handler coordinates for non-HandledScreen! (%s is not assignable to %s)".formatted(tuple.clazz().getSimpleName(), HandledScreen.class.getSimpleName()));
            }
            // We check that this works manually above
            //noinspection unchecked
            registry.registerContainerClickArea((SimpleClickArea<HandledScreen<ScreenHandler>>) clickArea,
                    (Class<HandledScreen<ScreenHandler>>) tuple.clazz(),
                    getCategoryIdentifier(tuple.category()));
        } else {
            registry.registerClickArea(clickArea, tuple.clazz(), getCategoryIdentifier(tuple.category()));
        }
    }

    @Override
    public void registerExclusionZones(ExclusionZones zones) {
        exclusionZoneProviders.forEach(provider -> zones.register(provider.clazz, provider));
    }

    @Override
    public void registerItemComparators(ItemComparatorRegistry registry) {
        itemComparators.forEach(comparator -> registry.register(comparator.comparator(), comparator.key()));
    }

    @Override
    public void registerFluidComparators(FluidComparatorRegistry registry) {
        fluidComparators.forEach(comparator -> registry.register(comparator.comparator(), comparator.key()));
    }

    @Override
    public void addCategory(TlaCategory category) {
        if (category instanceof BuiltInCategory) {
            return;
        }
        categories.put(category, new TlaDisplayCategory(category));
    }

    @Override
    public Optional<TlaCategory> getVanillaCategory(BuiltInRecipeCategory type) {
        return Optional.ofNullable(switch (type) {
            case CRAFTING -> CategoryIdentifier.of("minecraft", "plugins/crafting");
            case SMELTING -> CategoryIdentifier.of("minecraft", "plugins/smelting");
            case BLASTING -> CategoryIdentifier.of("minecraft", "plugins/blasting");
            case SMOKING -> CategoryIdentifier.of("minecraft", "plugins/smoking");
            case CAMPFIRE_COOKING -> CategoryIdentifier.of("minecraft", "plugins/campfire");
            case STONECUTTING -> CategoryIdentifier.of("minecraft", "plugins/stone_cutting");
            case SMITHING -> CategoryIdentifier.of("minecraft", "plugins/smithing");
            case ANVIL_REPAIRING -> CategoryIdentifier.of("minecraft", "plugins/anvil");
            case GRINDING -> null;//RecipeTypes.GRINDING;
            case BREWING -> CategoryIdentifier.of("minecraft", "plugins/brewing");
            case BEACON_PAYMENT -> CategoryIdentifier.of("minecraft", "plugins/beacon_payment");
            case WORLD_INTERACTION_BEACON_PYRAMID -> CategoryIdentifier.of("minecraft", "plugins/beacon_base");
            case WORLD_INTERACTION_OTHER -> null;//RecipeTypes.WORLD_INTERACTION;
            case WORLD_INTERACTION_STRIPPING -> CategoryIdentifier.of("minecraft", "plugins/stripping");
            case WORLD_INTERACTION_SCRAPING -> CategoryIdentifier.of("minecraft", "plugins/wax_scraping");
            case WORLD_INTERACTION_TILLING -> CategoryIdentifier.of("minecraft", "plugins/tilling");
            case WORLD_INTERACTION_FLATTENING -> CategoryIdentifier.of("minecraft", "plugins/pathing");
            case WORLD_INTERACTION_WAXING -> CategoryIdentifier.of("minecraft", "plugins/waxing");
            case WORLD_INTERACTION_OXIDIZING -> CategoryIdentifier.of("minecraft", "plugins/oxidizing");
            case WORLD_INTERACTION_DEOXIDIZING -> CategoryIdentifier.of("minecraft", "plugins/oxidation_scraping");
            case FUEL -> CategoryIdentifier.of("minecraft", "plugins/fuel");
            case COMPOSTING -> CategoryIdentifier.of("minecraft", "plugins/composting");
            case INFO -> CategoryIdentifier.of("roughlyenoughitems", "plugins/information");
        }).map(identifier -> builtInCategories.computeIfAbsent(identifier, id -> new BuiltInCategory(id, type.getSize())));
    }

    private CategoryIdentifier<?> getCategoryIdentifier(TlaCategory category) {
        if (category instanceof BuiltInCategory builtIn) {
            return builtIn.id();
        }
        return Objects.requireNonNull(categories.get(category), "Category " + category + " not registered").getCategoryIdentifier();
    }

    @Override
    public void addWorkstation(TlaCategory category, TlaIngredient... workstations) {
        this.workstations.putAll(category, List.of(workstations));
    }

    @Override
    public <I extends Inventory, T extends Recipe<I>> void addRecipeGenerator(RecipeType<T> type, Function<RecipeEntry<T>, TlaRecipe> generator) {
        recipeGenerators.add(new RecipeGenerator<>(type, generator));
    }

    @Override
    public void addGenerator(Function<MinecraftClient, List<TlaRecipe>> generator) {
        customGenerators.add(generator);
    }

    @Override
    public void removeStacks(Predicate<TlaStack> predicate) {
        hidePredicate = hidePredicate.or(predicate);
    }

    @Override
    public void removeRecipes(Predicate<TlaRecipe> predicate) {
        recipeHidePredicate = recipeHidePredicate.and(predicate);
    }

    @Override
    public <T extends Screen> void addClickArea(Class<T> clazz, TlaCategory category, Function<T, TlaBounds> boundsFunction) {
        clickAreas.add(new ClickAreaTuple<>(clazz, category, boundsFunction, false));
    }

    @Override
    public <T extends HandledScreen<?>> void addScreenHandlerClickArea(Class<T> clazz, TlaCategory category, Function<T, TlaBounds> boundsFunction) {
        clickAreas.add(new ClickAreaTuple<>(clazz, category, boundsFunction, true));
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

    @Override
    public Comparisons<ItemConvertible> getItemComparisons() {
        return itemComparisons;
    }

    @Override
    public Comparisons<Fluid> getFluidComparisons() {
        return fluidComparisons;
    }

    @Override
    public RecipeViewer getActiveViewer() {
        return RecipeViewer.REI;
    }

    @Override
    public String toString() {
        return "REI plugin handler";
    }

    private record Comparator<T, S>(T key, EntryComparator<S> comparator) {}
    private record RecipeGenerator<T extends Recipe<?>>(RecipeType<T> type, Function<RecipeEntry<T>, TlaRecipe> generator) {}
    private record ClickAreaTuple<T extends Screen>(Class<T> clazz, TlaCategory category, Function<T, TlaBounds> boundsFunction, boolean handledScreenCoords) {}
    private record BuiltInCategory(
            CategoryIdentifier<?> id,
            int[] fallbackSize,
            AtomicReference<DisplayCategory<?>> category,
            AtomicReference<Display> display
    ) implements TlaCategory {
        private static final CategoryIcon ICON = CategoryIcon.stack(TlaStack.empty());

        public BuiltInCategory(CategoryIdentifier<?> id, int[] fallbackSize) {
            this(id, fallbackSize, new AtomicReference<>(null), new AtomicReference<>(null));
        }

        @Override
        public Identifier getId() {
            return id.getIdentifier();
        }

        @Override
        public int getDisplayHeight() {
            DisplayCategory<?> i = category.get();
            return i == null ? fallbackSize[1] : i.getDisplayHeight();
        }

        @SuppressWarnings({ "rawtypes", "unchecked" })
        @Override
        public int getDisplayWidth() {
            DisplayCategory<?> i = category.get();
            Display d = display.get();
            return i == null || d == null ? fallbackSize[0] : ((DisplayCategory)i).getDisplayWidth(d);
        }

        @Override
        public CategoryIcon getIcon() {
            return ICON;
        }

        @Override
        public CategoryIcon getSimpleIcon() {
            return ICON;
        }
    }

    @SuppressWarnings("unchecked")
    private <T> T unsafeCast(Object o) {
        return (T) o;
    }
}
