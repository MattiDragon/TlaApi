package io.github.mattidragon.tlaapi.api.recipe;

import net.minecraft.fluid.Fluid;
import net.minecraft.item.Item;
import net.minecraft.item.ItemConvertible;
import net.minecraft.recipe.Ingredient;
import net.minecraft.registry.Registries;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.registry.entry.RegistryEntryList;
import net.minecraft.registry.tag.TagKey;

import java.util.*;
import java.util.stream.Stream;

/**
 * Represents a list of values that can be held by a single slot in a recipe.
 */
public final class TlaIngredient {
    /**
     * An empty ingredient, containing no stacks.
     */
    public static final TlaIngredient EMPTY = new TlaIngredient(List.of());

    private final Collection<TlaStack> stacks;

    private TlaIngredient(Collection<TlaStack> stacks) {
        this.stacks = stacks;
    }

    /**
     * Creates a new ingredient from the provided stacks.
     */
    public static TlaIngredient ofStacks(Collection<? extends TlaStack> stacks) {
        return new TlaIngredient(List.copyOf(stacks));
    }

    /**
     * Creates a new ingredient from the provided stacks.
     */
    public static TlaIngredient ofStacks(TlaStack... stacks) {
        return new TlaIngredient(List.of(stacks));
    }

    /**
     * Creates a new ingredient from the provided vanilla ingredient.
     */
    public static TlaIngredient ofIngredient(Ingredient ingredient) {
        return new TlaIngredient(Arrays.stream(ingredient.getMatchingStacks()).<TlaStack>map(TlaStack::of).toList());
    }

    /**
     * Creates a new ingredient from the provided items.
     */
    public static TlaIngredient ofItems(Collection<? extends ItemConvertible> items) {
        return ofStacks(items.stream().map(TlaStack::of).toList());
    }

    /**
     * Creates a new ingredient from the provided item tag.
     * Note that any tags used here or in recipes should be translated so that they appear nicely in EMI.
     */
    public static TlaIngredient ofItemTag(TagKey<Item> tag) {
        return ofItems(Registries.ITEM.getEntryList(tag)
                .map(RegistryEntryList.Named::stream)
                .orElse(Stream.empty())
                .map(RegistryEntry::value)
                .toList());
    }

    /**
     * Creates a new ingredient from the provided fluids.
     */
    public static TlaIngredient ofFluids(Collection<? extends Fluid> fluids) {
        return ofStacks(fluids.stream().map(TlaStack::bucketOf).toList());
    }

    /**
     * Creates a new ingredient from the provided fluid tag.
     * Note that any tags used here or in recipes should be translated so that they appear nicely in EMI.
     */
    public static TlaIngredient ofFluidTag(TagKey<Fluid> tag) {
        return ofFluids(Registries.FLUID.getEntryList(tag)
                .map(RegistryEntryList.Named::stream)
                .orElse(Stream.empty())
                .map(RegistryEntry::value)
                .toList());
    }

    /**
     * Combines multiple ingredients into a single ingredient.
     * Does not play well with EMIs tag names so should be avoided if possible.
     */
    public static TlaIngredient join(TlaIngredient... ingredients) {
        return new TlaIngredient(Arrays.stream(ingredients).flatMap(ingredient -> ingredient.stacks.stream()).toList());
    }

    /**
     * Combines multiple ingredients into a single ingredient.
     * Does not play well with EMIs tag names so should be avoided if possible.
     */
    public static TlaIngredient join(Collection<? extends TlaIngredient> ingredients) {
        return new TlaIngredient(ingredients.stream().flatMap(ingredient -> ingredient.getStacks().stream()).toList());
    }

    /**
     * Returns the stacks contained in this ingredient.
     */
    public Collection<TlaStack> getStacks() {
        return stacks;
    }

    /**
     * Create a new ingredient where every stack has the provided count.
     * Note that this applies equally to fluids and items.
     */
    public TlaIngredient withAmount(long amount) {
        return new TlaIngredient(stacks.stream().map(stack -> stack.withAmount(amount)).toList());
    }

    /**
     * Create a new ingredient where every stack has the provided chance.
     */
    public TlaIngredient withChance(double chance) {
        return new TlaIngredient(stacks.stream().map(stack -> stack.withChance(chance)).toList());
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) return true;
        if (obj == null || obj.getClass() != this.getClass()) return false;
        var that = (TlaIngredient) obj;
        return Objects.equals(this.stacks, that.stacks);
    }

    @Override
    public int hashCode() {
        return Objects.hash(stacks);
    }

    @Override
    public String toString() {
        return "TlaIngredient[" + stacks + ']';
    }
}
