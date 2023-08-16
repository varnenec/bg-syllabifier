package chernorizets.bg.syllabifier;

import java.util.*;
import java.util.stream.Collectors;

import static java.util.Map.entry;

public class Syllabifier {

    static class Context {
        final String word;

        final List<Integer> prefixSeparationPoints;

        Context(String word, List<Integer> separationPoints) {
            this.word = word;
            this.prefixSeparationPoints = Collections.unmodifiableList(separationPoints);
        }
    }

    // Consonant clusters that exhibit rising sonority, but should be
    // broken up regardless to produce natural-sounding syllables.
    // The breakpoint for clusters of 3 or more consonants can vary -
    // here we provide a zero-based offset within the cluster for each.
    static final Map<String, Integer> SONORITY_EXCEPTION_BREAK = Map.ofEntries(
            entry("км", 1), entry("гм", 1), entry("кн", 1), entry("гн", 1),
            entry("дн", 1), entry("вн", 1), entry("дм", 1), entry("вм", 1),
            entry("зм", 1), entry("зд", 1), entry("зч", 1), entry("зц", 1),
            entry("вк", 1), entry("вг", 1), entry("дл", 1), entry("жд", 1),
            entry("ўнл", 2), entry("згн", 1), entry("здн", 2), entry("вдж", 1)
    );

    // Consonant clusters that don't follow the rising sonority principle,
    // but should regardless be kept together.
    static final List<String> SONORITY_EXCEPTION_KEEP = Arrays.asList(
            "ств", "св", "вс"
    );

    public List<String> syllabify(String word) {
        var norm = normalizeWord(word);

        if (norm.isEmpty()) return List.of();

        long nVowels = countVowels(norm);
        var syllables = (nVowels <= 1) ? List.of(norm) : syllabifyPoly(norm);

        return syllables.stream()
                .map(this::normalizeSyllable)
                .collect(Collectors.toList());
    }

    private List<String> syllabifyPoly(String word) {
        var syllables = new ArrayList<String>();

        var ctx = new Context(word, PrefixSeparator.findSeparationPoints(word));

        int prevVowel = -1; int prevOnset = 0;
        for (int i = 0; i < word.length(); i++) {
            if (LetterClassifier.isVowel(word.charAt(i))) {
                // A vowel, yay!
                if (prevVowel == -1) {
                    prevVowel = i;
                    continue;
                }

                // This is not the first vowel we've seen. In-between
                // the previous vowel and this one, there is a syllable
                // break, and the first character after the break starts
                // a new syllable.
                int nextOnset = findNextSyllableOnset(ctx, prevVowel, i);
                syllables.add(word.substring(prevOnset, nextOnset));

                prevVowel = i;
                prevOnset = nextOnset;
            }
        }

        // Add the last syllable
        syllables.add(word.substring(prevOnset));

        return syllables;
    }

    private int findNextSyllableOnset(Context ctx, int leftVowel, int rightVowel) {
        int nCons = rightVowel - leftVowel - 1;

        // No consonants - syllable starts on rightVowel
        if (nCons == 0) return rightVowel;

        // Check for forced breaks
        int breakPos = ForcedBreak.findForcedBreak(ctx.word, leftVowel + 1, rightVowel);
        if (breakPos != -1) return breakPos + 1;

        // Single consonant between two vowels - starts a syllable
        if (nCons == 1) return leftVowel + 1;

        // Two or more consonants between the vowels. Find the point (if any)
        // where we break from rising sonority, and treat it as the tentative
        // onset of a new syllable.
        var sonorities = SonorityModel.getSonorityModel(ctx.word, leftVowel + 1, rightVowel);
        int sonorityBreak = findRisingSonorityBreak(sonorities);

        // Apply exceptions to the rising sonority principle to avoid
        // unnatural-sounding syllables.
        return fixupSyllableOnset(ctx, leftVowel, sonorityBreak, rightVowel);
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

    private int fixupSyllableOnset(Context ctx, int leftVowel, int sonorityBreak, int rightVowel) {
        var word = ctx.word;

        // 'щр' is a syllable onset when in front of a vowel.
        // Although 'щ' + sonorant technically follows rising sonority, syllables
        // like щнV, щлV etc. are unnatural and incorrect. In such cases, we treat
        // the sonorant as the onset of the next syllable.
        if (word.charAt(rightVowel - 2) == 'щ') {
            char penult = word.charAt(rightVowel - 1);

            if (penult == 'р') return (rightVowel - 2);

            if (LetterClassifier.isSonorant(penult)) return (rightVowel - 1);
        }

        // Check for situations where we shouldn't break the cluster.
        var matchFound = SONORITY_EXCEPTION_KEEP.stream().anyMatch(
                cluster -> matches(word, cluster, leftVowel + 1, rightVowel)
        );

        if (matchFound) return leftVowel + 1; // syllable onset == beginning of cluster

        // Check for situations where we should break the cluster even if
        // it obeys the principle of rising sonority.
        var maybeCluster = SONORITY_EXCEPTION_BREAK.keySet().stream()
                .filter(cluster -> matches(word, cluster, leftVowel + 1, rightVowel))
                .findAny()
                .orElse(null);

        if (maybeCluster != null) {
            int offset = SONORITY_EXCEPTION_BREAK.get(maybeCluster);
            return leftVowel + 1 + offset;
        }

        // Check for prefix separation points. If one is found, return
        // that, otherwise return the sonority break.
        List<Integer> separationPoints = ctx.prefixSeparationPoints;
        return separationPoints.stream()
                .filter(pos -> pos > leftVowel && pos < rightVowel)
                .findAny()
                .orElse(sonorityBreak);
    }

    private String normalizeWord(String word) {
        if (word == null) return "";

        return word.trim().toLowerCase();
    }

    private String normalizeSyllable(String syllable) {
        var normalized = ForcedBreak.stripForcedBreaks(syllable);
        normalized = normalized.replaceAll("ў", "у");

        return normalized;
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
