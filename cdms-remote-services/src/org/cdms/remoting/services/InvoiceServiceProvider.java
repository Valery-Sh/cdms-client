package org.cdms.remoting.services;

import org.cdms.remoting.InvoiceService;
import org.cdms.remoting.services.hessian.HessianInvoiceService;
import org.openide.util.Lookup;

/**
 * The base class for all Invoice service provider implementations. 
 * Looks up in the <code>Glbal Lookup</code> all registered providers of type 
 * <code>InvoiceServiceProvider</code>
 * and if there is no such provider then returns the default one. 
 * The method <code>getInstance</code> of the default service provider returns
 * an object of type 
 * {@link org.cdms.remoting.services.hessian.HessianInvoiceService }.
 * If we decide to use a different protocol to communicate with the server
 * ( for example web services or Spring HttpInvoker) we should implement
 * a subclass and register it in the Global Lookup as a Service Provider.

 * @see org.cdms.remoting.InvoiceService
 * @author V. Shyshkin
 */

public abstract class InvoiceServiceProvider {
    /**
     * Subclasses of this class must implement the method.
     * @return the instance of {@link org.cdms.remoting.InvoiceService }
     */
    public abstract InvoiceService getInstance();
    /**
     * Looks up in the <code>Glbal Lookup</code> all registered providers 
     * of type <code>InvoiceServiceProvide</code>
     * and if there is no such provider returns the default one. 
     * The method <code>getInstance</code> of the default service provider returns
     * an object of type 
     * {@link org.cdms.remoting.services.hessian.HessianInvoiceService }
     * @return the object of type <code>InvoiceServiceProvider</code>. 
     */
    public static InvoiceServiceProvider getDefault() {

        InvoiceServiceProvider services =
                Lookup.getDefault().lookup(InvoiceServiceProvider.class);
        if (services == null) {
            services = new DefaultServiceProvider();
        }
        return services;
    }

    protected static class DefaultServiceProvider extends InvoiceServiceProvider {

        @Override
        public InvoiceService getInstance() {
            return new HessianInvoiceService();
        }
    }
}

