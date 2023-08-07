package chernorizets.bg.syllabifier;

import java.util.ArrayList;
import java.util.List;

public class Syllabifier {
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

    int findSyllableOnsetIdx(String word, int leftVowel, int rightVowel) {
        int consClusterLen = rightVowel - leftVowel - 1;

        // No consonants - syllable starts on rightVowel
        if (consClusterLen == 0) return  rightVowel;

        // Single consonant between two vowels - starts a syllable
        if (consClusterLen == 1) return leftVowel + 1;

        // ---> Two or more consonants between the vowels <---

        // 'щр' is a syllable onset when in front of a vowel
        // otherwise, it belongs to the previous syllable
        if (word.charAt(rightVowel - 2) == 'щ' && word.charAt(rightVowel - 1) == 'р') {
            return (rightVowel - 2);
        }

        // TODO: implement me
        return leftVowel;
    }

    private String normalize(String word) {
        if (word == null) return "";

        return word.trim().toLowerCase();
    }

    private long countVowels(String word) {
        return word.chars()
                .mapToObj(c -> (char) c)
                .filter(LetterClassifier::isVowel)
                .count();
    }
}
