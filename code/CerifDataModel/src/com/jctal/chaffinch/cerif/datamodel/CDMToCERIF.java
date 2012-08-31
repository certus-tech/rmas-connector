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

import com.jctal.chaffinch.cerif.datamodel.schema.Field;
import com.jctal.chaffinch.util.DefaultFileData;
import com.jctal.chaffinch.util.FileData;
import com.jctal.chaffinch.util.MiscUtils;
import com.jctal.chaffinch.util.processor.Processor.ExitStatus;
import com.jctal.chaffinch.util.processor.ProcessorReturn;
import com.jctal.chaffinch.util.processor.xml.SchemaValidateProcessor;
import com.jctal.chaffinch.util.xml.XMLPrettyPrint;
import com.jctal.chaffinch.xquf.XQUFEngine;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URL;
import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import org.apache.log4j.Logger;

/**
 * Convert CDM compliant data to a CERIF XML serialisation. <p>You <b>must</b> call initialise first and destroy in a
 * finally block, else daemon threads will be left which will prevent the JVM from exiting.</p>
 *
 * @author Johnathon Harris
 */
public class CDMToCERIF {

  /**
   * Logger instance for this class.
   */
  private static Logger logger = Logger.getLogger(CDMToCERIF.class);
  /**
   * Root node of an empty CERIF document.
   */
  private static final String CERIF_XML_EMPTY = "<CERIF release=\"1.4\" date=\"%s\" "
  + "sourceDatabase=\"com.jctal.chaffinch.rmasconnector\" xmlns=\"urn:xmlns:org:eurocris:cerif-1.4-0\"></CERIF>";
  /**
   * CDM fields.
   */
  private final Collection<Field> fields;
  /**
   * Cache XQUF expressions against CDM field.
   */
  private final Map<String, String> xqufExpressions = new HashMap<String, String>();
  /**
   * XQUF evaluation engine.
   */
  private XQUFEngine engine;

  /**
   * Create new CDMToCERIF.
   */
  public CDMToCERIF() {
    fields = new CanonicalDataModel().getFields();
  }

  /**
   * Initialise the XQUF engine.
   *
   * @throws Exception If the engine cannot be completely initialised.
   */
  public void initialise() throws Exception {
    if (engine == null) {
      ClassLoader classLoader = getClass().getClassLoader();
      Properties props = new Properties();
      props.load(classLoader.getResourceAsStream("com/jctal/chaffinch/cerif/datamodel/CDMToCERIF.properties"));
      String engineClassName = (String)props.get("XQUFEngine");
      logger.debug("About to load XQUFEngine class: " + engineClassName);
      Class<?> unknownClass = classLoader.loadClass(engineClassName);
      Class<? extends XQUFEngine> engineClass = unknownClass.asSubclass(XQUFEngine.class);
      engine = engineClass.newInstance();
    }
    engine.initialise();
    engine.setDocument(getEmptyDocument());
  }

  /**
   * Destroy the XQUF engine.
   *
   * @throws Exception If the engine cannot be completely destroyed.
   */
  public void destroy() throws Exception {
    engine.destroy();
  }

  /**
   * Process supplied data to generate CERIF.
   *
   * @param data A bean containing data compliant with CDM fields.
   * @throws Exception If an error occurs accessing the supplied data or locating/executing XQUF expressions.
   */
  public void processData(Object data) throws Exception {
    try {
      // Get all data values from this object initially. This is required because a given XQUF expression
      // may need to access multiple values from the data object when serialising a single field.
      Map<String, Object> dataValues = getDataValues(data);

      // Process each field in turn to generate CERIF for it, for this data object.
      // We needn't bother if there is no data present for this field.
      for (Field field : fields) {
        if (field.isCerifSupported() != null && field.isCerifSupported().booleanValue()
        && dataValues.containsKey(field.getName())) {
          processDataField(dataValues, field);
        }
      }
    } catch (Exception e) {
      // Only toString the supplied data if in debug mode.
      if (logger.isDebugEnabled()) {
        logger.debug("Failed to serialise data as CERIF: " + data, e);
      }
      logger.error("Failed to serialise data as CERIF.", e);
      throw e;
    }
  }

