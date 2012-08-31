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
import java.util.HashMap;
import java.util.Map;

/**
 * This is the default flat implementation of the DynaBean interface. A property name may contain a '.' and this
 * DynaBean just carries on regardless.
 *
 * @author Rob Pumphrey
 */
public class DefaultFlatDynaBean implements DynaBean {

  /**
   * Container for properties.
   */
  private final Map properties;

  /**
   * Creates new DefaultFlatDynaBean.
   */
  public DefaultFlatDynaBean() {
    properties = new HashMap();
  }

  /**
   * Get the property.
   *
   * @param name The name of the property.
   * @return The property value.
   */
  @Override
  public Object get(String name) {
    return properties.get(name);
  }

  /**
   * Is the property set.
   *
   * @param name The name of the property.
   * @return true if the property has been set.
   */
  @Override
  public boolean isSet(String name) {
    return properties.containsKey(name);
  }

  /**
   * Set the property.
   *
   * @param name  The name of the property.
   * @param value The value of the bean.
   */
  @Override
  public void set(String name, Object value) {
    properties.put(name, value);
  }

  /**
   * Get the collection of properties in the bean.
   *
   * @return The collection of properties.
   */
  @Override
  public Collection getProperties() {
    return properties.keySet();
  }

  /**
   * Unset the property.
   *
   * @param name The name of the property.
   * @throws Exception If the property cannot be unset.
   */
  @Override
  public void unset(String name) throws Exception {
    properties.remove(name);
  }

  // Methods required so we can serialize to xml.
  /**
   * Get the internal property map. <b>DO NOT CALL THIS METHOD AS A DEVELOPER.</b>
   *
   * @return The internal property map.
   */
  public Map getInternalProperties() {
    return this.properties;
  }

  /**
   * Set the internal property map. <b>DO NOT CALL THIS METHOD AS A DEVELOPER.</b>
   *
   * @param properties The new internal property map.
   */
  public void setInternalProperties(Map properties) {
    this.properties.putAll(properties);
  }
}
