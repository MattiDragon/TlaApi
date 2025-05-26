package io.github.mattidragon.tlaapi.api.plugin;

import java.util.Locale;

/**
 * Pseudo-enum of recipe viewers. Users of TLA-Api will be given one of these to signify which recipe viewer the plugin is running under.
 * The same plugin may be called multiple times with different viewers if multiple are installed.
 */
public final class RecipeViewer {
    public static final RecipeViewer EMI = new RecipeViewer("emi");
    public static final RecipeViewer REI = new RecipeViewer("rei");
    public static final RecipeViewer JEI = new RecipeViewer("jei");

    private final String id;

    private RecipeViewer(String id) {
        this.id = id;
    }

    public String id() {
        return id;
    }

    @Override
    public String toString() {
        return "RecipeViewer[%s]".formatted(id.toUpperCase(Locale.ROOT));
    }
}
