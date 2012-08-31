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

import java.io.File;
import org.apache.log4j.Logger;

/**
 * Provides a File for storage during initialise and removes during destroy. For use by any implementation that commonly
 * requires a temporary directory for operations.
 *
 * @author Johnathon Harris
 */
public abstract class AbstractFileXQUFEngine implements XQUFEngine {

  /**
   * Logger instance for this class.
   */
  private static Logger logger = Logger.getLogger(AbstractFileXQUFEngine.class);
  /**
   * Temporary file location for any storage required by the implementation.
   */
  protected File storageDir;

  /**
   * Create a new temporary storage directory.
   *
   * @throws Exception If the engine cannot be completely initialised.
   */
  @Override
  public void initialise() throws Exception {
    // Initialise library container.
    storageDir = File.createTempFile("FileXQUFEngine", "xquf_repo");
    storageDir.delete(); // Can now be recreated as a directory.
  }

  /**
   * Remove the storage directory.
   *
   * @throws Exception If the engine cannot be completely destroyed.
   */
  @Override
  public void destroy() throws Exception {
    delete(storageDir);
  }

  /**
   * Recursively delete the supplied file.
   *
   * @param f File to delete.
   */
  private void delete(File f) {
    if (f.isDirectory()) {
      for (File c : f.listFiles()) {
        delete(c);
      }
    }
    if (!f.delete()) {
      logger.debug("Failed to delete file: " + f);
    }
  }
}
