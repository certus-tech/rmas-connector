/*
 * RMAS (Research Management and Administration System) Connector project
 * for data integration.
 * http://www.rmas.ac.uk/documentation/
 *
 * Copyright (c) 2012 Certus Technology Associates Limited.
 * http://www.certus-tech.com/
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.

 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 */
package com.jctal.chaffinch.util;

import java.io.StringReader;
import org.xml.sax.EntityResolver;
import org.xml.sax.InputSource;

/**
 * Null entity resolver for dtds.
 *
 * @author Robert Pumphrey
 */
public class NullEntityResolver implements EntityResolver {

  /**
   * {@inheritDoc}
   */
  @Override
  public InputSource resolveEntity(String publicId, String systemId) {
    if (systemId.endsWith(".dtd")) {
      StringReader stringInput = new StringReader(" ");
      return new InputSource(stringInput);
    } else {
      return null;    // default behavior
    }
  }
}
