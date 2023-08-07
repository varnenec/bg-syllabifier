package chernorizets.bg.syllabifier;

public class Sonority {
    private final int rank;
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
