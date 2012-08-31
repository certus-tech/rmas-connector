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
package com.jctal.chaffinch.demo.cerifresources;

import com.jctal.chaffinch.demo.common.ServiceCommon;
import java.io.IOException;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import org.apache.log4j.Logger;

/**
 * <p>Demonstration web service which consumes and persists CERIF. This resource will persist as a timestamped file any
 * content which is POSTted to
 * <code>cerif/</code>. Issuing a GET request to
 * <code>cerif/</code> will return a listing of the previously persisted files. A file 'id' (datestamp) from the list
 * may be added to a path segment at the end of the URI of a GET request to retrieve the content of that specific file,
 * i.e. with a URI like
 * <code>cerif/2012-04-01_13-13-25_221</code>.</p> <p>Files are persisted on disk, in a sub directory configured
 * internally. The listing of files returned by this resource is generated from the content of the directory. Files in
 * the directory are NOT automatically cleaned up, e.g. when the VM shuts down.</p>
 *
 * @author Peter Newman
 */
@Path("cerif")
public class CERIFResource {

  /**
   * Logger instance for this class.
   */
  private static Logger logger = Logger.getLogger(CERIFResource.class);
  /**
   * Path prefix.
   */
  private static final String PATH_PREFIX = "/webservice/resources/cerif/";
  /**
   * CERIF file handling common helper.
   */
  private ServiceCommon serviceCommon;

  /**
   * Creates a new instance of CERIFResource
   */
  public CERIFResource() {
    serviceCommon = new ServiceCommon();
  }

  /**
   * Get method for a listing of entries.
   *
   * @return List of entries with links.
   * @throws IOException Thrown from file handling operations in ServiceCommon.
   */
  @GET
  @Produces("text/html")
  public String getList() throws IOException {
    logger.debug("Listing files.");
    String[] files = serviceCommon.listFiles();
    StringBuilder sb = new StringBuilder();
    sb.append("<html><head/><body><h1>Stored Data</h1><ul>");
    sb.append("<!-- Data dir is ").append(serviceCommon.getDataDir()).append(" -->\n");
    if (files.length == 0) {
      sb.append("<li>Nothing has been posted yet</li>");
    } else {
      for (String name : files) {
        sb.append(String.format("<li><a href=\"%1$s%2$s\">%2$s</a></li>\n", PATH_PREFIX, name));
      }
    }
    sb.append("</ul></body></html>");
    final String s = sb.toString();
    logger.debug(s);
    return s;
  }

  /**
   * Get method for a specific entry, by ID.
   *
   * @param id ID (actually the name, and timestamp) of the file to get.
   * @return File contents.
   */
  @GET
  @Path("{id}")
  @Produces("text/plain")
  public String getEntry(@PathParam("id") String id) {
    logger.debug("Getting entry with ID " + id);
    return serviceCommon.getEntryContents(id);
  }

  /**
   * PUT method for updating or creating an instance of CERIFResource
   *
   * @param content representation for the resource
   * @return an HTTP response with content of the updated or created resource.
   */
  @POST
  @Produces("text/plain")
  public String postXml(String content) {
    logger.debug("Handling posted XML (" + content.length() + " chars)");
    String name = serviceCommon.createNewEntry(content);
    return "" + content.length() + " chars received, see " + PATH_PREFIX + name;
  }
}
