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

import com.qizx.api.DataModelException;
import com.qizx.api.Expression;
import com.qizx.api.util.PushStreamToSAX;
import com.qizx.xdm.DocumentPool;
import com.qizx.xdm.FONIDataModel;
import com.qizx.xdm.FONIDocument;
import com.qizx.xdm.IDocument;
import com.qizx.xdm.XMLPushStreamBase;
import com.qizx.xquery.DynamicContext;
import com.qizx.xquery.ExpressionImpl;
import com.qizx.xquery.UpdaterFactory;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import org.apache.log4j.Logger;

/**
 * Updates the DocumentPool after any expression updates a document.
 *
 * @author Johnathon Harris
 */
public class DocumentPoolUpdater implements UpdaterFactory {

  /**
   * Logger instance for this class.
   */
  private static Logger logger = Logger.getLogger(DocumentPoolUpdater.class);
  /**
   * The DocumentPool to be updated.
   */
  private final DocumentPool docPool;
  /**
   * Documents in the pool. Hold a reference in this class as we use reflection to get a handle on this from the
   * DocumentPool.
   */
  private final Collection<FONIDataModel> docPoolCache;
  /**
   * Context for an expression being watched. This allows us to detect when documents are modified.
   */
  private DynamicContext dynamicContext;
  /**
   * Documents modified during an expression evaluation.
   */
  private final Collection<FONIDocument> modifiedDocs = new ArrayList<FONIDocument>();

  /**
   * Create new DocumentPoolUpdater.
   *
   * @param docPool The DocumentPool to be updated.
   * @throws Exception If the required variables cannot be accessed on the The DocumentPool to be updated..
   */
  public DocumentPoolUpdater(DocumentPool docPool) throws Exception {
    this.docPool = docPool;
    // Will need access the the document pool to update it.
    Field docPoolCacheField = DocumentPool.class.getDeclaredField("cache");
    docPoolCacheField.setAccessible(true);
    docPoolCache = (Collection)docPoolCacheField.get(docPool);
  }

  /**
   * Call this prior to evaluating an expression so we can detect when documents are modified.
   *
   * @param expr Expression to be watched.
   */
  public void interceptExpression(Expression expr) {
    // Intercept the documents requested when Updates generates the updated documents.
    ExpressionImpl exprImpl = (ExpressionImpl)expr;
    if (logger.isDebugEnabled()) {
      logger.debug("Intercepting expression: " + exprImpl);
    }
    dynamicContext = exprImpl.getDynCtx();
    exprImpl.setUpdaterFactory(this);
    modifiedDocs.clear();
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public XMLPushStreamBase newLibraryDocument(String uri) throws DataModelException {
    return dynamicContext.newLibraryDocument(uri);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public XMLPushStreamBase newParsedDocument(String uri) throws DataModelException {
    IDocument newDoc = new IDocument();
    newDoc.setBaseURI(uri);
    modifiedDocs.add(newDoc);
    return new PushStreamToSAX(newDoc);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void endParsedDocument() throws DataModelException {
    dynamicContext.endParsedDocument();
    for (FONIDocument newDoc : modifiedDocs) {
      // Replace the updated document into the DocumentPool.
      // This is something which 'Updates' has a TODO note for but does not do.
      replaceDocument(new FONIDataModel(newDoc));
    }
  }

  /**
   * Replaces a document in the pool with the supplied model, according to the base URI of the underlying document.
   *
   * @param newDataModel Data to update in the pool.
   * @throws DataModelException If the base URI of a Document cannot be read.
   */
  private void replaceDocument(FONIDataModel newDataModel) throws DataModelException {
    // First remove the old one.
    for (Iterator<FONIDataModel> it = docPoolCache.iterator(); it.hasNext();) {
      FONIDataModel dataModel = it.next();
      if (dataModel.getDom().getBaseURI().equals(newDataModel.getDom().getBaseURI())) {
        it.remove();
      }
    }
    // And add the new document.
    docPoolCache.add(newDataModel);
  }

  /**
   * Calls cacheDocument on the DocumentPool, which isn't ordinarily available.
   *
   * @param newDataModel Data to be cached.
   * @throws Exception If the cacheDocument method cannot be made available.
   */
  public void cacheDocument(FONIDataModel newDataModel) throws Exception {
    Method cacheDocument = DocumentPool.class.getDeclaredMethod("cacheDocument", FONIDataModel.class, Long.class);
    cacheDocument.setAccessible(true);
    cacheDocument.invoke(docPool, newDataModel, System.currentTimeMillis());
  }
}
