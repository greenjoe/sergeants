package pl.joegreen.sergeants.simulator;

class Move {

    private final int from;
    private final int to;
    private final boolean half;


    Move(int from, int to, boolean half) {
        this.from = from;
        this.to = to;
        this.half = half;
    }

    public int getFrom() {
        return from;
    }

    public int getTo() {
        return to;
    }

    public boolean half() {
        return half;
    }
}
