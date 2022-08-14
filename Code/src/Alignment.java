public class Alignment {
    private final String source;
    private final String destination;
    private final int shifts;

    public Alignment(StringBuilder source, StringBuilder destination) {
        this.source = String.valueOf(source);
        this.destination = String.valueOf(destination);
        this.shifts = 0;
    }

    public Alignment(StringBuilder source, StringBuilder destination, int shifts) {
        this.source = String.valueOf(source);
        this.destination = String.valueOf(destination);
        this.shifts = shifts;
    }

    public String getSource() {
        return source;
    }

    public String getDestination() {
        return destination;
    }

    public int getShifts() {
        return shifts;
    }
}
