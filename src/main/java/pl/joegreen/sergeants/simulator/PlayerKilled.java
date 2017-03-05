package pl.joegreen.sergeants.simulator;

class PlayerKilled {
    private final int victim;
    private final int offender;

    public PlayerKilled(int victim, int offender) {
        this.victim = victim;
        this.offender = offender;
    }

    public int getOffender() {
        return offender;
    }

    public int getVictim() {
        return victim;
    }
}
