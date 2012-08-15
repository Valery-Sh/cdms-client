package org.cdms.remoting.services;

import org.cdms.remoting.ProductItemService;
import org.openide.util.lookup.ServiceProvider;

/**
 *
 * @author V. Shyshkin
 */
@ServiceProvider(service=ProductItemServiceProvider.class)
public class HessianProductItemServiceProvider extends ProductItemServiceProvider {

    @Override
    public ProductItemService getInstance() {
        return new HessianProductItemService();
    }
    
}
