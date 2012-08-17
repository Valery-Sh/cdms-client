
package org.cdms.remoting.services;

import org.cdms.remoting.StatisticsService;
import org.cdms.remoting.services.hessian.HessianInvoiceStatisticsService;
import org.openide.util.Lookup;

/**
 *
 * @author V. Shyshkin
 */
public abstract class InvoiceStatisticsServiceProvider    {

    public abstract StatisticsService getInstance();
    
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
