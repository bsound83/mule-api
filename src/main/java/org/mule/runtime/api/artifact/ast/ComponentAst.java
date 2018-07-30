/*
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.runtime.api.artifact.ast;

import static com.google.common.collect.ImmutableList.copyOf;
import static java.util.Collections.unmodifiableList;
import static java.util.Optional.ofNullable;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.mule.runtime.api.component.ComponentIdentifier;
import org.mule.runtime.api.component.TypedComponentIdentifier;
import org.mule.runtime.api.component.location.ComponentLocation;
import org.mule.runtime.api.util.Pair;
import org.mule.runtime.internal.dsl.DslConstants;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;

//TODO remove HasParameters from this component
public abstract class ComponentAst implements HasParametersAst, HasNestedComponentsAst {

  public static final ComponentIdentifier NAME_PARAMETER_IDENTIFIER =
      ComponentIdentifier.builder().namespace(DslConstants.CORE_PREFIX).name("name").build();
  ComponentIdentifier componentIdentifier;
  Multimap<ComponentIdentifier, ParameterAst> parametersMap = ArrayListMultimap.create();
  ComponentLocation componentLocation;
  SourceCodeLocation sourceCodeLocation;

  // TODO review if this parameters make sense here
  private boolean enabled = true;
  private List<ComponentAst> allNestedComponentsAst;
  private Class<?> type;
  private Object beanDefinition;
  private Object beanReference;
  private TypedComponentIdentifier.ComponentType componentType;
  private Object objectInstance;

  public ComponentLocation getComponentLocation() {
    return componentLocation;
  }

  public SourceCodeLocation getSourceCodeLocation() {
    return sourceCodeLocation;
  }

  public ComponentIdentifier getComponentIdentifier() {
    return componentIdentifier;
  }

  public Optional<ParameterAst> getParameter(ComponentIdentifier componentIdentifier) {
    Collection<ParameterAst> values = parametersMap.get(componentIdentifier);
    return ofNullable(values.isEmpty() ? null : values.iterator().next());
  }

  public List<ParameterAst> getParameters(ComponentIdentifier componentIdentifier) {
    return (List<ParameterAst>) parametersMap.get(componentIdentifier);
  }

  @Override
  public List<ParameterAst> getParameters() {
    return copyOf(parametersMap.values());
  }

  public boolean isEnabled() {
    return enabled;
  }

  public Class<?> getType() {
    return type;
  }

  public Optional<TypedComponentIdentifier.ComponentType> getComponentType() {
    return ofNullable(componentType);
  }

  @Override
  public List<ComponentAst> getNestedComponentsAst() {
    return getParameters().stream()
        .filter(parameterAst -> parameterAst.getValue() instanceof ComplexParameterValueAst)
        .map(parameterAst -> parameterAst.getValueAsComplexParameterValueAst().getComponent())
        .collect(Collectors.toList());
  }

  @Override
  public List<ComponentAst> getAllNestedComponentAst() {
    return getNestedComponentsAst();
  }

  /**
   * Provides access to all the {@link SimpleParameterValueAst} including the ones from nested components.
   * 
   * @return all the {@link SimpleParameterValueAst} including nested ones.
   */
  public List<Pair<ComponentAst, SimpleParameterValueAst>> getNestedSimpleParameterValues() {
    return parametersMap.values().stream()
        .filter(parameterAst -> parameterAst.getValue() instanceof SimpleParameterValueAst)
        .map(parameterAst -> new Pair<>(this, parameterAst.getValueAsSimpleParameterValueAst()))
        .collect(Collectors.toList());
  }

  public List<ComponentAst> getAllNestedComponentAstRecursively() {
    if (allNestedComponentsAst == null) {
      allNestedComponentsAst = unmodifiableList(parametersMap.values().stream()
          .filter(parameterAst -> parameterAst.getValue() instanceof ComplexParameterValueAst)
          .map(parameterAst -> parameterAst.getValueAsComplexParameterValueAst().getComponent())
          .collect(Collectors.toList()));
    }
    return allNestedComponentsAst;
  }

  public Optional<ParameterAst> getNameParameter() {
    return getParameter(NAME_PARAMETER_IDENTIFIER);
  }

  // TODO move this to optional
  public String getNameParameterValueOrNull() {
    return getParameter(NAME_PARAMETER_IDENTIFIER)
        .map(parameterAst -> parameterAst.getValueAsSimpleParameterValueAst().getResolvedValueResult())
        .filter(resolvedValue -> resolvedValue.isRight())
        .map(resolvedValue -> resolvedValue.getRight().getResolvedValue().toString())
        .orElse(null);
  }

  public Optional<String> getSameNamespaceSimpleParameterValue(String parameterName) {
    return getParameter(ComponentIdentifier.builder().namespace(DslConstants.CORE_PREFIX).name(parameterName).build())
        .map(parameterAst -> parameterAst.getValueAsSimpleParameterValueAst().getResolvedValueResult())
        .filter(resolvedValue -> resolvedValue.isRight())
        .map(resolvedValue -> resolvedValue.getRight().getResolvedValue().toString());
  }

  public void setEnabled(boolean enabled) {
    this.enabled = enabled;
  }

  /**
   * @param type the type of the object to be created when processing this {@code ComponentModel}.
   */
  public void setType(Class<?> type) {
    this.type = type;
  }

  public Object getBeanDefinition() {
    return beanDefinition;
  }

  public void setBeanDefinition(Object beanDefinition) {
    this.beanDefinition = beanDefinition;
  }

  public Object getBeanReference() {
    return beanReference;
  }

  public void setBeanReference(Object beanReference) {
    this.beanReference = beanReference;
  }

  public Object getObjectInstance() {
    return objectInstance;
  }

  public void setComponentLocation(ComponentLocation componentLocation) {
    this.componentLocation = componentLocation;
  }



  public static abstract class ComponentAstBuilder<T extends ComponentAstBuilder, BuilderType extends ComponentAst> {

    private Multimap<ComponentIdentifier, ParameterAst> parametersMap = ArrayListMultimap.create();
    private ComponentLocation componentLocation;
    private SourceCodeLocation sourceCodeLocation;
    private ComponentIdentifier componentIdentifier;

    protected abstract BuilderType newInstance();

    public T withParameters(List<ParameterAst> parameters) {
      parameters.stream().forEach(parameterAst -> {
        parametersMap.put(parameterAst.getParameterIdentifier().getIdentifier(), parameterAst);
      });
      return (T) this;
    }

    public T withComponentLocation(ComponentLocation componentLocation) {
      this.componentLocation = componentLocation;
      return (T) this;
    }

    public T withSourceCodeLocation(SourceCodeLocation sourceCodeLocation) {
      this.sourceCodeLocation = sourceCodeLocation;
      return (T) this;
    }

    public T withComponentIdentifier(ComponentIdentifier componentIdentifier) {
      this.componentIdentifier = componentIdentifier;
      return (T) this;
    }

    public BuilderType build() {
      BuilderType instance = newInstance();
      instance.parametersMap = this.parametersMap;
      instance.sourceCodeLocation = this.sourceCodeLocation;
      instance.componentIdentifier = this.componentIdentifier;
      return instance;
    }

  }


}
