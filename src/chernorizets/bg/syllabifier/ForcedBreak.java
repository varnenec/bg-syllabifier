package chernorizets.bg.syllabifier;

/**
 * Facility for user-indicated forced syllable breaks.
 */
class ForcedBreak {

    /**
     * Indicator character for a forced syllable break, e.g: над.живея
     */
    static final char MARKER = '.';

    static int findForcedBreak(String word, int rangeStart, int rangeEnd) {
        if (rangeStart >= rangeEnd) return -1;

        int markerPos = word.indexOf(MARKER, rangeStart);
        return markerPos >= rangeEnd ? -1 : markerPos;
    }

    static String stripForcedBreaks(String segment) {
        return segment.replaceAll("\\.", "");
    }
}
