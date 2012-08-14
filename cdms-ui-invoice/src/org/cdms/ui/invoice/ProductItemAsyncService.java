package org.cdms.ui.invoice;


import org.cdms.entities.Invoice;
import org.cdms.entities.ProductItem;
import org.cdms.remoting.EntityService;
import org.cdms.remoting.QueryPage;
import org.cdms.ui.common.AsyncServiceProcessor;
import org.openide.util.Lookup;
import org.openide.util.TaskListener;

/**Invoice
 *
 * @author V. Shyshkin
 */
public  class ProductItemAsyncService<E extends ProductItem> extends EntityAsyncService{

    @Override
    public EntityService getEntityService() {
        return (Lookup.getDefault().lookup(ProductItemServiceProvider.class)).getInstance();
    }
}
