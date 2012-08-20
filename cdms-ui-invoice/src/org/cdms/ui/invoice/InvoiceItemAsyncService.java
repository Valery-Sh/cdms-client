package org.cdms.ui.invoice;


import org.cdms.entities.InvoiceItem;
import org.cdms.remoting.EntityService;
import org.cdms.remoting.services.InvoiceItemServiceProvider;
import org.cdms.ui.common.EntityAsyncService;

/**
 *
 * @author V. Shyshkin
 */
public  class InvoiceItemAsyncService<E extends InvoiceItem> extends EntityAsyncService{

    @Override
    public EntityService getEntityService() {
        //return (Lookup.getDefault().lookup(InvoiceItemServiceProvider.class)).getInstance();
        return InvoiceItemServiceProvider.getDefault().getInstance();
    }
}
