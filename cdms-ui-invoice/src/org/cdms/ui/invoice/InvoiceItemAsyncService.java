package org.cdms.ui.invoice;


import org.cdms.entities.InvoiceItem;
import org.cdms.entities.ProductItem;
import org.cdms.remoting.EntityService;
import org.cdms.remoting.services.InvoiceItemServiceProvider;
import org.openide.util.Lookup;

/**
 *
 * @author V. Shyshkin
 */
public  class InvoiceItemAsyncService<E extends InvoiceItem> extends EntityAsyncService{

    @Override
    public EntityService getEntityService() {
        return (Lookup.getDefault().lookup(InvoiceItemServiceProvider.class)).getInstance();
    }
}
