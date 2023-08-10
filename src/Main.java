import chernorizets.bg.syllabifier.Syllabifier;

import java.util.Arrays;
import java.util.List;

class TestCase {
    final String heading;
    final List<String> examples;

    TestCase(String heading, String ... examples) {
        this.heading = heading;
        this.examples = Arrays.asList(examples);
    }
}

class TestCaseRunner {
    private final Syllabifier syllabifier = new Syllabifier();

    void runTests(List<TestCase> testCases) {
        for (var test : testCases) {
            System.out.println(test.heading + ":");

            test.examples.forEach(example -> {
                var formatted = String.format("* %s --> %s",
                        example,
                        String.join("-", syllabifier.syllabify(example)));

                System.out.println(formatted);
            });

            System.out.println();
        }
    }
}

public class Main {

    static final List<TestCase> TEST_CASES = Arrays.asList(
            new TestCase("Single-letter words",
                    "в", "с", "у", "о", "ѝ"
            ),
            new TestCase("Simple monosyllabic words",
                    "аз", "ти", "той", "тя", "във", "със"
            ),
            new TestCase("More complex monosyllabic words",
                    "принц", "спринт", "глист", "скункс"
            ),
            new TestCase("Single consonant between two vowels: 3-letter words",
                    "ами", "ала", "ако", "уви", "или"
            ),
            new TestCase("Single consonant between two vowels: stops and fricatives",
                    "саламура", "барабан", "сполука", "щавя", "стрина", "когато"
            ),
            new TestCase("Single consonant between two vowels: щ, and single-letter affricates",
                    "старицата", "получените", "подобаващите", "обучаващите"
            ),
            new TestCase("Single consonant between two vowels: дж",
                    "джудже", "суджук", "дамаджана", "джаджите"
            ),
            new TestCase("Single consonant between two vowels: й",
                    "койот", "майонеза", "пейоративен", "майор"
            ),
            new TestCase("Single consonant between two vowels: morphological prefixes get split",
                    "безименен", "изопачавам", "отивам", "разоран"
            ),
            new TestCase("Single consonant between two vowels: palatalized by ь",
                    "бульон", "фризьор", "шедьовър", "гьозум", "ликьор"
            ),
            new TestCase("Zero consonants between two vowels: at most one elsewhere in word",
                    "воал", "маоизъм", "феерия", "воайор", "миокард", "кьопоолу",
                    "аятолах", "авария", "позиции", "хазяи", "дерибеи", "преодолея"
            ),
            new TestCase("Two or more consonants between two vowels: щр",
                    "нащрек", "поощрявам", "защриховам", "поощрителен",
                    "изщракване", "Вайерщрас", "Кьонигщрасе"
            ),
            new TestCase("Two or more consonants between two vowels: щ + other sonorant before vowel",
                    "общност", "всъщност", "помощник", "чорапогащник", "нощница",
                    "чудовищност", "немощливо", "съобщавам", "въобще"
            ),
            new TestCase("Two or more consonants between two vowels: дж affricate present",
                    "манджа", "калайджия", "авджия", "изджвака"
            ),
            new TestCase("Two or more consonants between two vowels: adjacent sonorants or stops",
                    "пленник", "майка", "профашистки", "гледка", "крачка", "цедка"
            ),
            new TestCase("Two consonants between two vowels: other",
                    "звезда", "спринцовка", "бързо", "малко", "партия", "гланцов",
                    "пепелник", "пилци", "аншоа", "ядро"

            ),
            new TestCase("Complex consonant clusters: general",
                    "сестра", "царство", "нравствен", "мандраджия", "мизансцен",
                    "странство", "пространство", "робство", "транспорт"
            ),
            new TestCase("Consonant cluster not split: св", "посвикна"),
            new TestCase("Complex consonant clusters: без- + lower/equal sonority",
                    "безсилен", "безшумен", "безвъзвратен", "безхаберен",
                    "безстрашен", "безхлебна", "безвремие"
            ),
            new TestCase("Complex consonant clusters: без- + rising sonority",
                    "безмерен", "безличен", "безнаказан", "безразборен",
                    "бездетен", "безпардонен", "безтелесен", "безглав", "безчестен",
                    "безпризорен", "безгрешен", "безкраен", "безбрежен", "бездна"
            )
    );

    public static void main(String[] args) {
        var testRunner = new TestCaseRunner();
        
        testRunner.runTests(TEST_CASES);
    }
}