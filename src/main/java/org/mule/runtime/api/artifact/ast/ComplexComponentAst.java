/*
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.runtime.api.artifact.ast;

import static com.google.common.collect.Streams.concat;
import static java.util.stream.Collectors.toList;

import java.util.ArrayList;
import java.util.List;

public abstract class ComplexComponentAst extends ProcessorComponentAst implements HasNestedComponentsAst {

  List<ComponentAst> nestedComponentAst;

  public List<ComponentAst> getNestedComponentsAst() {
    return nestedComponentAst;
  }

  @Override
  public List<SimpleParameterValueAst> getNestedSimpleParameterValues() {
    return concat(super.getNestedSimpleParameterValues().stream(), nestedComponentAst.stream()
        .map(ComponentAst::getNestedSimpleParameterValues)
        .flatMap(List::stream)).collect(toList());
  }

  ComplexComponentAst() {}

  public static abstract class ComplexComponentAstBuilder<T extends ComplexComponentAstBuilder, BuilderType extends ComplexComponentAst>
      extends ComponentAstBuilder<T, BuilderType> {

    List<ComponentAst> processorComponents = new ArrayList<>();

    public ComplexComponentAstBuilder withNestedComponentsAst(List<ComponentAst> processorComponents) {
      this.processorComponents.addAll(processorComponents);
      return this;
    }

    public ComplexComponentAstBuilder withNestedComponentsAst(ComponentAst processorComponent) {
      this.processorComponents.add(processorComponent);
      return this;
    }

    public BuilderType build() {
      BuilderType complexComponent = super.build();
      complexComponent.nestedComponentAst = this.processorComponents;
      return complexComponent;
    }
  }
}
