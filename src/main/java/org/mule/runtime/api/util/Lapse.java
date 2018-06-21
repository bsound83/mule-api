/*
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.runtime.api.util;

public class Lapse {

  private long time = System.currentTimeMillis();


  public void mark(String step) {
    long newTime = System.currentTimeMillis();
    long delta = newTime - time;
    time = newTime;

    System.out.println("<<<<<<<<<<" + step + ": " + delta + ">>>>> ms");
  }


}
