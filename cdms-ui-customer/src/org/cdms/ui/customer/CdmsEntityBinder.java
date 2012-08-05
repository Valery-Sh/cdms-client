package org.cdms.ui.customer;

import javax.swing.JComponent;
import javax.swing.JTextField;
import org.jdesktop.beansbinding.AutoBinding;
import org.jdesktop.beansbinding.BeanProperty;
import org.jdesktop.beansbinding.Binding;
import org.jdesktop.beansbinding.BindingGroup;
import org.jdesktop.beansbinding.Bindings;
import org.jdesktop.beansbinding.ELProperty;

/**
 *
 * @author Valery
 */
public class CdmsEntityBinder  {
/*    
    private BindingGroup bindingGroup;
    private JComponent source;
    
    public CdmsEntityBinder() {
        this.bindingGroup = new BindingGroup();        
    }
    public CdmsEntityBinder(JComponent source) {
        this.bindingGroup = new BindingGroup();        
        this.source = source;
    }    
    public CdmsEntityBinder(BindingGroup bindingGroup,JComponent source) {
        this.bindingGroup = bindingGroup;        
        this.source = source;
    }    
    
    protected void addBinding(Binding binding) {
        bindingGroup.addBinding(binding);
    }
    
    public void addTextFieldBinder(JTextField textField,String propertyName) {
        Binding b = Bindings.createAutoBinding(
                    AutoBinding.UpdateStrategy.READ_WRITE, 
                    source, 
                    ELProperty.create("${" + propertyName + "}"), 
                    textField, 
                    BeanProperty.create("text"));        
        
        
        addBinding(b);        
    }
    public void refresh() {
        bindingGroup.unbind();
        bindingGroup.bind();
    }

    public BindingGroup getBindingGroup() {
        return this.bindingGroup;
    }

    public void addCalendarBinder(net.sf.nachocalendar.components.DateField textField, String propertyName) {
        Binding b = Bindings.createAutoBinding(
                    AutoBinding.UpdateStrategy.READ_WRITE, 
                    source, 
                    ELProperty.create("${" + propertyName + "}"), 
                    textField, 
                    BeanProperty.create("value"));   

        addBinding(b);        
    }
*/    
}
