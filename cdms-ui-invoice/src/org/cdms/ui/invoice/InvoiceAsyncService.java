package org.cdms.ui.invoice;


import org.cdms.entities.Invoice;
import org.cdms.remoting.EntityService;
import org.cdms.remoting.services.InvoiceServiceProvider;
import org.cdms.ui.common.EntityAsyncService;

/**Invoice
 *
 * @author V. Shyshkin
 */
public class InvoiceAsyncService <E extends Invoice> extends EntityAsyncService{

    @Override
    public EntityService getEntityService() {
        //return (Lookup.getDefault().lookup(InvoiceServiceProvider.class)).getInstance();
        return InvoiceServiceProvider.getDefault().getInstance();        
    }
}
