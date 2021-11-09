import java.util.Arrays;

enum Nucleotide {
    ADENINE ('A'),
    CYTOSINE ('C'),
    GUANINE ('G'),
    TYMINE ( 'T'),
    NEUTRAL('N'),
    GAP ('-');

    private final char value;

    Nucleotide(char value) {
        this.value = value;
    }

    public char getValue() {
        return value;
    }

    public static boolean isGap(char b) {
        return (char) GAP.getValue() == b;
    }

    static char getComplement (char c) {
        return switch (c) {
            case 'A' -> (char) 'T';
            case 'C' -> (char) 'G';
            case 'G' -> (char) 'C';
            case 'T' -> (char) 'A';
            default -> (char) '-';
        };
    }
}