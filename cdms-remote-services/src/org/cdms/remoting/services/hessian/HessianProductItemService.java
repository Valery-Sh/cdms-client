package org.cdms.remoting.services.hessian;

import org.cdms.entities.ProductItem;
import org.cdms.remoting.ProductItemService;

/**
 *
 * @author V. Shyshkin
 */
public class HessianProductItemService<E extends ProductItem> extends HessianEntityService<E>
        implements ProductItemService<E> {

    @Override
    protected Class getServiceClass() {
        return ProductItemService.class;
    }

}
