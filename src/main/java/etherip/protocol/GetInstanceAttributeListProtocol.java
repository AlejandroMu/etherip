/*******************************************************************************
 * Copyright (c) 2026 IasLab - Universidad Icesi
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package etherip.protocol;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import etherip.data.TagInfo;

/**
 * Protocol decoder for CIP Service 0x55 (Get_Instance_Attribute_List)
 * on Symbol Object (0x6B).
 *
 * @author IasLab
 */
public class GetInstanceAttributeListProtocol extends ProtocolAdapter
{
    private static final Logger log = Logger.getLogger(GetInstanceAttributeListProtocol.class.getName());

    private final List<TagInfo> tags = new ArrayList<>();
    private long lastInstanceId = 0;

    @Override
    public int getRequestSize()
    {
        // 2 bytes count + 2 bytes attr 1 + 2 bytes attr 2 + 2 bytes attr 8 = 8 bytes
        return 8;
    }

    @Override
    public void encode(final ByteBuffer buf, final StringBuilder logBuilder)
    {
        buf.putShort((short) 3); // 3 attributes requested
        buf.putShort((short) 1); // Attribute 1: Symbol Name
        buf.putShort((short) 2); // Attribute 2: Symbol Type
        buf.putShort((short) 8); // Attribute 8: Array dimensions
        if (logBuilder != null)
        {
            logBuilder.append("GetInstanceAttributeList Request\n");
            logBuilder.append("UINT attribute count    : 3\n");
            logBuilder.append("UINT attribute 1        : 1 (Symbol Name)\n");
            logBuilder.append("UINT attribute 2        : 2 (Symbol Type)\n");
            logBuilder.append("UINT attribute 3        : 8 (Array Dimensions)\n");
        }
    }

    @Override
    public void decode(final ByteBuffer buf, final int available, final StringBuilder logBuilder) throws Exception
    {
        final int startPos = buf.position();
        final int endPos = startPos + available;

        while (buf.position() + 8 <= endPos) // At least 4 (ID) + 2 (len) + 2 (type)
        {
            final long instanceId = buf.getInt() & 0xFFFFFFFFL;
            final int nameLen = buf.getShort() & 0xFFFF;

            if (buf.position() + nameLen > endPos)
            {
                break;
            }

            final byte[] nameBytes = new byte[nameLen];
            buf.get(nameBytes);
            final String tagName = new String(nameBytes, "UTF-8");

            int symbolType = 0;
            if (buf.position() + 2 <= endPos)
            {
                symbolType = buf.getShort() & 0xFFFF;
            }

            long dim1 = 0;
            long dim2 = 0;
            long dim3 = 0;

            if (buf.position() + 12 <= endPos)
            {
                dim1 = buf.getInt() & 0xFFFFFFFFL;
                dim2 = buf.getInt() & 0xFFFFFFFFL;
                dim3 = buf.getInt() & 0xFFFFFFFFL;
            }

            final TagInfo tag = new TagInfo(instanceId, tagName, symbolType, dim1, dim2, dim3);
            this.tags.add(tag);
            this.lastInstanceId = instanceId;
        }

        if (buf.position() < endPos)
        {
            buf.position(endPos);
        }

        if (logBuilder != null)
        {
            logBuilder.append("Decoded ").append(this.tags.size()).append(" tags (last instance ID: ")
                    .append(this.lastInstanceId).append(")\n");
        }
    }

    public List<TagInfo> getTags()
    {
        return this.tags;
    }

    public long getLastInstanceId()
    {
        return this.lastInstanceId;
    }
}
