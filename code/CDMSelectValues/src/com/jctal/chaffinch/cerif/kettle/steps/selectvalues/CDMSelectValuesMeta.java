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
package com.jctal.chaffinch.cerif.kettle.steps.selectvalues;

import org.pentaho.di.trans.Trans;
import org.pentaho.di.trans.TransMeta;
import org.pentaho.di.trans.step.StepDataInterface;
import org.pentaho.di.trans.step.StepInterface;
import org.pentaho.di.trans.step.StepMeta;
import org.pentaho.di.trans.steps.selectvalues.SelectValuesMeta;

/**
 * The CDM SelectValues step meta.
 *
 * @author Adam Cooney
 * @author Peter Newman
 */
public class CDMSelectValuesMeta extends SelectValuesMeta {

  /**
   * Creates a new CDMSelectValuesMeta.
   */
  public CDMSelectValuesMeta() {
    super();
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public StepInterface getStep(StepMeta stepMeta, StepDataInterface stepDataInterface, int cnr, TransMeta transMeta,
  Trans disp) {
    return new CDMSelectValues(stepMeta, stepDataInterface, cnr, transMeta, disp);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public StepDataInterface getStepData() {
    return new CDMSelectValuesData();
  }
}
