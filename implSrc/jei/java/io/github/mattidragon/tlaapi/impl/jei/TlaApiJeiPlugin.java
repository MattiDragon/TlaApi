package io.github.mattidragon.tlaapi.impl.jei;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;

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
import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.constants.RecipeTypes;
import mezz.jei.api.fabric.constants.FabricTypes;
import mezz.jei.api.gui.handlers.IGhostIngredientHandler;
import mezz.jei.api.gui.handlers.IGuiClickableArea;
import mezz.jei.api.gui.handlers.IGuiContainerHandler;
import mezz.jei.api.gui.handlers.IGuiProperties;
import mezz.jei.api.ingredients.ITypedIngredient;
import mezz.jei.api.registration.IGuiHandlerRegistration;
import mezz.jei.api.registration.IRecipeCatalystRegistration;
import mezz.jei.api.registration.IRecipeCategoryRegistration;
import mezz.jei.api.registration.IRecipeRegistration;
import mezz.jei.api.registration.ISubtypeRegistration;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.util.math.Rect2i;
import net.minecraft.fluid.Fluid;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemConvertible;
import net.minecraft.recipe.Recipe;
import net.minecraft.recipe.RecipeManager;
import net.minecraft.recipe.RecipeType;
import net.minecraft.util.Identifier;

import java.util.*;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

@JeiPlugin
public class TlaApiJeiPlugin implements IModPlugin, PluginContext {
    private final Map<Identifier, BuiltInTlaCategory> builtInCategories = new HashMap<>();
    private final List<TlaCategory> categories = new ArrayList<>();
    private final List<BiFunction<MinecraftClient, RecipeManager, Stream<TlaRecipe>>> recipeFunctions = new ArrayList<>();
    private final Multimap<TlaCategory, TlaIngredient> workstations = HashMultimap.create();
    private final List<ClickAreaTuple<?>> clickAreas = new ArrayList<>();
    private final List<GhostHandler<?>> ghostHandlers = new ArrayList<>();
    private final List<ScreenSizeProvider<?>> sizeProviders = new ArrayList<>();
    private final List<ExclusionZoneProvider<?>> exclusionZoneProviders = new ArrayList<>();
    private final List<Consumer<ISubtypeRegistration>> subTypeProviders = new ArrayList<>();

    private final Map<TlaCategory, TlaRecipeCategory> preparedCategories = new HashMap<>();

    private final Comparisons<ItemConvertible> itemComparisons = new Comparisons<>() {
        @Override
        public void register(ItemConvertible item, TlaStackComparison comparison) {
            subTypeProviders.add(registration -> registration.registerSubtypeInterpreter(item.asItem(), (stack, context) -> {
                return comparison.hashFunction().hash(TlaStack.of(stack)) + "";
            }));
        }

    };
    private final Comparisons<Fluid> fluidComparisons = new Comparisons<>() {
        @Override
        public void register(Fluid fluid, TlaStackComparison comparison) {
            subTypeProviders.add(registration -> registration.registerSubtypeInterpreter(FabricTypes.FLUID_STACK, fluid, (fluidVariant, context) -> {
                return comparison.hashFunction().hash(TlaStack.bucketOf(FluidVariant.of(fluidVariant.getFluid(), fluidVariant.getTag().orElse(null)))) + "";
            }));
        }
    };

    @Override
    public Identifier getPluginUid() {
        return Identifier.of("tla-api", "plugin");
    }

    private void init() {
        builtInCategories.clear();
        categories.clear();
        recipeFunctions.clear();
        workstations.clear();
        preparedCategories.clear();
        subTypeProviders.clear();
        PluginLoader.loadPlugins(this);
    }

    @Override
    public void registerCategories(IRecipeCategoryRegistration registration) {
        // This is the earliest event, and we don't get anything before that.
        init();

        for (var category : categories) {
            var jeiCategory = new TlaRecipeCategory(registration.getJeiHelpers(), category);
            preparedCategories.put(category, jeiCategory);
            registration.addRecipeCategories(jeiCategory);
        }
    }

    @Override
    public void registerItemSubtypes(ISubtypeRegistration registration) {
        subTypeProviders.forEach(provider -> provider.accept(registration));
    }

    @Override
    public void registerRecipeCatalysts(IRecipeCatalystRegistration registration) {
        workstations.forEach((category, workstations) ->
                JeiUtils.convertIngredient(registration.getJeiHelpers(), workstations).forEach(workstation -> addCatalyst(registration, category, workstation)));
    }

    private <T> void addCatalyst(IRecipeCatalystRegistration registration, TlaCategory category, ITypedIngredient<T> workstation) {
        registration.addRecipeCatalyst(workstation.getType(), workstation.getIngredient(), getRecipeType(category));
    }

