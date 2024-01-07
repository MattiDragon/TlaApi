package io.github.mattidragon.tlaapi.impl;

import io.github.mattidragon.tlaapi.api.plugin.PluginContext;
import io.github.mattidragon.tlaapi.api.plugin.TlaApiPlugin;
import net.fabricmc.loader.api.FabricLoader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TlaApi {
    public static final Logger LOGGER = LoggerFactory.getLogger("TLA Api");

	public static void loadPlugins(PluginContext implementation) {
		var entrypoints = FabricLoader.getInstance().getEntrypoints("tla-api", TlaApiPlugin.class);
		LOGGER.info("Loading {} plugins for {}", entrypoints.size(), implementation);
        for (var entrypoint : entrypoints) {
		 	try {
				entrypoint.register(implementation);
			} catch (RuntimeException e) {
				 throw new RuntimeException("Error while handling tla api plugin " + entrypoint, e);
			}
        }
    }
}