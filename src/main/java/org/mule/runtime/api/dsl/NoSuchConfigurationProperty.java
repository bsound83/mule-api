/*
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.runtime.api.dsl;

import org.mule.runtime.api.i18n.I18nMessage;

/**
 * Exception thrown when a configuration property key could not be resolved.
 *
 * @since 1.2
 */
public class NoSuchConfigurationProperty extends ConfigurationPropertyResolutionException {

  private final String unresolvedKey;

  public String getUnresolvedKey() {
    return unresolvedKey;
  }

  public NoSuchConfigurationProperty(I18nMessage message, String unresolvedKey) {
    super(message);
    this.unresolvedKey = unresolvedKey;
  }
}
