package org.cdms.auth;

import com.caucho.hessian.client.HessianProxyFactory;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.net.Authenticator;
import java.util.List;
import java.util.prefs.Preferences;
import org.cdms.entities.User;
import org.cdms.remoting.AuthService;
import org.cdms.remoting.UserInfo;
import org.cdms.remoting.UserService;
import org.cdms.remoting.services.AuthServiceProvider;
import org.openide.DialogDescriptor;
import org.openide.DialogDisplayer;
import org.openide.LifecycleManager;
import org.openide.modules.ModuleInstall;
import org.openide.util.Lookup;
import org.openide.util.NbPreferences;
import org.openide.windows.WindowManager;

/**
 *
 * @author V. Shyshkin
 */
public class Installer extends ModuleInstall implements ActionListener {

//    private LoginPanel panel = new LoginPanel();
    private LoginPane pane = new LoginPane();
    private DialogDescriptor descr = null;

    @Override
    public void restored() {
        
        descr = new DialogDescriptor(pane, "Login", true, this);
        Preferences node = NbPreferences.root();
        String name = node.get("server.name", "localhost");
        int port = node.getInt("server.port", 8080);
        pane.setHttpAddress(name);
        pane.setHttpPort("" + port);
        
        descr.setClosingOptions(new Object[]{});
        PropertyChangeListener pcl = new PropertyChangeHandler();

        descr.addPropertyChangeListener(pcl);
        DialogDisplayer.getDefault().notifyLater(descr);
        /*        WindowManager.getDefault().invokeWhenUIReady(new Runnable() {
         @Override
         public void run() {
         DialogDisplayer.getDefault().notify(descr);
         }
         });
         */
    }

    @Override
    public void actionPerformed(ActionEvent event) {
        if (event.getSource() == DialogDescriptor.CANCEL_OPTION) {
            LifecycleManager.getDefault().exit();
        } else {
            pane.setLoginMsg("");
            AuthServiceProvider rs = Lookup.getDefault().lookup(AuthServiceProvider.class);
            AuthService authServise = rs.getInstance();
            Authenticator.setDefault(null); // to disable pop up window
            UserInfo userInfo = authServise.authenticate(pane.getUsername(), pane.getPassword());
            
            if (userInfo == null) {
                pane.setLoginMsg("Invalid user name or password");
            } else {
                descr.setClosingOptions(null);
                UserLookup.getDefault().set(userInfo);
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
