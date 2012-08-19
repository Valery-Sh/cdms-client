package org.cdms.remoting.services;

import org.cdms.remoting.StatisticsService;
import org.cdms.remoting.services.hessian.HessianInvoiceStatisticsService;
import org.openide.util.Lookup;

/**
 * The base class for all Invoice Statistics service provider implementations. 
 * Looks up in the <code>Glbal Lookup</code> all registered providers of type 
 * <code>InvoiceStatisticsServiceProvider</code>
 * and if there is no such provider then returns the default one. 
 * The method <code>getInstance</code> of the default service provider returns
 * an object of type 
 * {@link org.cdms.remoting.services.hessian.HessianInvoiceService }.
 * If we decide to use a different protocol to communicate with the server
 * ( for example web services or Spring HttpInvoker) we should implement
 * a subclass and register it in the Global Lookup as a Service Provider.

 * @see org.cdms.remoting.StatisticsService
 * @author V. Shyshkin
 */
public abstract class InvoiceStatisticsServiceProvider    {
    /**
     * Subclasses of this class must implement the method.
     * @return the instance of {@link org.cdms.remoting.StatisticsService }
     */
    public abstract StatisticsService getInstance();
    /**
     * Looks up in the <code>Glbal Lookup</code> all registered providers 
     * of type <code>InvoiceStatisticsServiceProvide</code>
     * and if there is no such provider returns the default one. 
     * The method <code>getInstance</code> of the default service provider returns
     * an object of type 
     * {@link org.cdms.remoting.services.hessian.HessianInvoiceStatisticsService }
     * @return the object of type <code>InvoiceStatisticsServiceProvider</code>. 
     */
    public static InvoiceStatisticsServiceProvider getDefault() {

        InvoiceStatisticsServiceProvider services =
                Lookup.getDefault().lookup(InvoiceStatisticsServiceProvider.class);
        if (services == null) {
            services = new InvoiceStatisticsServiceProvider.DefaultServiceProvider();
        }
        return services;
    }

    protected static class DefaultServiceProvider extends InvoiceStatisticsServiceProvider {

        @Override
        public StatisticsService getInstance() {
            return new HessianInvoiceStatisticsService();
        }
    }
}
