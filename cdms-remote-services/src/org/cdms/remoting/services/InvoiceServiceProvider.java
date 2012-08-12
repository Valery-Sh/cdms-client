/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cdms.remoting.services;

import org.cdms.remoting.InvoiceService;
import org.openide.util.Lookup;

/**
 *
 * @author Valery
 */
public abstract class InvoiceServiceProvider {
    
    public abstract InvoiceService getInstance();
    
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

