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

import java.io.ByteArrayInputStream;
import javax.xml.XMLConstants;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.Unmarshaller;
import javax.xml.bind.util.ValidationEventCollector;
import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.SchemaFactory;
import org.apache.log4j.Logger;

/**
 * Standalone version of JaxbUtil. Uses only logging, JDK classes and Jaxb classes.
 *
 * @author Johnathon Harris
 */
public final class StandaloneJaxbUtil {

  /**
   * Logger instance for this class.
   */
  private static Logger logger = Logger.getLogger(StandaloneJaxbUtil.class);

  /**
   * Creates new StandaloneJaxbUtil.
   */
  private StandaloneJaxbUtil() {
  }

  /**
   * Unmarshal xml to jaxb beans.
   *
   * @param xml         The xml as a string.
   * @param packageName The jaxb classes package.
   * @param loader      The classloader.
   * @param schema      The schema used for validation. May be null.
   * @throws Exception Thrown on Jaxb error.
   * @return The unmarshalled object.
   */
  public static Object unmarshalFromString(String xml, String packageName, ClassLoader loader, String schema)
  throws Exception {
    Source schemaSource = null;
    if (schema != null) {
      schemaSource = new StreamSource(new ByteArrayInputStream(schema.getBytes()));
    }
    return unmarshalFromString(xml, packageName, loader, schemaSource);
  }

  /**
   * Unmarshal xml to jaxb beans.
   *
   * @param xml         The xml as a string.
   * @param packageName The jaxb classes package.
   * @param loader      The classloader.
   * @param schema      The schema used for validation. May be null.
   * @throws Exception Thrown on Jaxb error.
   * @return The unmarshalled object.
   */
  private static Object unmarshalFromString(String xml, String packageName, ClassLoader loader, Source schema)
  throws Exception {
    // A JAXBContext instance is created for handling classes generated in the package.
    JAXBContext jc = JAXBContext.newInstance(packageName, loader);

    // An Unmarshaller instance is created.
    Unmarshaller unmarshaller = jc.createUnmarshaller();
    // Setup a validation event collector, we want validation to be performed.
    ValidationEventCollector validEventCollector = new ValidationEventCollector();
    unmarshaller.setEventHandler(validEventCollector);

    // The validation above will generate events for things like unrecognised elements,
    // but will not validate for XSD features like maxOccurs.
    // To do this we need to embed the schema.
    SchemaFactory sf = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);

    // Have to load the resource from the JAR in a real deployment, so load from a stream.
    if (schema != null) {
      unmarshaller.setSchema(sf.newSchema(schema));
      if (logger.isDebugEnabled()) {
        logger.debug("Unmarshalling config with schema ");
      }
    }

    // Unmarshal.
    if (logger.isDebugEnabled()) {
      logger.debug("Un-marshalling xml data: \n" + xml);
    }
    return unmarshaller.unmarshal(new ByteArrayInputStream(xml.getBytes()));
  }
}
