package io.github.mattidragon.tlaapi.impl;

import io.github.mattidragon.tlaapi.api.plugin.PluginContext;
import io.github.mattidragon.tlaapi.api.plugin.TlaApiPlugin;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.loader.api.FabricLoader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TlaApi {
    public static final Logger LOGGER = LoggerFactory.getLogger("tla-api");

	public static void loadPlugins(PluginContext implementation) {
		FabricLoader.getInstance().getEntrypoints("tla-api", TlaApiPlugin.class).forEach(entrypoint -> entrypoint.register(implementation));
	}
}