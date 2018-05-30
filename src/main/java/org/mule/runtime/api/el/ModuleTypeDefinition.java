/*
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.runtime.api.el;


import org.mule.metadata.api.model.MetadataType;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class ModuleTypeDefinition {

  private ModuleNamespace name;
  private Map<String, MetadataType> declaredElements;

  public ModuleTypeDefinition(ModuleNamespace name, Map<String, MetadataType> declaredElements) {
    this.name = name;
    this.declaredElements = declaredElements;
  }

  public ModuleNamespace getName() {
    return name;
  }

  public Collection<String> identifiers() {
    return declaredElements.keySet();
  }

  public Optional<MetadataType> lookup(String identifier) {
    return Optional.ofNullable(declaredElements.get(identifier));
  }

  public static Builder builder(String name) {
    return new Builder(ModuleNamespace.fromString(name));
  }

  public static Builder builder(ModuleNamespace name) {
    return new Builder(name);
  }

  public static class Builder {

    private ModuleNamespace name;
    private Map<String, MetadataType> declaredElements;

    public Builder(ModuleNamespace name) {
      this.name = name;
      declaredElements = new HashMap<>();
    }

    public Builder addElement(String name, MetadataType type) {
      declaredElements.put(name, type);
      return this;
    }

    public ModuleTypeDefinition build() {
      return new ModuleTypeDefinition(name, declaredElements);
    }
  }
}
