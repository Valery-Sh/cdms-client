package org.cdms.ui.common;

import javax.swing.AbstractAction;
import javax.swing.JMenuItem;
import org.cdms.remoting.ConfigService;
import org.cdms.remoting.UserInfo;
import org.openide.util.ImageUtilities;
import org.openide.util.Lookup;
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
        
        //UserInfo info = UserLookup.getDefault().lookup(UserInfo.class);
        UserInfo info = ((ConfigService) Lookup.getDefault().lookup(ConfigService.class)).getConfig();
        boolean enable = false;
        if ( info.inRole(role)) {
            enable = true;
        }
        menuItem.setEnabled(enable);
        return menuItem;
    }

}
