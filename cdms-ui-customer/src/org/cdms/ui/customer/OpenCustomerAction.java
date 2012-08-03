/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cdms.ui.customer;

import java.awt.event.ActionEvent;
import javax.swing.JMenuItem;
import org.cdms.ui.shared.AbstractOpenTopComponentAction;
import org.openide.awt.ActionID;
import org.openide.awt.ActionReference;
import org.openide.awt.ActionRegistration;
import org.openide.util.NbBundle;
import org.openide.util.NbBundle.Messages;

@ActionID(
    category = "Applications",
id = "org.cdms.ui.customer.OpenCustomerAction")
@ActionRegistration(
    iconBase = "org/cdms/ui/customer/customers.png",
displayName = "#CTL_OpenCustomerAction")
@ActionReference(path = "Menu/Applications", position = 3233)
@Messages("CTL_OpenCustomerAction=Customers")

public final class OpenCustomerAction extends AbstractOpenTopComponentAction {    
   
   public OpenCustomerAction() {
       super();
   }
    @Override
    public void actionPerformed(ActionEvent e) {
        openTopComponent("customerTopComponent");
    }   

    @Override
    public JMenuItem getMenuPresenter() {
        return getMenuPresenter(NbBundle.getMessage(OpenCustomerAction.class, "CTL_OpenCustomerAction"), 
                "org/cdms/ui/customer/customers.png", 
                "view","edit");
    }
}