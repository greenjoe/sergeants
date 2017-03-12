package pl.joegreen.sergeants.simulator;

public interface SimulatorListener {
    void beforeGameStart(Player[] players, GameMap gameMap);

    void afterHalfTurn(int halfTurnCounter, Tile[] tiles);

    void onGameEnd(Player winner);

    void onGameAborted(Player[] players);
}
