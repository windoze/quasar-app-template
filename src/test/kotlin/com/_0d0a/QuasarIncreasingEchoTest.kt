package com._0d0a;

import org.junit.Assert.*
import org.junit.Test
import com._0d0a.QuasarIncreasingEchoApp

import org.hamcrest.CoreMatchers.*

/**
 * Increasing-Echo Quasar Text
 *
 * @author circlespainter
 */
public class QuasarIncreasingEchoTest {
    @Test
    public fun test() {
        assertEquals(QuasarIncreasingEchoApp.doAll(), 10);
    }
}