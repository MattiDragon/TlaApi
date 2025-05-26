package io.github.mattidragon.tlaapi.api.recipe;

import java.util.Objects;
import java.util.function.BiPredicate;
import java.util.function.Function;

/**
 * A combined equality and hashing function for TlaStacks.
 *
 * @see PluginContext#setDefaultComparison
 */
public record TlaStackComparison(EqualityPredicate equalityPredicate, HashFunction hashFunction) {
    private static final TlaStackComparison COMPARE_NBT = compareData(TlaStack::getNbt);
    public static final TlaStackComparison DEFAULT_COMPARISON = of((a, b) -> true, a -> 0);

    public static TlaStackComparison of() {
        return DEFAULT_COMPARISON;
    }

    public static TlaStackComparison compareNbt() {
        return COMPARE_NBT;
    }


    public static TlaStackComparison of(EqualityPredicate equalityPredicate, HashFunction hashFunction) {
        return new TlaStackComparison(equalityPredicate, hashFunction);
    }

    public static <T> TlaStackComparison compareData(Function<TlaStack, T> function) {
        return of(
                (a, b) -> Objects.equals(function.apply(a), function.apply(b)),
                stack -> Objects.hashCode(function.apply(stack))
        );
    }

    /**
     * A comparison function to determine whether two stacks are equivalent.
     * <p>
     * Returns true if the two input stacks should be considered the same when searching
     * for recipes that take or produce the queried stack.
     */
    public interface EqualityPredicate extends BiPredicate<TlaStack, TlaStack> {

    }

    public interface HashFunction {
        int hash(TlaStack stack);
    }
}
