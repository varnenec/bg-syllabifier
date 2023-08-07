package chernorizets.bg.syllabifier;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import static java.util.Map.entry;

public class Syllabifier {

    // Consonant clusters that exhibit rising sonority, but should be
    // broken up regardless to produce natural-sounding syllables.
    // The breakpoint for clusters of 3 or more consonants can vary -
    // here we provide a zero-based offset within the cluster for each.
    static final Map<String, Integer> SONORITY_EXCEPTION_BREAK = Map.ofEntries(
            entry("км", 1), entry("гм", 1), entry("кн", 1), entry("гн", 1),
            entry("дн", 1), entry("вн", 1), entry("дм", 1), entry("вм", 1),
            entry("зм", 1), entry("зд", 1), entry("зч", 1), entry("зц", 1),
            entry("вк", 1), entry("вг", 1),
            entry("згн", 1), entry("здн", 2), entry("вдж", 1)
    );

    // Consonant clusters that don't follow the rising sonority principle,
    // but should regardless be kept together.
    static final List<String> SONORITY_EXCEPTION_KEEP = Arrays.asList(
            "ств", "св", "вс"
    );

    public List<String> syllabify(String word) {
        String norm = normalize(word);

        if (norm.length() == 0) return List.of();

        long nVowels = countVowels(norm);
        if (nVowels <= 1) return List.of(norm);

        return syllabifyPoly(norm);
    }

    private List<String> syllabifyPoly(String word) {
        List<String> syllables = new ArrayList<>();

        int prevVowel = -1; int prevOnset = 0;
        for (int i = 0; i < word.length(); i++) {
            if (LetterClassifier.isVowel(word.charAt(i))) {
                // A vowel, yay!
                if (prevVowel == -1) {
                    prevVowel = i;
                    continue;
                }

                // This is not the first vowel we're seeing.
                // Determine the syllable onset within the
                // consonants between the first vowel and this one.
                int currOnset = findSyllableOnsetIdx(word, prevVowel, i);
                syllables.add(word.substring(prevOnset, currOnset));

                prevVowel = i;
                prevOnset = currOnset;
            }
        }

        // Add the last syllable
        syllables.add(word.substring(prevOnset));

        return syllables;
    }

    private int findSyllableOnsetIdx(String word, int leftVowel, int rightVowel) {
        int nCons = rightVowel - leftVowel - 1;

        // No consonants - syllable starts on rightVowel
        if (nCons == 0) return  rightVowel;

        // Single consonant between two vowels - starts a syllable
        if (nCons == 1) return leftVowel + 1;

        // ---> Two or more consonants between the vowels <---

        // 'щр' is a syllable onset when in front of a vowel.
        // Although 'щ' + sonorant technically follows rising sonority, syllables
        // like щнV, щлV etc. are unnatural and incorrect.
        if (word.charAt(rightVowel - 2) == 'щ') {
            char penult = word.charAt(rightVowel - 1);

            if (penult == 'р') return (rightVowel - 2);

            if (LetterClassifier.isSonorant(penult)) return (rightVowel - 1);
        }

        List<Sonority> sonorities = SonorityModel.getSonorityModel(word, leftVowel + 1, rightVowel);
        int sonorityBreak = findRisingSonorityBreak(sonorities);

        return fixupSyllableOnset(word, leftVowel, sonorityBreak, rightVowel);
    }

    // Find the first index where we break from the rule of rising sonority
    private int findRisingSonorityBreak(List<Sonority> sonorities) {
        int prevRank = -1;

        for (Sonority curr : sonorities) {
            if (curr.rank() <= prevRank) {
                // Found a break.
                return curr.firstIndex();
            }

            prevRank = curr.rank();
        }

        // There was no rising sonority break. Start syllable at first index.
        return sonorities.get(0).firstIndex();
    }

    private int fixupSyllableOnset(String word, int leftVowel, int sonorityBreak, int rightVowel) {
        // Check for situations where we shouldn't break the cluster.
        boolean matchFound = SONORITY_EXCEPTION_KEEP.stream().anyMatch(
                cluster -> matches(word, cluster, leftVowel + 1, rightVowel)
        );

        if (matchFound) return leftVowel + 1; // syllable onset == beginning of cluster

        // Check for situations where we should break the cluster even if
        // it obeys the principle of rising sonority.
        var maybeCluster = SONORITY_EXCEPTION_BREAK.keySet().stream()
                .filter(cluster -> matches(word, cluster, leftVowel + 1, rightVowel))
                .findFirst();

        return maybeCluster.map(cluster -> {
            int offset = SONORITY_EXCEPTION_BREAK.get(cluster);
            return leftVowel + 1 + offset;
        }).orElse(sonorityBreak);

    }

    private String normalize(String word) {
        if (word == null) return "";

        return word.trim().toLowerCase();
    }

    private boolean matches(String str, String sub, int startIdx, int endIdx) {
        int len = endIdx - startIdx;
        if (len != sub.length()) return false;

        for (int i = startIdx, j = 0; i < endIdx; i++, j++) {
            if (str.charAt(i) != sub.charAt(j)) return false;
        }

        return true;
    }

    private long countVowels(String word) {
        return word.chars()
                .mapToObj(c -> (char) c)
                .filter(LetterClassifier::isVowel)
                .count();
    }
}
