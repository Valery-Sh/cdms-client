package org.cdms.ui.customer;

import org.cdms.ui.common.CustomerAsyncServiceProvider;
import org.cdms.ui.common.EntityAsyncService;
import org.openide.util.lookup.ServiceProvider;

/**
 * The service is exposed, as it is used in the <code>Statistics</code> module.
 * @author V. Shyshkin
 */
@ServiceProvider(service=CustomerAsyncServiceProvider.class)
public class CustomerAsyncServiceProviderImpl implements CustomerAsyncServiceProvider {

    @Override
    public EntityAsyncService getInstance() {
        return new CustomerAsyncService();
    }
    
}

