public class Alignment {
    private final String source;
    private final String destination;

    public Alignment(StringBuilder source, StringBuilder destination) {
        this.source = String.valueOf(source);
        this.destination = String.valueOf(destination);
    }

    public String getSource() {
        return source;
    }

    public String getDestination() {
        return destination;
    }
}
