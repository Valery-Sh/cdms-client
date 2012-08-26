package org.cdms.remoting.services.hessian;

import org.cdms.shared.entities.ProductItem;
import org.cdms.shared.remoting.ProductItemService;

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
