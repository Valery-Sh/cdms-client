package org.cdms.ui.shared;

import java.util.ArrayList;
import java.util.List;
import javax.swing.JTable;
import javax.swing.JTextField;
import org.jdesktop.beansbinding.AutoBinding;
import org.jdesktop.beansbinding.BeanProperty;
import org.jdesktop.beansbinding.Binding;
import org.jdesktop.beansbinding.BindingGroup;
import org.jdesktop.beansbinding.Bindings;
import org.jdesktop.beansbinding.ELProperty;
import org.jdesktop.swingbinding.JTableBinding;
import org.jdesktop.swingbinding.SwingBindings;

/**
 *
 * @author V. Shyshkin
 */
public class TableBinder {

    private JTable table;
    private TableBinder parentTableBinder;    
    protected List<TableBinder> childs;
    
    private List masterList;
    
    JTableBinding jTableBinding;
    private BindingGroup bindingGroup;

    protected String linkProperty;
    
    public TableBinder(JTable table, List masterList) {
        this.bindingGroup = new BindingGroup();
        this.parentTableBinder = null;
        this.table = table;
        this.masterList = masterList;
        
        if (masterList == null) {
            this.masterList = new ArrayList();
        }
        initList();
    }
    protected TableBinder(JTable childTable, String linkProperty) {
        this.table = childTable;
        this.linkProperty = linkProperty;
    }
    public TableBinder addChild(JTable childTable,String linkProperty) {
        TableBinder tb = new TableBinder(childTable,linkProperty);
        tb.parentTableBinder = this;
        ELProperty eLProperty = ELProperty.create("${selectedElement." + linkProperty + "}");
        tb.setjTableBinding(SwingBindings.createJTableBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, table, eLProperty, childTable));
        tb.setBindingGroup(bindingGroup);
        addBinding(tb.getjTableBinding());  
        if ( childs == null ) {
            childs = new ArrayList<TableBinder>();
        }
        childs.add(tb);

        return tb;
        
    }
    protected final void initList() {
        jTableBinding = SwingBindings.createJTableBinding(
                AutoBinding.UpdateStrategy.READ_WRITE, masterList, table);
        table.setAutoscrolls(true);
        table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
    }

    public JTableBinding.ColumnBinding addColumn(String name, Class columnType) {
        JTableBinding.ColumnBinding columnBinding = jTableBinding.addColumnBinding(ELProperty.create("${" + name + "}"));
        columnBinding.setColumnName(name);
        columnBinding.setColumnClass(columnType);
        return columnBinding;
    }

    public JTableBinding.ColumnBinding addColumn(String name, Class columnType, String header) {
        JTableBinding.ColumnBinding columnBinding = jTableBinding.addColumnBinding(ELProperty.create("${" + name + "}"));
        columnBinding.setColumnName(header);
        columnBinding.setColumnClass(columnType);
        return columnBinding;
    }

    public void addBinding(Binding binding) {
        bindingGroup.addBinding(binding);
    }
    public boolean isChild() {
        return parentTableBinder != null;
    }
    public void bindTable() {
        if ( ! isChild() ) {
            addBinding(jTableBinding);
        }
    }
    public void addTextFieldBinder(JTextField textField,String propertyName) {
        Binding b = Bindings.createAutoBinding(
                AutoBinding.UpdateStrategy.READ_WRITE,
                table,
                ELProperty.create("${selectedElement." + propertyName + "}"),
                textField,
                BeanProperty.create("text"));
        
        addBinding(b);        
    }
    public void refresh() {
        if ( isChild() ) {
            return;
        }
        bindingGroup.unbind();
        bindingGroup.bind();
    }


    public JTableBinding getjTableBinding() {
        return jTableBinding;
    }
    protected void setjTableBinding(JTableBinding tb) {
        this.jTableBinding = tb;
    }

    public BindingGroup getBindingGroup() {
        return bindingGroup;
    }

    protected void setBindingGroup(BindingGroup bindingGroup) {
        this.bindingGroup = bindingGroup;
    }
    
}
