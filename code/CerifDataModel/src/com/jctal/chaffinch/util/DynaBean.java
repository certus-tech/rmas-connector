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

import java.util.Collection;

/**
 * Classes should implement this interface if they want to operate as a dynamic bean.
 *
 * @author Rob Pumphrey
 */
public interface DynaBean {

  /**
   * Is the property set.
   *
   * @param name The name of the property.
   * @return true if the property has been set.
   * @throws Exception If the property status cannot be determined.
   */
  boolean isSet(String name) throws Exception;

  /**
   * Set the property.
   *
   * @param name  The name of the property.
   * @param value The value of the bean.
   * @throws Exception If the property cannot be set.
   */
  void set(String name, Object value) throws Exception;

  /**
   * Unset the property.
   *
   * @param name The name of the property.
   * @throws Exception If the property cannot be unset.
   */
  void unset(String name) throws Exception;

  /**
   * Get the property.
   *
   * @param name The name of the property.
   * @return The property value.
   * @throws Exception If the property value cannot be obtained.
   */
  Object get(String name) throws Exception;

  /**
   * Get the collection of properties in the bean.
   *
   * @return The collection of properties.
   * @throws Exception Thrown if the properties cannot be found.
   */
  Collection getProperties() throws Exception;
}
