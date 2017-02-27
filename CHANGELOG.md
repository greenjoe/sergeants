#0.4
* Major refactoring to make bots more testable. Client code may need to be updated:
** GameState.Field and GameState.VisibleField are not inner classes of GameState anymore, they are moved to separate files in the same package
(pl.joegreen.sergeants.framework.model.{GameState.VisibleField -> VisibleField, GameState.Field -> Field})
Their dependence on parent GameState is now clearly visible in a GameStateFieldContext interface.
** GameState becomes an interface with lots of default methods, does not depend on API responses anymore.
Game state class that can be updated with API responses is now called UpdatableGameState
** GameStarted becomes an interface.

# 0.3
* Fixed #1 (Array index out of bounds exceptions being thrown) - sending leave_game after a game is finished, checking if game update message has expected turn number before applying it.
