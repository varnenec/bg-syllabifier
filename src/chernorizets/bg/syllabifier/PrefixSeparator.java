package chernorizets.bg.syllabifier;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import static chernorizets.bg.syllabifier.SonorityModel.getSonorityRank;

/**
 * This class brings morphological prefix awareness to syllabification.
 * <p>
 * This is necessary, because following the principle of rising sonority
 * alone fails to determine syllable boundaries correctly in some cases -
 * that is, when certain prefixes should be kept together as a first syllable.
 */
class PrefixSeparator {

    /**
     * Affected prefixes. Each of them ends in a consonant that can be followed
     * by another consonant of a higher sonority in some words. In such cases,
     * naive syllable breaking would chop off the prefix's last consonant, and
     * glue it to the onset of the next syllable.
     */
    static final List<String> PREFIXES = Arrays.asList(
            // ending in fricatives
            "без", "из", "въз", "раз", "екс", "таз",

            // ending in stops
            "от", "пред"
    );

    /**
     * Finds the (zero-based) separation point between a
     * morphological prefix and the rest of the word.
     * <p>
     * By convention, that's the index of the first character
     * after the prefix.
     *
     * @param word the word to check for prefixes
     *
     * @return -1 if no prefix found, or if the separation point
     * is handled by the sonority model. A non-zero index otherwise
     */
    static int findSeparationPos(String word) {
        var matchingPrefixes = PREFIXES.stream()
                .filter(word::startsWith)
                .collect(Collectors.toList());

        if (matchingPrefixes.isEmpty()) return -1;

        if (matchingPrefixes.size() > 1) {
            // At present, no prefixes are substrings of each other, so this is a
            // sanity guard.
            throw new RuntimeException("More than one matching prefix: " + matchingPrefixes);
        }

        var matchingPrefix = matchingPrefixes.get(0);
        char prefixLastChar = matchingPrefix.charAt(matchingPrefix.length() - 1);
        char firstCharAfterPrefix = word.charAt(matchingPrefix.length());

        // Prefixes followed by vowels do, in fact, get broken up.
        if (LetterClassifier.isVowel(firstCharAfterPrefix)) return -1;

        if (getSonorityRank(prefixLastChar) < getSonorityRank(firstCharAfterPrefix)) {
            // This is precisely the case where rising sonority-based syllable breaking
            // would try to lop off the last consonant of the prefix.
           return matchingPrefix.length();
        }

        return -1;
    }

    public static void main(String ... args) {
        // Tests with a representative prefix
        Arrays.asList(
                "безавариен", "безобразие", "безупречен", "безистен",
                "безсилен", "безшумен", "безвъзвратен", "безхаберен",
                "безстрашен", "безхлебна", "безвремие",
                "безмерен", "безличен", "безнаказан", "безразборен",
                "бездетен", "безпардонен", "безтелесен", "безглав", "безчестен",
                "безпризорен", "безгрешен", "безкраен", "безбрежен", "бездна"
        ).forEach(word -> System.out.println(word + ": " + findSeparationPos(word)));
    }
}
