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
package com.jctal.chaffinch.xquf;

import java.util.Map;

/**
 * Loads an XML document, allow modification with XQUF, make modified document available.
 *
 * @author Johnathon Harris
 */
public interface XQUFEngine {

  /**
   * Call prior to any other method on the engine.
   *
   * @throws Exception If the engine cannot be completely initialised.
   */
  void initialise() throws Exception;

  /**
   * Call when the engine is no longer required.
   *
   * @throws Exception If the engine cannot be completely destroyed.
   */
  void destroy() throws Exception;

  /**
   * Set the XML document which is to be modified with XQUF expressions.
   *
   * @param documentStr Serialised XML.
   * @throws Exception If an error occurs parsing the XML document.
   */
  void setDocument(String documentStr) throws Exception;

  /**
   * Execute an XQUF expression on the document.
   *
   * @param xqufStr   The XQuery Update Facility expression to execute.
   * @param variables Optional variables used by the expression.
   * @throws Exception If an error occurs during XQUF parsing or evaluation.
   */
  void executeExpression(String xqufStr, Map<String, Object> variables) throws Exception;

  /**
   * Get a serialisation of the document modified by any XQUF expressions which have been executed.
   *
   * @return Serialised XML document.
   * @throws Exception If the document cannot be serialised.
   */
  byte[] getDocument() throws Exception;
}
