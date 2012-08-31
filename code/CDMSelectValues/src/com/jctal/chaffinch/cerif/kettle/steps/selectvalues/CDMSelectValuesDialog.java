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

import com.jctal.chaffinch.cerif.datamodel.CanonicalDataModel;
import java.lang.reflect.Field;
import java.util.Collection;
import java.util.Iterator;
import org.eclipse.swt.events.ShellAdapter;
import org.eclipse.swt.events.ShellEvent;
import org.eclipse.swt.widgets.Shell;
import org.pentaho.di.core.logging.LogChannelInterface;
import org.pentaho.di.trans.TransMeta;
import org.pentaho.di.trans.step.StepMetaInterface;
import org.pentaho.di.ui.core.widget.ColumnInfo;
import org.pentaho.di.ui.core.widget.TableView;
import org.pentaho.di.ui.spoon.Spoon;
import org.pentaho.di.ui.trans.steps.selectvalues.SelectValuesDialog;

/**
 * The CDM SelectValues step dialog.
 *
 * @author Adam Cooney
 * @author Johnathon Harris
 * @author Peter Newman
 */
public class CDMSelectValuesDialog extends SelectValuesDialog {

  /**
   * Interface for logging messages via Kettle's logging impl.
   */
  private static LogChannelInterface logger;
  /**
   * Flag to indicate whether the UI has been modified - should only be done once.
   */
  private boolean uiModified;

  /**
   * Creates a new CDMSelectValuesDialog.
   *
   * @param parent    a shell which will be the parent of the new instance.
   * @param in        Expected to be a BaseStepMeta.
   * @param transMeta TransMeta object.
   * @param sname     The 'step' name.
   */
  public CDMSelectValuesDialog(Shell parent, Object in, TransMeta transMeta, String sname) {
    super(parent, in, transMeta, sname);
    log("Instantiated CDMSelectValuesDialog, step name: " + sname);
    uiModified = false;
  }

  /**
   * Open the step properties window.
   *
   * @return The stepname.
   */
  @Override
  public String open() {
    // It would be convenient to call super.open and then modify after that method returns.
    // However super.open will block, waiting on !shell.isDisposed().
    String returnStr = super.open();
    log("Open method returned: " + returnStr); // This is the stepname passed to the super ctor.
    return returnStr;
  }

  /**
   * The original purpose of this image is for setting the image to use for the steo, i.e. its icon. It is repurposed
   * here so that we can get hold of the Shell to modify it. This is not possible in {@link #open()} because of
   * blocking.
   *
   * @param shell             The Shell (window or frame) to set.
   * @param stepMetaInterface StepMetaInterface which defines the icon for the step.
   */
  @Override
  public void setShellImage(Shell shell, StepMetaInterface stepMetaInterface) {
    // Provides a hook for us to get the super shell when it is constructed during the open method.
    shell.addShellListener(new ShellAdapter() {
      /**
       * Shell activated event handler. Used to get hold of the super shell for modification.
       *
       * @param e Event object. Not used.
       */
      @Override
      public void shellActivated(ShellEvent e) {
        log("CDMSelectValuesDialog: Shell activated.");
        modifyUI();
      }
    });
    super.setShellImage(shell, stepMetaInterface);
  }

  /**
   * Modify the superclass constructed widgets.
   */
  private void modifyUI() {
    // This will get called every time the shell is activated, so avoid modifying more than once.
    if (uiModified) {
      return;
    } else {
      uiModified = true;
    }
    modifyMetaDataTableView();
    log("Completed modifications.");
  }

  /**
   * Modify the metadata table view in the parent shell.
   */
  private void modifyMetaDataTableView() {
    // NOTE the column index values- TableView ignores the '#' column, but Table does not.

    // Can we modify a column in an existing TableView?
    TableView metadataTableView = (TableView)getPrivateVariable("wMeta");
    // Note title is not used because by the time we modify the TableView the headers have already been prepared.
    ColumnInfo columnInfo = new ColumnInfo("not used", ColumnInfo.COLUMN_TYPE_CCOMBO, getFieldNames(), true);
    metadataTableView.setColumnInfo(1, columnInfo);

    // Can we change the column header after it has been prepared?
    metadataTableView.getTable().getColumn(2).setText("CDM Field");
  }

  /**
   * Access a private variable from this object. Used to access superclass privates.
   *
   * @param name Name of the private field.
   * @return Value of the field.
   */
  private Object getPrivateVariable(String name) {
    Field field = getField(SelectValuesDialog.class, name);
    log("Got variable '" + name + "' field: " + field);
    try {
      return field.get(this);
    } catch (IllegalAccessException iae) {
      throw new RuntimeException("Got IllegalAccessException but reflection should've made this accessible.", iae);
    }
  }

  /**
   * Use reflection to get the Field for a class, making it accessible to all.
   *
   * @param clazz     Class to get field from.
   * @param fieldName Name of the field to get.
   * @return Reflected Field object.
   */
  private Field getField(Class<?> clazz, String fieldName) {
    Class<?> tmpClass = clazz;
    do {
      for (Field field : tmpClass.getDeclaredFields()) {
        String candidateName = field.getName();
        if (!candidateName.equals(fieldName)) {
          continue;
        }
        field.setAccessible(true);
        return field;
      }
      tmpClass = tmpClass.getSuperclass();
    } while (clazz != null);
    throw new RuntimeException("Field '" + fieldName + "' not found on class " + clazz);
  }

  /**
   * Log a message via the appropriate mechanism.
   *
   * @param msg Message to log.
   * @todo Currently logs to System.out as a logger instance obtained from Kettle does not work.
   */
  private void log(String msg) {
    msg = this.getClass().getSimpleName() + ": " + msg;

    // Something like this should work, but I haven't got it to.
    logger = logger == null ? Spoon.getInstance().getLog() : logger;
    logger.logDebug(msg);

    // Here is a quick hack instead.
    System.out.println(msg);
  }

  /**
   * Get the CDM field names from the common data model.
   *
   * @return Array of field names.
   */
  private static String[] getFieldNames() {
    CanonicalDataModel dataModel = new CanonicalDataModel();
    Collection<com.jctal.chaffinch.cerif.datamodel.schema.Field> fields = dataModel.getFields();
    String[] fieldNames = new String[fields.size()];
    int i = 0;
    for (Iterator<com.jctal.chaffinch.cerif.datamodel.schema.Field> it = fields.iterator(); it.hasNext();) {
      com.jctal.chaffinch.cerif.datamodel.schema.Field field = it.next();
      fieldNames[i++] = field.getName();
    }
    return fieldNames;
  }
}
