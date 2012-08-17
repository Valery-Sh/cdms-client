
package org.cdms.remoting.services.hessian;

import org.cdms.remoting.InvoiceStatisticsService;

/**
 *
 * @author V. Shyshkin
 */
public class HessianInvoiceStatisticsService extends HessianStatisticsService {

    @Override
    protected Class getServiceClass() {
        return InvoiceStatisticsService.class;
    }
}
