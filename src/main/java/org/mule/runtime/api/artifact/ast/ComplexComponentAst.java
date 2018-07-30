/*
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.runtime.api.artifact.ast;

import static java.util.Collections.unmodifiableList;
import static java.util.stream.Collectors.toList;
import static java.util.stream.Stream.concat;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import org.mule.runtime.api.util.Pair;

import com.google.common.collect.Streams;

public abstract class ComplexComponentAst extends ProcessorComponentAst implements HasNestedComponentsAst {

  List<ComponentAst> nestedComponentAst;
  private List<ComponentAst> allNestedComponentsAstRecursively;
  private List<ComponentAst> allNestedComponentsAst;

  public List<ComponentAst> getNestedComponentsAst() {
    return nestedComponentAst;
  }

  @Override
  public List<Pair<ComponentAst, SimpleParameterValueAst>> getNestedSimpleParameterValues() {
    return Streams.concat(super.getNestedSimpleParameterValues().stream(), nestedComponentAst.stream()
        .map(ComponentAst::getNestedSimpleParameterValues)
        .flatMap(List::stream)).collect(toList());
  }

  @Override
  public List<ComponentAst> getAllNestedComponentAstRecursively() {
    if (allNestedComponentsAstRecursively == null) {
      Stream<ComponentAst> currentComponentInnerAst = concat(super.getAllNestedComponentAstRecursively().stream(),
                                                             nestedComponentAst.stream());
      Stream<ComponentAst> innerComponentsInnerAsts = nestedComponentAst.stream()
          .map(componentAst -> componentAst.getAllNestedComponentAstRecursively())
          .flatMap(list -> list.stream());
      allNestedComponentsAstRecursively =
          unmodifiableList(concat(currentComponentInnerAst, innerComponentsInnerAsts).collect(toList()));
    }
    return allNestedComponentsAstRecursively;
  }

  @Override
  public List<ComponentAst> getAllNestedComponentAst() {
    if (allNestedComponentsAst == null) {
      allNestedComponentsAst = unmodifiableList(concat(super.getAllNestedComponentAstRecursively().stream(),
                                                       nestedComponentAst.stream())
                                                           .collect(toList()));
    }
    return allNestedComponentsAst;
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
