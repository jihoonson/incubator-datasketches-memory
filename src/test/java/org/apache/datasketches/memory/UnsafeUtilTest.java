/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package org.apache.datasketches.memory;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;
import static org.testng.Assert.fail;

import java.util.ArrayList;
import java.util.List;

import org.testng.annotations.Test;


/**
 * @author Lee Rhodes
 */
@SuppressWarnings("javadoc")
public class UnsafeUtilTest {
  long testField = 1; //Do not remove & cannot be static. Used in reflection check.

  @Test
  public void checkJDK7methods() {
    try {
      final byte[] byteArr = new byte[16];
      byteArr[0] = (byte) 1;
      final long one = JDK7Compatible.getAndAddLong(byteArr, 16, 1L);
      assertEquals(one, 1L);

      final long two = JDK7Compatible.getAndSetLong(byteArr, 16, 3L);
      assertEquals(two, 2L);
      assertEquals(byteArr[0], 3);

    } catch (Exception e) {
      throw new RuntimeException("Failed");
    }
  }

  @Test
  public void checkJdkString() {
    String jdkVer;
    int[] p = new int[2];
    String[] good1_8Strings = {"1.8.0_121", "1.8.0_162"};
    int len = good1_8Strings.length;
    for (int i = 0; i < len; i++) {
      jdkVer = good1_8Strings[i];
      p = UnsafeUtil.parseJavaVersion(jdkVer);
      UnsafeUtil.checkJavaVersion(jdkVer, p[0], p[1]);
      int jdkMajor = (p[0] == 1) ? p[1] : p[0]; //model the actual JDK_MAJOR
      if (p[0] == 1) { assertTrue(jdkMajor == p[1]); }
      if (p[0] > 1 ) { assertTrue(jdkMajor == p[0]); }
    }
    try {
      jdkVer = "9.0.4"; //ver 9 string
      p = UnsafeUtil.parseJavaVersion(jdkVer);
      UnsafeUtil.checkJavaVersion(jdkVer, p[0], p[1]);
    } catch (Error e) {
      println("" + e);
    }

    try {
      jdkVer = "1.7.0_80"; //1.7 string
      p = UnsafeUtil.parseJavaVersion(jdkVer);
      UnsafeUtil.checkJavaVersion(jdkVer, p[0], p[1]);
    } catch (Error e) {
      println("" + e);
    }
    try {
      jdkVer = "1.6.0_65"; //valid string but < 1.7
      p = UnsafeUtil.parseJavaVersion(jdkVer);
      UnsafeUtil.checkJavaVersion(jdkVer, p[0], p[1]); //throws
      fail();
    } catch (Error e) {
      println("" + e);
    }
    try {
      jdkVer = "b"; //invalid string
      p = UnsafeUtil.parseJavaVersion(jdkVer);
      UnsafeUtil.checkJavaVersion(jdkVer, p[0], p[1]); //throws
      fail();
    } catch (Exception | Error e) {
      println("" + e);
    }
    try {
      jdkVer = ""; //invalid string
      p = UnsafeUtil.parseJavaVersion(jdkVer);
      UnsafeUtil.checkJavaVersion(jdkVer, p[0], p[1]); //throws
      fail();
    } catch (Exception | Error e) {
      println("" + e);
    }
  }

  @Test
  public void checkFieldOffset() {
    assertEquals(testField, 1);
    long offset = UnsafeUtil.getFieldOffset(this.getClass(), "testField");
    assertEquals(offset, 16);
    try {
      offset = UnsafeUtil.getFieldOffset(this.getClass(), "testField2");
      fail();
    } catch (IllegalStateException e) {
      //OK
    }
  }

  @Test(expectedExceptions = IllegalArgumentException.class)
  public void checkInts() {
    Ints.checkedCast(1L << 32);
  }

  @Test
  public void checkArrayBaseOffset()
  {
    final List<Class<?>> classes = new ArrayList<>();
    classes.add(byte[].class);
    classes.add(int[].class);
    classes.add(long[].class);
    classes.add(float[].class);
    classes.add(double[].class);
    classes.add(boolean[].class);
    classes.add(short[].class);
    classes.add(char[].class);
    classes.add(Object[].class);
    classes.add(byte[][].class); // An array type that is not cached

    for (Class<?> clazz : classes) {
      assertEquals(
          UnsafeUtil.getArrayBaseOffset(clazz),
          UnsafeUtil.unsafe.arrayBaseOffset(clazz),
          clazz.getTypeName()
      );
    }
  }

  @Test
  public void printlnTest() {
    println("PRINTING: "+this.getClass().getName());
  }

  /**
   * @param s String to print
   */
  static void println(final String s) {
    //System.out.println(s);
  }

}
