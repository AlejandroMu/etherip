/*******************************************************************************
 * Copyright (c) 2026 IasLab - Universidad Icesi
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package etherip.protocol;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.List;

import org.junit.Test;

import etherip.data.TagInfo;

public class GetInstanceAttributeListProtocolTest
{
    @Test
    public void testEncode()
    {
        final GetInstanceAttributeListProtocol proto = new GetInstanceAttributeListProtocol();
        assertEquals(8, proto.getRequestSize());

        final ByteBuffer buf = ByteBuffer.allocate(8);
        buf.order(ByteOrder.LITTLE_ENDIAN);
        proto.encode(buf, new StringBuilder());
        buf.flip();

        assertEquals(3, buf.getShort()); // 3 attributes requested
        assertEquals(1, buf.getShort()); // Attr 1 (Name)
        assertEquals(2, buf.getShort()); // Attr 2 (Type)
        assertEquals(8, buf.getShort()); // Attr 8 (Dimensions)
    }

    @Test
    public void testDecode() throws Exception
    {
        final GetInstanceAttributeListProtocol proto = new GetInstanceAttributeListProtocol();

        // Build mock response buffer containing two tags:
        // Tag 1: ID=10, Name="START_PB", Type=0x00C1 (BOOL), Dims=0,0,0
        // Tag 2: ID=11, Name="TEMPERATURES", Type=0x20CA (REAL[5]), Dims=5,0,0
        final ByteBuffer buf = ByteBuffer.allocate(256);
        buf.order(ByteOrder.LITTLE_ENDIAN);

        // Tag 1
        final byte[] name1 = "START_PB".getBytes("UTF-8");
        buf.putInt(10); // Instance ID
        buf.putShort((short) name1.length); // Name length
        buf.put(name1); // Name bytes
        buf.putShort((short) 0x00C1); // Type = BOOL
        buf.putInt(0); // Dim 1
        buf.putInt(0); // Dim 2
        buf.putInt(0); // Dim 3

        // Tag 2
        final byte[] name2 = "TEMPERATURES".getBytes("UTF-8");
        buf.putInt(11); // Instance ID
        buf.putShort((short) name2.length); // Name length
        buf.put(name2); // Name bytes
        buf.putShort((short) 0x20CA); // Type = REAL array
        buf.putInt(5); // Dim 1
        buf.putInt(0); // Dim 2
        buf.putInt(0); // Dim 3

        final int totalBytes = buf.position();
        buf.flip();

        proto.decode(buf, totalBytes, new StringBuilder());

        final List<TagInfo> tags = proto.getTags();
        assertEquals(2, tags.size());

        final TagInfo tag1 = tags.get(0);
        assertEquals(10, tag1.getInstanceId());
        assertEquals("START_PB", tag1.getName());
        assertEquals("BOOL", tag1.getTypeName());
        assertFalse(tag1.isArray());

        final TagInfo tag2 = tags.get(1);
        assertEquals(11, tag2.getInstanceId());
        assertEquals("TEMPERATURES", tag2.getName());
        assertEquals("REAL", tag2.getTypeName());
        assertTrue(tag2.isArray());
        assertEquals(5, tag2.getArraySizes()[0]);

        assertEquals(11, proto.getLastInstanceId());
    }
}
