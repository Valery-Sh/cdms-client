
package org.cdms.remoting.services.hessian;

import org.cdms.entities.Customer;
import org.cdms.remoting.CustomerService;

/**
 *
 * @author V.Shyshkin
 */
public class HessianCustomerService<E extends Customer> extends HessianEntityService<E>
        implements CustomerService<E> {

    @Override
    protected Class getServiceClass() {
        return CustomerService.class;
    }

}
