
package org.cdms.remoting.services.hessian;

import org.cdms.shared.entities.Customer;
import org.cdms.shared.remoting.CustomerService;


/**
 * Implements the <code>CustomerService</code> interface to access it's remote
 * methods trough <code>Hessian Remote Protocol</code>.
 * Actually the class extends <code>HessianEntityService</code> and overrides 
 * a single method {@link #getServiceClass() }
 * 
 * @see org.cdms.remoting.CustomerService
 * @see HessianEntityService
 * @author V. Shyshkin
 */
public class HessianCustomerService<E extends Customer> extends HessianEntityService<E>
        implements CustomerService<E> {
    /**
     * @return the type <code>CustomerService.class</code> of the service class 
     *    who is responsible for the maintenance of the entity of 
     *    type {@link org.cdms.entities.Customer }
     * @see org.cdms.remoting.CustomerService   
     */
    @Override
    protected Class getServiceClass() {
        return CustomerService.class;
    }

}
