package io.github.mattidragon.tlaapi.testmod;

import io.github.mattidragon.tlaapi.api.StackDragHandler;
import io.github.mattidragon.tlaapi.api.gui.TlaBounds;
import io.github.mattidragon.tlaapi.api.plugin.PluginContext;
import io.github.mattidragon.tlaapi.api.plugin.TlaApiPlugin;
import io.github.mattidragon.tlaapi.api.recipe.TlaStack;
import io.github.mattidragon.tlaapi.testmod.content.TestMod;
import io.github.mattidragon.tlaapi.testmod.content.TestScreen;
import net.minecraft.item.Items;

import java.util.List;

public class TestPlugin implements TlaApiPlugin {
    public static final TestCategory TEST_CATEGORY = new TestCategory();

    @Override
    public void register(PluginContext context) {
        context.addCategory(TEST_CATEGORY);
        context.addWorkstation(TEST_CATEGORY, TlaStack.of(Items.DIRT).asIngredient());
        context.addRecipeGenerator(TestMod.RECIPE_TYPE, TestTlaRecipe::new);
        context.addGenerator(client -> List.of(new TestCustomRecipe(), new TestTextDisplay(), new ProgressBarTestRecipe()));
        context.addStackDragHandler(TestScreen.class, screen -> {
            var bounds = new TlaBounds(screen.width / 2, screen.height / 2, 18, 18);
            return List.of(new StackDragHandler.DropTarget(bounds, stack -> {
                if (stack instanceof TlaStack.TlaItemStack itemStack) {
                    screen.filterA = itemStack.getItemVariant();
                    return true;
                }
                return false;
            }));
        });
        context.addScreenSizeProvider(TestScreen.class, screen -> new TlaBounds(screen.getX(), screen.getY(), screen.getWidth(), screen.getHeight()));
        context.addExclusionZoneProvider(TestScreen.class, screen -> List.of(new TlaBounds(screen.getX() + screen.getWidth(), screen.getY(), 100, screen.getHeight())));
        context.addScreenHandlerClickArea(TestScreen.class, TEST_CATEGORY, screen -> new TlaBounds(10, 10, 18, 18));
    }
}
