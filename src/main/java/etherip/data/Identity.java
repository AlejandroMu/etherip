/*******************************************************************************
 * Copyright (c) 2017 NETvisor Ltd.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package etherip.data;

import java.util.Arrays;

/**
 * Identity Object.
 * <p>
 * Status parameter is not decoded.<br>
 * Example for decoding:<br>
 * Status: 0x0060<br>
 * .... .... .... ...0 = Owned: 0<br>
 * .... .... .... .0.. = Configured: 0<br>
 * .... .... 0110 .... = Extended Device Status: 0x6<br>
 * .... ...0 .... .... = Minor Recoverable Fault: 0<br>
 * .... ..0. .... .... = Minor Unrecoverable Fault: 0<br>
 * .... .0.. .... .... = Major Recoverable Fault: 0<br>
 * .... 0... .... .... = Major Unrecoverable Fault: 0<br>
 * 0000 .... .... .... = Extended Device Status 2: 0x0<br>
 *
 * @see CIP_VOL1-3.3: 5-2 Identity Object
 * @author László Pataki
 */
public class Identity
{
    private Integer vendorId, deviceType, productCode;

    private Integer[] revision;

    private String productName, serialNumber, status;

    public Identity()
    {
        this.vendorId = null;
        this.deviceType = null;
        this.productCode = null;
        this.revision = new Integer[] { null, null };
        this.status = null;
        this.serialNumber = null;
        this.productName = null;
    }

    public int getVendorId()
    {
        return this.vendorId;
    }

    public void setVendorId(final int vendorId)
    {
        this.vendorId = vendorId;
    }

    public int getDeviceTypeRaw()
    {
        return this.deviceType;
    }

    public void setDeviceType(final int deviceType)
    {
        this.deviceType = deviceType;
    }

    public int getProductCode()
    {
        return this.productCode;
    }

    public void setProductCode(final int productCode)
    {
        this.productCode = productCode;
    }

    public Integer[] getRevision()
    {
        return this.revision;
    }

    public void setRevision(final Integer[] revision)
    {
        this.revision = revision;
    }

    public int getMajorRevision()
    {
        return this.revision[0];
    }

    public int getMinorRevision()
    {
        return this.revision[1];
    }

    public String getStatusRaw()
    {
        return this.status;
    }

    public void setStatus(final String status)
    {
        this.status = status;
    }

    public String getSerialNumberRaw()
    {
        return this.serialNumber;
    }

    public void setSerialNumber(final String serialNumber)
    {
        this.serialNumber = serialNumber;
    }

    public String getProductName()
    {
        return this.productName;
    }

    public void setProductName(final String productName)
    {
        this.productName = productName;
    }

    public int getStatusValue()
    {
        if (this.status == null || this.status.isEmpty())
        {
            return 0;
        }
        try
        {
            String s = this.status.trim();
            if (s.startsWith("0x") || s.startsWith("0X"))
            {
                s = s.substring(2);
            }
            return Integer.parseInt(s, 16);
        }
        catch (final NumberFormatException ex)
        {
            return 0;
        }
    }

    public int getExtendedDeviceStatus()
    {
        return (getStatusValue() >> 4) & 0x0F;
    }

    public boolean isRunMode()
    {
        return getExtendedDeviceStatus() == 0x06;
    }

    public boolean isProgramMode()
    {
        return getExtendedDeviceStatus() == 0x07;
    }

    public boolean isFaulted()
    {
        final int ext = getExtendedDeviceStatus();
        return ext == 0x05 || hasMajorRecoverableFault() || hasMajorUnrecoverableFault();
    }

    public boolean hasMinorRecoverableFault()
    {
        return (getStatusValue() & (1 << 8)) != 0;
    }

    public boolean hasMinorUnrecoverableFault()
    {
        return (getStatusValue() & (1 << 9)) != 0;
    }

    public boolean hasMajorRecoverableFault()
    {
        return (getStatusValue() & (1 << 10)) != 0;
    }

    public boolean hasMajorUnrecoverableFault()
    {
        return (getStatusValue() & (1 << 11)) != 0;
    }

    public String getOperatingMode()
    {
        if (isFaulted())
        {
            return "FAULTED";
        }
        final int ext = getExtendedDeviceStatus();
        switch (ext)
        {
            case 0x06:
                return "RUN";
            case 0x07:
                return "PROGRAM";
            case 0x01:
                return "FIRMWARE_UPDATE";
            case 0x02:
                return "IO_FAULT";
            case 0x03:
                return "NO_IO_CONNECTIONS";
            case 0x04:
                return "CONFIG_FAULT";
            case 0x05:
                return "MAJOR_FAULT";
            default:
                return "UNKNOWN (0x" + Integer.toHexString(ext) + ")";
        }
    }

    public String getVendorName()
    {
        if (this.vendorId == null)
        {
            return "Unknown";
        }
        if (this.vendorId == 1)
        {
            return "Rockwell Automation/Allen-Bradley";
        }
        return "Vendor (" + this.vendorId + ")";
    }

    public String getDeviceTypeName()
    {
        if (this.deviceType == null)
        {
            return "Unknown";
        }
        switch (this.deviceType)
        {
            case 0x0E:
                return "Programmable Logic Controller";
            case 0x02:
                return "AC Drive";
            case 0x0C:
                return "Communications Adapter";
            case 0x18:
                return "Human-Machine Interface";
            case 0x2C:
                return "Managed Switch";
            default:
                return "Device Type (" + this.deviceType + ")";
        }
    }

    @Override
    public String toString()
    {
        return "Identity [vendor=" + getVendorName() + " (id=" + this.vendorId + "), deviceType="
                + getDeviceTypeName() + " (code=" + this.deviceType + "), productCode=" + this.productCode
                + ", revision=" + Arrays.toString(this.revision)
                + ", productName=" + this.productName + ", serialNumber="
                + this.serialNumber + ", status=" + this.status + " (" + getOperatingMode() + ")]";
    }

}
