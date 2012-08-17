/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cdms.remoting.services.hessian;

import org.cdms.entities.ProductItem;
import org.cdms.remoting.ProductItemService;

/**
 *
 * @author Valery
 */
public class HessianProductItemService<E extends ProductItem> extends HessianEntityService<E>
        implements ProductItemService<E> {

    @Override
    protected Class getServiceClass() {
        return ProductItemService.class;
    }

}
