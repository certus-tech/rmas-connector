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

import com.jctal.chaffinch.cerif.datamodel.CanonicalDataModel;
import com.jctal.chaffinch.cerif.datamodel.schema.Field;
import java.util.Collection;
import org.pentaho.di.core.exception.KettleStepException;
import org.pentaho.di.core.row.RowMetaInterface;
import org.pentaho.di.core.row.ValueMeta;
import org.pentaho.di.core.variables.VariableSpace;
import org.pentaho.di.core.xml.XMLHandler;
import org.pentaho.di.trans.step.StepMeta;
import org.pentaho.di.trans.steps.rowsfromresult.RowsFromResultMeta;

/**
 * The CDM RowsFromResult step meta.
 *
 * @author Adam Cooney
 */
public class CDMRowsFromResultMeta extends RowsFromResultMeta {

  /**
   * String used in XML snippet for spacing.
   */
  private static final String XML_SPACER = "        ";
  /**
   * Collection of Common Data Model fields.
   */
  private final Collection<Field> cdmFields;

  /**
   * Create new CDMRowsFromResultMeta.
   */
  public CDMRowsFromResultMeta() {
    super();
    CanonicalDataModel dataModel = new CanonicalDataModel();
    cdmFields = dataModel.getFields();
  }

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
    for (Field field : cdmFields) {
      ValueMeta v = new ValueMeta(field.getName(), ValueMeta.TYPE_STRING, -1, -1);
      v.setOrigin(origin);
      r.addValueMeta(v);
    }
  }

  /**
   * Get the classname for the dialog to display.
   *
   * @return Dialog class name.
   */
  @Override
  public String getDialogClassName() {
    return CDMRowsFromResultDialog.class.getName();
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public String getXML() {
    StringBuilder retval = new StringBuilder("    <fields>");
    for (Field field : cdmFields) {
      retval.append(XML_SPACER).append("<field>");
      retval.append(XML_SPACER).append(XMLHandler.addTagValue("name", field.getName()));
      // Currently the 'Field' element does not contain methods for getting the type, length or precision so set them 
      // to some common defaults. We believe -1 is equivalent to 'not set'.
      retval.append(XML_SPACER).append(XMLHandler.addTagValue("type", ValueMeta.TYPE_STRING));
      retval.append(XML_SPACER).append(XMLHandler.addTagValue("length", -1));
      retval.append(XML_SPACER).append(XMLHandler.addTagValue("precision", -1));
      retval.append(XML_SPACER).append("</field>");
    }
    retval.append("      </fields>");
    return retval.toString();
  }
}