    @SuppressWarnings("unchecked")
    private mezz.jei.api.recipe.RecipeType<PreparedRecipe> getRecipeType(TlaCategory category) {
        if (category instanceof BuiltInTlaCategory builtIn) {
            return (mezz.jei.api.recipe.RecipeType<PreparedRecipe>)builtIn.type();
        }
        return Objects.requireNonNull(preparedCategories.get(category), "Category " + category.getId() + " must be registered").getRecipeType();
    }

    @Override
    public void registerRecipes(IRecipeRegistration registration) {
        var client = MinecraftClient.getInstance();
        var manager = Objects.requireNonNull(client.world, "World should exist").getRecipeManager();
        var jeiHelpers = registration.getJeiHelpers();

        recipeFunctions.stream()
                .flatMap(function -> function.apply(client, manager))
                .map(recipe -> new PreparedRecipe(jeiHelpers, recipe))
                .collect(Collectors.groupingBy(PreparedRecipe::getCategory))
                .forEach((category, recipes) -> registration.addRecipes(getRecipeType(category), recipes));
    }

    @Override
    public void registerGuiHandlers(IGuiHandlerRegistration registration) {
        class Adders {
            <T extends HandledScreen<?>> void addClickArea(ClickAreaTuple<T> tuple) {
                registration.addGuiContainerHandler(tuple.clazz, new IGuiContainerHandler<>() {
                    @Override
                    public Collection<IGuiClickableArea> getGuiClickableAreas(T containerScreen, double guiMouseX, double guiMouseY) {
                        var bounds = tuple.boundsFunction.apply(containerScreen);
                        var area = IGuiClickableArea.createBasic(bounds.x(), bounds.y(), bounds.width(), bounds.height(), getRecipeType(tuple.category));
                        return List.of(area);
                    }
                });
            }

            <T extends Screen> void addSizeProvider(ScreenSizeProvider<T> provider) {
                registration.addGuiScreenHandler(provider.clazz, screen -> {
                    var bounds = provider.provider.apply(screen);
                    return new IGuiProperties() {
                        @Override
                        public Class<? extends Screen> getScreenClass() {
                            return provider.clazz;
                        }

                        @Override
                        public int getGuiLeft() {
                            return bounds.left();
                        }

                        @Override
                        public int getGuiTop() {
                            return bounds.top();
                        }

                        @Override
                        public int getGuiXSize() {
                            return bounds.width();
                        }

                        @Override
                        public int getGuiYSize() {
                            return bounds.height();
                        }

                        @Override
                        public int getScreenWidth() {
                            return screen.width;
                        }

                        @Override
                        public int getScreenHeight() {
                            return screen.height;
                        }
                    };
                });
            }

            <T extends Screen> void addGhostHandler(GhostHandler<T> ghostHandler) {
                var handler = ghostHandler.handler;
                registration.addGhostIngredientHandler(ghostHandler.clazz, new IGhostIngredientHandler<>() {
                    @Override
                    public <I> List<Target<I>> getTargetsTyped(T gui, ITypedIngredient<I> typedIngredient, boolean doStart) {
                        if (!handler.appliesTo(gui)) return List.of();
                        return handler.getDropTargets(gui)
                                .stream()
                                .<Target<I>>map(target -> new Target<>() {
                                    @Override
                                    public Rect2i getArea() {
                                        var bounds = target.bounds();
                                        return new Rect2i(bounds.x(), bounds.y(), bounds.width(), bounds.height());
                                    }

                                    @Override
                                    public void accept(I ingredient) {
                                        target.ingredientConsumer().apply(JeiUtils.convertStack(typedIngredient));
                                    }
                                })
                                .toList();
                    }

                    @Override
                    public void onComplete() {
                    }
                });
            }

            <T extends HandledScreen<?>> void addExclusionZoneProvider(ExclusionZoneProvider<T> provider) {
                registration.addGuiContainerHandler(provider.clazz, new IGuiContainerHandler<>() {
                    @Override
                    public List<Rect2i> getGuiExtraAreas(T containerScreen) {
                        return StreamSupport.stream(provider.provider.apply(containerScreen).spliterator(), false)
                                .map(bounds -> new Rect2i(bounds.x(), bounds.y(), bounds.width(), bounds.height()))
                                .toList();
                    }
                });
            }
        }
        var adders = new Adders();
        clickAreas.forEach(adders::addClickArea);
        ghostHandlers.forEach(adders::addGhostHandler);
        sizeProviders.forEach(adders::addSizeProvider);
        exclusionZoneProviders.forEach(adders::addExclusionZoneProvider);
    }

    @Override
    public void addCategory(TlaCategory category) {
        if (category instanceof BuiltInTlaCategory) {
            return;
        }
        categories.add(category);
    }

