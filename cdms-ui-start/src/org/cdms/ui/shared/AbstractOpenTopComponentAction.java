package org.cdms.ui.shared;

import java.awt.event.ActionEvent;
import javax.swing.AbstractAction;
import javax.swing.JMenuItem;
import org.cdms.auth.UserLookup;
import org.cdms.remoting.UserInfo;
import org.openide.util.ImageUtilities;
import org.openide.util.NbBundle;
import org.openide.util.actions.Presenter;
import org.openide.windows.TopComponent;
import org.openide.windows.WindowManager;

/**
 *
 * @author V. Shyshkin
 */
public abstract class AbstractOpenTopComponentAction extends AbstractAction implements Presenter.Menu {
   
   public AbstractOpenTopComponentAction() {
   }
   
   public void openTopComponent(String preferredID) {
        TopComponent tc = WindowManager.getDefault().findTopComponent(preferredID);
        if ( tc != null ) {
            tc.open();
            tc.requestActive();
        }
   }
    public JMenuItem getMenuPresenter(String menuText, String iconPath, String... role) {
        JMenuItem menuItem = new JMenuItem(menuText);
        menuItem.setAction(this);
        menuItem.setText(menuText);
        menuItem.setIcon(ImageUtilities.loadImageIcon(iconPath, true));
        
        UserInfo info = UserLookup.getDefault().lookup(UserInfo.class);
        boolean enable = false;
        if ( info.inRole(role)) {
            enable = true;
        }
        menuItem.setEnabled(enable);
        return menuItem;
    }

}
