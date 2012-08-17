/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cdms.ui.invoice;

import org.cdms.ui.common.EntityAsyncService;
import org.cdms.ui.common.InvoiceAsyncServiceProvider;
import org.openide.util.lookup.ServiceProvider;

/**
 *
 * @author V. Shyshkin
 */
@ServiceProvider(service=InvoiceAsyncServiceProvider.class)
public class InvoiceAsyncServiceProviderImpl implements InvoiceAsyncServiceProvider {

    @Override
    public EntityAsyncService getInstance() {
        return new InvoiceAsyncService();
    }
    
}