    @Override
    public Optional<TlaCategory> getVanillaCategory(BuiltInRecipeCategory type) {
        return Optional.ofNullable(switch (type) {
            case CRAFTING -> RecipeTypes.CRAFTING;
            case SMELTING -> RecipeTypes.SMELTING;
            case BLASTING -> RecipeTypes.BLASTING;
            case SMOKING -> RecipeTypes.SMOKING;
            case CAMPFIRE_COOKING -> RecipeTypes.CAMPFIRE_COOKING;
            case STONECUTTING -> RecipeTypes.STONECUTTING;
            case SMITHING -> RecipeTypes.SMITHING;
            case ANVIL_REPAIRING -> RecipeTypes.ANVIL;
            case GRINDING -> null;//RecipeTypes.GRINDING;
            case BREWING -> RecipeTypes.BREWING;
            case BEACON_PAYMENT -> null;
            case WORLD_INTERACTION_BEACON_PYRAMID, WORLD_INTERACTION_STRIPPING, WORLD_INTERACTION_SCRAPING,
                WORLD_INTERACTION_TILLING, WORLD_INTERACTION_FLATTENING, WORLD_INTERACTION_WAXING,
                WORLD_INTERACTION_OXIDIZING, WORLD_INTERACTION_DEOXIDIZING, WORLD_INTERACTION_OTHER -> null;//RecipeTypes.WORLD_INTERACTION;
            case FUEL -> RecipeTypes.FUELING;
            case COMPOSTING -> RecipeTypes.COMPOSTING;
            case INFO -> RecipeTypes.INFORMATION;
        }).map(jeiCategory -> builtInCategories.computeIfAbsent(jeiCategory.getUid(), id -> new BuiltInTlaCategory(jeiCategory, id, type.getSize())));
    }

    @Override
    public void addWorkstation(TlaCategory category, TlaIngredient... workstations) {
        this.workstations.putAll(category, Arrays.asList(workstations));
    }

    @Override
    public <I extends Inventory, T extends Recipe<I>> void addRecipeGenerator(RecipeType<T> type, Function<T, TlaRecipe> generator) {
        recipeFunctions.add((client, manager) -> manager.listAllOfType(type).stream().map(generator));
    }

    @Override
    public void addGenerator(Function<MinecraftClient, List<TlaRecipe>> generator) {
        recipeFunctions.add((client, manager) -> generator.apply(client).stream());
    }

    @Override
    public void removeStacks(Predicate<TlaStack> predicate) {
        // JEI doesn't support removing stacks
    }

    @Override
    public void removeStacks(TlaStack stack) {
        // JEI doesn't support removing stacks
    }

    @Override
    public void removeRecipes(Predicate<TlaRecipe> predicate) {
        // JEI doesn't support removing recipes
    }

    @Override
    public void removeRecipes(Identifier id) {
       // JEI doesn't support removing recipes
    }

    @Override
    public <T extends Screen> void addClickArea(Class<T> clazz, TlaCategory category, Function<T, TlaBounds> boundsFunction) {
        // JEI doesn't support click areas for non-handled screens
    }

    @Override
    public <T extends HandledScreen<?>> void addScreenHandlerClickArea(Class<T> clazz, TlaCategory category, Function<T, TlaBounds> boundsFunction) {
        clickAreas.add(new ClickAreaTuple<>(clazz, category, boundsFunction));
    }

    @Override
    public <T extends Screen> void addStackDragHandler(Class<T> clazz, StackDragHandler<T> handler) {
        ghostHandlers.add(new GhostHandler<>(clazz, handler));
    }

    @Override
    public <T extends Screen> void addScreenSizeProvider(Class<T> clazz, Function<T, TlaBounds> provider) {
        sizeProviders.add(new ScreenSizeProvider<>(clazz, provider));
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T extends Screen> void addExclusionZoneProvider(Class<T> clazz, Function<T, ? extends Iterable<TlaBounds>> provider) {
        if (!HandledScreen.class.isAssignableFrom(clazz)) return; // JEI only supports exclusion zones on handled screens
        exclusionZoneProviders.add(new ExclusionZoneProvider<>((Class<HandledScreen<?>>) clazz, (Function<HandledScreen<?>, ? extends Iterable<TlaBounds>>) provider));
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
        return RecipeViewer.JEI;
    }

    private record ClickAreaTuple<T extends HandledScreen<?>>(Class<T> clazz, TlaCategory category, Function<T, TlaBounds> boundsFunction) {}
    private record GhostHandler<T extends Screen>(Class<T> clazz, StackDragHandler<T> handler) {}
    private record ScreenSizeProvider<T extends Screen>(Class<T> clazz, Function<T, TlaBounds> provider) {}
    private record ExclusionZoneProvider<T extends HandledScreen<?>>(Class<T> clazz, Function<T, ? extends Iterable<TlaBounds>> provider) {}
    private record BuiltInTlaCategory(mezz.jei.api.recipe.RecipeType<?> type, Identifier id, int[] size) implements TlaCategory {
        static final CategoryIcon ICON = CategoryIcon.stack(TlaStack.empty());

        @Override
        public Identifier getId() {
            return id;
        }

        @Override
        public int getDisplayHeight() {
            return size[1];
        }

        @Override
        public int getDisplayWidth() {
            return size[0];
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
}
