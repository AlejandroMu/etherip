/*******************************************************************************
 * Copyright (c) 2026 IasLab - Universidad Icesi
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package etherip.data;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class TagInfoTest
{
    @Test
    public void testScalarTag()
    {
        // DINT scalar tag (0x00C4)
        final TagInfo tag = new TagInfo(101, "TANK_LEVEL", 0x00C4, 0, 0, 0);
        assertEquals(101, tag.getInstanceId());
        assertEquals("TANK_LEVEL", tag.getName());
        assertEquals("DINT", tag.getTypeName());
        assertFalse(tag.isArray());
        assertFalse(tag.isStruct());
        assertEquals(0, tag.getArrayDimensions());
        assertFalse(tag.isSystemTag());
        assertEquals("TANK_LEVEL (DINT, ID=101)", tag.toString());
    }

    @Test
    public void testArrayTag()
    {
        // DINT 1D array of 50 elements (dimension bits: (1 << 13) | 0x00C4 = 0x20C4)
        final TagInfo tag = new TagInfo(102, "SENSOR_HISTORY", 0x20C4, 50, 0, 0);
        assertEquals(102, tag.getInstanceId());
        assertEquals("SENSOR_HISTORY", tag.getName());
        assertEquals("DINT", tag.getTypeName());
        assertTrue(tag.isArray());
        assertFalse(tag.isStruct());
        assertEquals(1, tag.getArrayDimensions());
        assertArrayEquals(new int[] { 50 }, tag.getArraySizes());
        assertFalse(tag.isSystemTag());
        assertEquals("SENSOR_HISTORY (DINT[50], ID=102)", tag.toString());
    }

    @Test
    public void test2DArrayTag()
    {
        // REAL 2D array of 10x20 elements (dimension bits: (2 << 13) | 0x00CA = 0x40CA)
        final TagInfo tag = new TagInfo(103, "MATRIX_DATA", 0x40CA, 10, 20, 0);
        assertEquals(103, tag.getInstanceId());
        assertEquals("MATRIX_DATA", tag.getName());
        assertEquals("REAL", tag.getTypeName());
        assertTrue(tag.isArray());
        assertEquals(2, tag.getArrayDimensions());
        assertArrayEquals(new int[] { 10, 20 }, tag.getArraySizes());
        assertEquals("MATRIX_DATA (REAL[10,20], ID=103)", tag.toString());
    }

    @Test
    public void testStructTag()
    {
        // UDT / Struct with Template ID 0x02A0 or custom template 0x83A5
        final TagInfo tag = new TagInfo(104, "PUMP_CTRL", 0x83A5, 0, 0, 0);
        assertEquals(104, tag.getInstanceId());
        assertEquals("PUMP_CTRL", tag.getName());
        assertTrue(tag.isStruct());
        assertEquals(0x03A5, tag.getDataType());
        assertTrue(tag.getTypeName().startsWith("STRUCT (Template 0x3A5)"));
    }

    @Test
    public void testSystemTagsFiltering()
    {
        final TagInfo normalTag = new TagInfo(1, "VALVE_1", 0x00C1, 0, 0, 0);
        assertFalse(normalTag.isSystemTag());

        final TagInfo programTag = new TagInfo(2, "Program:MainProgram.COUNTER", 0x00C4, 0, 0, 0);
        assertFalse(programTag.isSystemTag());

        final TagInfo systemInternal = new TagInfo(3, "__SystemTag", 0x00C4, 0, 0, 0);
        assertTrue(systemInternal.isSystemTag());

        final TagInfo routineTag = new TagInfo(4, "Routine:AutoCycle", 0x00C4, 0, 0, 0);
        assertTrue(routineTag.isSystemTag());
    }
}
