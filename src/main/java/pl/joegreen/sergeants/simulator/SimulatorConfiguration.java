package pl.joegreen.sergeants.simulator;

import java.io.File;
import java.util.Optional;

public class SimulatorConfiguration {
    private int maxTurns = Integer.MAX_VALUE;
    private File replayFile = null;

    public static SimulatorConfiguration configuration() {
        return new SimulatorConfiguration();
    }

    public int getMaxTurns() {
        return maxTurns;
    }

    public Optional<File> getReplayFile() {
        return Optional.ofNullable(replayFile);
    }

    public SimulatorConfiguration withMaxTurns(int maxTurns){
        this.maxTurns = maxTurns;
        return this;
    }

    /**
    File where replay will be saved when simulation is run. It can be later browsed with simulator-viewer.html.
    If file is not set, the replay will not be saved. Replay files can become large with large number of turns,
    so please consider also using withMaxTurns when setting the replayFile.
     **/
    public SimulatorConfiguration withReplayFile(File replayFile){
        this.replayFile = replayFile;
        return this;
    }

}
