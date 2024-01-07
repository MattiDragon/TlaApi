package io.github.mattidragon.tlaapi.impl.rei.util;

import io.github.mattidragon.tlaapi.api.gui.TlaBounds;
import me.shedaniel.math.Rectangle;
import me.shedaniel.rei.api.client.registry.screen.DisplayBoundsProvider;
import net.minecraft.client.gui.screen.Screen;
import org.jetbrains.annotations.Nullable;

import java.util.function.Function;

public class TlaScreenSizeProvider<T extends Screen> implements DisplayBoundsProvider<T> {
    private final Class<T> clazz;
    private final Function<T, TlaBounds> provider;

    public TlaScreenSizeProvider(Class<T> clazz, Function<T, TlaBounds> provider) {
        this.clazz = clazz;
        this.provider = provider;
    }

    @Override
    public @Nullable Rectangle getScreenBounds(T screen) {
        var tlaBounds = provider.apply(screen);
        return new Rectangle(tlaBounds.x(), tlaBounds.y(), tlaBounds.width(), tlaBounds.height());
    }

    @Override
    public <R extends Screen> boolean isHandingScreen(Class<R> screen) {
        return clazz.isAssignableFrom(screen);
    }
}
