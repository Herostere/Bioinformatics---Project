public class semiGlobalScoreMatrix {
    private final int score;
    private final int[][] matrix;

    public semiGlobalScoreMatrix(int score, int[][] matrix) {
        this.score = score;
        this.matrix = matrix;
    }

    public int getScore() {
        return score;
    }

    public int[][] getMatrix() {
        return matrix;
    }
}
