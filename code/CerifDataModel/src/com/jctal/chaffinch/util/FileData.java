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
package com.jctal.chaffinch.util;

import java.sql.Timestamp;

/**
 * <p>Classes which want to represent some file data should implement this class. This class should be considered as a
 * container for the contents of a file and includes some extra meta data about the file. The file does not have to be
 * on a file system or even persisted.</p>
 *
 * @author Gareth Smith
 */
public interface FileData {

  /**
   * Constant for a plain file.
   */
  int PLAIN_FILE = 1;
  /**
   * Constant for a directory.
   */
  int DIRECTORY = 2;
  /**
   * Constant for a link.
   */
  int LINK = 3;

  /**
   * Get the name of the file.
   *
   * @throws Exception If the implementation could not return the name attribute.
   * @return The name of the file data.
   */
  String getName() throws Exception;

  /**
   * Sets the name for the file which this object represents.
   *
   * @param name The name of the file.
   * @throws Exception If the implementation could not set the name.
   */
  void setName(String name) throws Exception;

  /**
   * Get the file type.
   *
   * @throws Exception If the implementation could not return the fileType attribute.
   * @return The type of file. This should be one of <code>FileData.FileType</code>.
   */
  int getFileType() throws Exception;

  /**
   * Sets the type of file.
   *
   * @param fileType The type of the file. Should be one of <code>FileData.FileType</code>.
   * @throws Exception If the implementation could not set the fileType.
   */
  void setFileType(int fileType) throws Exception;

  /**
   * Get the size of the file data.
   *
   * @throws Exception If the implementation could not return the fileSize attribute.
   * @return The size of the file data.
   */
  long getFileSize() throws Exception;

  /**
   * Sets the size of file.
   *
   * @param fileSize The size of the file.
   * @throws Exception If the implementation could not set the fileSize.
   */
  void setFileSize(long fileSize) throws Exception;

  /**
   * Get the MIME type of the file data.
   *
   * @throws Exception If the implementation could not return the mimeType attribute.
   * @return The MIME type of the file data.
   */
  String getMimeType() throws Exception;

  /**
   * Sets the MIME type for the file which this object represents.
   *
   * @param mimeType The MIME type of the file.
   * @throws Exception If the implementation could not set the mimeType.
   */
  void setMimeType(String mimeType) throws Exception;

  /**
   * Get the file data.
   *
   * @throws Exception If the implementation could not return the data attribute.
   * @return The <code>byte[]</code> representing the file data.
   */
  byte[] getData() throws Exception;

  /**
   * Sets the file data.
   *
   * @param data The data of the file.
   * @throws Exception If the implementation could not set the data.
   */
  void setData(byte[] data) throws Exception;

  /**
   * Get the file data as a string.
   *
   * @throws Exception If the implementation could not return the data attribute.
   * @return The <code>String</code> representing the file data.
   */
  String getDataAsString() throws Exception;

  /**
   * Sets the file data as a string. Using this method will implicitly make the filedata binary flag false.
   *
   * @param data The data of the file.
   * @throws Exception If the implementation could not set the data.
   */
  void setDataAsString(String data) throws Exception;

  /**
   * Get the last changed date.
   *
   * @throws Exception If the implementation could not return the last changed attribute.
   * @return The <code>Timestamp</code> representing the last changed date.
   */
  Timestamp getLastChanged() throws Exception;

  /**
   * Sets the last changed date.
   *
   * @param lastChanged The last changed date of the file.
   * @throws Exception If the implementation could not set the last changed date.
   */
  void setLastChanged(Timestamp lastChanged) throws Exception;

  /**
   * Get the time created.
   *
   * @throws Exception If the implementation could not return the CreatedTime attribute.
   * @return The <code>Timestamp</code> representing the created time.
   */
  Timestamp getCreated() throws Exception;

  /**
   * Sets the time created.
   *
   * @param created The time the file was created.
   * @throws Exception If the implementation could not set the last created date.
   */
  void setCreated(Timestamp created) throws Exception;

  /**
   * Set the binary status of the file.
   *
   * @throws Exception If the implementation could not return the binary attribute.
   * @return The binary status of the file.
   */
  boolean getBinary() throws Exception;

  /**
   * Sets the binary status of the file.
   *
   * @param binary The binary status of the file.
   * @throws Exception If the implementation could not set the binary status.
   */
  void setBinary(boolean binary) throws Exception;

  /**
   * Get the creator of the file data.
   *
   * @throws Exception If the implementation could not return the creator attribute.
   * @return The creator type of the file data.
   */
  String getCreator() throws Exception;

  /**
   * Sets the creator for the file which this object represents.
   *
   * @param creator The creator of the file.
   * @throws Exception If the implementation could not set the creator.
   */
  void setCreator(String creator) throws Exception;

  /**
   * Get the modifier of the file data.
   *
   * @throws Exception If the implementation could not return the modifier attribute.
   * @return The modifier type of the file data.
   */
  String getModifier() throws Exception;

  /**
   * Sets the modifier for the file which this object represents.
   *
   * @param modifier The creator of the file.
   * @throws Exception If the implementation could not set the modifier.
   */
  void setModifier(String modifier) throws Exception;
}
