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
package com.jctal.chaffinch.cerif.kettle.steps.cerif;

import com.jctal.chaffinch.cerif.datamodel.CDMToCERIF;
import com.jctal.chaffinch.util.DefaultFlatDynaBean;
import com.jctal.chaffinch.util.DynaBean;
import org.pentaho.di.core.exception.KettleException;
import org.pentaho.di.core.row.RowMeta;
import org.pentaho.di.core.row.ValueMeta;
import org.pentaho.di.core.row.ValueMetaInterface;
import org.pentaho.di.trans.Trans;
import org.pentaho.di.trans.TransMeta;
import org.pentaho.di.trans.step.BaseStep;
import org.pentaho.di.trans.step.StepDataInterface;
import org.pentaho.di.trans.step.StepInterface;
import org.pentaho.di.trans.step.StepMeta;
import org.pentaho.di.trans.step.StepMetaInterface;

/**
 * The CDM Cerif step. This produces a CERIF XML serialisation of the incoming data, expected to be CDM compliant.
 * StepInterface makes the step execute.
 *
 * @author Johnathon Harris
 */
public class CDMCerif extends BaseStep implements StepInterface {

  /**
   * Creates a new CDMCerif.
   *
   * @param stepMeta          The StepMeta object to run.
   * @param stepDataInterface the data object to store temporary data, database connections, caches, result sets,
   *                          hashtables etc.
   * @param copyNr            The copynumber for this step.
   * @param transMeta         The TransInfo of which the step stepMeta is part of.
   * @param trans             The (running) transformation to obtain information shared among the steps.
   */
  public CDMCerif(StepMeta stepMeta, StepDataInterface stepDataInterface, int copyNr, TransMeta transMeta,
  Trans trans) {
    super(stepMeta, stepDataInterface, copyNr, transMeta, trans);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public boolean processRow(StepMetaInterface smi, StepDataInterface sdi) throws KettleException {
    CDMToCERIF cdmToCERIF;
    try {
      cdmToCERIF = new CDMToCERIF();
      cdmToCERIF.initialise();
    } catch (Exception e) {
      throw new KettleException("Failed to initialise CDMToCERIF.", e);
    }

    String cerifStr;
    try {
      // Read incoming row data.
      Object[] rowData = getRow();
      while (rowData != null) {
        DynaBean rowBean = convertRowToBean(rowData);
        cdmToCERIF.processData(rowBean);
        rowData = getRow();
      }
      cerifStr = cdmToCERIF.getCERIF();
    } catch (Exception e) {
      throw new KettleException("Failed to generate CERIF.", e);
    } finally {
      try {
        cdmToCERIF.destroy();
      } catch (Exception e) {
        throw new KettleException("Failed to clean up CDMToCERIF.", e);
      }
    }

    // Output a single row containing the CERIF XML.
    RowMeta rowMeta = new RowMeta();
    rowMeta.addValueMeta(new ValueMeta(CDMCerifMeta.FIELD_NAME, ValueMetaInterface.TYPE_STRING));
    putRow(rowMeta, new Object[]{cerifStr});

    // This step will only need to generate a single row output and then stop.    
    setOutputDone(); // StepInterface javadoc says: Signal output done to destination steps.    
    return false; // StepInterface javadoc says: Return false if no more rows can be processed.
  }

  /**
   * Convert supplied rowData to a bean with values keyed on the input field names.
   *
   * @param rowData Data for a particular row.
   * @return Bean with row values keyed on field names.
   */
  private DynaBean convertRowToBean(Object[] rowData) {
    // Add to the known field names.
    String[] fieldNamesArray = getInputRowMeta().getFieldNames();

    // Convert the row data to a bean.
    DynaBean rowBean = new DefaultFlatDynaBean();
    for (int i = 0; i < fieldNamesArray.length; i++) {
      Object value = rowData[i];
      String valueStr;
      if (value == null) {
        valueStr = null;
      } else if (value instanceof byte[]) {
        valueStr = new String((byte[])value);
      } else {
        valueStr = value.toString();
      }
      String fieldName = fieldNamesArray[i];
      try {
        rowBean.set(fieldName, valueStr);
      } catch (Exception e) {
        // Never really going to happen.
        throw new RuntimeException(e);
      }
    }
    return rowBean;
  }
}
