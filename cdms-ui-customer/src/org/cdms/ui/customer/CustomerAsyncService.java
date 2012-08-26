package org.cdms.ui.customer;

import org.cdms.remoting.services.CustomerServiceProvider;
import org.cdms.shared.entities.Customer;
import org.cdms.shared.remoting.EntityService;
import org.cdms.ui.common.EntityAsyncService;

/**
 *
 * @author V. Shyshkin
 */
public class CustomerAsyncService<E extends Customer> extends EntityAsyncService{

    @Override
    public EntityService getEntityService() {
        return CustomerServiceProvider.getDefault().getInstance();        
    }

}
