/*
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.runtime.api.artifact.ast;

import org.mule.runtime.api.artifact.sintax.SourceCodeLocation;
import org.mule.runtime.api.component.location.BaseLocation;

public class ParameterValueAst {

  protected BaseLocation location;
  protected SourceCodeLocation sourceCodeLocation;

  public BaseLocation getLocation() {
    return location;
  }

  public SourceCodeLocation getSourceCodeLocation() {
    return sourceCodeLocation;
  }
}