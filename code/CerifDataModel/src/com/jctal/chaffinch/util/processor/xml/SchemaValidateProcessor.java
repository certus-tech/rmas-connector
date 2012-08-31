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
package com.jctal.chaffinch.util.processor.xml;

import com.jctal.chaffinch.util.DefaultFileData;
import com.jctal.chaffinch.util.FileData;
import com.jctal.chaffinch.util.processor.Processor;
import com.jctal.chaffinch.util.processor.ProcessorReturn;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Map;
import javax.xml.XMLConstants;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;
import org.apache.log4j.Logger;
import org.w3c.dom.Document;

/**
 * Validates input document against the configured schema.
 *
 * @author Johnathon Harris
 */
public class SchemaValidateProcessor implements Processor {

  /**
   * Logger for this class.
   */
  private static Logger logger = Logger.getLogger(SchemaValidateProcessor.class);
  /**
   * Classpath reference for the schema to be used.
   */
  private String schemaClasspath;
  /**
   * Stream for loading schema data
   */
  private InputStream schemaStream;
  /**
   * ClassLoader for resources. A client may sometimes need to provide their own.
   */
  private ClassLoader classLoader;

  /**
   * {@inheritDoc}
   */
  @Override
  public void initialise() throws Exception {
    schemaStream = getClassLoader().getResourceAsStream(getSchemaClasspath());
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public ProcessorReturn process(Map<String, Object> processParams, FileData inputData,
  Map<String, FileData> inputFiles) throws Exception {
    // Run the validator to ensure the ouput XML is valid.
    SchemaFactory schemaFactory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
    Schema schema = schemaFactory.newSchema(new StreamSource(schemaStream));
    // Create a Validator instance, which can be used to validate a document.
    Validator validator = schema.newValidator();
    // Load the new XML into a DOM and validate it against the schema Validator.
    DocumentBuilderFactory domFactory = DocumentBuilderFactory.newInstance();
    domFactory.setNamespaceAware(true);
    DocumentBuilder domBuilder = domFactory.newDocumentBuilder();
    Document fileDom = domBuilder.parse(new ByteArrayInputStream(inputData.getData()));
    if (logger.isDebugEnabled()) {
      logger.debug("Created XML validation classes= " + schema.getClass() + ", validator=" + validator.getClass()
      + ", Document= " + fileDom.getClass());
    }
    try {
      validator.validate(new DOMSource(fileDom));
      return new ProcessorReturn(inputData, Processor.ExitStatus.SUCCESS);
    } catch (Exception e) {
      logger.error("Validation failed.", e);
      StringWriter errorString = new StringWriter();
      PrintWriter errorWriter = new PrintWriter(errorString);
      e.printStackTrace(errorWriter);
      FileData errorData = new DefaultFileData();
      errorData.setName(getClass().getSimpleName());
      // Can't have a cyclic dependency on FileMgmt, so have to hardcode these.
      errorData.setMimeType("text/plain"); // aka IFileMgmtConfig.MimeTypes.PLAIN_TEXT
      errorData.setFileType(1); // aka IFileMgmtConfig.FileType.PLAIN_FILE
      errorData.setData(errorString.getBuffer().toString().getBytes());
      return new ProcessorReturn(errorData, Processor.ExitStatus.FAIL_AND_STOP);
    }
  }

  /**
   * Classpath reference for the schema to be used.
   *
   * @param schemaClasspath the schemaClasspath to set
   */
  public void setSchemaClasspath(String schemaClasspath) {
    this.schemaClasspath = schemaClasspath;
  }

  /**
   * Classpath reference for the schema to be used.
   *
   * @return the schemaClasspath
   */
  public String getSchemaClasspath() {
    return schemaClasspath;
  }

  /**
   * ClassLoader for resources. A client may sometimes need to provide their own.
   *
   * @return the classLoader
   */
  public ClassLoader getClassLoader() {
    return classLoader == null ? Thread.currentThread().getContextClassLoader() : classLoader;
  }

  /**
   * ClassLoader for resources. A client may sometimes need to provide their own.
   *
   * @param classLoader the classLoader to set
   */
  public void setClassLoader(ClassLoader classLoader) {
    this.classLoader = classLoader;
  }
}
