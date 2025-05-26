package io.github.mattidragon.tlaapi.api;

/**
 * Identifiers for the built-in recipe categories that come with the game.
 *
 * Use these with {@link PluginContext#getVanillaCategory} to get reference to an instance of a category in the active plugin.
 */
public enum BuiltInRecipeCategory {
    CRAFTING,
    SMELTING,
    BLASTING,
    SMOKING,
    CAMPFIRE_COOKING,
    STONECUTTING,
    SMITHING,
    ANVIL_REPAIRING,
    GRINDING,
    BREWING,
    FUEL,
    COMPOSTING,
    INFO,
    WORLD_INTERACTION_OTHER,
    WORLD_INTERACTION_STRIPPING,
    WORLD_INTERACTION_TILLING,
    WORLD_INTERACTION_FLATTENING,
    WORLD_INTERACTION_WAXING,
    WORLD_INTERACTION_SCRAPING,
    /**
     * Only supported by REI
     */
    WORLD_INTERACTION_OXIDIZING,
    /**
     * Only supported by REI
     */
    WORLD_INTERACTION_DEOXIDIZING,
    /**
     * Only supported by REI
     */
    WORLD_INTERACTION_BEACON_PYRAMID,
    /**
     * Only supported by REI
     */
    BEACON_PAYMENT;


    public int[] getSize() {
        return switch (this) {
            case CRAFTING -> new int[] {118, 54};
            case SMELTING, BLASTING, SMOKING, CAMPFIRE_COOKING -> new int[] {82, 38};
            case STONECUTTING -> new int[] {76, 18};
            case SMITHING -> new int[] {112, 18};
            case ANVIL_REPAIRING -> new int[] {125, 18};
            case GRINDING -> new int[] {116, 56};
            case BREWING -> new int[] {120, 61};
            case BEACON_PAYMENT -> new int[] {150, 140};
            case WORLD_INTERACTION_BEACON_PYRAMID, WORLD_INTERACTION_STRIPPING, WORLD_INTERACTION_SCRAPING,
                    WORLD_INTERACTION_TILLING, WORLD_INTERACTION_FLATTENING, WORLD_INTERACTION_WAXING,
                    WORLD_INTERACTION_OXIDIZING, WORLD_INTERACTION_DEOXIDIZING, WORLD_INTERACTION_OTHER -> new int[] {125, 18};
            case FUEL -> new int[] {144, 18};
            case COMPOSTING -> new int[] {108, 18};
            case INFO -> new int[] {144, 18};
        };
    }
}
