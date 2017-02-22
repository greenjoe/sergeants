# Sergeants 

Sergeants is a framework for creating bots for the generals.io game. It consists of two packages:
* API, with the `GeneralsApi` class, that acts as a simple wrapper over the generals.io API and makes it easier to use it from Java. 
It doesn't offer any convenience methods and does only basic type transformations. 
* Framework which performs some data transformations and adds convenience methods 
to make writing code of bots and joining games with them as easy as possible. `Games` class can be used to start games when
`Bot` interface is implemented with appropriate logic.


# Common framework use case
Add maven dependency
```xml
       <dependency>
            <groupId>pl.joegreen</groupId>
            <artifactId>sergeants</artifactId>
            <version>0.1</version>
        </dependency>
```


implement the `Bot` interface 
```java

public class SimpleBorderExpandingBot implements Bot {

    private final Actions actions;

    public SimpleBorderExpandingBot(Actions actions) {
        this.actions = actions;
    }

    @Override
    public void onGameStateUpdate(GameState newGameState) {
        Optional<GameState.VisibleField> maybeFieldToAttack = newGameState.getVisibleFields().stream()
                .filter(this::canBeAttacked)
                .findFirst();

        maybeFieldToAttack.ifPresent(fieldToAttack -> {
            GameState.VisibleField attackFrom = fieldToAttack.getVisibleNeighbours().stream()
                    .filter(GameState.VisibleField::isOwnedByMe)
                    .sorted(Comparator.comparing(GameState.VisibleField::getArmy).reversed())
                    .findFirst().get();

            actions.move(attackFrom, fieldToAttack);
        });


    }

    private boolean canBeAttacked(GameState.VisibleField potentialTarget) {
        return !potentialTarget.isObstacle() && !potentialTarget.isOwnedByMyTeam() &&
                potentialTarget.getVisibleNeighbours().stream().anyMatch(
                        neighbour -> neighbour.isOwnedByMe() &&
                                neighbour.getArmy() > potentialTarget.getArmy() + 1
                );
    }


}


```
The bot above is very simple - it attacks nearby fields that can be conquered and does nothing if there are no such fields. 
The `GameState` class provides a lot of information so it should be easy to add more advanced logic. See the `Actions`
interface to find out what actions can be performed by the bot. 


Create a main class that plays the FFA game (this call uses random user identifier, change `UserConfiguration` to set up the player.

```java
public class MainClass {
    public static void main(String[] args) throws InterruptedException {

        Games.play(1, SimpleBorderExpandingBot::new, QueueConfiguration.freeForAll(false), UserConfiguration.random())
                .forEach(System.out::println);

    }
}
```
Run the class and wait for results. If constructor is used as a bot provider new instance of the bot will be created for every game.
`Games.playAsynchronously` method can be used to play multiple games (with different user identifiers) at the same time as it doesn't
block a calling thread.


# Low-level API

If the way framework works doesn't meet your needs you can still use low-level Java wrappers for the generals.io API. 
The following example shows how to set the username using the `GeneralsApi` class:

```java
        GeneralsApi generalsApi = GeneralsApi.create().onSetUsernameError(System.out::println);
        generalsApi.onConnected(() ->
                generalsApi.setUsername("userId", "userName"));
```



