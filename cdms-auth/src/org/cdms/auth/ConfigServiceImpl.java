/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cdms.auth;

import org.cdms.remoting.ConfigService;
import org.cdms.remoting.UserInfo;
import org.openide.util.lookup.ServiceProvider;

/**
 * 
 * @author V. Shyshkin
 */
@ServiceProvider(service=ConfigService.class)
public class ConfigServiceImpl implements ConfigService{

    @Override
    public UserInfo getConfig() {
        UserLookup l =  UserLookup.getDefault();
        if ( l == null ) {
            return null;
        }
        return l.lookup(UserInfo.class);
    }
    
}
