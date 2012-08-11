/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cdms.ui.common.dialog;

import javax.swing.JPanel;
import org.cdms.remoting.exception.RemoteDataAccessException;
import org.cdms.remoting.exception.RemoteValidationException;
import org.openide.DialogDescriptor;
import org.openide.DialogDisplayer;

/**
 *
 * @author Valery
 */
public class ErrorDetailsHandler {
//    private LoginPanel panel = new LoginPanel();
    private ErrorDataAccesDetailsPane daPane = new ErrorDataAccesDetailsPane();
    private ErrorValidationDetailsPane vPane = new ErrorValidationDetailsPane();
    private ErrorOtherDetailsPane oPane = new ErrorOtherDetailsPane();
    
    private DialogDescriptor descr = null;
    private Exception exception;
    
    public void show() {
        Object[] options = {DialogDescriptor.OK_OPTION}; 
        boolean isModal = true;
        String title = "Error Details";
        Object initialValue = DialogDescriptor.OK_OPTION;
        int allignOptions = DialogDescriptor.BOTTOM_ALIGN;
        JPanel pane = oPane;
        oPane.setException(exception);
        
        if ( exception instanceof RemoteDataAccessException ) {
            pane = daPane;
            daPane.setException((RemoteDataAccessException)exception);
        } else if ( exception instanceof RemoteValidationException ) {
            pane = vPane;
            vPane.setException((RemoteValidationException)exception);
        }
        descr = new DialogDescriptor(pane, title,isModal, options, initialValue, allignOptions,null,null);          
        descr.setClosingOptions(null); // all buttons close the dialog       
        
        DialogDisplayer.getDefault().notifyLater(descr);
    }

    public void setException(Exception exception) {
        this.exception = exception;
    }
    
}