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
        int counter = 0;

        for (var test : testCases) {
            ++ counter;
            System.out.printf("%d. %s%n", counter, test.heading);

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
                    "а", "в", "е", "и", "ѝ", "о", "с", "у"
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
            new TestCase("Forced syllable breaks: when absent",
                    "скръндза", "годзила", "камикадзе", "надживея"
            ),
            new TestCase("Forced syllable breaks: when present",
                    "скрън.дза", "го.дзила", "камика.дзе", "над.живея"
            ),
            new TestCase("Morphological prefix handling: без- + equal sonority",
                    "безсилен", "безшумен", "безвъзвратен", "безхаберен",
                    "безстрашен", "безхлебна", "безвремие"
            ),
            new TestCase("Morphological prefix handling: без- + higher sonority",
                    "безмерен", "безличен", "безнаказан", "безразборен",
                    "бездетен", "безпардонен", "безтелесен", "безглав", "безчестен",
                    "безпризорен", "безгрешен", "безкраен", "безбрежен", "бездна"
            ),
            new TestCase("Morphological prefix handling: из- + equal sonority",
                    "изхвърлям", "изстена", "извор", "извозвам",
                    "извлача", "изхрачване", "изшмугна", "изживяното"
            ),
            new TestCase("Morphological prefix handling: из- + higher sonority",
                    "изненада", "излъгах", "измяна", "изрод",
                    "изтрезвително", "изпроставял", "изключвам", "изблиза"
            ),
            new TestCase("Morphological prefix handling: над- + equal/lower sonority",
                    "надслов", "надхвърлен", "надвиквам", "надве",
                    "надгробен", "надпис", "надценявам", "надделея"
            ),
            new TestCase("Morphological prefix handling: над- + higher sonority",
                    "над.раствам", "надмощие", "ненадминат", "безнадзорен",
                    "надница", "надменност", "на.длъж", "надробен",
                    "надрънкам", "надраскам", "надрусам", "надран"
            ),
            new TestCase("Morphological prefix handling: под- + equal/lower sonority",
                    "подстрекател", "подход", "подвижен", "подзаглавие",
                    "подклаждам", "подбор", "подпирам", "подценявам"
            ),
            new TestCase("Morphological prefix handling: под- + higher sonority",
                    "подновявам", "подмамвам", "подлост", "под.разделение",
                    "подробен", "подражавам", "подремя", "подрусам"
            ),
            new TestCase("Multiple prefixes",
                    "безизразен", "безизразност", "безвъзмезден", "безвъздушен",
                    "безразличен", "безразборност", "безпредметен", "поизправя",
                    "поизмъча", "поизгладя", "произношение", "произтича",
                    "наизмислил", "наизлезлите", "предразположение", "преразглеждане",
                    "преразпределение", "преразказ", "превъзмогна", "превъзпитание",
                    "преиздавам", "преизбирам", "невъзможен", "невъзпитан",
                    "неизбежен", "неизменност", "неразделен", "неразположение",
                    "поразмисля", "пораздрусам", "наразказах", "наразлепил",
                    "неотложен", "неотменим", "поотложа", "поотмина"
            ),
            new TestCase("Loanwords with /w/ as a consonant: default spelling with 'у'",
                    "уелски", "уебсайт", "уестърн", "Оуен",
                    "ноухау", "Джоузеф", "боулинг", "даунлоуд",
                    "уиски", "уикенд", "Уоруик", "Хелоуин"
            ),
            new TestCase("Loanwords with /w/ as a consonant: alternative spelling with 'ў'",
                    "ўелски", "ўебсайт", "ўестърн", "Оўен",
                    "ноўхаў", "Джоўзеф", "боўлинг", "даўнлоўд",
                    "ўиски", "ўикенд", "Ўорўик", "Хелоўин"
            ),
            new TestCase("Explicit clusters to break: км, гм, кн, гн, дн, дм",
                    "тъкмо", "чекмедже", "сегмент", "фрагмент",
                    "прагматичен", "парадигма", "стъкмя", "енигма",
                    "тръгна", "стигна", "вдигна", "помогна",
                    "обикновен", "измъкна", "възкликна", "възникна",
                    "заедно", "погледна", "седмица", "веднага",
                    "предмет", "веднъж", "седна", "административен"
            ),
            new TestCase("Explicit clusters to break: зм, зд, зч, зц",
                    "създам", "звезда", "измама", "яздя",
                    "гнездо", "грозде", "ездач", "бразда",
                    "изчезна", "разчитам", "безцветен", "разцепя",
                    "изчерпвам", "безценен", "безчувствен", "разцъфна",
                    "приказчица", "железце", "абхазци", "магданозче"
            )
    );

    public static void main(String[] args) {
        var testRunner = new TestCaseRunner();
        
        testRunner.runTests(TEST_CASES);
    }
}