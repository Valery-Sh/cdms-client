package org.cdms.auth;

import org.cdms.remoting.ConfigService;
import org.cdms.remoting.UserInfo;
import org.openide.util.lookup.ServiceProvider;

/**
 * The class exposes a lookup service that allows different parts of the application 
 * to access information about the authenticated user.  
 * Mainly, such information is used to check the user's permissions (roles).
 * 
 * @author V. Shyshkin
 */
@ServiceProvider(service=ConfigService.class)
public class ConfigServiceImpl implements ConfigService{
    /**
     * @return the object of type {@link org.cdms.remoting.UserInfo }
     */
    @Override
    public UserInfo getConfig() {
        UserLookup l =  UserLookup.getDefault();
        if ( l == null ) {
            return null;
        }
        return l.lookup(UserInfo.class);
    }
    
}
