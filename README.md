# bg-syllabifier
Prototype implementation of a syllabifier for Bulgarian.

## Caveat Emptor
As of the time of writing, this is a quick-and-dirty prototype. It doesn't follow best coding practices,
and the ad-hoc testing mini-framework is a variant of manual snapshot testing. The project isn't ready
for collaboration, pull requests, handling issues, etc. Its usefulness is to a small group of English Wiktionary
contributors who, like me, are aiming to improve the support for Bulgarian lemmas in the dictionary.

If you've read this and still want to play around with the code, here's the setup I've used:
* OpenJDK 11
* IntelliJ IDEA 2023.1 (Community Edition)
* MacOS Monterey 12.6.8

If you're compatible with the first two items on the above list, you should *probably* be fine.

The project uses IntelliJ's build system, hence the IDE-specific files committed to the repository.
It is distributed under the MIT License.

## Overview
This prototype implements the syllable breaking rules described in the textbook [Съвременен български език](https://archive.org/details/20201113_20201113_0920/%D0%A1%D1%8A%D0%B2%D1%80%D0%B5%D0%BC%D0%B5%D0%BD%D0%B5%D0%BD%20%D0%B1%D1%8A%D0%BB%D0%B3%D0%B0%D1%80%D1%81%D0%BA%D0%B8%20%D0%B5%D0%B7%D0%B8%D0%BA%20-%20%D0%A2%D0%BE%D0%B4%D0%BE%D1%80%20%D0%91%D0%BE%D1%8F%D0%B4%D0%B6%D0%B8%D0%B5%D0%B2%20.pdf/)
*(Contemporary Bulgarian Language)* by Todor Boyadzhiev, Ivan Kutsarov and Yordan Penchev. The relevant portion of the
textbook is Chapter 7, pages 52-58. The rules are reproduced here in English in their original sequence:

1. When a single consonant is between two vowels, it forms a syllable with the second vowel: го-ле-ми-на, ра-йон, ко-раб.
2. When there is more than one consonant between two vowels, their assignment to syllables depends on other factors:
    1. A sonorant followed by another consonant stays with the first syllable: бър-зо, мал-ко, май-ка, бор-ба, тор-та, пар-ти-я, стом-на.
    2. If a fricative is followed by an obstruent, the boundary is ambiguous: те-сто and тес-то, ма-ска and мас-ка. The same is true when an obstruent is followed by a sonorant: ле-сно and лес-но, тя-сно and тяс-но, гре-бло and греб-ло, кре-сльо and крес-льо.
    3. Two consecutive obstruents get split up: глед-ка, крач-ка, сек-та.
3. A group of two or three consonants ordered by increasing sonority - such as стр, здр, скл, ств, шк, жд, ст, etc - is not typically split: ма-стило, се-стра, по-здрав. If, in such cases, morpheme boundaries are taken into account, another division is possible: круш-ка, при-каз-ка.
4. Morphological prefixes and suffixes are typically kept together: без-домен, от-кач-вам. If that results in a following syllable that starts with a vowel, prefixes and suffixes get split up: и-зо-ра, бе-зи-ме-нен.

In the textbook, the sonority hierarchy is: fricatives < stops and affricates < sonorants < vowels.

### Note on the Bulgarian Alphabet
While there's a mostly 1-to-1 correspondence between sounds and letters in Bulgarian, there are exceptions:
* the letters `ю` (yu) and `я` (ya) represent either an iotated vowel, or palatalization of the preceding consonant. We treat them the same as other vowels.
* the letter `щ` stands for `шт`. For sonority analysis, we break it up into its two sonic components.
* the digraph `дж` usually stands for the affricate corresponding to "j" in "jug". In rare cases, it is a `д` followed by a `ж`.
* conversely, the digraph `дз` usually stands for a `д` followed by a `з`. In rare cases, it stands for the affricate in "a**dz**e".
* the letter `ь` is only found after consonants, and has the effect of palatalizing them.

## This Implementation
At a high level, the algorithm implemented here works like this:
* If a word has zero or one vowels, it's a single syllable: с, в, шприц, скункс.
* Otherwise, consider the consonant cluster between two consecutive vowels:
  * if it's of zero length, the vowels are adjacent, and the second one starts a syllable: ма-о-и-зъм, по-и-грах.
  * if there's just one consonant, it starts the next syllable (as per the textbook)
  * if there are two or more consonants, we look at their sonorities. A consonant cluster ordered by rising sonority is usually kept together, and starts a syllable with the second vowel. Alternatively, if we encounter a consonant of equal or lower sonority to the preceding one, we usually treat it as the start of the next syllable.
  * while sonority analysis is the dominant way of determining syllable breaks, we support certain exceptions (see below)

### Rising Sonority Exceptions
Custom lists of consonant clusters to keep intact or break apart are informed by the frequency-ordered general dictionary
of the [Bulgarian National Corpus](https://dcl.bas.bg/bulnc/en/dostap/retchnitsi/). It provides a good overview of the
behavior of clusters in common (and less common) words.

#### User-provided syllable break markers
Users can indicate a syllable break explicitly, using the full-stop character: `.` This is mainly useful when handling
affricates, e.g. a `дж` that's really two separate sounds, or a `дз` that's really just one. Examples:
над.живея, камика.дзе

#### Custom list of consonant clusters to break
Certain consonant clusters - e.g. здн, вдж - are ordered by rising sonority, but would result in awkward syllable onsets.
Per the textbook, in such situations the syllable boundary is ambiguous - we disambiguate by disallowing certain clusters
like that explicitly.

#### Custom list of consonant clusters to keep
Conversely, there's a small number of consonant clusters - like "св" - which don't follow rising sonority, but should
nevertheless not be broken up between syllables. One of the examples given in the textbook is "ств".

#### Morphological prefixes
We maintain a custom list of morphological prefixes that can participate in a consonant cluster by virtue of ending in
a consonant, and being followed immediately by one or more other consonants. Sonority analysis will break up such clusters
whenever the consonant following the prefix is of equal or lower sonority to the consonant at the end of the prefix.

However, if the consonant following the prefix is of *higher* sonority, then we'd naively break up the prefix:
и-зне-на-да, въ-змо-жен. Instead, we recognize "из-" and "въз-" as prefixes, and indicate to the algorithm that a new
syllable should start on the consonant *after* the prefix: из-не-на-да, въз-мо-жен.

#### Order of evaluation
Before running the main loop of the algorithm, we check for morphological prefixes, and remember where to start the
next syllable if one of them is a match.

When considering where to break up a consonant cluster, we proceed in the following order:
* if there's a user-provided syllable break using `.`, break there
* determine a tentative syllable break using rising sonority analysis
* check for clusters to break - if there's a match, break up the cluster as appropriate
* check for clusters to keep - if there's a match, keep the cluster
* check whether the cluster contains a consonant following a morphological prefix (precomputed at start). If so, it stats a new syllable.
* otherwise, just use the sonority break determined in Step 2

## Feature Support
We can currently handle the following types of input:
* words made from contiguous uppercase or lowercase Bulgarian Cyrillic letters
* user-specified syllable breaks using `.` in such words

We **don't** currently correctly handle the following types of input:
* compound words with hyphens: e.g. джаста-праста
* compound words with spaces: e.g. тенис корт, заместник министър
* words with characters not in the Bulgarian Cyrillic alphabet: e.g. DJ, SIM карта
* (likely) degenerate inputs - e.g. words including numbers, punctuation characters, characters from other alphabets, etc.
