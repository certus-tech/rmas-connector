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

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Method;
import java.net.URL;
import java.util.Map;

/**
 * Class that contains so misc utility functions that don't really fit in elsewhere.
 *
 * @author Rob Pumphrey
 * @author Johnathon Harris
 */
public final class MiscUtils {

  /**
   * Creates new MiscUtils. Never need to do this.
   */
  private MiscUtils() {
  }

  /**
   * Find out if a string is empty. Returns true if str is null, or str is length = 0, or str only contains whitespace
   *
   * @param str The string to check.
   * @return True if the string is null, empty, or only contains whitespace, else false.
   */
  public static boolean isStringEmpty(String str) {
    boolean bret = (str == null);
    if (!bret) {
      bret = true;
      for (int i = 0, len = str.length(); i < len; i++) {
        if (!Character.isWhitespace(str.charAt(i))) {
          bret = false;
          break;
        }
      }
    }
    return bret;
  }

  /**
   * Get a bean property. This method works with either a standard bean pattern, or a DynaBean.
   *
   * @param form     The object that contains the property.
   * @param property The name of the property to upper case.
   * @return The value of the property, may be null.
   * @throws Exception If the property cannot be found.
   */
  public static Object beanProperty(Object form, String property) throws Exception {
    return beanProperty(form, property, null, false);
  }

  /**
   * Get a bean property. This method works with either a standard bean pattern, or a DynaBean.
   *
   * @param form           The object that contains the property.
   * @param property       The name of the property to upper case.
   * @param defaultValue   The default value to return, if the actual property is null.
   * @param ignoreDynaBean If true, ignore the fact we have got a DynaBean.
   * @return The value of the property, or defaultValue if that value is null.
   * @throws Exception If the property cannot be found.
   */
  public static Object beanProperty(Object form, String property, Object defaultValue, boolean ignoreDynaBean)
  throws Exception {
    if (form == null) {
      return defaultValue;
    }

    // We always prefer to deal with real methods instead of get(property) methods.
    // So see if an actual method exists for this property before falling back to treating it as a DynaBean.
    String getterMethodName = getterMethodName(property);
    Method getterMethod = null;
    try {
      getterMethod = form.getClass().getMethod(getterMethodName);
    } catch (NoSuchMethodException noSuchMethodException1) {

      // Failure might be due to a boolean getter in the style 'isXXX'.
      getterMethodName = createMethodName("is", property);
      try {
        getterMethod = form.getClass().getMethod(getterMethodName);
      } catch (NoSuchMethodException noSuchMethodException2) {
        // NOTE: noSuchMethodException2 is unused, but that is fine. It is more likely developers will care
        // about the 'get' version of the method missing.

        // Not a problem for the method to not exist at this point, since the form may be a DynaBean.
        if (form instanceof DynaBean && !ignoreDynaBean) {
          Object propertyValue = ((DynaBean)form).get(property);
          if (propertyValue == null) {
            return defaultValue;
          }
          return propertyValue;

        } else if (form instanceof Map) {
          Object propertyValue = ((Map)form).get(property);
          if (propertyValue == null) {
            return defaultValue;
          }
          return propertyValue;

        } else if (property.matches(".*\\..*")) {
          // Handle nested properties e.g. 'relatedEntity.name'.
          Object obj = beanProperty(form, property.replaceAll("\\..*$", ""), null, ignoreDynaBean);
          return beanProperty(obj, property.replaceAll("^[^.]*\\.", ""), defaultValue, ignoreDynaBean);

        } else {
          // Supplied prop maps to neither a DynaBean property or a real method, so throw the exception on.
          throw noSuchMethodException1;
        }
      }
    }

    // If we are here then the getterMethod should exist, else the code above is broken.
    if (getterMethod == null) {
      throw new Exception("Getter method not located, object should have been dealt with before this point.");
    }
    Object propertyValue = getterMethod.invoke(form);
    if (propertyValue == null) {
      return defaultValue;
    }
    return propertyValue;
  }

  /**
   * Get the getter method name for a property.
   *
   * @param property The property name.
   * @return The name of the getter method.
   */
  public static String getterMethodName(String property) {
    return createMethodName("get", property);
  }

  /**
   * Create a method name.
   *
   * @param prefix   The method name prefix.
   * @param property The property to create the method for.
   * @return A method name.
   */
  private static String createMethodName(String prefix, String property) {
    if (property != null && property.length() > 0) {
      StringBuilder s = new StringBuilder();
      return s.append(prefix).append(property.substring(0, 1).toUpperCase()).append(property.substring(1)).toString();
    }
    return prefix;
  }

  /**
   * Load a byte [] from a URL. This method can be used to read a file or other URL into a byte array.
   *
   * @param url The URL to open the stream from.
   * @return A byte array representing the resource defined by the URL.
   * @throws IOException On URL read error.
   */
  public static byte[] loadByteArray(URL url) throws IOException {
    // Open an input stream from the URL
    InputStream iStream = new BufferedInputStream(url.openStream());
    // Open an output byte stream to write to.
    ByteArrayOutputStream oStream = new ByteArrayOutputStream();
    // Create a tmp buffer.
    byte[] blk = new byte[8192];
    int bytesRead;
    while ((bytesRead = iStream.read(blk)) != -1) {
      oStream.write(blk, 0, bytesRead);
    }
    iStream.close();
    // Return the bytes
    return oStream.toByteArray();
  }
}
