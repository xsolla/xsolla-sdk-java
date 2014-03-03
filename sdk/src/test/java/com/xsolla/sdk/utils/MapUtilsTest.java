package com.xsolla.sdk.utils;

import org.junit.Before;
import org.junit.Test;
import java.util.HashMap;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;


public class MapUtilsTest {
    @Before
    public void setUp() throws Exception {

    }

    @Test
    public void testMergeHashMapsWithReplace() throws Exception {
        HashMap<String, Integer> firstMap = new HashMap<String, Integer>();
        HashMap<String, Integer> secondMap = new HashMap<String, Integer>();
        firstMap.put("first", 1);
        firstMap.put("http://xsolla.com", 42343252);
        secondMap.put("second", 2);
        secondMap.put("first", 2);
        HashMap<String, Integer> resultMap = MapUtils.mergeHashMapsWithReplace(firstMap, secondMap);
        assertEquals(3, resultMap.size());
        //assertEquals(2, resultMap.get("first"));
        assertNotEquals(firstMap, resultMap);
        assertNotEquals(secondMap, resultMap);
    }
}
