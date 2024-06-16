package io.github.mattidragon.tlaapi.testmod.content;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.recipe.RecipeType;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.resource.featuretoggle.FeatureSet;
import net.minecraft.screen.NamedScreenHandlerFactory;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.server.command.CommandManager;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

public class TestMod implements ModInitializer {
    public static final RecipeType<TestRecipe> RECIPE_TYPE = Registry.register(Registries.RECIPE_TYPE, Identifier.of("testmod:test_recipe"), new RecipeType<TestRecipe>() {
        @Override
        public String toString() {
            return "testmod:test_recipe";
        }
    });
    public static final RecipeSerializer<TestRecipe> RECIPE_SERIALIZER = new TestRecipe.Serializer();
    public static final ScreenHandlerType<TestScreenHandler> SCREEN_HANDLER_TYPE = new ScreenHandlerType<>(TestScreenHandler::new, FeatureSet.empty());

    @Override
    public void onInitialize() {
        Registry.register(Registries.RECIPE_SERIALIZER, "testmod:test_recipe", RECIPE_SERIALIZER);
        Registry.register(Registries.SCREEN_HANDLER, "testmod:test_screen_handler", SCREEN_HANDLER_TYPE);

        CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> {
            dispatcher.register(CommandManager.literal("testmod").executes(context -> {
                context.getSource().getPlayerOrThrow().openHandledScreen(new NamedScreenHandlerFactory() {
                    @Override
                    public Text getDisplayName() {
                        return Text.of("Test Screen");
                    }

                    @Override
                    public ScreenHandler createMenu(int syncId, PlayerInventory playerInventory, PlayerEntity player) {
                        return new TestScreenHandler(syncId, playerInventory);
                    }
                });
                return 1;
            }));
        });
    }
}
