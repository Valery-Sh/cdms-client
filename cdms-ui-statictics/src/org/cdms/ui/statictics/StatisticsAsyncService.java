/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cdms.ui.statictics;

import java.util.Date;
import org.cdms.entities.InvoiceStatView;
import org.cdms.remoting.QueryPage;
import org.cdms.remoting.StatisticsService;
import org.cdms.remoting.services.InvoiceStatisticsServiceProvider;
import org.cdms.ui.common.AsyncServiceProcessor;
import org.openide.util.Lookup;
import org.openide.util.TaskListener;

/**
 *
 * @author Valery
 */
public class StatisticsAsyncService {
    
    protected AsyncServiceProcessor processor;
    
    
    
    public StatisticsService getStatisticsService() {
        return (Lookup.getDefault().lookup(InvoiceStatisticsServiceProvider.class)).getInstance();
    }

    public void requestInvoice(TaskListener taskListener, final QueryPage<InvoiceStatView> queryPage) {
        String msg = "Invoice statistics ... ";// + queryPage.getEntityAsExample().getClass().getSimpleName() + "s by Filter...";
        processor = new AsyncServiceProcessor(msg) {
            @Override
            public Object perform() {
                return getStatisticsService().requestInvoice(queryPage); // TODO paging                
            }
        };
        
        processor.run(taskListener);
    }

    
    public Object getResult() {
        return processor.getResult();
    }
    
}
