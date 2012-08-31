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
package com.jctal.chaffinch.demo.soap;

import com.jctal.chaffinch.demo.common.ServiceCommon;
import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebService;
import org.apache.log4j.Logger;

/** A simple service which receives CERIF data via SOAP. This service cannot be used from the Web Services Lookup step,
 * and must be posted to using the HTTP-post method used for Worked Example 4.

 * @author Peter Newman
 */
@WebService(serviceName = "CERIFService")
public class CERIFService {

  /** Logger instance for this class. */
  private static Logger logger = Logger.getLogger(CERIFService.class);
  /** CERIF file handling common helper. */
  private ServiceCommon serviceCommon;

  /** Default constructor.
   */
  public CERIFService() {
    serviceCommon = new ServiceCommon();
  }

  /** This is a sample web service operation */
  @WebMethod(operationName = "postCERIF")
  public String postCERIF(@WebParam(name = "cerifDataContents") String cerifData) {
    if (cerifData == null || cerifData.isEmpty()) {
      logger.debug("Received empty request");
      return "No data was posted";
    }
    logger.debug("Received " + cerifData.length() + " chars");
    String name = serviceCommon.createNewEntry(cerifData);
    logger.debug("Stored as " + name);
    return "Received " + cerifData.length() + " chars of data, stored as " + name;
  }
  // To list the available files use the RESTful service (CERIFResource).
}
