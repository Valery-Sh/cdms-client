/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cdms.ui.customer;

import org.cdms.ui.common.CustomerAsyncServiceProvider;
import org.cdms.ui.common.EntityAsyncService;
import org.openide.util.lookup.ServiceProvider;

/**
 *
 * @author V. Shyshkin
 */
@ServiceProvider(service=CustomerAsyncServiceProvider.class)
public class CustomerAsyncServiceProviderImpl implements CustomerAsyncServiceProvider {

    @Override
    public EntityAsyncService getInstance() {
        return new CustomerAsyncService();
    }
    
}
