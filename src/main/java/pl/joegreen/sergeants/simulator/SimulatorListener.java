package pl.joegreen.sergeants.simulator;

interface SimulatorListener {
    void beforeGameStart(Player[] players, GameMap gameMap);

    void afterHalfTurn(int halfTurnCounter, Tile[] tiles);

    void onGameEnd(Player winner);

    void onGameAborted(Player[] players);
}
