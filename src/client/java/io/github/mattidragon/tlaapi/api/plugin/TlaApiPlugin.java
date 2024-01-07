package io.github.mattidragon.tlaapi.api.plugin;

import io.github.mattidragon.tlaapi.impl.PluginOnly;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

/**
 * This interface serves as the entrypoint for all plugins to the TLA API.
 * Plugins should implement this interface and register themselves with the provided {@link PluginContext}.
 * Plugins are registered using {@code tla-api} entrypoint in their {@code fabric.mod.json}.
 * @see PluginContext
 */
@PluginOnly
public interface TlaApiPlugin {
    /**
     * Called when the plugin is registered.
     * This method is called whenever a recipe viewer is reloaded. This can happen multiple times during the game.
     * Plugins should register everything each time this method is called.
     */
    void register(PluginContext context);
}
