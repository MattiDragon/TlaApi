package io.github.mattidragon.tlaapi.impl.rei.util;

import io.github.mattidragon.tlaapi.api.gui.TlaBounds;
import me.shedaniel.math.Rectangle;
import me.shedaniel.rei.api.client.registry.screen.ExclusionZonesProvider;
import net.minecraft.client.gui.screen.Screen;

import java.util.Collection;
import java.util.function.Function;
import java.util.stream.StreamSupport;

public class TlaExclusionZoneProvider<T extends Screen> implements ExclusionZonesProvider<T> {
    public final Class<T> clazz;
    private final Function<T, ? extends Iterable<TlaBounds>> provider;

    public TlaExclusionZoneProvider(Class<T> clazz, Function<T, ? extends Iterable<TlaBounds>> provider) {
        this.clazz = clazz;
        this.provider = provider;
    }

    @Override
    public Collection<Rectangle> provide(T screen) {
        return StreamSupport.stream(provider.apply(screen).spliterator(), false)
                .map(tlaBounds -> new Rectangle(tlaBounds.x(), tlaBounds.y(), tlaBounds.width(), tlaBounds.height()))
                .toList();
    }
}
