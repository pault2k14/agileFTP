package com.agileFTP;

import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Created by HackerToad on 8/4/2015.
 */
public class PathHelperTest {
    private PathHelper pathHelper = new PathHelper();

    @Test
    public void testDownladsPath() throws Exception {
        assertTrue(pathHelper.getDownloadsPath().contains("Downloads"));
    }
    @Test
    public void testUserHome() throws Exception {
        String s = pathHelper.getPathFromUserHome("home", "test");
        if(System.getProperty("os.name").contains("Windows"))
            assertTrue(s.contains("home\\test"));
        else {
            assertTrue(s.contains("home/test"));
        }
    }

}
