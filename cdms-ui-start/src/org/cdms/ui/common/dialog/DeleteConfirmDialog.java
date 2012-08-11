/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cdms.ui.common.dialog;

import org.openide.DialogDescriptor;
import org.openide.DialogDisplayer;
import org.openide.NotifyDescriptor;

/**
 *
 * @author V. Shyshkin
 */
public class DeleteConfirmDialog {


    public static boolean confirm(String entityName, long id) {
        Object[] options = {DialogDescriptor.CANCEL_OPTION,DialogDescriptor.OK_OPTION}; 
        String title = "Confirm Delete for " + entityName;
        String message = "The " + entityName + " with an id equals to " + id + " wilbe be deleted. Please, confirm.";
        NotifyDescriptor nd = new NotifyDescriptor(
                message,  //  message 
                title, //  title 
                NotifyDescriptor.OK_CANCEL_OPTION, 
                NotifyDescriptor.INFORMATION_MESSAGE, 
                options,
                DialogDescriptor.CANCEL_OPTION);// Initial( when Enter)
 

        Object result = DialogDisplayer.getDefault().notify(nd);         
        
        return result == NotifyDescriptor.OK_OPTION ? true : false;

    }
}
