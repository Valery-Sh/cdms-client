package org.cdms.remoting.services;

import org.cdms.entities.Customer;
import org.cdms.remoting.CustomerService;
import org.openide.util.Lookup;

/**
 *
 * @author V. Shyshkin
 */
public abstract class CustomerServiceProvider {
    
    public abstract CustomerService getInstance();
    
    public static CustomerServiceProvider getDefault() {

        CustomerServiceProvider services =
                Lookup.getDefault().lookup(CustomerServiceProvider.class);
        if (services == null) {
            services = new DefaultCustomerServiceProvider();
        }
        return services;
    }

    protected static class DefaultCustomerServiceProvider extends CustomerServiceProvider {

        @Override
        public CustomerService getInstance() {
            return new HessianCustomerService();
        }
    }
}
