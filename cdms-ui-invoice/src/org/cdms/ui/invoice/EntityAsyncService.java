
package org.cdms.ui.invoice;

import org.cdms.remoting.EntityService;
import org.cdms.remoting.QueryPage;
import org.cdms.ui.common.AsyncServiceProcessor;
import org.openide.util.TaskListener;

/**
 *
 * @author V . Shyshkin
 */
public abstract class EntityAsyncService<E> {

    protected AsyncServiceProcessor processor;
    
    public E findById(long l) {
        throw new UnsupportedOperationException("Not supported yet.");
    }
    
    public abstract EntityService<E> getEntityService();

    public void findByExample(TaskListener taskListener, final QueryPage<E> queryPage) {
        String msg = "Search " + queryPage.getEntityAsExample().getClass().getSimpleName() + "s by Filter...";
        processor = new AsyncServiceProcessor(msg) {
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
//    protected ProductItemService getEntityService() {
//            return (Lookup.getDefault().lookup(ProductItemProvider.class)).getInstance();
//    }

    public void insert(TaskListener taskListener,final E entity) {
        String msg = "Insert " + entity.getClass().getSimpleName() + " ...";
        processor = new AsyncServiceProcessor(msg) {
            @Override
            public Object perform() {
                return getEntityService().insert(entity); // TODO paging                
            }
        };
        processor.run(taskListener);

    }

    public void update(TaskListener taskListener,final E entity) {
        String msg = "Update " + entity.getClass().getSimpleName() + " ...";
        processor = new AsyncServiceProcessor(msg) {
            @Override
            public Object perform() {
                return getEntityService().update(entity); // TODO paging                
            }
        };
        
        processor.run(taskListener);
    }

    public void delete(TaskListener taskListener,final E entity) {
        String msg = "Delete " + entity.getClass().getSimpleName() + " ...";
        processor = new AsyncServiceProcessor(msg) {
            @Override
            public Object perform() {
                return getEntityService().delete(entity); // TODO paging                
            }
        };
        
        processor.run(taskListener);

    }
    
    
}
