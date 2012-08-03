package org.cdms.auth;


import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.net.Authenticator;
import java.util.List;
import java.util.prefs.Preferences;
import org.cdms.remoting.AuthService;
import org.cdms.remoting.UserInfo;
import org.cdms.remoting.services.AuthServiceProvider;
import org.openide.DialogDescriptor;
import org.openide.DialogDisplayer;
import org.openide.LifecycleManager;
import org.openide.modules.ModuleInstall;
import org.openide.util.Lookup;
import org.openide.util.NbPreferences;
import org.openide.windows.TopComponent;
import org.openide.windows.WindowManager;
import org.openide.windows.WindowSystemEvent;
import org.openide.windows.WindowSystemListener;

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
        // TODO change with 8080 
        int port = node.getInt("server.port", 8084);
        pane.setHttpAddress(name);
        pane.setHttpPort("" + port);
        
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
            pane.setLoginMsg("");
            AuthServiceProvider rs = Lookup.getDefault().lookup(AuthServiceProvider.class);
            AuthService authService = rs.getInstance();
            Authenticator.setDefault(null); // to disable pop up window
            UserInfo userInfo = authService.authenticate(pane.getUsername(), pane.getPassword());
            
            if (userInfo == null) {
                pane.setLoginMsg("Invalid user name or password");
            } else {
                descr.setClosingOptions(null);
                UserLookup.getDefault().set(userInfo);
                UserInfo info = UserLookup.getDefault().lookup(UserInfo.class);                
                if ( info == null ) {
                    int a = 0;
                }
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
