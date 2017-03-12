package pl.joegreen.sergeants.simulator;

import org.junit.Assert;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class SimulatorFactoryTest {
    @Test
    public void testCreateMap() throws Exception {
        GameMap map = SimulatorFactory.createMapFromReplayFile(getClass().getResource("/gioreplay8.json").getFile());
        Assert.assertNotNull(map);
        for (Tile tile : map.getTiles()) {
            Assert.assertNotNull(tile);
        }
        assertEquals(map.getTiles().length, 504);
    }
}
