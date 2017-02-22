package pl.joegreen.sergeants;

import org.junit.Assert;
import org.junit.Test;
import pl.joegreen.sergeants.api.util.MapPatcher;

public class MapPatcherTest {

    @Test
    public void testExample1(){
        int[] old = new int[]{0,0};
        int[] patch = new int[]{1,1,3};
        int[] expectedNew = new int[]{0,3};
        Assert.assertArrayEquals(expectedNew, MapPatcher.patch(patch, old));
    }


    @Test
    public void testExample2(){
        int[] old = new int[]{0,0};
        int[] patch = new int[]{0,1,2,1};
        int[] expectedNew = new int[]{2,0};
        Assert.assertArrayEquals(expectedNew, MapPatcher.patch(patch, old));
    }

    @Test
    public void shouldGrow(){
        int[] old = new int[]{};
        int[] patch = new int[]{0, 1, 357};
        int[] expectedNew = new int[]{357};
        Assert.assertArrayEquals(expectedNew, MapPatcher.patch(patch, old));

    }

    @Test
    public void shouldGrowAndReplace(){
        int[] old = new int[]{1,2};
        int[] patch = new int[]{1,2,3,4};
        int[] expectedNew = new int[]{1,3,4};
        Assert.assertArrayEquals(expectedNew, MapPatcher.patch(patch, old));

    }


    @Test
    public void testPatchingEmpty(){
        int[] patch = new int[]{0,2,2,3};
        int[] expectedNew = new int[]{2,3};
        Assert.assertArrayEquals(expectedNew, MapPatcher.patch(patch, new int[]{}));
    }
}
