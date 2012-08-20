
package org.cdms.remoting.services;

import org.cdms.remoting.InvoiceItemService;
import org.cdms.remoting.services.hessian.HessianInvoiceItemService;
import org.openide.util.Lookup;

/**
 *
 * @author V. Shyshkin
 */
public abstract class InvoiceItemServiceProvider    {

    public abstract InvoiceItemService getInstance();
    
    public static InvoiceItemServiceProvider getDefault() {

        InvoiceItemServiceProvider services =
                Lookup.getDefault().lookup(InvoiceItemServiceProvider.class);
        if (services == null) {
            services = new InvoiceItemServiceProvider.DefaultServiceProvider();
        }
        return services;
    }

    protected static class DefaultServiceProvider extends InvoiceItemServiceProvider {

        @Override
        public InvoiceItemService getInstance() {
            return new HessianInvoiceItemService();
        }
    }
}
