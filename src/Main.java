import chernorizets.bg.syllabifier.Syllabifier;

import java.util.Arrays;
import java.util.List;

public class Main {
    static List<String> testCases = Arrays.asList(
            "в", "с", "у", "о", "ѝ",
            "аз", "ти", "той", "тя",
            "във", "със", "принц", "спринт", "глист",
            "ами", "ала", "ако", "уви", "или",
            "саламура", "барабан", "сполука", "щавя", "стрина",
            "старицата", "получените", "подобаващите", "безименен", "изопачавам",
            "койот", "майонеза", "пейоративен", "майор",
            "воал", "маоизъм", "феерия", "воайор", "миокард",
            "нащрек", "поощрявам", "защриховам", "поощрителен"
    );

    public static void main(String[] args) {
        testCases.stream()
                .map(Main::formatSyllables)
                .forEach(System.out::println);
    }

    private static String formatSyllables(String word) {
        Syllabifier syllabifier = new Syllabifier();

        return String.format("%s --> %s",
                word,
                String.join("-", syllabifier.syllabify(word)));
    }
}