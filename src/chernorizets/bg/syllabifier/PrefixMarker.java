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
class PrefixMarker {

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
            "над", "под", "от", "пред"
    );

    static final char PREFIX_SEPARATOR = '|';

    static String markPrefix(String word) {
        var marked = new StringBuilder();

        var matchingPrefixes = PREFIXES.stream()
                .filter(word::startsWith)
                .collect(Collectors.toList());

        if (matchingPrefixes.isEmpty()) return word;

        if (matchingPrefixes.size() > 1) {
            // At present, no prefixes are substrings of each other, so this is a
            // sanity guard.
            throw new RuntimeException("More than one matching prefix: " + matchingPrefixes);
        }

        var matchingPrefix = matchingPrefixes.get(0);
        marked.append(matchingPrefix);

        char prefixLastChar = matchingPrefix.charAt(matchingPrefix.length() - 1);
        char firstCharAfterPrefix = word.charAt(matchingPrefix.length());

        // Prefixes followed by vowels do, in fact, get broken up.
        if (LetterClassifier.isVowel(firstCharAfterPrefix)) return word;

        if (getSonorityRank(prefixLastChar) < getSonorityRank(firstCharAfterPrefix)) {
            // This is precisely the case where rising sonority-based syllable breaking
            // would try to lop off the last consonant of the prefix. Warn the syllabifier
            // against doing that.
            marked.append(PREFIX_SEPARATOR);
        }

        marked.append(word.substring(matchingPrefix.length()));
        return marked.toString();
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
        ).forEach(word -> System.out.println(markPrefix(word)));
    }
}
