package org.cdms.ui.invoice;


import org.cdms.entities.ProductItem;
import org.cdms.remoting.EntityService;
import org.cdms.remoting.services.ProductItemServiceProvider;
import org.cdms.ui.common.EntityAsyncService;
import org.openide.util.Lookup;

/**
 *
 * @author V. Shyshkin
 */
public  class ProductItemAsyncService<E extends ProductItem> extends EntityAsyncService{

    @Override
    public EntityService getEntityService() {
        return (Lookup.getDefault().lookup(ProductItemServiceProvider.class)).getInstance();
    }
}
