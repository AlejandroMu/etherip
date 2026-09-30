/*******************************************************************************
 * Copyright (c) 2026 IasLab - Universidad Icesi
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package etherip.data;

/**
 * Information about a PLC Tag / Symbol discovered via CIP Symbol Object (Class 0x6B).
 *
 * @author IasLab
 */
public class TagInfo
{
    private final long instanceId;
    private final String name;
    private final int symbolType;
    private final int dataType;
    private final String typeName;
    private final boolean isStruct;
    private final boolean isArray;
    private final int arrayDimensions;
    private final int[] arraySizes;

    public TagInfo(final long instanceId, final String name, final int symbolType,
                   final long dim1, final long dim2, final long dim3)
    {
        this.instanceId = instanceId;
        this.name = name;
        this.symbolType = symbolType;
        this.dataType = symbolType & 0x0FFF;
        this.isStruct = (symbolType & 0x8000) != 0;
        this.arrayDimensions = (symbolType & 0x6000) >> 13;
        this.isArray = this.arrayDimensions > 0;
        this.typeName = decodeTypeName(symbolType);

        if (dim3 > 0)
        {
            this.arraySizes = new int[] { (int) dim1, (int) dim2, (int) dim3 };
        }
        else if (dim2 > 0)
        {
            this.arraySizes = new int[] { (int) dim1, (int) dim2 };
        }
        else if (dim1 > 0)
        {
            this.arraySizes = new int[] { (int) dim1 };
        }
        else
        {
            this.arraySizes = new int[0];
        }
    }

    /**
     * Decode the symbol type into a human-readable data type name.
     *
     * @param symbolType 16-bit symbol type word
     * @return Data type string representation
     */
    public static String decodeTypeName(final int symbolType)
    {
        final boolean isStruct = (symbolType & 0x8000) != 0;
        final int atomicCode = symbolType & 0x00FF;

        if (isStruct)
        {
            return "STRUCT (Template 0x" + Integer.toHexString(symbolType & 0x0FFF).toUpperCase() + ")";
        }

        switch (atomicCode)
        {
            case 0xC1:
                return "BOOL";
            case 0xC2:
                return "SINT";
            case 0xC3:
                return "INT";
            case 0xC4:
                return "DINT";
            case 0xC5:
                return "LINT";
            case 0xC6:
                return "USINT";
            case 0xC7:
                return "UINT";
            case 0xC8:
                return "UDINT";
            case 0xC9:
                return "ULINT";
            case 0xCA:
                return "REAL";
            case 0xCB:
                return "LREAL";
            case 0xD0:
                return "DWORD";
            case 0xD3:
                return "BITS";
            case 0xA0:
                return "STRUCT";
            default:
                return "TYPE_0x" + Integer.toHexString(atomicCode).toUpperCase();
        }
    }

    /**
     * Check if this tag represents an internal controller / system tag.
     *
     * @return true if system tag that is typically filtered out of user catalogs
     */
    public boolean isSystemTag()
    {
        if (this.name == null || this.name.isEmpty())
        {
            return true;
        }
        if (this.name.startsWith("__"))
        {
            return true;
        }
        if (this.name.contains(":") && !this.name.startsWith("Program:"))
        {
            return true;
        }
        return false;
    }

    public long getInstanceId()
    {
        return this.instanceId;
    }

    public String getName()
    {
        return this.name;
    }

    public int getSymbolType()
    {
        return this.symbolType;
    }

    public int getDataType()
    {
        return this.dataType;
    }

    public String getTypeName()
    {
        return this.typeName;
    }

    public boolean isStruct()
    {
        return this.isStruct;
    }

    public boolean isArray()
    {
        return this.isArray;
    }

    public int getArrayDimensions()
    {
        return this.arrayDimensions;
    }

    public int[] getArraySizes()
    {
        return this.arraySizes;
    }

    @Override
    public String toString()
    {
        final StringBuilder sb = new StringBuilder();
        sb.append(this.name).append(" (");
        sb.append(this.typeName);
        if (this.isArray)
        {
            sb.append("[");
            for (int i = 0; i < this.arraySizes.length; i++)
            {
                if (i > 0)
                {
                    sb.append(",");
                }
                sb.append(this.arraySizes[i]);
            }
            sb.append("]");
        }
        sb.append(", ID=").append(this.instanceId).append(")");
        return sb.toString();
    }
}
