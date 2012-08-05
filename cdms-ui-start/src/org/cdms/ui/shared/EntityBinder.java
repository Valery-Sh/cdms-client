package org.cdms.ui.shared;

import javax.swing.JComponent;
import javax.swing.JTextField;

/**
 *
 * @author V. Shyshkin
 */
public interface EntityBinder {
    void addTextFieldBinder(JTextField textField,String propertyName);
    void addCalendarBinder(JComponent textField,String propertyName);
    public void refresh();
    //Object getBindingGroup();
}
