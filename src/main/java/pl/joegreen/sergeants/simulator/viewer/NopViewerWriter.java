package pl.joegreen.sergeants.simulator.viewer;

import pl.joegreen.sergeants.simulator.GameMap;

public class NopViewerWriter implements ViewerWriter {
    @Override
    public void write(GameMap gameMap) {}

    @Override
    public void close() {}
}
