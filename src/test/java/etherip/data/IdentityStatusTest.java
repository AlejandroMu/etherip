/*******************************************************************************
 * Copyright (c) 2026 IasLab - Universidad Icesi
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package etherip.data;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class IdentityStatusTest
{
    @Test
    public void testRunModeStatus()
    {
        final Identity id = new Identity();
        id.setVendorId(1);
        id.setDeviceType(0x0E);
        id.setProductCode(153);
        id.setRevision(new Integer[] { 32, 11 });
        id.setProductName("1769-L33ERM");
        id.setSerialNumber("0x12345678");
        id.setStatus("0x0060");

        assertEquals(0x0060, id.getStatusValue());
        assertEquals(0x06, id.getExtendedDeviceStatus());
        assertTrue(id.isRunMode());
        assertFalse(id.isProgramMode());
        assertFalse(id.isFaulted());
        assertEquals("RUN", id.getOperatingMode());
        assertEquals("Rockwell Automation/Allen-Bradley", id.getVendorName());
        assertEquals("Programmable Logic Controller", id.getDeviceTypeName());
    }

    @Test
    public void testProgramModeStatus()
    {
        final Identity id = new Identity();
        id.setStatus("0x0070");

        assertEquals(0x0070, id.getStatusValue());
        assertEquals(0x07, id.getExtendedDeviceStatus());
        assertFalse(id.isRunMode());
        assertTrue(id.isProgramMode());
        assertFalse(id.isFaulted());
        assertEquals("PROGRAM", id.getOperatingMode());
    }

    @Test
    public void testFaultedStatus()
    {
        final Identity id = new Identity();
        // Major recoverable fault (bit 10 = 0x0400) + major fault state 0x0050 = 0x0450
        id.setStatus("0x0450");

        assertEquals(0x0450, id.getStatusValue());
        assertTrue(id.isFaulted());
        assertTrue(id.hasMajorRecoverableFault());
        assertEquals("FAULTED", id.getOperatingMode());
    }
}
