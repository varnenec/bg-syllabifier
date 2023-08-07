package chernorizets.bg.syllabifier;

import java.util.Set;

class LetterClassifier {
    static final Set<Character> VOWELS = Set.of(
            'а', 'ъ', 'о', 'у', 'е', 'и', 'ю', 'я'
    );

    static final Set<Character> SONORANTS = Set.of(
            'л', 'м', 'н', 'р', 'й'
    );

    static final Set<Character> STOPS = Set.of(
            'б', 'п', 'г', 'к', 'д', 'т'
    );

    static final Set<Character> FRICATIVES = Set.of(
            'в', 'ф', 'ж', 'ш', 'з', 'с', 'х'
    );

    static final Set<Character> AFFRICATES = Set.of(
            'ч', 'ц'
    );

    static boolean isVowel(char ch) {
        return VOWELS.contains(ch);
    }

    static boolean isConsonant(char ch) {
        return ch == 'щ'
                || isSonorant(ch)
                || isStop(ch)
                || isFricative(ch)
                || isAffricate(ch);
    }

    static boolean isPalatalizer(char ch) {
        return ch == 'ь';
    }

    static boolean isSonorant(char ch) {
        return SONORANTS.contains(ch);
    }

    /*
     * Opposite of sonorant.
     */
    static boolean isObstruent(char ch) {
        return isStop(ch) || isFricative(ch) || isAffricate(ch);
    }

    static boolean isStop(char ch) {
        return STOPS.contains(ch);
    }

    static boolean isFricative(char ch) {
        return FRICATIVES.contains(ch);
    }

    static  boolean isAffricate(char ch) {
        return AFFRICATES.contains(ch);
    }
}