  /**
   * Get CERIF XML serialisation of the processed data.
   *
   * @return Serialised DOM as XML.
   * @throws Exception If an error occurs accessing the supplied data, locating/executing XQUF expressions or
   *                   serialising the DOM.
   */
  public String getCERIF() throws Exception {
    // Return the modified DOM.
    byte[] cerifXml = engine.getDocument();
    validateCERIF(cerifXml);
    XMLPrettyPrint xmlPrettyPrint = new XMLPrettyPrint(false);
    xmlPrettyPrint.setClassLoader(getClass().getClassLoader());
    cerifXml = xmlPrettyPrint.prettyPrintXML(cerifXml);
    return new String(cerifXml);
  }

  /**
   * Validate the supplied XML against the CERIF schema.
   *
   * @param cerifXml XML to be validated.
   * @throws Exception If a validation error occurs.
   */
  private void validateCERIF(byte[] cerifXml) throws Exception {
    // Prepare data to be validated.
    FileData input = new DefaultFileData();
    input.setName("Input");
    input.setMimeType("text/xml");
    input.setFileType(1);
    input.setData(cerifXml);

    // Check valid data passes.
    // Note we must provider our own ClassLoader for this to work in a Kettle plugin environment.
    SchemaValidateProcessor processor = new SchemaValidateProcessor();
    processor.setClassLoader(getClass().getClassLoader());
    processor.setSchemaClasspath("com/jctal/chaffinch/cerif/CERIF_1.4_0.xsd");
    processor.initialise();
    ProcessorReturn processorOutput = processor.process(null, input, null);
    if (processorOutput.getExitStatus() != ExitStatus.SUCCESS) {
      throw new Exception("Validation failed: " + new String(processorOutput.getOutputFile().getData()));
    }
  }

  /**
   * Get all the data values from a given row object.
   *
   * @param dataObj Row object containing data values.
   * @return All CDM data values located.
   * @throws Exception If an error occurs accessing the supplied object getters.
   */
  private Map<String, Object> getDataValues(Object dataObj) throws Exception {
    Map<String, Object> values = new HashMap<String, Object>();
    for (Field field : fields) {
      Object value = MiscUtils.beanProperty(dataObj, field.getName());
      if (value != null) {
        values.put(field.getName(), value);
      }
    }
    return values;
  }

  /**
   * Produce CERIF for the supplied field.
   *
   * @param dataValues All CDM values for the data row being processed.
   * @param field      Field to produce CERIF for.
   * @throws Exception If an error occurs getting the XQUF expression or executing it.
   */
  private void processDataField(Map<String, Object> dataValues, Field field) throws Exception {
    // Prepare XQUF expression.
    String xqufStr = getQueryExpression(field);

    // Include all values as external variables.
    Map<String, Object> queryParams = new HashMap<String, Object>();
    for (Field fieldObj : fields) {
      String fieldName = fieldObj.getName();
      Object value = dataValues.get(fieldName);
      if (value != null) {
        queryParams.put(fieldName, value);
      }
    }

    // Evaluate expression.
    engine.executeExpression(xqufStr, dataValues);
  }

  /**
   * Get an XQUF expression for producing CERIF for the supplied field.
   *
   * @param field Field to get XQUF expression for.
   * @return An XQUF expression.
   * @throws IOException If the XQUF expression file cannot be located or an error occurs during reading.
   */
  private String getQueryExpression(Field field) throws IOException {
    // Get the expression source.
    String xqufStr = xqufExpressions.get(field.getName());
    if (xqufStr == null) {
      String path = "com/jctal/chaffinch/cerif/datamodel/" + field.getName() + ".xquf";
      URL resource = CanonicalDataModel.class.getClassLoader().getResource(path);
      if (resource == null) {
        throw new FileNotFoundException("Failed to load file at: " + path);
      }

      // Load the expression source and cache it.
      // We do NOT cache the expression object as we will want to bind a particular value to its external
      // variables, which can't be done more than once.
      xqufStr = new String(MiscUtils.loadByteArray(resource));
      xqufExpressions.put(field.getName(), xqufStr);
    }
    return xqufStr;
  }

  /**
   * Generates an empty CERIF document.
   *
   * @return String containing the CERIF root node with date attribute as current system time.
   */
  private String getEmptyDocument() {
    Format formatter = new SimpleDateFormat("yyyy-MM-dd");
    String dateToday = formatter.format(System.currentTimeMillis());
    return String.format(CERIF_XML_EMPTY, dateToday);
  }
}
