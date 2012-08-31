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

import java.util.List;
import java.util.Map;
import org.pentaho.di.core.CheckResultInterface;
import org.pentaho.di.core.Counter;
import org.pentaho.di.core.database.DatabaseMeta;
import org.pentaho.di.core.exception.KettleException;
import org.pentaho.di.core.exception.KettleStepException;
import org.pentaho.di.core.exception.KettleXMLException;
import org.pentaho.di.core.row.RowMetaInterface;
import org.pentaho.di.core.row.ValueMeta;
import org.pentaho.di.core.variables.VariableSpace;
import org.pentaho.di.repository.ObjectId;
import org.pentaho.di.repository.Repository;
import org.pentaho.di.trans.Trans;
import org.pentaho.di.trans.TransMeta;
import org.pentaho.di.trans.step.BaseStepMeta;
import org.pentaho.di.trans.step.StepDataInterface;
import org.pentaho.di.trans.step.StepInterface;
import org.pentaho.di.trans.step.StepMeta;
import org.pentaho.di.trans.step.StepMetaInterface;
import org.w3c.dom.Node;

/**
 * The CDM Cerif step meta. StepMetaInterface defines the metadata and takes care of XML representation, saving loading
 * from/to repository, checks, etc.
 *
 * @author Johnathon Harris
 */
public class CDMCerifMeta extends BaseStepMeta implements StepMetaInterface {

  /**
   * Name of the field used to hold the output from this step.
   */
  protected static final String FIELD_NAME = "CERIF_XML";

  /**
   * Get the fields. This is called by any steps connected to this one to retrieve the fields.
   *
   * @param r        Row metadata
   * @param origin   Unknown
   * @param info     Unknown
   * @param nextStep The following step.
   * @param space    Variable space (contains variables such as params).
   * @throws KettleStepException Unknown what throws this exception.
   */
  @Override
  public void getFields(RowMetaInterface r, String origin, RowMetaInterface[] info, StepMeta nextStep,
  VariableSpace space) throws KettleStepException {
    // A single field containing the XML.
    ValueMeta v = new ValueMeta(FIELD_NAME, ValueMeta.TYPE_STRING, -1, -1);
    v.setOrigin(origin);
    r.addValueMeta(v);
  }

  /**
   * Get the classname for the dialog to display.
   *
   * @return Dialog class name.
   */
  @Override
  public String getDialogClassName() {
    return CDMCerifDialog.class.getName();
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void setDefault() {
    // Nothing to do atm.
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void loadXML(Node stepnode, List<DatabaseMeta> databases, Map<String, Counter> counters) throws
  KettleXMLException {
    // Nothing to do atm.
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void saveRep(Repository rep, ObjectId idTransformation, ObjectId idStep) throws KettleException {
    // Nothing to do atm.
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void readRep(Repository rep, ObjectId idStep, List<DatabaseMeta> databases, Map<String, Counter> counters)
  throws KettleException {
    // Nothing to do atm.
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void check(List<CheckResultInterface> remarks, TransMeta transmeta, StepMeta stepMeta, RowMetaInterface prev,
  String[] input, String[] output, RowMetaInterface info) {
    // Nothing to do atm.
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public StepInterface getStep(StepMeta stepMeta, StepDataInterface stepDataInterface, int cnr, TransMeta transMeta,
  Trans disp) {
    return new CDMCerif(stepMeta, stepDataInterface, cnr, transMeta, disp);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public StepDataInterface getStepData() {
    return new CDMCerifData();
  }
}
