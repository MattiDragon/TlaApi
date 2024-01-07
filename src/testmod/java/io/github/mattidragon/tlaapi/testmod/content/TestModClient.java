package io.github.mattidragon.tlaapi.testmod.content;

import net.fabricmc.api.ClientModInitializer;
import net.minecraft.client.gui.screen.ingame.HandledScreens;

public class TestModClient implements ClientModInitializer {

    @Override
    public void onInitializeClient() {
        HandledScreens.register(TestMod.SCREEN_HANDLER_TYPE, TestScreen::new);
    }
}
