/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cdms.ui.invoice;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JMenuItem;
import org.cdms.ui.shared.AbstractOpenTopComponentAction;
import org.openide.awt.ActionID;
import org.openide.awt.ActionReference;
import org.openide.awt.ActionRegistration;
import org.openide.util.NbBundle;
import org.openide.util.NbBundle.Messages;

@ActionID(
    category = "Applications",
id = "org.cdms.ui.invoice.OpenInvoiceAction")
@ActionRegistration(
    iconBase = "org/cdms/ui/invoice/invoice16x16.png",
displayName = "#CTL_OpenInvoiceAction")
@ActionReference(path = "Menu/Applications", position = 3033)
@Messages("CTL_OpenInvoiceAction=Invoices")
public final class OpenInvoiceAction extends AbstractOpenTopComponentAction {    
   
   public OpenInvoiceAction() {
       super();
   }
    @Override
    public void actionPerformed(ActionEvent e) {
        openTopComponent("invoiceTopComponent");
    }   

    @Override
    public JMenuItem getMenuPresenter() {
        return getMenuPresenter(NbBundle.getMessage(OpenInvoiceAction.class, "CTL_OpenInvoiceAction"), 
                "org/cdms/ui/invoice/invoice16x16.png", 
                "view","edit");
    }

}
