package org.cdms.remoting.services.hessian;

import org.cdms.remoting.InvoiceItemService;
import org.cdms.remoting.services.InvoiceItemServiceProvider;
import org.openide.util.lookup.ServiceProvider;

/**
 *
 * @author V. Shyshkin
 */
@ServiceProvider(service=InvoiceItemServiceProvider.class)
public class HessianInvoiceItemServiceProvider extends InvoiceItemServiceProvider {

    @Override
    public InvoiceItemService getInstance() {
        return new HessianInvoiceItemService();
    }
    
}
