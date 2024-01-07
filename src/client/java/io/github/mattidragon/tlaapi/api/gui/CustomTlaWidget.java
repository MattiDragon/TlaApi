package io.github.mattidragon.tlaapi.api.gui;

import io.github.mattidragon.tlaapi.impl.PluginsExtend;
import net.minecraft.client.gui.Drawable;

@PluginsExtend
public interface CustomTlaWidget extends Drawable {
    TlaBounds getBounds();
}
