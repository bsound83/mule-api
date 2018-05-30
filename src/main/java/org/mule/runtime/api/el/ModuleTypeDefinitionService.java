/*
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.runtime.api.el;


import org.mule.runtime.api.service.Service;

import java.util.Optional;

/**
 * This service will generate the {@link ExpressionModule} definition out of a {@link ExpressionModule}
 */
public interface ModuleTypeDefinitionService extends Service {

  /**
   * Returns the definition of the module if found otherwise return empty
   *
   * @param moduleNamespace  The name of the module
   * @param resourceResolver The Resource resolver that is used to resolve the contents
   * @return The module definition if found
   */
  Optional<ModuleTypeDefinition> createDefinition(ModuleNamespace moduleNamespace, ModuleResourceResolver resourceResolver);

}
