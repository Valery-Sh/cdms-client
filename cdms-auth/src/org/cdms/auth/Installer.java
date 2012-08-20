package org.cdms.auth;


import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.net.Authenticator;
import java.util.ArrayList;
import java.util.List;
import java.util.prefs.Preferences;
import org.cdms.remoting.AuthService;
import org.cdms.remoting.UserInfo;
import org.cdms.remoting.services.AuthServiceProvider;
import org.openide.DialogDescriptor;
import org.openide.DialogDisplayer;
import org.openide.LifecycleManager;
import org.openide.modules.ModuleInstall;
import org.openide.util.NbPreferences;

/**
 * Handles the application authentication providing a dialog to input
 * a user name and a password.
 * The dialog lets you specify the http server address and port.
 * @author V. Shyshkin
 */
public class Installer extends ModuleInstall implements ActionListener {

    private LoginPane pane = new LoginPane();
    private DialogDescriptor descr = null;
    /**
     * Defines and creates the authentication dialog.
     */
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
    /**
     * Handles the user's actions.
     * @param event the object of type <code>ActionEvent</code> 
     */
    @Override
    public void actionPerformed(ActionEvent event) {
        if (event.getSource() == DialogDescriptor.CANCEL_OPTION) {
            LifecycleManager.getDefault().exit();
        } else {
            pane.setLoginMsg("");
            //AuthServiceProvider rs = Lookup.getDefault().lookup(AuthServiceProvider.class);
            AuthServiceProvider rs = AuthServiceProvider.getDefault();
            AuthService authService = rs.getInstance();
            Authenticator.setDefault(null); // to disable pop up window
            UserInfo userInfo = authService.authenticate(pane.getUsername(), pane.getPassword());
            //////////////////
/*            UserInfo userInfo = new UserInfo();
            userInfo.setFirstName("sss");
            userInfo.setLastName("ll");
            userInfo.setUserName("uuu");
            userInfo.setTicket("ttt");
            
            
            List<String> r1 = new ArrayList<String>();
            r1.add("edit");
            r1.add("view statistics");
            userInfo.setRoles(r1);
            */ 
            ///////////////////////        
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
