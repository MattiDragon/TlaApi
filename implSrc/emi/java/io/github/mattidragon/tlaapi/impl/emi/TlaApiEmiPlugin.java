package io.github.mattidragon.tlaapi.impl.emi;

import dev.emi.emi.api.EmiDragDropHandler;
import dev.emi.emi.api.EmiPlugin;
import dev.emi.emi.api.EmiRegistry;
import dev.emi.emi.api.recipe.EmiRecipeCategory;
import dev.emi.emi.api.stack.Comparison;
import dev.emi.emi.api.recipe.VanillaEmiRecipeCategories;
import dev.emi.emi.api.stack.EmiIngredient;
import dev.emi.emi.api.widget.Bounds;
import io.github.mattidragon.tlaapi.api.StackDragHandler;
import io.github.mattidragon.tlaapi.api.BuiltInRecipeCategory;
import io.github.mattidragon.tlaapi.api.gui.TlaBounds;
import io.github.mattidragon.tlaapi.api.plugin.Comparisons;
import io.github.mattidragon.tlaapi.api.plugin.PluginContext;
import io.github.mattidragon.tlaapi.api.plugin.PluginLoader;
import io.github.mattidragon.tlaapi.api.plugin.RecipeViewer;
import io.github.mattidragon.tlaapi.api.recipe.CategoryIcon;
import io.github.mattidragon.tlaapi.api.recipe.TlaCategory;
import io.github.mattidragon.tlaapi.api.recipe.TlaIngredient;
import io.github.mattidragon.tlaapi.api.recipe.TlaRecipe;
import io.github.mattidragon.tlaapi.api.recipe.TlaStackComparison;
import io.github.mattidragon.tlaapi.api.recipe.TlaStack;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.fluid.Fluid;
import net.minecraft.item.ItemConvertible;
import net.minecraft.recipe.Recipe;
import net.minecraft.recipe.RecipeEntry;
import net.minecraft.recipe.RecipeType;
import net.minecraft.recipe.input.RecipeInput;
import net.minecraft.util.Identifier;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Predicate;

public class TlaApiEmiPlugin implements EmiPlugin {
    @Override
    public void register(EmiRegistry registry) {
        PluginLoader.loadPlugins(new EmiImplementation(registry));
    }

    private static final class EmiImplementation implements PluginContext {
        private final EmiRegistry registry;
        private final Map<EmiRecipeCategory, TlaCategory> builtInCategories = new HashMap<>();
        private final Map<TlaCategory, EmiRecipeCategory> categories = new HashMap<>();

        private final Comparisons<ItemConvertible> itemComparisons = new EmiComparisons<>();
        private final Comparisons<Fluid> fluidComparisons = new EmiComparisons<>();

        private EmiImplementation(EmiRegistry registry) {
            this.registry = registry;
        }

        @Override
        public void addCategory(TlaCategory category) {
            if (category instanceof BuiltInCategory) {
                return;
            }
            var emiCategory = new TlaEmiRecipeCategory(category);
            categories.put(category, emiCategory);
            registry.addCategory(emiCategory);
        }

        @Override
        public Optional<TlaCategory> getVanillaCategory(BuiltInRecipeCategory type) {
            return Optional.ofNullable(switch (type) {
                case CRAFTING -> VanillaEmiRecipeCategories.CRAFTING;
                case SMELTING -> VanillaEmiRecipeCategories.SMELTING;
                case BLASTING -> VanillaEmiRecipeCategories.BLASTING;
                case SMOKING -> VanillaEmiRecipeCategories.SMOKING;
                case CAMPFIRE_COOKING -> VanillaEmiRecipeCategories.CAMPFIRE_COOKING;
                case STONECUTTING -> VanillaEmiRecipeCategories.STONECUTTING;
                case SMITHING -> VanillaEmiRecipeCategories.SMITHING;
                case ANVIL_REPAIRING -> VanillaEmiRecipeCategories.ANVIL_REPAIRING;
                case GRINDING -> VanillaEmiRecipeCategories.GRINDING;
                case BREWING -> VanillaEmiRecipeCategories.BREWING;
                case BEACON_PAYMENT -> null;
                case WORLD_INTERACTION_BEACON_PYRAMID, WORLD_INTERACTION_STRIPPING, WORLD_INTERACTION_SCRAPING,
                        WORLD_INTERACTION_TILLING, WORLD_INTERACTION_FLATTENING, WORLD_INTERACTION_WAXING,
                        WORLD_INTERACTION_OXIDIZING, WORLD_INTERACTION_DEOXIDIZING, WORLD_INTERACTION_OTHER -> VanillaEmiRecipeCategories.WORLD_INTERACTION;
                case FUEL -> VanillaEmiRecipeCategories.FUEL;
                case COMPOSTING -> VanillaEmiRecipeCategories.COMPOSTING;
                case INFO -> VanillaEmiRecipeCategories.INFO;
            }).map(category -> builtInCategories.computeIfAbsent(category, id -> new BuiltInCategory(id, type.getSize())));
        }

        @Override
        public void addWorkstation(TlaCategory category, TlaIngredient... workstations) {
            var emiCategory = getEmiCategory(category);
            for (TlaIngredient workstation : workstations) {
                registry.addWorkstation(emiCategory, EmiUtils.convertIngredient(workstation));
            }
        }

