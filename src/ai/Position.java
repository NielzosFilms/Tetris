package ai;

public class Position {
    private int offset_x, offset_y;
    private int rotation;
    private int score;

    public Position(int offset_x, int offset_y, int rotation) {
        this.offset_x = offset_x;
        this.offset_y = offset_y;
        this.rotation = rotation;
    }

    public int getOffset_x() {
        return offset_x;
    }

    public void setOffset_x(int offset_x) {
        this.offset_x = offset_x;
    }

    public int getOffset_y() {
        return offset_y;
    }

    public void setOffset_y(int offset_y) {
        this.offset_y = offset_y;
    }

    public int getRotation() {
        return rotation;
    }

    public void setRotation(int rotation) {
        this.rotation = rotation;
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }
}
