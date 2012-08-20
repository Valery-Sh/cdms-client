package org.cdms.remoting.services.hessian;

import org.cdms.entities.InvoiceItem;
import org.cdms.remoting.InvoiceItemService;

/**
 *
 * @author V. Shyshkin
 */
public class HessianInvoiceItemService<E extends InvoiceItem> extends HessianEntityService<E>
        implements InvoiceItemService<E> {

    @Override
    protected Class getServiceClass() {
        return InvoiceItemService.class;
    }

}