        private EmiRecipeCategory getEmiCategory(TlaCategory category) {
            if (category instanceof BuiltInCategory builtIn) {
                return builtIn.category();
            }
            return Objects.requireNonNull(categories.get(category), "Category " + category + " not registered");
        }

        @Override
        public <I extends RecipeInput, T extends Recipe<I>> void addRecipeGenerator(RecipeType<T> type, Function<RecipeEntry<T>, TlaRecipe> generator) {
            registry.getRecipeManager()
                    .listAllOfType(type)
                    .forEach(recipe -> {
                        var tlaRecipe = generator.apply(recipe);
                        var emiRecipe = new TlaEmiRecipe(tlaRecipe, getEmiCategory(tlaRecipe.getCategory()));
                        registry.addRecipe(emiRecipe);
                    });
        }

        @Override
        public void addGenerator(Function<MinecraftClient, List<TlaRecipe>> generator) {
            for (var tlaRecipe : generator.apply(MinecraftClient.getInstance())) {
                var emiRecipe = new TlaEmiRecipe(tlaRecipe, getEmiCategory(tlaRecipe.getCategory()));
                registry.addRecipe(emiRecipe);
            }
        }

        @Override
        public void removeStacks(TlaStack stack) {
            registry.removeEmiStacks(EmiUtils.convertStack(stack));
        }

        @Override
        public void removeStacks(Predicate<TlaStack> predicate) {
            registry.removeEmiStacks(stack -> predicate.test(EmiUtils.convertStack(stack)));
        }

        @Override
        public void removeRecipes(Predicate<TlaRecipe> predicate) {
            registry.removeRecipes(recipe -> {
                return predicate.test(EmiTlaRecipe.of(category -> {
                    if (category instanceof TlaEmiRecipeCategory c) {
                        return c.category;
                    }
                    return new BuiltInCategory(category, new int[] {recipe.getDisplayWidth(), recipe.getDisplayHeight() });
                }, recipe));
            });
        }

        @Override
        public void removeRecipes(Identifier id) {
            registry.removeRecipes(id);
        }

        @Override
        public <T extends Screen> void addClickArea(Class<T> clazz, TlaCategory category, Function<T, TlaBounds> tTlaBoundsFunction) {
            // Emi doesn't provide this feature
        }

        @Override
        public <T extends HandledScreen<?>> void addScreenHandlerClickArea(Class<T> clazz, TlaCategory category, Function<T, TlaBounds> tTlaBoundsFunction) {
            // Emi doesn't provide this feature
        }

        @Override
        public <T extends Screen> void addStackDragHandler(Class<T> clazz, StackDragHandler<T> handler) {
            registry.addDragDropHandler(clazz, new EmiDragDropHandler<T>() {
                @Override
                public boolean dropStack(T screen, EmiIngredient ingredient, int x, int y) {
                    if (!handler.appliesTo(screen)) return false;

                    var stacks = ingredient.getEmiStacks();
                    if (stacks.isEmpty()) return false;
                    var stack = EmiUtils.convertStack(stacks.getFirst());

                    var targets = handler.getDropTargets(screen);
                    for (StackDragHandler.DropTarget target : targets) {
                        if (target.bounds().contains(x, y)) {
                            var accepted = target.ingredientConsumer().apply(stack);
                            if (accepted) return true;
                        }
                    }

                    return false;
                }

                @Override
                public void render(T screen, EmiIngredient dragged, DrawContext draw, int mouseX, int mouseY, float delta) {
                    if (!handler.appliesTo(screen)) return;

                    var targets = handler.getDropTargets(screen);
                    for (StackDragHandler.DropTarget target : targets) {
                        var bounds = target.bounds();
                        draw.fill(bounds.left(), bounds.top(), bounds.right(), bounds.bottom(), 0x8822BB33);
                    }
                }
            });
        }

        @Override
        public <T extends Screen> void addExclusionZoneProvider(Class<T> clazz, Function<T, ? extends Iterable<TlaBounds>> provider) {
            registry.addExclusionArea(clazz, (screen, consumer) -> {
                for (var tlaBounds : provider.apply(screen)) {
                    var bounds = new Bounds(tlaBounds.x(), tlaBounds.y(), tlaBounds.width(), tlaBounds.height());
                    consumer.accept(bounds);
                }
            });
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
            return RecipeViewer.EMI;
        }

        @Override
        public String toString() {
            return "EMI plugin handler";
        }

        private class EmiComparisons<T> implements Comparisons<T> {
            @Override
            public void register(T key, TlaStackComparison comparison) {
                registry.setDefaultComparison(key, Comparison.of(
                        (a, b) -> comparison.equalityPredicate().test(EmiUtils.convertStack(a), EmiUtils.convertStack(b)),
                        stack -> comparison.hashFunction().hash(EmiUtils.convertStack(stack)))
                );
            }
        }
    }

    private record BuiltInCategory(EmiRecipeCategory category, int[] size) implements TlaCategory {
        private static final CategoryIcon ICON = CategoryIcon.stack(TlaStack.empty());

        @Override
        public Identifier getId() {
            return category.getId();
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
