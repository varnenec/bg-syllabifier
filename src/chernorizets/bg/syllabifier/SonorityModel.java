package chernorizets.bg.syllabifier;

import java.util.ArrayList;
import java.util.List;

public class SonorityModel {

    public static List<Sonority> getSonorityModel(String word, int startIdx, int endIdx) {
        var sonorities = new ArrayList<Sonority>();

        for (int i = startIdx; i < endIdx; i++) {
            char curr = word.charAt(i);

            if (curr == 'щ') {
                // One letter representing 2 sounds - decompose it.
                sonorities.add(new Sonority(getSonorityRank('ш'), i));
                sonorities.add(new Sonority(getSonorityRank('т'), i));
            } else if (curr == 'д') {
                // Handle affricates with 'д' - only 'дж' here for illustration.
                int next = (i == endIdx - 1) ? ' ' : word.charAt(i + 1);

                if (next == 'ж') {
                    sonorities.add(new Sonority(2, i));
                    ++i; // Skip over the 'ж'
                    continue;
                }

                sonorities.add(new Sonority(getSonorityRank('д'), i));
            } else if (!LetterClassifier.isPalatalizer(curr)) {
                // Skip over 'ь' since it doesn't change the sonority.
                sonorities.add(new Sonority(getSonorityRank(curr), i));
            }
        }

        return sonorities;
    }

    public static int getSonorityRank(char ch) {
        if (LetterClassifier.isFricative(ch)) {
            return 1;
        }

        if (LetterClassifier.isStop(ch) || LetterClassifier.isAffricate(ch)) {
            return 2;
        }

        if (LetterClassifier.isSonorant(ch)) {
            return 3;
        }

        if (LetterClassifier.isVowel(ch)) {
            return 4;
        }

        return 0;
    }
}
