/*
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.runtime.api.artifact.ast;

import static com.google.common.collect.ImmutableList.copyOf;
import static java.util.Optional.empty;
import static java.util.Optional.of;
import static java.util.Optional.ofNullable;
import static java.util.stream.Collectors.toList;
import static java.util.stream.Stream.concat;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

import org.mule.runtime.api.component.ComponentIdentifier;
import org.mule.runtime.api.exception.MuleRuntimeException;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;

public class ArtifactAst implements HasParametersAst {

  // TODO add artifact type like module/mule/policy, etc
  // TODO refactor to reuse code between this and ComponentAst
  private Set<String> configFiles = new HashSet<>(); // TODO temporary until we get rid of ApplicationModel
  private boolean disableXmlValidations;// TODO temporary until we get rid of ApplicationModel
  private Map<ComponentIdentifier, ParameterAst> parametersMap = new HashMap<>();
  private List<ComponentAst> globalComponents;
  private String artifactType;
  private Cache<String, Optional<ComponentAst>> globalComponentByNameCache = CacheBuilder.newBuilder().build();
  private Cache<String, Optional<ComponentAst>> componentByNameCache = CacheBuilder.newBuilder().build();
  private Cache<SourceCodeLocation, Optional<ComponentAst>> parentComponentAstCache = CacheBuilder.newBuilder().build();
  private List<ComponentAst> allNestedComponentsAst;

  public Set<String> getConfigFiles() {
    return configFiles;
  }

  public boolean isDisableXmlValidations() {
    return disableXmlValidations;
  }

  public List<ComponentAst> getGlobalComponents() {
    return globalComponents;
  }

  public String getArtifactType() {
    return artifactType;
  }

  public List<ParameterAst> getParameters() {
    return copyOf(parametersMap.values());
  }

  public Optional<ParameterAst> getParameter(ComponentIdentifier componentIdentifier) {
    return ofNullable(parametersMap.get(componentIdentifier));
  }

  public Optional<ComponentAst> getParentComponentAst(ComponentAst componentAst) {
    try {
      return parentComponentAstCache.get(componentAst.getSourceCodeLocation(), () -> {
        return getAllNestedComponentsAst().stream()
            .filter(nestedComponentAst -> nestedComponentAst.getSourceCodeLocation().equals(componentAst.getSourceCodeLocation()))
            .findFirst();
      });
    } catch (ExecutionException e) {
      throw new MuleRuntimeException(e);
    }
  }

  public List<ComponentAst> getAllGlobalComponentsById(ComponentIdentifier componentIdentifier) {
    return getGlobalComponents().stream()
        .filter(componentAst -> componentAst.getComponentIdentifier().equals(componentIdentifier))
        .collect(toList());
  }

  public List<ComponentAst> getAllNestedComponentsAst() {
    if (allNestedComponentsAst == null) {
      allNestedComponentsAst = concat(globalComponents.stream(), globalComponents.stream()
          .map(globalComponentAst -> globalComponentAst.getAllNestedComponentAstRecursively())
          .flatMap(list -> list.stream())).collect(Collectors.toList());
    }
    return allNestedComponentsAst;
  }

  public static ArtifactBuilder builder() {
    return new ArtifactBuilder();
  }

  public Optional<ComponentAst> getGlobalComponentByName(String componentName) {
    try {
      return globalComponentByNameCache.get(componentName, () -> getGlobalComponents().stream()
          .filter(componentAst -> componentAst.getNameParameter().isPresent())
          .filter(componentAst -> componentAst.getNameParameter().get().getValueAsSimpleParameterValueAst()
              .getResolvedValueResult()
              .isRight())
          .filter(componentAst -> componentAst.getNameParameter().get().getValueAsSimpleParameterValueAst()
              .getResolvedValueResult()
              .getRight().getResolvedValue().toString().equals(componentName))
          .findAny());
    } catch (ExecutionException e) {
      throw new MuleRuntimeException(e);
    }
  }

  /**
   * Find a named component within all the components including nested ones.
   *
   * @param name the expected value for the name attribute configuration.
   * @return the component if present, if not, an empty {@link Optional}
   */
  public Optional<ComponentAst> getComponentByName(String name) {
    return Optional.ofNullable(getGlobalComponentByName(name)
        .orElseGet(() -> {
          try {
            return componentByNameCache.get(name, () -> getGlobalComponents()
                .stream()
                .map(componentAst -> recursivelyGetComponentByName(name, componentAst).orElse(null))
                .findAny()).orElse(null);
          } catch (ExecutionException e) {
            throw new MuleRuntimeException(e);
          }
        }));
  }

  private Optional<ComponentAst> recursivelyGetComponentByName(String name, ComponentAst componentAst) {
    Optional<ParameterAst> nameParameter = componentAst.getNameParameter();
    if (nameParameter.isPresent() && nameParameter.get().getValueAsSimpleParameterValueAst().getResolvedValueResult().isRight()) {
      if (nameParameter.get().getValueAsSimpleParameterValueAst().getResolvedValueResult().getRight().getResolvedValue()
          .equals(name)) {
        return of(componentAst);
      }
    }
    if (componentAst instanceof ConstructAst) {
      Optional<ComponentAst> foundComponentAst = ((ConstructAst) componentAst).getNestedComponentsAst()
          .stream()
          .map(innerComponentAst -> recursivelyGetComponentByName(name, innerComponentAst).orElse(null))
          .findAny();
      if (foundComponentAst.isPresent()) {
        return foundComponentAst;
      }
    }
    return empty();
  }

  public Optional<ComponentAst> getGlobalComponentByIdentifier(ComponentIdentifier componentIdentifier) {
    return globalComponents.stream()
        .filter(componentAst -> componentAst.getComponentIdentifier().equals(componentIdentifier))
        .findFirst();
  }

  public static final class ArtifactBuilder {

    private Set<String> configFiles = new HashSet<>(); // TODO temporary until we get rid of ApplicationModel
    private String artifactType;
    private List<ComponentAst> globalComponents = new ArrayList<>();
    private boolean disableXmlValidations;
    private Map<ComponentIdentifier, ParameterAst> parametersMap = new HashMap<>();
    private Object configResources;


    private ArtifactBuilder() {}

    public ArtifactBuilder withGlobalComponents(List<ComponentAst> globalComponents) {
      this.globalComponents = globalComponents;
      return this;
    }

    public ArtifactBuilder withParameters(List<ParameterAst> parameters) {
      parameters.stream().forEach(parameterAst -> {
        parametersMap.put(parameterAst.getParameterIdentifier().getIdentifier(), parameterAst);
      });
      return this;
    }

    // TODO remove
    public ArtifactBuilder withConfigFiles(Set<String> configFiles) {
      this.configFiles.addAll(configFiles);
      return this;
    }

    public ArtifactBuilder withArtifactType(String artifactType) {
      this.artifactType = artifactType;
      return this;
    }

    // TODO remove
    public ArtifactBuilder withDisableXmlValidations(boolean disableXmlValidations) {
      this.disableXmlValidations = disableXmlValidations;
      return this;
    }

    public ArtifactAst build() {
      ArtifactAst artifact = new ArtifactAst();
      artifact.configFiles = this.configFiles;
      artifact.disableXmlValidations = this.disableXmlValidations;
      artifact.globalComponents = this.globalComponents;
      artifact.parametersMap = this.parametersMap;
      artifact.artifactType = this.artifactType;
      return artifact;
    }
  }
}
