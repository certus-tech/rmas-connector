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
package com.jctal.chaffinch.demo.common;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.text.Collator;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Properties;
import org.apache.log4j.Logger;

/**
 * Helper containing common code for the "CERIF Repository" functionality provided by CERIFResource (RESTful) and
 * CERIFService (SOAP).
 *
 * @author Peter Newman
 */
public class ServiceCommon {

  /**
   * Logger instance for this class.
   */
  private static Logger logger = Logger.getLogger(ServiceCommon.class);
  /**
   * Data dir to store files in.
   */
  private File dataDir;
  /**
   * RMAS helper.
   */
  private RMASHelper rmasHelper;

  /**
   * Create new instance of ServiceCommon.
   */
  public ServiceCommon() {
    rmasHelper = new RMASHelper();
    initVars();
  }

  /**
   * Initialise internal vars.
   */
  private final void initVars() {
    // Determine where data dir will be and initialise it.
    String dataDirPath;
    try {
      // Load properties from CP. Set dataDir from the properties, or fall back to defaults.
      InputStream propsStream = Thread.currentThread().getContextClassLoader().getResourceAsStream(
      "cerifservice.properties");
      Properties props = new Properties();
      props.load(propsStream);
      dataDirPath = props.getProperty("dataDir", rmasHelper.getProjectDirPath() + File.separator + "exampledata"
      + File.separator + "webservice");
      logger.debug(String.format("Configured with dataDirPath=%s", dataDirPath));
    } catch (Exception e) {
      throw new RuntimeException("Unable to configure from properties file", e);
    }
    File dir = null;
    try {
      dir = new File(dataDirPath);
      if (!dir.exists() && !dir.mkdirs()) {
        throw new IOException("Could not create data directory: " + dir.getAbsolutePath());
      }
      logger.debug("Got data dir: " + dir);
    } catch (IOException ex) {
      logger.error("Unable to create data dir", ex);
    } finally {
      dataDir = dir;
    }
  }

  /**
   * Get the data directory path.
   *
   * @return Full data directory path.
   * @throws IOException If the path cannot be determined.
   */
  public String getDataDir() throws IOException {
    return dataDir.getCanonicalPath();
  }

  /**
   * List the files in the "repository".
   *
   * @return Array of file names.
   */
  public String[] listFiles() {
    String[] files = dataDir.list();
    Arrays.sort(files, Collator.getInstance());
    return files;
  }

  /**
   * Get the contents of an entry.
   *
   * @param name Name of the entry.
   * @return Content of the file as a String.
   */
  public String getEntryContents(String name) {
    File f = new File(dataDir, name);
    if (!f.exists()) {
      return "There is no file " + name;
    }
    return getFileContent(f);
  }

  /**
   * Create a new entry with some content.
   *
   * @param content Entry content
   * @return Name of the entry that is created.
   */
  public String createNewEntry(String content) {
    String name = new SimpleDateFormat("yyy-MM-dd_HH-mm-ss_SSS").format(Calendar.getInstance().getTime());
    writeFileContent(name, content);
    return name;
  }

  /////////////
  // HELPERS //
  /////////////
  /**
   * Write a file's contents
   *
   * @param name    Name of the file in the data dir. It is assumed not to exist and is created.
   * @param content Content to write.
   */
  private void writeFileContent(String name, String content) {
    File f = new File(dataDir, name);
    logger.debug("Attempting to write " + content.length() + " chars to " + f);
    try {
      f.createNewFile();
      FileWriter writer = new FileWriter(f);
      writer.write(content);
      writer.close();
      logger.debug("Succeeded.");
    } catch (IOException ex) {
      logger.error("Unable to populate file " + f, ex);
    }
  }

  /**
   * Get the contents of a file.
   *
   * @param f File to read, assumed to exist.
   * @return Content of the file as a String.
   */
  private String getFileContent(File f) {
    logger.debug("Getting content of " + f);
    StringBuilder sb = new StringBuilder();
    try {
      FileReader r = new FileReader(f);
      char[] buf = new char[4096];
      int read;
      while ((read = r.read(buf)) != -1) {
        sb.append(buf, 0, read);
      }
    } catch (IOException ioe) {
      logger.error("Error reading data file " + f, ioe);
    }
    logger.debug("Read " + sb.length() + " chars");
    return sb.toString();
  }
}
