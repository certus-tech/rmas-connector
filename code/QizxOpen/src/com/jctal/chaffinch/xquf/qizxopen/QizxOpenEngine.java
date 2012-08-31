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
package com.jctal.chaffinch.xquf.qizxopen;

import com.jctal.chaffinch.xquf.AbstractFileXQUFEngine;
import com.qizx.api.CompilationException;
import com.qizx.api.Expression;
import com.qizx.api.ItemSequence;
import com.qizx.api.Message;
import com.qizx.api.XQuerySession;
import com.qizx.api.XQuerySessionManager;
import com.qizx.api.util.PushStreamToDOM;
import com.qizx.xdm.FONIDataModel;
import com.qizx.xdm.FONIDocument;
import java.io.ByteArrayInputStream;
import java.io.StringWriter;
import java.util.Map;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import org.apache.log4j.Logger;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.xml.sax.InputSource;

/**
 * Uses the Qizx/open library to execute XQUF expressions.
 *
 * @author Johnathon Harris
 */
public class QizxOpenEngine extends AbstractFileXQUFEngine {

  /**
   * Logger instance for this class.
   */
  private static Logger logger = Logger.getLogger(QizxOpenEngine.class);
  /**
   * URI used to locate the document being modified by the engine.
   */
  private static final String DOCUMENT_URI = "file:/QizxOpenEngine_document";
  /**
   * Implicit collection query for XQUF expressions to modify the document.
   */
  private static final String IMPLICIT_COLLECTION_EXPR = "fn:collection('" + DOCUMENT_URI + "')";
  /**
   * Manages XQUF session.
   */
  private XQuerySessionManager sessionManager;
  /**
   * The XQUF session being used by this engine.
   */
  private XQuerySession session;
  /**
   * Updates the document pool when XQUF produces modified documents.
   */
  private DocumentPoolUpdater docPoolUpdater;

  /**
   * {@inheritDoc}
   */
  @Override
  public void initialise() throws Exception {
    super.initialise();
    sessionManager = new XQuerySessionManager(storageDir.toURI().toURL());
    session = sessionManager.createSession();
    docPoolUpdater = new DocumentPoolUpdater(sessionManager.getDocumentCache());
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void destroy() throws Exception {
    super.destroy();
    // No specific tear down for Qizx/open to do, just relinquish vars to allow for GC.
    sessionManager = null;
    session = null;
    docPoolUpdater = null;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void setDocument(String documentStr) throws Exception {
    FONIDocument document =
    sessionManager.getDocumentCache().parseDocument(new InputSource(new ByteArrayInputStream(documentStr.getBytes())));
    document.setBaseURI(DOCUMENT_URI);
    docPoolUpdater.cacheDocument(new FONIDataModel(document));
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void executeExpression(String xqufStr, Map<String, Object> variables) throws Exception {
    Expression expr;
    try {
      expr = session.compileExpression(xqufStr);
    } catch (CompilationException ce) {
      logger.error("Expression compilation failed: " + xqufStr);
      for (Message msg : ce.getMessages()) {
        logger.error("Compilation error: " + msg);
      }
      throw ce;
    }

    if (variables != null) {
      for (Map.Entry<String, Object> variableEntry : variables.entrySet()) {
        expr.bindVariable(session.getQName(variableEntry.getKey()), variableEntry.getValue(), null);
      }
    }

    // In order to get the XQUF to update the single document we implicitly look for the known document URI.
    Expression implicitExpr = session.compileExpression(IMPLICIT_COLLECTION_EXPR);
    ItemSequence implicitSeq = implicitExpr.evaluate();
    expr.bindImplicitCollection(implicitSeq);

    docPoolUpdater.interceptExpression(expr);

    expr.evaluate();
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public byte[] getDocument() throws Exception {
    FONIDataModel dataModel = sessionManager.getDocumentCache().findDocument(DOCUMENT_URI);
    return printDataModel(dataModel).getBytes();
  }

  /**
   * Get serialisation of an XQuery data model.
   *
   * @param dataModel The XDM to get a serialisation of.
   * @return XML serialisation.
   * @throws Exception If the data model could not be serialised.
   */
  private String printDataModel(FONIDataModel dataModel) throws Exception {
    // Convert to W3C Node.
    Node node = new PushStreamToDOM().exportNode(dataModel.getDocumentNode());
    if (node instanceof Document) {
      ((Document)node).setXmlStandalone(true);
    }

    // Serialise DOM.
    TransformerFactory tf = TransformerFactory.newInstance();
    Transformer serializer = tf.newTransformer();
    StringWriter sw = new StringWriter();
    serializer.transform(new DOMSource(node), new StreamResult(sw));
    return sw.getBuffer().toString();
  }
}
