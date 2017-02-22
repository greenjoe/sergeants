package pl.joegreen.sergeants.api.util;

import java.util.ArrayList;

public class MapPatcher {

    public static int[] patch(int[] patch, int[] old) {
        ArrayList<Integer> newArrayList = new ArrayList<>();
        int patchIndex = 0;
        while (patchIndex < patch.length) {
            //matching
            int matchingElements = patch[patchIndex++];
            int startingIndex = newArrayList.size();
            for (int oldIndex = startingIndex; oldIndex < startingIndex + matchingElements; ++oldIndex) {
                if (oldIndex < old.length) {
                    newArrayList.add(old[oldIndex]);
                } else {
                    newArrayList.add(0);
                }
            }

            //mismatching
            if (patchIndex < patch.length) {
                int misMatchingElements = patch[patchIndex++];
                while (misMatchingElements > 0) {
                    newArrayList.add(patch[patchIndex++]);
                    misMatchingElements--;
                }
            }
        }
        return newArrayList.stream().mapToInt(i -> i).toArray();
    }
}
