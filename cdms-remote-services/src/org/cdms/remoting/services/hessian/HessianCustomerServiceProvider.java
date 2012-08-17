package org.cdms.remoting.services.hessian;

import org.cdms.remoting.CustomerService;
import org.cdms.remoting.services.CustomerServiceProvider;
import org.openide.util.lookup.ServiceProvider;

/**
 *
 * @author V. Shyshkin
 */
@ServiceProvider(service=CustomerServiceProvider.class)
public class HessianCustomerServiceProvider extends CustomerServiceProvider {

    @Override
    public CustomerService getInstance() {
        return new HessianCustomerService();
    }
    
}
