package org.cdms.ui.customer;

import java.util.List;
import org.cdms.entities.Customer;
import org.cdms.remoting.CustomerService;
import org.cdms.remoting.services.CustomerServiceProvider;
import org.cdms.ui.shared.AsyncServiceProcessor;
import org.openide.util.Lookup;
import org.openide.util.TaskListener;

/**
 *
 * @author V. Shyshkin
 */
public class CustomerAsyncService {

    protected AsyncServiceProcessor processor;
    
    public Customer findById(long l) {
        throw new UnsupportedOperationException("Not supported yet.");
    }
    
    public void findByExample(TaskListener taskListener,final Customer filter, final int firstRecord, final int pageSize) {
        
        processor = new AsyncServiceProcessor("Search Customers by Filter...") {
            @Override
            public Object perform() {
                return getCustomerService().findByExample(filter,firstRecord, pageSize); // TODO paging                
            }
        };
        
        processor.run(taskListener);
    }
    
    protected Object getResult() {
        return processor.getResult();
    }
    protected CustomerService getCustomerService() {
            return (Lookup.getDefault().lookup(CustomerServiceProvider.class)).getInstance();
    }

    public void insert(TaskListener taskListener,final Customer customer) {
        processor = new AsyncServiceProcessor("Insert Customer ...") {
            @Override
            public Object perform() {
                return getCustomerService().insert(customer); // TODO paging                
            }
        };
        processor.run(taskListener);

    }

    public void update(TaskListener taskListener,final Customer customer) {
        
        processor = new AsyncServiceProcessor("Update Customer ...") {
            @Override
            public Object perform() {
                return getCustomerService().update(customer); // TODO paging                
            }
        };
        
        processor.run(taskListener);
    }

    public void delete(long l) {
        throw new UnsupportedOperationException("Not supported yet.");
    }
    
    
}
