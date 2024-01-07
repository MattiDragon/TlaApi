package io.github.mattidragon.tlaapi.testmod.content;

import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.Items;
import net.minecraft.text.Text;

public class TestScreen extends HandledScreen<TestScreenHandler> {
    public ItemVariant filterA = ItemVariant.of(Items.STONE);

    public TestScreen(TestScreenHandler handler, PlayerInventory playerInventory, Text title) {
        super(handler, playerInventory, title);
    }

    @Override
    protected void init() {
        super.init();
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        context.fill(x, y, x + backgroundWidth, y + backgroundHeight, 0xffffffff);
        super.render(context, mouseX, mouseY, delta);
        context.fill(width / 2, height / 2, width / 2 + 18, height / 2 + 18, 0xffff0000);
        context.drawItem(filterA.toStack(), width / 2, height / 2);
    }

    @Override
    protected void drawBackground(DrawContext context, float delta, int mouseX, int mouseY) {

    }

    public int getWidth() {
        return backgroundWidth;
    }

    public int getHeight() {
        return backgroundHeight;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }
}
