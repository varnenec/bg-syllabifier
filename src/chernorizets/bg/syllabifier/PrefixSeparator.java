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
            // без- family
            "без",

            // из- family
            "безиз", "наиз", "поиз", "произ", "преиз", "неиз", "из",

            // въз- family
            "безвъз", "превъз", "невъз", "въз",

            // раз- family
            "безраз", "предраз", "пораз", "нараз", "прераз", "нераз", "раз",

            // от- family
            "неот", "поот", "от",

            // ending in fricatives
            "екс", "таз",

            // ending in stops
            "пред"
    );

    /**
     * Finds the (zero-based) separation points between
     * morphological prefixes and the rest of the word.
     * <p>
     * Since prefixes can be combined, each separation point
     * corresponds to where one prefix ends and (possibly) the
     * next one starts.
     * <p>
     * By convention, a separation point is the index of the first character
     * after a prefix.
     *
     * @param word the word to check for prefixes
     *
     * @return empty list if no prefixes found, or if the separation points
     * are handled by the sonority model. A non-zero list of string indices otherwise
     */
    static List<Integer> findSeparationPoints(String word) {
        return PREFIXES.stream()
                .filter(word::startsWith)
                .filter(pref -> followedByHigherSonorityCons(pref, word))
                .map(String::length)
                .collect(Collectors.toList());
    }

    private static boolean followedByHigherSonorityCons(String prefix, String word) {
        char prefixLastChar = prefix.charAt(prefix.length() - 1);
        char firstCharAfterPrefix = word.charAt(prefix.length());

        // Prefixes followed by vowels do, in fact, get broken up.
        if (LetterClassifier.isVowel(firstCharAfterPrefix)) return false;

        return getSonorityRank(prefixLastChar) < getSonorityRank(firstCharAfterPrefix);
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
        ).forEach(word -> System.out.println(word + ": " + findSeparationPoints(word)));
    }
}
