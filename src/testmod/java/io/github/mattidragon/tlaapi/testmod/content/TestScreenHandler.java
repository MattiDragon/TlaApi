package io.github.mattidragon.tlaapi.testmod.content;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.ScreenHandler;

public class TestScreenHandler extends ScreenHandler {
    protected TestScreenHandler(int syncId, PlayerInventory inventory) {
        super(TestMod.SCREEN_HANDLER_TYPE, syncId);
    }

    @Override
    public ItemStack quickMove(PlayerEntity player, int slot) {
        return ItemStack.EMPTY;
    }

    @Override
    public boolean canUse(PlayerEntity player) {
        return true;
    }
}
