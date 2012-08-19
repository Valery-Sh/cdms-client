package org.cdms.ui.common;

import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JFormattedTextField;
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
public class EntityBinderImpl implements EntityBinder {

    private BindingGroup bindingGroup;
    private JComponent source;

    public EntityBinderImpl() {
        this.bindingGroup = new BindingGroup();
    }

    /**
     *
     * @param source TopComponent as a rule
     */
    public EntityBinderImpl(JComponent source) {
        this.bindingGroup = new BindingGroup();
        this.source = source;
    }

    public EntityBinderImpl(BindingGroup bindingGroup, JComponent source) {
        this.bindingGroup = bindingGroup;
        this.source = source;
    }

    protected void addBinding(Binding binding) {
        bindingGroup.addBinding(binding);
    }

    @Override
    public void addTextFieldBinder(JTextField textField, String propertyName) {
        Binding b = Bindings.createAutoBinding(
                AutoBinding.UpdateStrategy.READ_WRITE,
                source,
                ELProperty.create("${" + propertyName + "}"),
                textField,
                BeanProperty.create("text"));


        addBinding(b);
    }

    @Override
    public void refresh() {
        bindingGroup.unbind();
        bindingGroup.bind();
    }

    /*    @Override
     public BindingGroup getBindingGroup() {
     return this.bindingGroup;
     }
     */
    @Override
    public void addCalendarBinder(JComponent textField, String propertyName) {
        Binding b = Bindings.createAutoBinding(
                AutoBinding.UpdateStrategy.READ_WRITE,
                source,
                ELProperty.create("${" + propertyName + "}"),
                textField,
                BeanProperty.create("value"));
        addBinding(b);
    }
    @Override
    public void addDatePickerBinder(JComponent textField, String propertyName) {
        Binding b = Bindings.createAutoBinding(
                AutoBinding.UpdateStrategy.READ_WRITE,
                source,
                ELProperty.create("${" + propertyName + "}"),
                textField,
                BeanProperty.create("value"));
        addBinding(b);
    }

    @Override
    public void addFormattedTextFieldBinder(JFormattedTextField textField, String propertyName) {
        Binding b = Bindings.createAutoBinding(
                AutoBinding.UpdateStrategy.READ_WRITE,
                source,
                ELProperty.create("${" + propertyName + "}"),
                textField,
                BeanProperty.create("value"));
        addBinding(b);
    }

    @Override
    public void addConcatTextFieldBinder(JTextField textField, String... propertyName) {
        String concat = "";
        for (String s : propertyName) {
            concat += " ${" + s + "}";
        }
        Binding b = Bindings.createAutoBinding(
                AutoBinding.UpdateStrategy.READ_WRITE,
                source,
                //ELProperty.create("${selectedElement." + propertyName + "}"),
                ELProperty.create(concat),
                textField,
                BeanProperty.create("text"));

        addBinding(b);
    }

    @Override
    public void addCheckBoxBinder(JCheckBox checkBox, String propertyName) {
         Binding b = Bindings.createAutoBinding(
                AutoBinding.UpdateStrategy.READ_WRITE,
                source,
                ELProperty.create("${" + propertyName + "}"),
                checkBox,
                BeanProperty.create("selected"));


        addBinding(b);

    }

    
}