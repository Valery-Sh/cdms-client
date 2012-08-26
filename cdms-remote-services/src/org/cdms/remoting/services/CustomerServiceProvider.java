package org.cdms.remoting.services;

import org.cdms.remoting.services.hessian.HessianCustomerService;
import org.cdms.shared.remoting.CustomerService;
import org.openide.util.Lookup;

/**
 * The base class for all Customer service provider implementations. 
 * Looks up in the <code>Glbal Lookup</code> all registered providers of type 
 * <code>CustomerServiceProvide</code>
 * and if there is no such provider returns the default one. 
 * The method <code>getInstance</code> of the default service provider returns
 * an object of type 
 * {@link org.cdms.remoting.services.hessian.HessianCustomerService }.
 * If we decide to use a different protocol to communicate with the server
 * ( for example web services or Spring HttpInvoker) we should implement
 * a subclass and register it in the Global Lookup as a Service Provider.

 * @see org.cdms.remoting.CustomerService
 * @author V. Shyshkin
 */
public abstract class CustomerServiceProvider {
    /**
     * Subclasses of this class must implement the method.
     * @return the instance of {@link org.cdms.remoting.CustomerService }
     */
    public abstract CustomerService getInstance();
    /**
     * Looks up in the <code>Glbal Lookup</code> all registered providers 
     * of type <code>CustomerServiceProvide</code>
     * and if there is no such provider returns the default one. 
     * The method <code>getInstance</code> of the default service provider returns
     * an object of type 
     * {@link org.cdms.remoting.services.hessian.HessianCustomerService }
     * @return the object of type <code>CustomerServiceProvider</code>. 
     */
    public static CustomerServiceProvider getDefault() {

        CustomerServiceProvider services =
                Lookup.getDefault().lookup(CustomerServiceProvider.class);
        if (services == null) {
            services = new DefaultCustomerServiceProvider();
        }
        return services;
    }

    protected static class DefaultCustomerServiceProvider extends CustomerServiceProvider {
        /**
         * @return an instance of type 
         * {@link org.cdms.remoting.services.hessian.HessianCustomerService }.
         */
        @Override
        public CustomerService getInstance() {
            return new HessianCustomerService();
        }
    }
}
