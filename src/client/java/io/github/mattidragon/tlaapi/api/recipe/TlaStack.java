package io.github.mattidragon.tlaapi.api.recipe;

import net.fabricmc.fabric.api.transfer.v1.fluid.FluidConstants;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant;
import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant;
import net.minecraft.fluid.Fluid;
import net.minecraft.item.Item;
import net.minecraft.item.ItemConvertible;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;

import java.util.Objects;
import java.util.OptionalDouble;

/**
 * Represents a stack of items or fluids for use with recipe viewers.
 */
@SuppressWarnings("OptionalUsedAsFieldOrParameterType")
public sealed abstract class TlaStack {
    protected final long amount;
    protected final OptionalDouble chance;

    protected TlaStack(long amount, OptionalDouble chance) {
        this.amount = amount;
        this.chance = chance;
    }

    /**
     * Creates a stack of 0 air items.
     */
    public static TlaStack empty() {
        return of(Items.AIR, 0);
    }

    public static TlaFluidStack of(FluidVariant fluid, long amount) {
        return new TlaFluidStack(fluid, amount, OptionalDouble.empty());
    }

    public static TlaFluidStack of(Fluid fluid, long amount) {
        return of(FluidVariant.of(fluid), amount);
    }

    public static TlaFluidStack bucketOf(FluidVariant fluid) {
        return of(fluid, FluidConstants.BUCKET);
    }

    public static TlaFluidStack bucketOf(Fluid fluid) {
        return bucketOf(FluidVariant.of(fluid));
    }

    public static TlaItemStack of(ItemVariant item, long amount) {
        return new TlaItemStack(item, amount, OptionalDouble.empty());
    }

    public static TlaItemStack of(ItemVariant item) {
        return of(item, 1);
    }

    public static TlaItemStack of(ItemConvertible item, long amount) {
        return of(ItemVariant.of(item), amount);
    }

    public static TlaItemStack of(ItemConvertible item) {
        return of(item, 1);
    }

    public static TlaItemStack of(ItemStack stack) {
        return of(ItemVariant.of(stack), stack.getCount());
    }

    public long getAmount() {
        return amount;
    }

    public OptionalDouble getChance() {
        return chance;
    }

    /**
     * Returns a copy of this stack with the provided amount.
     */
    public abstract TlaStack withAmount(long amount);

    /**
     * Returns a copy of this stack with the provided chance.
     * @implNote The chances for each stack in an ingredient are averaged for display in EMI.
     */
    public abstract TlaStack withChance(double chance);

    /**
     * Returns a copy of this stack without a chance.
     */
    public abstract TlaStack withoutChance();

    public TlaIngredient asIngredient() {
        return TlaIngredient.ofStacks(this);
    }

    public static final class TlaFluidStack extends TlaStack {
        private final FluidVariant fluid;

        private TlaFluidStack(FluidVariant fluid, long amount, OptionalDouble chance) {
            super(amount, chance);
            this.fluid = fluid;

        }

        @Override
        public TlaFluidStack withAmount(long amount) {
            return new TlaFluidStack(fluid, amount, chance);
        }

        @Override
        public TlaFluidStack withChance(double chance) {
            return new TlaFluidStack(fluid, amount, OptionalDouble.of(chance));
        }

        @Override
        public TlaFluidStack withoutChance() {
            return new TlaFluidStack(fluid, amount, OptionalDouble.empty());
        }

        public FluidVariant getFluidVariant() {
            return fluid;
        }

        public Fluid getFluid() {
            return fluid.getFluid();
        }

        @Override
        public boolean equals(Object obj) {
            if (obj == this) return true;
            if (obj == null || obj.getClass() != this.getClass()) return false;
            var that = (TlaFluidStack) obj;
            return Objects.equals(this.fluid, that.fluid) &&
                   this.amount == that.amount &&
                   Objects.equals(this.chance, that.chance);
        }

        @Override
        public int hashCode() {
            return Objects.hash(fluid, amount, chance);
        }

        @Override
        public String toString() {
            return "TlaFluidStack[" +
                   "fluid=" + fluid + ", " +
                   "amount=" + amount + ", " +
                   "chance=" + chance + ']';
        }
    }

    public static final class TlaItemStack extends TlaStack {
        private final ItemVariant item;

        private TlaItemStack(ItemVariant item, long amount, OptionalDouble chance) {
            super(amount, chance);
            this.item = item;
        }

        @Override
        public TlaItemStack withAmount(long amount) {
            return new TlaItemStack(item, amount, chance);
        }

        @Override
        public TlaItemStack withChance(double chance) {
            return new TlaItemStack(item, amount, OptionalDouble.of(chance));
        }

        @Override
        public TlaItemStack withoutChance() {
            return new TlaItemStack(item, amount, OptionalDouble.empty());
        }

        public Item getItem() {
            return item.getItem();
        }

        public ItemVariant getItemVariant() {
            return item;
        }

        public ItemStack toStack() {
            return item.toStack((int) amount);
        }

        @Override
        public boolean equals(Object obj) {
            if (obj == this) return true;
            if (obj == null || obj.getClass() != this.getClass()) return false;
            var that = (TlaItemStack) obj;
            return Objects.equals(this.item, that.item) &&
                   this.amount == that.amount &&
                   Objects.equals(this.chance, that.chance);
        }

        @Override
        public int hashCode() {
            return Objects.hash(item, amount, chance);
        }

        @Override
        public String toString() {
            return "TlaItemStack[" +
                   "item=" + item + ", " +
                   "amount=" + amount + ", " +
                   "chance=" + chance + ']';
        }
    }
}
