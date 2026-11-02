package com.hurricane.model;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThrows;

/**
 * Tests for adding vectors and their dot product.
 *
 * AI was used to write the test code. Test list and reasoning:
 * +--------------------------------+-------------------------------------------------------------------+
 * | Test                           | Reasoning                                                         |
 * +--------------------------------+-------------------------------------------------------------------+
 * |                                |                                                                   |
 * +--------------------------------+-------------------------------------------------------------------+
 *
 * @author Kevin Beni
 */
public class Vector2Test {

    @Test
    public void testAddSumsX() {
        Vector2 sum = new Vector2(1, 2).add(new Vector2(3, 4));
        assertEquals(4, sum.getX(), 0.001);
    }

    @Test
    public void testAddSumsY() {
        Vector2 sum = new Vector2(1, 2).add(new Vector2(3, 4));
        assertEquals(6, sum.getY(), 0.001);
    }

    @Test
    public void testAddNegativeVectorCancelsOut() {
        Vector2 sum = new Vector2(2, 5).add(new Vector2(-2, -5));
        assertEquals(0, sum.getX(), 0.001);
    }

    @Test
    public void testAddNullThrows() {
        assertThrows(NullPointerException.class, () -> new Vector2(1, 2).add(null));
    }

    @Test
    public void testDotMultipliesAndSums() {
        assertEquals(11, new Vector2(1, 2).dot(new Vector2(3, 4)), 0.001);
    }

    @Test
    public void testDotOfPerpendicularVectorsIsZero() {
        assertEquals(0, new Vector2(1, 0).dot(new Vector2(0, 1)), 0.001);
    }

    @Test
    public void testDotNullThrows() {
        assertThrows(NullPointerException.class, () -> new Vector2(1, 2).dot(null));
    }
}
