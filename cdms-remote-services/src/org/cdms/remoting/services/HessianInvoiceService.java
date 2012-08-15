/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cdms.remoting.services;

import org.cdms.entities.Invoice;
import org.cdms.remoting.InvoiceService;

/**
 *
 * @author V.Shyshkin
 */
public class HessianInvoiceService <E extends Invoice> extends HessianEntityService<E>
        implements InvoiceService<E> {

    @Override
    protected Class getServiceClass() {
        return InvoiceService.class;
    }

}
