/*
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.runtime.api.el;

import java.net.URL;
import java.util.Optional;

/**
 * Resolves the Resource of a given ModuleNamespace
 */
public interface ModuleResourceResolver {

  Optional<Resource> resourceResolver(ModuleNamespace moduleNamespace);

  class Resource {

    private String content;
    private String url;

    public Resource(String content, String url) {
      this.content = content;
      this.url = url;
    }

    public String getContent() {
      return content;
    }

    public String getUrl() {
      return url;
    }
  }
}
