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
import java.util.Map;

/**
 * Classes should implement this interface if they want to perform intermediate processing that do not depend on DPA.
 *
 * @author Richard Campbell
 * @author Johnathon Harris
 */
public interface Processor {

  /**
   * Enumerated constants for the exit status of a process.
   */
  enum ExitStatus {

    /**
     * The processor has not been executed yet, the exit status cannot be discerned.
     */
    NOT_EXECUTED,
    /**
     * The processor failed and hence the entire process should stop.
     */
    FAIL_AND_STOP,
    /**
     * The processor failed, but its output should be immediately piped to the reporting stage. This allows user
     * friendly reports of the failure to be generated.
     */
    FAIL_AND_REPORT,
    /**
     * The processor completed successfully. The next stage is either another processor, or the reporting stage.
     */
    SUCCESS
  }

  /**
   * Use this interface method to pass in any arguments for this processor. The Processor may access the module
   * configuration in order to get access to any configuration files it may require.
   *
   * @throws Exception If the Processor is unable to continue with the initialisation details supplied.
   */
  void initialise() throws Exception;

  /**
   * Perform processing as specified by the implementation.
   *
   * @param processParams {@link Map} of DPA process arguments. These are passed through the entire DPA process.
   * @param inputData     The primary data passed to this processor. At the start of the DPA process this is the data
   *                      set on the primary InputHandler. Subsequent processors receive the output from the previous
   *                      processor.
   * @param inputFiles    A Map of <CODE>FileData</CODE> objects. The map is keyed on the name of the InputHandlerDesc
   *                      entity, the data in the map value is the output from each input handler.
   * @throws Exception If the process stage cannot complete.
   * @return Data to be processed for the report stage.
   */
  ProcessorReturn process(Map<String, Object> processParams, FileData inputData, Map<String, FileData> inputFiles)
  throws Exception;
}
