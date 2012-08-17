/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cdms.remoting.services.hessian;

import org.cdms.remoting.services.InvoiceStatisticsServiceProvider;
import org.openide.util.lookup.ServiceProvider;

/**
 *
 * @author Valery
 */
@ServiceProvider(service=InvoiceStatisticsServiceProvider.class)
public class HessianInvoiceStatisticsServiceProvider extends InvoiceStatisticsServiceProvider {

    @Override
    public HessianInvoiceStatisticsService getInstance() {
        return new HessianInvoiceStatisticsService();
    }
}
