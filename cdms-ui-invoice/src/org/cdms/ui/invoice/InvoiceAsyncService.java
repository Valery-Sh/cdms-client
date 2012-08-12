package org.cdms.ui.invoice;


import org.cdms.entities.Invoice;
import org.cdms.remoting.InvoiceService;
import org.cdms.remoting.QueryPage;
import org.cdms.remoting.services.InvoiceServiceProvider;
import org.cdms.ui.common.AsyncServiceProcessor;
import org.openide.util.Lookup;
import org.openide.util.TaskListener;

/**Invoice
 *
 * @author V. Shyshkin
 */
public class InvoiceAsyncService {

    protected AsyncServiceProcessor processor;
    
    public Invoice findById(long l) {
        throw new UnsupportedOperationException("Not supported yet.");
    }
    

    public void findByExample(TaskListener taskListener, final QueryPage<Invoice> queryPage) {
        
        processor = new AsyncServiceProcessor("Search Invoices by Filter...") {
            @Override
            public Object perform() {
                return getEntityService().findByExample(queryPage); // TODO paging                
            }
        };
        
        processor.run(taskListener);
    }
    
    protected Object getResult() {
        return processor.getResult();
    }
    protected InvoiceService getEntityService() {
            return (Lookup.getDefault().lookup(InvoiceServiceProvider.class)).getInstance();
    }

    public void insert(TaskListener taskListener,final Invoice entity) {
        processor = new AsyncServiceProcessor("Insert Invoice ...") {
            @Override
            public Object perform() {
                return getEntityService().insert(entity); // TODO paging                
            }
        };
        processor.run(taskListener);

    }

    public void update(TaskListener taskListener,final Invoice entity) {
        
        processor = new AsyncServiceProcessor("Update Invoice ...") {
            @Override
            public Object perform() {
                return getEntityService().update(entity); // TODO paging                
            }
        };
        
        processor.run(taskListener);
    }

    public void delete(TaskListener taskListener,final Invoice entity) {
        processor = new AsyncServiceProcessor("Delete Invoice ...") {
            @Override
            public Object perform() {
                return getEntityService().delete(entity.getId()); // TODO paging                
            }
        };
        
        processor.run(taskListener);

    }
    
    
}
