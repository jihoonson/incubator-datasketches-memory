/*
 * Copyright 2017, Yahoo! Inc. Licensed under the terms of the
 * Apache License 2.0. See LICENSE file at the project root for terms.
 */

package com.yahoo.memory;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertTrue;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

import org.testng.annotations.Test;

public class WritableMemoryTest {

  @Test
  public void wrapBigEndian() {
    ByteBuffer bb = ByteBuffer.allocate(64); //big endian
    WritableMemory wmem = WritableMemory.wrap(bb);
    assertTrue(wmem.swapBytes());
    assertEquals(wmem.getResourceOrder(), ByteOrder.BIG_ENDIAN);
  }

  @Test
  public void wrapBigEndian2() {
    ByteBuffer bb = ByteBuffer.allocate(64);
    WritableBuffer wbuf = WritableBuffer.wrap(bb);
    assertTrue(wbuf.swapBytes());
    assertEquals(wbuf.getResourceOrder(), ByteOrder.BIG_ENDIAN);
  }

  @Test
  public void checkGetArray() {
    byte[] byteArr = new byte[64];
    WritableMemory wmem = WritableMemory.wrap(byteArr);
    assertTrue(wmem.getArray() == byteArr);
    WritableBuffer wbuf = wmem.asWritableBuffer();
    assertTrue(wbuf.getArray() == byteArr);
  }

  @Test(expectedExceptions = IllegalArgumentException.class)
  public void checkSelfArrayCopy() {
    byte[] srcAndDst = new byte[128];
    WritableMemory wmem = WritableMemory.wrap(srcAndDst);
    wmem.getByteArray(0, srcAndDst, 64, 64);  //non-overlapping
  }

  @Test
  public void checkEquals() {
    int len = 7;
    WritableMemory wmem1 = WritableMemory.allocate(len);
    assertTrue(wmem1.equalTo(wmem1));

    WritableMemory wmem2 = WritableMemory.allocate(len + 1);
    assertFalse(wmem1.equalTo(wmem2));

    WritableMemory reg1 = wmem1.writableRegion(0, wmem1.getCapacity());
    assertTrue(wmem1.equalTo(reg1));

    wmem2 = WritableMemory.allocate(len);
    for (int i = 0; i < len; i++) {
      wmem1.putByte(i, (byte) i);
      wmem2.putByte(i, (byte) i);
    }
    assertTrue(wmem1.equalTo(wmem2));
    assertTrue(wmem1.equalTo(0, wmem1, 0, len));

    reg1 = wmem1.writableRegion(0, wmem1.getCapacity());
    assertTrue(wmem1.equalTo(0, reg1, 0, len));

    len = 24;
    wmem1 = WritableMemory.allocate(len);
    wmem2 = WritableMemory.allocate(len);
    for (int i = 0; i < len; i++) {
      wmem1.putByte(i, (byte) i);
      wmem2.putByte(i, (byte) i);
    }
    assertTrue(wmem1.equalTo(0, wmem2, 0, len - 1));
    assertTrue(wmem1.equalTo(0, wmem2, 0, len));
    wmem2.putByte(0, (byte) 10);
    assertFalse(wmem1.equalTo(0, wmem2, 0, len));
    wmem2.putByte(0,  (byte) 0);
    wmem2.putByte(len - 2, (byte) 0);
    assertFalse(wmem1.equalTo(0, wmem2, 0, len - 1));
  }

  @Test
  public void checkEquals2() {
    int len = 23;
    WritableMemory wmem1 = WritableMemory.allocate(len);
    assertTrue(wmem1.equalTo(wmem1));

    WritableMemory wmem2 = WritableMemory.allocate(len + 1);
    assertFalse(wmem1.equalTo(wmem2));



    for (int i = 0; i < len; i++) {
      wmem1.putByte(i, (byte) i);
      wmem2.putByte(i, (byte) i);
    }

    assertTrue(wmem1.equalTo(0, wmem2, 0, len));
    assertTrue(wmem1.equalTo(1, wmem2, 1, len - 1));
  }


}
