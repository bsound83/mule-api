/*
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.runtime.api.dsl;

public class ResolvedValue {

  private Object resolvedValue;

  public ResolvedValue(Object resolvedValue) {
    this.resolvedValue = resolvedValue;
  }

  public Object getResolvedValue() {
    return resolvedValue;
  }
}
