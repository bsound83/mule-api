/*
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.runtime.api.artifact.ast;

import static com.google.common.collect.ImmutableList.copyOf;
import static java.util.Optional.ofNullable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.mule.runtime.api.component.ComponentIdentifier;

public class ArtifactAst implements HasParametersAst {

  // TODO add artifact type like module/mule/policy, etc
  // TODO refactor to reuse code between this and ComponentAst
  private Set<String> configFiles = new HashSet<>(); // TODO temporary until we get rid of ApplicationModel
  private boolean disableXmlValidations;// TODO temporary until we get rid of ApplicationModel
  private Map<ComponentIdentifier, ParameterAst> parametersMap = new HashMap<>();
  private List<ComponentAst> globalComponents;
  private String artifactType;

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

  public List<ComponentAst> getAllGlobalComponentsById(ComponentIdentifier componentIdentifier) {
    return getGlobalComponents().stream()
        .filter(componentAst -> componentAst.getComponentIdentifier().equals(componentIdentifier))
        .collect(Collectors.toList());
  }

  public static ArtifactBuilder builder() {
    return new ArtifactBuilder();
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
