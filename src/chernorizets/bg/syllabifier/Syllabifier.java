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

        int firstVowel = -1; int prevSylStart = 0;
        for (int i = 0; i < word.length(); i++) {
            if (LetterClassifier.isVowel(word.charAt(i))) {
                // A vowel, yay!
                if (firstVowel == -1) {
                    firstVowel = i;
                    continue;
                }

                // This is not the first vowel we're seeing.
                // Determine the syllable onset within the
                // consonants between the first vowel and this one.
                int offset = syllableStartOffset(word, firstVowel + 1, i);
                int newSylStart = firstVowel + offset;
                syllables.add(word.substring(prevSylStart, newSylStart));

                firstVowel = i;
                prevSylStart = newSylStart;
            }
        }

        // Add the last syllable
        syllables.add(word.substring(prevSylStart));

        return syllables;
    }

    int syllableStartOffset(String word, int firstCons, int onePastLastCons) {
        int consClusterLen = onePastLastCons - firstCons - 1;
        if (consClusterLen <= 1) return consClusterLen;

        // TODO: implement me
        return 0;
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
