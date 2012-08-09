package org.cdms.ui.common;

import javax.swing.JComponent;
import javax.swing.JFormattedTextField;
import javax.swing.JTextField;

/**
 *
 * @author V. Shyshkin
 */
public interface EntityBinder {
    void addTextFieldBinder(JTextField textField,String propertyName);
    void addCalendarBinder(JComponent textField,String propertyName);
    void addFormattedTextFieldBinder(JFormattedTextField textField, String propertyName);
    void addConcatTextFieldBinder(JTextField textField, String... propertyName);
    
    public void refresh();
    //Object getBindingGroup();
}
