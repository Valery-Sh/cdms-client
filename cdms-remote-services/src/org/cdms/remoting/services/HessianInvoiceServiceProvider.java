package org.cdms.remoting.services;

import org.cdms.remoting.InvoiceService;
import org.openide.util.lookup.ServiceProvider;

/**
 *
 * @author V. Shyshkin
 */
@ServiceProvider(service=InvoiceServiceProvider.class)
public class HessianInvoiceServiceProvider extends InvoiceServiceProvider {

    @Override
    public InvoiceService getInstance() {
        return new HessianInvoiceService();
    }
    
}
