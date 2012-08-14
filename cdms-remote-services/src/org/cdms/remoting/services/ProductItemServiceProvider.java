
package org.cdms.remoting.services;

import org.cdms.remoting.InvoiceService;
import org.openide.util.Lookup;

/**
 *
 * @author V. Shyshkin
 */
public abstract class ProductItemServiceProvider    {

    public abstract InvoiceService getInstance();
    
    public static ProductItemServiceProvider getDefault() {

        ProductItemServiceProvider services =
                Lookup.getDefault().lookup(ProductItemServiceProvider.class);
        if (services == null) {
            services = new ProductItemServiceProvider.DefaultServiceProvider();
        }
        return services;
    }

    protected static class DefaultServiceProvider extends ProductItemServiceProvider {

        @Override
        public ProductItemService getInstance() {
            return new HessianProductItemService();
        }
    }
}
