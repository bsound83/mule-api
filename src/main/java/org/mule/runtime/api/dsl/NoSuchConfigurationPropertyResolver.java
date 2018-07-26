/*
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.runtime.api.dsl;

import org.mule.runtime.api.i18n.I18nMessage;

/**
 * Exception thrown when a configuration property resolver could not be found.
 * <p/>
 * This is the case, for instance, when the users uses ${secure::password} and there is no provider present in the artifact to
 * resolve a configuration property prefixed with secure::.
 *
 * @since 1.2
 */
public class NoSuchConfigurationPropertyResolver extends ConfigurationPropertyResolutionException {

  public NoSuchConfigurationPropertyResolver(I18nMessage message) {
    super(message);
  }
}
