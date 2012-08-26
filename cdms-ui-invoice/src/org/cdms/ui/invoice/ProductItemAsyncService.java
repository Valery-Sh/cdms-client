package org.cdms.ui.invoice;


import org.cdms.remoting.services.ProductItemServiceProvider;
import org.cdms.shared.entities.ProductItem;
import org.cdms.shared.remoting.EntityService;
import org.cdms.ui.common.EntityAsyncService;

/**
 *
 * @author V. Shyshkin
 */
public  class ProductItemAsyncService<E extends ProductItem> extends EntityAsyncService{

    @Override
    public EntityService getEntityService() {
        //return (Lookup.getDefault().lookup(ProductItemServiceProvider.class)).getInstance();
        return ProductItemServiceProvider.getDefault().getInstance();
    }
}
