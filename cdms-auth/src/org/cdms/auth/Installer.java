package org.cdms.auth;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import org.openide.DialogDescriptor;
import org.openide.DialogDisplayer;
import org.openide.LifecycleManager;
import org.openide.modules.ModuleInstall;

/**
 *
 * @author V. Shyshkin
 */
public class Installer extends ModuleInstall implements ActionListener {

    private LoginPanel panel = new LoginPanel();
    private DialogDescriptor descr = null;

    @Override
    public void restored() {
        descr = new DialogDescriptor(panel, "Login", true, this);
        descr.setClosingOptions(new Object[]{});
        PropertyChangeListener pcl = new PropertyChangeHandler();
        
        descr.addPropertyChangeListener(pcl);
        DialogDisplayer.getDefault().notifyLater(descr);
    }

    @Override
    public void actionPerformed(ActionEvent event) {
        if (event.getSource() == DialogDescriptor.CANCEL_OPTION) {
            LifecycleManager.getDefault().exit();
        } else {
            AuthManager m = new AuthManager();
            if (!m.login(panel.getUsername(), panel.getPassword())) {
                panel.setMsg("Invalid user name or password");
            } else {
                descr.setClosingOptions(null);
            }
            
        }
    }

    public static class PropertyChangeHandler implements PropertyChangeListener {

        @Override
        public void propertyChange(PropertyChangeEvent e) {
            if (e.getPropertyName().equals(DialogDescriptor.PROP_VALUE)
                    && e.getNewValue() == DialogDescriptor.CLOSED_OPTION) {
                LifecycleManager.getDefault().exit();
            }
        }
    }
}
