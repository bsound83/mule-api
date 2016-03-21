/*
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.api.metadata.descriptor;

import org.mule.api.temporary.MuleMessage;

/**
 * Represents the view of all the Metadata associated to an Component's
 * {@link MuleMessage} output
 *
 * @since 1.0
 */
public interface OutputMetadataDescriptor
{

    /**
     * @return the {@link TypeMetadataDescriptor} of the Component's
     * output {@link MuleMessage#getPayload}
     */
    TypeMetadataDescriptor getPayloadMetadata();

    /**
     * @return the {@link TypeMetadataDescriptor} of the Component's
     * output {@link MuleMessage#getAttributes}
     */
    TypeMetadataDescriptor getAttributesMetadata();

}
