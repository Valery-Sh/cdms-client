
package org.cdms.remoting.services.hessian;

import org.cdms.shared.remoting.InvoiceStatisticsService;

/**
 * Implements the <code>StatisticsService</code> interface to access it's remote
 * methods trough <code>Hessian Remote Protocol</code>.
 * Actually the class extends <code>HessianStatisticsService</code> and overrides 
 * a single method {@link #getServiceClass() }
 * 
 * @see org.cdms.remoting.StatisticsService
 * @see HessianStatisticsService
 * @author V. Shyshkin
 */
public class HessianInvoiceStatisticsService extends HessianStatisticsService {
    /**
     * @return the type <code>InvoiceStatisticsService.class</code> of the service class 
     *    who provides <code>StatisticsService</code> functionality for <code>Invoice</code>
     *    class.
     * @see org.cdms.remoting.InvoiceService   
     */
    @Override
    protected Class getServiceClass() {
        return InvoiceStatisticsService.class;
    }
}
