/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cdms.remoting.services;

import org.cdms.entities.InvoiceItem;
import org.cdms.remoting.InvoiceItemService;

/**
 *
 * @author Valery
 */
public class HessianInvoiceItemService<E extends InvoiceItem> extends HessianEntityService<E>
        implements InvoiceItemService<E> {

    @Override
    protected Class getServiceClass() {
        return InvoiceItemService.class;
    }

}
