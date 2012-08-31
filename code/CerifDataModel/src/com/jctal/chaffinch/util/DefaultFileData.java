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

import java.io.Serializable;
import java.sql.Timestamp;

/**
 * <p>This is the default implementation of the FileData interface.</p>
 *
 * @author Gareth Smith
 * @author Johnathon Harris
 */
public class DefaultFileData implements FileData, Serializable {

  /**
   * String representation of value when attribute isn't set.
   */
  private static final String NOT_SET = "Not Set";
  /**
   * The name of the file data.
   */
  private String name;
  /**
   * The type of file. Should be one of
   * <code>FileData.FileType</code>.
   */
  private int fileType;
  /**
   * The size of the file data.
   */
  private long fileSize;
  /**
   * The MIME type of the file.
   */
  private String mimeType;
  /**
   * The file data.
   */
  private byte[] data;
  /**
   * The last changed date.
   */
  private Timestamp lastChanged;
  /**
   * The created date.
   */
  private Timestamp created;
  /**
   * The user who last modified the file.
   */
  private String modifier;
  /**
   * The user who created the file.
   */
  private String creator;
  /**
   * The file contents as a string for non-binary files.
   */
  private String dataAsString;
  /**
   * Flag to mark the file as boolean.
   */
  private boolean binary;

  /**
   * Creates new DefaultFileData.
   */
  public DefaultFileData() {
    this.fileType = FileData.PLAIN_FILE;
    this.fileSize = 0;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public String getName() {
    return name;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void setName(String name) {
    this.name = name;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public int getFileType() {
    return fileType;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void setFileType(int fileType) {
    this.fileType = fileType;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public long getFileSize() {
    return fileSize;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void setFileSize(long fileSize) {
    this.fileSize = fileSize;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public String getMimeType() {
    return mimeType;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void setMimeType(String mimeType) {
    this.mimeType = mimeType;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public byte[] getData() {
    if (!getBinary() && dataAsString != null) {
      return dataAsString.getBytes();
    }
    return data;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void setData(byte[] data) {
    this.dataAsString = null;
    this.setBinary(true);
    this.data = data;
    this.setFileSize(data == null ? 0 : data.length);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public Timestamp getLastChanged() {
    return lastChanged;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void setLastChanged(Timestamp lastChanged) {
    this.lastChanged = lastChanged;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public Timestamp getCreated() {
    return created;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void setCreated(Timestamp created) {
    this.created = created;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public String getModifier() {
    return modifier;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void setModifier(String modifier) {
    this.modifier = modifier;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public String getCreator() {
    return creator;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void setCreator(String creator) {
    this.creator = creator;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public boolean getBinary() {
    return binary;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void setBinary(boolean binary) {
    this.binary = binary;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public String getDataAsString() {
    return dataAsString;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void setDataAsString(String dataAsString) {
    this.data = null;
    setBinary(false);
    if (dataAsString == null) {
      setFileSize(0);
    } else {
      setFileSize(dataAsString.getBytes().length);
    }
    this.dataAsString = dataAsString;
  }

  /**
   * Override the tostring method.
   *
   * @return A string view of the file (not including the contents).
   */
  @Override
  public String toString() {
    java.text.Format f = new java.text.SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
    StringBuilder str = new StringBuilder();
    str.append("Type: ");
    switch (this.fileType) {
      case PLAIN_FILE:
        str.append("Plain File\n");
        break;
      case DIRECTORY:
        str.append("Plain File\n");
        break;
      case LINK:
        str.append("Link\n");
        break;
      default:
        str.append("Not Set\n");
        break;
    }
    str.append("Name: ").append(this.name == null ? NOT_SET : this.name).append("\n");
    str.append("MimeType: ").append(this.mimeType == null ? NOT_SET : this.mimeType).append("\n");
    str.append("FileSize: ").append(this.fileSize).append("\n");
    str.append("Binary: ").append(this.binary).append("\n");
    str.append("Created By: ").append(this.creator == null ? NOT_SET : this.creator).append("\n");
    str.append("Modified By: ").append(this.modifier == null ? NOT_SET : this.modifier).append("\n");
    str.append("Created at: ").append(this.created == null ? NOT_SET : f.format(this.created)).append("\n");
    str.append("Modified at: ").append(this.lastChanged == null ? NOT_SET : f.format(this.lastChanged)).append("\n");

    return str.toString();
  }

  /**
   * Print the file data for the file.
   *
   * @param fileData The fileData. This may be null.
   * @return The data as a String if this FileData has some.
   * @throws Exception If there's a problem accessing the FileData getters.
   */
  public static String printFileData(FileData fileData) throws Exception {
    String returnData;
    if (fileData == null) {
      returnData = "FileData is null.";
    } else {
      returnData = fileData.getName() + " data: ";
      if (fileData.getBinary()) {
        if (fileData.getData() == null) {
          returnData += "No data";
        } else {
          returnData += new String(fileData.getData());
        }
      } else {
        returnData += fileData.getDataAsString();
      }
    }
    return returnData;
  }
}
