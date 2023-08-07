package chernorizets.bg.syllabifier;

public class Sonority {
    // Numeric rank representing a point in the sonority hierarchy
    private final int rank;

    // The index of the first letter in a word with this sonority rank.
    // The affricates "дж" and "дз" are represented by two letters each, but
    // for sonority purposes they function as a "unit", hence we just need
    // the index of the first letter of the affricate.
    private final int firstIndex;

    public Sonority(int rank, int firstIndex) {
        this.rank = rank;
        this.firstIndex = firstIndex;
    }

    public int rank() {
        return this.rank;
    }

    public int firstIndex() {
        return this.firstIndex;
    }
}
