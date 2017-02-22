package pl.joegreen.sergeants.framework.queue;

import pl.joegreen.sergeants.api.GeneralsApi;

public abstract class QueueConfiguration {
    protected final boolean force;

    protected QueueConfiguration(boolean force) {
        this.force = force;
    }

    public abstract void joinQueue(GeneralsApi api, String botUserId);

    public static QueueConfiguration oneVsOne() {
        return new OneVsOneQueueConfiguration();
    }

    /**
     * Joins Free For All game queue.
     *
     * @param force indicates if player should vote for "force start"
     */
    public static QueueConfiguration freeForAll(boolean force) {
        return new FreeForAllQueueConfiguration(force);
    }

    /**
     * Joins 2vs2 game queue.
     *
     * @param force indicates if player should vote for "force start"
     * @param teamId identifier of the team to join (displayed in browser URL when starting 2v2)
     */
    public static QueueConfiguration twoVsTwo(String teamId, boolean force) {
        return new TwoVsTwoQueueConfiguration(teamId, force);
    }

    /**
     * Joins custom game.
     * @param force indicates if player should vote for "force start"
     * @param gameId identifier of the game to join (displayed in browser URL when starting custom game)
     */
    public static QueueConfiguration customGame(boolean force, String gameId) {
        return new CustomGameQueueConfiguration(force, gameId);
    }

    /**
     * Joins custom game.
     * @param force indicates if player should vote for "force start"
     * @param gameId identifier of the game to join (displayed in browser URL when starting custom game)
     * @param team number of team that bot will join in the custom game
     */
    public static QueueConfiguration customGame(boolean force, String gameId, int team) {
        return new CustomGameQueueConfiguration(force, gameId, team);
    }
}
