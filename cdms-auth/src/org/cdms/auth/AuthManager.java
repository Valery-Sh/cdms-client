package org.cdms.auth;

import org.cdms.entities.User;

/**
 *
 * @author V.Shyshkin
 */
public class AuthManager {
    public boolean login(String userName,String password) {
        boolean result = false;
        
        User u = HessianAuthService.getUser(userName, password);
        //User u = HessianAuthService.getUser(userName, password);
        return result;
    }
    public boolean getPermissions() {
        boolean result = false;
        return result;
    }
}
