package org.cdms.ui.invoice;

import org.cdms.remoting.services.InvoiceServiceProvider;
import org.cdms.shared.entities.Invoice;
import org.cdms.shared.remoting.EntityService;
import org.cdms.ui.common.EntityAsyncService;

/**
 *
 * @author V. Shyshkin
 */
public class InvoiceAsyncService <E extends Invoice> extends EntityAsyncService{

    @Override
    public EntityService getEntityService() {
        return InvoiceServiceProvider.getDefault().getInstance();        
    }
}
