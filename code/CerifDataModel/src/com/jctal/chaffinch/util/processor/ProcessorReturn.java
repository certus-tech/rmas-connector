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
package com.jctal.chaffinch.util.processor;

import com.jctal.chaffinch.util.FileData;

/**
 * Holds and provides information on what the Processor returns.
 *
 * @author Richard Campbell
 */
public class ProcessorReturn {

  /**
   * The FileData that is the output from the Processor.
   */
  private final FileData outputFile;
  /**
   * The ExitStatus that is output form the Proecssor.
   */
  private final Processor.ExitStatus exitStatus;

  /**
   * Creates new ProcessorReturn.
   *
   * @param outputFile The outputFile from the Processor
   * @param exitStatus The exitStatus from the Processor
   */
  public ProcessorReturn(FileData outputFile, Processor.ExitStatus exitStatus) {
    this.outputFile = outputFile;
    this.exitStatus = exitStatus;
  }

  /**
   * Getter for the output FileData.
   *
   * @return The outputFile data from the Processor.
   */
  public FileData getOutputFile() {
    return outputFile;
  }

  /**
   * Getter for the output exitStatus.
   *
   * @return The output exitStatus from the Processor.
   */
  public Processor.ExitStatus getExitStatus() {
    return exitStatus;
  }
}
