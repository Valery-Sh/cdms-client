package org.cdms.ui.common;

import java.util.List;
import javax.swing.JTable;
import org.openide.util.Lookup;

/**
 *
 * @author V. Shyshkin
 */

public abstract class AbstractTableBinderProvider {
    
    public abstract TableBinder getInstance(JTable table,List model);
    
    public static AbstractTableBinderProvider getDefault() {

        AbstractTableBinderProvider services =
                Lookup.getDefault().lookup(AbstractTableBinderProvider.class);
        if (services == null) {
            services = new DefaultAuthServiceProvider();
        }
        return services;
    }

    protected static class DefaultAuthServiceProvider extends AbstractTableBinderProvider {


        @Override
        public TableBinder getInstance(JTable table, List model) {
            return new TableBinder(table, model);
        }
    }
    
}
