package com.xsolla.sdk.utils;

import java.util.HashMap;

public class MapUtils {

    /**
     * Merge two HashMaps, if both contain the same keys, the item will be taken from the second map
     * @param firstMap
     * @param secondMap
     * @param <T>
     * @param <U>
     * @return
     */
    public static <T, U> HashMap<T, U> mergeHashMapsWithReplace(HashMap<T, U> firstMap, HashMap<T, U> secondMap) {
        //noinspection unchecked
        HashMap<T, U> mergedMap = (HashMap)firstMap.clone();
        for (T key : secondMap.keySet()) {
            if (mergedMap.containsKey(key)) {
                mergedMap.remove(key);
            }
            mergedMap.put(key, secondMap.get(key));
        }
        return mergedMap;
    }
}
