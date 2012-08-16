package org.cdms.ui.customer;

import org.cdms.entities.Customer;
import org.cdms.remoting.CustomerService;
import org.cdms.remoting.QueryPage;
import org.cdms.remoting.services.CustomerServiceProvider;
import org.cdms.ui.common.AsyncServiceProcessor;
import org.openide.util.Lookup;
import org.openide.util.TaskListener;

/**
 *
 * @author V. Shyshkin
 */
public class CustomerAsyncService1 {

    protected AsyncServiceProcessor processor;
    
    public Customer findById(long l) {
        throw new UnsupportedOperationException("Not supported yet.");
    }
    
/*    public void findByExample(TaskListener taskListener,final Customer filter, final long firstRecordMaxId, final int pageSize) {
        
        processor = new AsyncServiceProcessor("Search Customers by Filter...") {
            @Override
            public Object perform() {
                return getCustomerService().findByExample(filter,firstRecordMaxId, pageSize); // TODO paging                
            }
        };
        
        processor.run(taskListener);
    }
*/
    public void findByExample(TaskListener taskListener, final QueryPage<Customer> queryPage) {
        
        processor = new AsyncServiceProcessor("Search Customers by Filter...") {
            @Override
            public Object perform() {
                return getCustomerService().findByExample(queryPage); // TODO paging                
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

    public void delete(TaskListener taskListener,final Customer customer) {
        processor = new AsyncServiceProcessor("Delete Customer ...") {
            @Override
            public Object perform() {
                return getCustomerService().delete(customer); // TODO paging                
            }
        };
        
        processor.run(taskListener);

    }
    
    
}
