import chernorizets.bg.syllabifier.Sonority;
import chernorizets.bg.syllabifier.SonorityModel;
import chernorizets.bg.syllabifier.Syllabifier;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

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
            "нащрек", "поощрявам", "защриховам", "поощрителен",
            "джудже", "суджук", "манджа", "калайджия", "авджия",
            "бульон", "фризьор", "кьопоолу", "шедьовър", "гьозум", "ликьор",
            "сестра", "пленник", "майка", "преодолея", "звезда", "спринцовка", "царство", "профашистки",
            "бързо", "малко", "партия", "гледка", "крачка", "цедка", "гланцов", "бездомен", "откачвам",
            "нравствен", "мандраджия", "мизансцен", "пепелник", "пилци",
            "аятолах", "авария", "позиции", "хазяи", "дерибеи", "аншоа",
            "свинщина", "общност", "всъщност", "помощник", "чорапогащник", "нощница", "чудовищност"
    );

    public static void main(String[] args) {
        testCases.stream()
                .map(Main::formatSyllables)
                .forEach(System.out::println);

        System.out.println("\n\n");

        testCases.stream()
                .map(Main::formatSonorityModel)
                .forEach(System.out::println);
    }

    private static String formatSyllables(String word) {
        Syllabifier syllabifier = new Syllabifier();

        return String.format("%s --> %s",
                word,
                String.join("-", syllabifier.syllabify(word)));
    }

    private static String formatSonorityModel(String word) {
        List<Sonority> sonorityModel = SonorityModel.getSonorityModel(word, 0, word.length());
        List<String> ranks = sonorityModel.stream()
                .map(s -> String.valueOf(s.rank()))
                .collect(Collectors.toList());

        return String.format("%s --> %s",
                word,
                String.join("", ranks));
    }
}