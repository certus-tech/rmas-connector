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
package com.jctal.chaffinch.cerif.datamodel;

import com.jctal.chaffinch.cerif.datamodel.schema.Cdm;
import com.jctal.chaffinch.cerif.datamodel.schema.Field;
import com.jctal.chaffinch.util.MiscUtils;
import com.jctal.chaffinch.util.StandaloneJaxbUtil;
import java.io.FileNotFoundException;
import java.net.URL;
import java.util.Collection;
import org.apache.log4j.Logger;

/**
 * Used to access the CERIF backed Canonical Data Model.
 *
 * @author Adam Cooney
 * @author Johnathon Harris
 */
public class CanonicalDataModel {

  /**
   * Logger instance for this class.
   */
  private static Logger logger = Logger.getLogger(CanonicalDataModel.class);
  /**
   * Path to the CDM XML.
   */
  private static final String CDM_XML_FILE_PATH = "com/jctal/chaffinch/cerif/datamodel/CanonicalDataModel.xml";
  /**
   * Path to the CDM XSD.
   */
  private static final String CDM_XSD_FILE_PATH = "com/jctal/chaffinch/cerif/datamodel/CanonicalDataModel.xsd";
  /**
   * The CDM XSD package.
   */
  private static final String CDM_XSD_PACKAGE = "com.jctal.chaffinch.cerif.datamodel.schema";
  /**
   * Cached XSD. This cannot change in a deployment, so make it static.
   */
  private static final String CDM_XSD;

  static {
    try {
      CDM_XSD = loadFile(CDM_XSD_FILE_PATH);
    } catch (Exception e) {
      String msg = "Failed to load jaxb schema " + CDM_XSD_FILE_PATH;
      logger.error(msg, e);
      throw new RuntimeException(msg, e);
    }
  }

  /**
   * Create new CanonicalDataModel.
   */
  public CanonicalDataModel() {
    super();
  }

  /**
   * Get all CDM fields.
   *
   * @return All CDM fields.
   */
  public Collection<Field> getFields() {
    Cdm cdm = unmarshal();
    return cdm.getField();
  }

  /**
   * Unmarshal the common data model XML from the classpath.
   *
   * @return The root node of the unmarshalled XML bean from JAXB.
   */
  private Cdm unmarshal() {
    ClassLoader cl = this.getClass().getClassLoader();
    try {
      String cdmXML = loadFile(CDM_XML_FILE_PATH);
      return (Cdm)StandaloneJaxbUtil.unmarshalFromString(cdmXML, CDM_XSD_PACKAGE, cl, CDM_XSD);
    } catch (Exception e) {
      String msg = "Failed to unmarshal CDM XML from: " + CDM_XML_FILE_PATH;
      logger.error(msg, e);
      throw new RuntimeException(msg, e);
    }
  }

  /**
   * Load file from a resource path.
   *
   * @param path The resource path.
   * @return File contents.
   * @throws Exception If the file could not be found or there was a problem loading the data.
   */
  private static String loadFile(String path) throws Exception {
    URL resource = CanonicalDataModel.class.getClassLoader().getResource(path);
    if (resource == null) {
      throw new FileNotFoundException("Failed to load the file '" + path
      + "'. This will mean that the file is not available on the class path.");
    }
    return new String(MiscUtils.loadByteArray(resource));
  }
}
