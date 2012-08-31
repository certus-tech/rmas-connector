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
package com.jctal.chaffinch.util.xml;

import com.jctal.chaffinch.util.MiscUtils;
import com.jctal.chaffinch.util.NullEntityResolver;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.URL;
import javax.xml.parsers.SAXParserFactory;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.sax.SAXSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import org.xml.sax.InputSource;
import org.xml.sax.XMLReader;

/**
 * Pretty prints XML.
 *
 * @author Johnathon Harris
 */
public class XMLPrettyPrint {

  /**
   * Location of transform on the classpath.
   */
  private static final String TRANSFORM_PATH = "com/jctal/chaffinch/util/xml/PrettyPrint.xslt";
  /**
   * Static reference to the XSLT contents. This is for performance in the majority of cases where the standard thread
   * ClassLoader is suitable to locate the transform.
   */
  private static byte[] staticTransform;
  /**
   * Class instance reference to the XSLT contents.
   */
  private byte[] transform;
  /**
   * If true then the XSLT will be cached statically, else it will be loaded once for each class instance. You'll want
   * false if you're providing your own ClassLoader.
   */
  private boolean useStaticTransform;
  /**
   * ClassLoader for resources. A client may sometimes need to provide their own.
   */
  private ClassLoader classLoader;

  /**
   * Create new XMLPrettyPrint.
   */
  public XMLPrettyPrint() {
    this(true);
  }

  /**
   * Create new XMLPrettyPrint.
   *
   * @param useStaticTransform If true then the XSLT will be cached statically, else it will be loaded once for each
   *                           class instance. You'll want false if you're providing your own ClassLoader.
   */
  public XMLPrettyPrint(boolean useStaticTransform) {
    this.useStaticTransform = useStaticTransform;
  }

  /**
   * Get the pretty print transform contents.
   *
   * @return The pretty print transform contents.
   * @throws IOException If the transform could not be loaded.
   */
  private byte[] getTransform() throws IOException {
    if (useStaticTransform) {
      synchronized (XMLPrettyPrint.class) {
        URL resource = getClassLoader().getResource(TRANSFORM_PATH);
        staticTransform = MiscUtils.loadByteArray(resource);
      }
      return staticTransform;
    } else {
      URL resource = getClassLoader().getResource(TRANSFORM_PATH);
      transform = MiscUtils.loadByteArray(resource);
      return transform;
    }
  }

  /**
   * Pretty print XML.
   *
   * @param sourceXML The source XML to pretty print.
   * @return A pretty version of the source.
   * @throws Exception From the XML transformer.
   */
  public String prettyPrintXML(String sourceXML) throws Exception {
    return new String(prettyPrintXML(sourceXML.getBytes()));
  }

  /**
   * Pretty print XML.
   *
   * @param sourceXML The source XML to pretty print.
   * @return A pretty version of the source.
   * @throws Exception From the XML transformer.
   */
  public byte[] prettyPrintXML(byte[] sourceXML) throws Exception {
    TransformerFactory tfactory = javax.xml.transform.TransformerFactory.newInstance();
    Source xsdSource = new StreamSource(new ByteArrayInputStream(getTransform()));
    Transformer serializer = tfactory.newTransformer(xsdSource);
    serializer.setOutputProperty(javax.xml.transform.OutputKeys.INDENT, "yes");
    serializer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "2");

    SAXParserFactory saxParserFactory = SAXParserFactory.newInstance();
    saxParserFactory.setNamespaceAware(true);
    XMLReader xmlReader = saxParserFactory.newSAXParser().getXMLReader();
    xmlReader.setEntityResolver(new NullEntityResolver());
    SAXSource source = new SAXSource(xmlReader, new InputSource(new ByteArrayInputStream(sourceXML)));

    ByteArrayOutputStream resStream = new ByteArrayOutputStream();
    serializer.transform(source, new StreamResult(resStream));
    return resStream.toByteArray();
  }

  /**
   * ClassLoader for resources. A client may sometimes need to provide their own.
   *
   * @return the classLoader
   */
  public final ClassLoader getClassLoader() {
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
