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
package com.jctal.chaffinch.demo.trigger;

import com.jctal.chaffinch.demo.common.RMASHelper;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import org.apache.log4j.Logger;

/**
 * Trivial "REST" webservice. It accepts data via POST and will run Kettle on the data.
 *
 * @author Peter Newman
 * @author Adam Cooney
 */
@Path("trigger")
public class TriggerServiceResource {

  /**
   * Logger instance for this class.
   */
  private static Logger logger = Logger.getLogger(TriggerServiceResource.class);
  /**
   * Name of the Kettle repository (must be configured for this user in ~/.kettle)
   */
  private static final String DEFAULT_REPO_NAME = "RMASConnector";
  /**
   * Dir in the repo which everything is in.
   */
  private static final String DIR = "/WorkedExample4/";
  /**
   * Where to find the job to run, without extension.
   */
  private static final String JOB = "WorkedExample4";
  /**
   * Repository name to use.
   */
  private String repoName;
  /**
   * RMAS helper instance.
   */
  private RMASHelper rmasHelper;

  /**
   * Creates a new instance of TriggerServiceResource
   */
  public TriggerServiceResource() {
    super();
    initVars();
    rmasHelper = new RMASHelper();
  }

  /**
   * Initialise the service configuration. Override the default values from the constants with values from a properties
   * file, if specified.
   */
  private final void initVars() {
    try {
      // Load properties from CP. Set projectDir, repoName, kettleHome from the properties, or fall back to defaults.
      InputStream propsStream = Thread.currentThread().getContextClassLoader().getResourceAsStream(
      "trigger.properties");
      Properties props = new Properties();
      props.load(propsStream);
      repoName = props.getProperty("repoName", DEFAULT_REPO_NAME);
      logger.debug(String.format("Configured with repoName=%s", repoName));
    } catch (Exception e) {
      throw new RuntimeException("Unable to configure service from properties file", e);
    }
  }

  /**
   * Receive posted data and fire off the Kettle processes with the posted data.
   *
   * @param content posted data.
   * @return an HTTP response.
   */
  @POST
  public String postXml(String content) {
    // Don't bother forking the process if nothing was posted.
    if (content == null || content.isEmpty()) {
      return "No data was posted";
    }
    logger.debug("Received " + content.length() + " chars");
    String inputFile = writeContentToTmp(content);
    ProcessBuilder procBuilder = buildKettleProcess(inputFile);
    Process proc = null;
    try {
      // Start the process, capture the output.
      proc = procBuilder.start();
      InputStream stderr = proc.getErrorStream();
      InputStream stdout = proc.getInputStream();

      // Wait for the process to exit.
      int retCode = proc.waitFor();

      // Dump the contents of the stdout and sterr streams.
      StringBuilder errs = new StringBuilder();
      byte[] buf = new byte[4096];
      while (stderr.read(buf) != -1) {
        errs.append(new String(buf));
      }
      StringBuilder out = new StringBuilder();
      while (stdout.read(buf) != -1) {
        errs.append(new String(buf));
      }
      proc.destroy();
      // For convenience just return everything we know.
      return String.format("Processing completed. Exit code: %d.\n\nStandard Output:\n%s\n\nStandard Error:\n%s",
      retCode, out, errs);
    } catch (Exception ex) {
      throw new RuntimeException(ex);
    } finally {
      if (proc != null) {
        proc.destroy();
      }
    }
  }

  /**
   * Write the content to a temp file.
   *
   * @param content The content to write to the file.
   * @return The path to the file.
   */
  private String writeContentToTmp(String content) {
    String tempFilePath;
    try {
      File temp = File.createTempFile("TriggerService", null);
      temp.deleteOnExit();
      BufferedWriter out = new BufferedWriter(new FileWriter(temp));
      out.write(content);
      out.close();
      tempFilePath = temp.getAbsolutePath();
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
    logger.debug("Wrote temp file " + tempFilePath);
    return tempFilePath;
  }

  /**
   * Build a ProcessBuilder from which Kettle processes can be started.
   *
   * @param inputFile Path to the file which forms the <var>input.file</var> param to the transform.
   * @return ProcessBuilder initialised and ready to have the process created with its <code>start()</code> method.
   */
  private ProcessBuilder buildKettleProcess(String inputFile) {
    // Build command line.
    List<String> cmdLine = new ArrayList<String>();
    cmdLine.addAll(rmasHelper.getShellCmdLine());
    cmdLine.addAll(rmasHelper.getKitchenCmdLine(inputFile, repoName, DIR, JOB));
    logger.debug("Final command line is: " + cmdLine);
    // Create process builder.
    ProcessBuilder processBuilder = new ProcessBuilder();
    File directory = new File(rmasHelper.getPDIPath());
    logger.debug("Setting command directory to " + directory);
    processBuilder.directory(directory);
    processBuilder.command(cmdLine.toArray(new String[0]));
    return processBuilder;
  }
}
