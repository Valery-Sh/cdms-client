package org.cdms.ui.common;

import java.util.List;
import javax.swing.JTable;
import org.openide.util.lookup.ServiceProvider;

/**
 *
 * @author V. Shyshkin
 */
@ServiceProvider(service=AbstractTableBinderProvider.class)
public class TableBinderProvider extends AbstractTableBinderProvider{

    @Override
    public TableBinder getInstance(JTable table, List model) {
        return new TableBinder(table, model);
    }
    
}
