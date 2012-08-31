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

import org.eclipse.swt.widgets.Shell;
import org.pentaho.di.trans.TransMeta;
import org.pentaho.di.trans.step.BaseStepMeta;
import org.pentaho.di.ui.trans.step.BaseStepDialog;

/**
 * The CDM Cerif step dialog. StepDialogInterface has GUI/dialog code to edit the meta-data.
 *
 * @author Johnathon Harris
 */
public class CDMCerifDialog extends BaseStepDialog {

  /**
   * Creates a new CDMCerifDialog.
   *
   * @param parent    a shell which will be the parent of the new instance.
   * @param in        Expected to be a BaseStepMeta.
   * @param transMeta TransMeta object.
   * @param sname     The 'step' name.
   */
  public CDMCerifDialog(Shell parent, BaseStepMeta in, TransMeta transMeta, String sname) {
    super(parent, in, transMeta, sname);
  }
}
