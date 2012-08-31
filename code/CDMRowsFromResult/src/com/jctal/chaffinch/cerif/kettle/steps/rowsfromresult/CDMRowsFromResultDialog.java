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
package com.jctal.chaffinch.cerif.kettle.steps.rowsfromresult;

import org.eclipse.swt.widgets.Shell;
import org.pentaho.di.trans.TransMeta;
import org.pentaho.di.ui.trans.steps.rowsfromresult.RowsFromResultDialog;

/**
 * The CDM RowsFromResult step dialog.
 *
 * @author Adam Cooney
 */
public class CDMRowsFromResultDialog extends RowsFromResultDialog {

  /**
   * Creates a new CDMRowsFromResultDialog.
   *
   * @param parent    a shell which will be the parent of the new instance.
   * @param in        Expected to be a BaseStepMeta.
   * @param transMeta TransMeta object.
   * @param sname     The 'step' name.
   */
  public CDMRowsFromResultDialog(Shell parent, Object in, TransMeta transMeta, String sname) {
    super(parent, in, transMeta, sname);
  }

  /**
   * Override the open method. This prevents a dialog being opened at all as we cannot allow the user to edit this step.
   * {@inheritDoc}
   */
  @Override
  public String open() {
    return stepname;
  }
}
