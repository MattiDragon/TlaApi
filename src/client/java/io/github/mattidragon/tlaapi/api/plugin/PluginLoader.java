package io.github.mattidragon.tlaapi.api.plugin;

import io.github.mattidragon.tlaapi.impl.ImplementationOnly;
import io.github.mattidragon.tlaapi.impl.TlaApi;

/**
 * Provides implementations with the ability to load plugins.
 */
@ImplementationOnly
public class PluginLoader {
    private PluginLoader() {}

    /**
     * Loads plugins into the provided context.
     */
    public static void loadPlugins(PluginContext implementation) {
        TlaApi.loadPlugins(implementation);
    }
}
