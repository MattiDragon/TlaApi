package io.github.mattidragon.tlaapi.impl.emi;

import dev.emi.emi.api.EmiDragDropHandler;
import dev.emi.emi.api.EmiPlugin;
import dev.emi.emi.api.EmiRegistry;
import dev.emi.emi.api.recipe.EmiRecipeCategory;
import dev.emi.emi.api.stack.EmiIngredient;
import dev.emi.emi.api.widget.Bounds;
import io.github.mattidragon.tlaapi.api.StackDragHandler;
import io.github.mattidragon.tlaapi.api.gui.TlaBounds;
import io.github.mattidragon.tlaapi.api.plugin.PluginContext;
import io.github.mattidragon.tlaapi.api.plugin.PluginLoader;
import io.github.mattidragon.tlaapi.api.plugin.RecipeViewer;
import io.github.mattidragon.tlaapi.api.recipe.TlaCategory;
import io.github.mattidragon.tlaapi.api.recipe.TlaIngredient;
import io.github.mattidragon.tlaapi.api.recipe.TlaRecipe;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.recipe.Recipe;
import net.minecraft.recipe.RecipeEntry;
import net.minecraft.recipe.RecipeType;
import net.minecraft.recipe.input.RecipeInput;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

public class TlaApiEmiPlugin implements EmiPlugin {
    @Override
    public void register(EmiRegistry registry) {
        PluginLoader.loadPlugins(new EmiImplementation(registry));
    }

    private static final class EmiImplementation implements PluginContext {
        private final EmiRegistry registry;
        private final Map<TlaCategory, EmiRecipeCategory> categories = new HashMap<>();

        private EmiImplementation(EmiRegistry registry) {
            this.registry = registry;
        }

        @Override
        public void addCategory(TlaCategory category) {
            var emiCategory = new TlaEmiRecipeCategory(category);
            categories.put(category, emiCategory);
            registry.addCategory(emiCategory);
        }

        @Override
        public void addWorkstation(TlaCategory category, TlaIngredient... workstations) {
            var emiCategory = categories.get(category);
            if (emiCategory == null) throw new IllegalArgumentException("Category " + category + " not registered");
            for (TlaIngredient workstation : workstations) {
                registry.addWorkstation(emiCategory, EmiUtils.convertIngredient(workstation));
            }
        }

        @Override
        public <I extends RecipeInput, T extends Recipe<I>> void addRecipeGenerator(RecipeType<T> type, Function<RecipeEntry<T>, TlaRecipe> generator) {
            registry.getRecipeManager()
                    .listAllOfType(type)
                    .forEach(recipe -> {
                        var tlaRecipe = generator.apply(recipe);
                        var emiRecipe = new TlaEmiRecipe(tlaRecipe, categories.get(tlaRecipe.getCategory()));
                        registry.addRecipe(emiRecipe);
                    });
        }

        @Override
        public void addGenerator(Function<MinecraftClient, List<TlaRecipe>> generator) {
            for (var tlaRecipe : generator.apply(MinecraftClient.getInstance())) {
                var emiRecipe = new TlaEmiRecipe(tlaRecipe, categories.get(tlaRecipe.getCategory()));
                registry.addRecipe(emiRecipe);
            }
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
        public RecipeViewer getActiveViewer() {
            return RecipeViewer.EMI;
        }

        @Override
        public String toString() {
            return "EMI plugin handler";
        }
    }
}
