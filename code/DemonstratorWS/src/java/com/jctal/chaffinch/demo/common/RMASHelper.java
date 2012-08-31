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
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import org.apache.log4j.Logger;

/**
 * Class for helping with RMAS demonstrator directory structure.
 *
 * @author Peter Newman
 */
public class RMASHelper {

  /**
   * Logger instance for this class.
   */
  private static Logger logger = Logger.getLogger(RMASHelper.class);
  /**
   * Project dir to use.
   */
  private String projectDir;
  /**
   * PDI dir to use.
   */
  private String pdiDir;
  /**
   * Detected OS.
   */
  private OS os;

  /**
   * Supported OS.
   */
  private enum OS {

    /**
     * Linux of any flavour.
     */
    LINUX,
    /**
     * MS Windows.
     */
    WINDOWS;
  }

  public RMASHelper() {
    detectOS();
    initVars();
  }

  /**
   * Initialise internal variables.
   */
  private void initVars() throws RuntimeException {
    // Load properties from CP. Set projectDir and pdiDir from the properties, or fall back to defaults.
    try {
      InputStream propsStream = Thread.currentThread().getContextClassLoader().getResourceAsStream("rmas.properties");
      Properties props = new Properties();
      props.load(propsStream);
      projectDir = props.getProperty("projectDir", getDefaultProjectDirPath());
      pdiDir = props.getProperty("pdiDir", getDefaultPDIPath());
      logger.debug(String.format("Configured with projectDir=%s, pdiDir=%s", projectDir, pdiDir));
    } catch (Exception e) {
      throw new RuntimeException("Unable to configure RMAS properties from file", e);
    }
  }

  /**
   * Detect the OS being used.
   */
  private void detectOS() {
    String osName = System.getProperty("os.name").toLowerCase();
    if (osName.contains("windows")) {
      os = OS.WINDOWS;
    } else if (osName.contains("linux")) {
      os = OS.LINUX;
    } else {
      logger.warn(String.format("Don't know what sort of OS '%s' is, going with Linux", osName));
      os = OS.LINUX;
    }
    logger.debug(String.format("Detected OS %s (%s)", os, osName));
  }

  /**
   * Get the expected path to the root of the RMAS demonstrator's PDI installation.
   */
  public String getDefaultPDIPath() {
    return getDefaultProjectDirPath() + File.separatorChar + "kettle";
  }

  /**
   * Get the expected path to the root of the RMAS demonstrator.
   */
  public String getDefaultProjectDirPath() {
    return getHomeDirPath() + File.separatorChar + "RMAS";
  }

  /**
   * Get the user's home dir path. This accounts for their home dir being different from their user dir on Windows.
   *
   * @return Home dir path.
   */
  public String getHomeDirPath() {
    String userHome = System.getProperty("user.home");
    if (os == OS.WINDOWS) {
      // Users home dir may differ from their user dir. We have seen this on deskwin1.
      String homeDrive = System.getenv("HOMEDRIVE");
      String homePath = System.getenv("HOMEPATH");
      String winHome = homeDrive + homePath;
      logger.debug(String.format("user.home is %s, homedrive/homepath is %s", userHome, winHome));
      if (userHome.equals(winHome)) {
        logger.debug("user.home and homedrive/homepath are the same: " + userHome);
      } else {
        logger.info("user.home and homedrive/homepath differ, maybe you should use: " + winHome);
      }
    }
    return userHome;
  }

  /**
   * Get the path to the PDI installation.
   */
  public String getPDIPath() {
    return pdiDir;
  }

  /**
   * Get the path to the project dir.
   */
  public String getProjectDirPath() {
    return projectDir;
  }

  /**
   * Get the parts of the command line responsible for allowing execution of the Kitchen script. This is the shell which
   * runs the relevant script - i.e. a suitable Linux shell such as /bin/sh for the .sh or the Windows cmd.exe for the
   * .bat
   *
   * @return List of cmd line parts.
   */
  public List<String> getShellCmdLine() {
    List<String> cmdLine = new ArrayList<String>();
    switch (os) {
      case WINDOWS:
        cmdLine.add("cmd.exe");
        cmdLine.add("/Q");
        cmdLine.add("/C");
        break;
      case LINUX:
      default:
        cmdLine.add("/bin/sh");
    }
    return cmdLine;
  }

  /**
   * Get the parts of the command line responsible for invoking Kitchen. This includes the job, directory, input files
   * and any transform params.
   *
   * @param inputFile Path to a file to pass as the input.file param.
   * @param repoName  PDI repository name
   * @param repoDir   Repository subdir.
   * @param jobName   Name of the job in the repo.
   */
  public List<String> getKitchenCmdLine(String inputFile, String repoName, String repoDir, String jobName) {
    List<String> cmdLine = new ArrayList<String>();
    switch (os) {
      case WINDOWS:
        cmdLine.add(getPDIPath() + File.separatorChar + "Kitchen.bat");
        break;
      case LINUX:
      default:
        cmdLine.add(getPDIPath() + File.separatorChar + "kitchen.sh");
    }
    cmdLine.add(getCmdLineSwitch("rep", repoName));
    cmdLine.add(getCmdLineSwitch("dir", repoDir));
    cmdLine.add(getCmdLineSwitch("job", jobName));
    cmdLine.add(getCmdLineSwitch("param:project.dir", getProjectDirPath()));
    cmdLine.add(getCmdLineSwitch("param:input.file", inputFile));
    return cmdLine;
  }

  /**
   * Generate an OS-specific command line switch for the Kitchen launcher.
   *
   * @param key   Switch key
   * @param value Switch value.
   * @return Complete switch string.
   */
  private String getCmdLineSwitch(String key, String value) {
    switch (os) {
      case WINDOWS:
        return String.format("/%s:%s", key, value);
      case LINUX:
      default:
        return String.format("-%s=%s", key, value);
    }
  }
}
