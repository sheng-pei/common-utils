package ppl.common.utils.character.ascii;

import java.util.function.Predicate;

public interface MaskCharPredicate extends Predicate<Character> {
    Mask mask();

    @Override
    default boolean test(Character character) {
        if (character == null) {
            return false;
        }

        return mask().isSet(character);
    }

    @Override
    default Predicate<Character> or(Predicate<? super Character> other) {
        if (other instanceof MaskCharPredicate) {
            MaskCharPredicate otherPredicate = (MaskCharPredicate) other;
            Mask mask = mask().bitOr(otherPredicate.mask());
            return (MaskCharPredicate) () -> mask;
        }
        return Predicate.super.or(other);
    }

    @Override
    default Predicate<Character> and(Predicate<? super Character> other) {
        if (other instanceof MaskCharPredicate) {
            MaskCharPredicate otherPredicate = (MaskCharPredicate) other;
            Mask mask = mask().bitAnd(otherPredicate.mask());
            return (MaskCharPredicate) () -> mask;
        }
        return Predicate.super.and(other);
    }

    @Override
    default Predicate<Character> negate() {
        Mask mask = mask().bitNot();
        return (MaskCharPredicate) () -> mask;
    }
}
