public class Alignment {
    private String source;
    private String destination;
    private int shifts;

    public Alignment(StringBuilder source, StringBuilder destination) {
        this.source = String.valueOf(source);
        this.destination = String.valueOf(destination);
        this.shifts = 0;
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

    public void setShifts(int shifts) {
        this.shifts = shifts;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public void setDestination(String destination) {
        this.destination = destination;
    }
}
