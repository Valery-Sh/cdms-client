package org.cdms.remoting.services;


import org.cdms.remoting.AuthService;
import org.openide.util.Lookup;

/**
 *
 * @author V. Shyshkin
 */
public abstract class AuthServiceProvider {
    
    public abstract AuthService getInstance();
    
    public static AuthServiceProvider getDefault() {

        AuthServiceProvider services =
                Lookup.getDefault().lookup(AuthServiceProvider.class);
        if (services == null) {
            services = new DefaultAuthServiceProvider();
        }
        return services;
    }

    private static class DefaultAuthServiceProvider extends AuthServiceProvider {

        @Override
        public AuthService getInstance() {
            //return new HessianAuthService();
            return null;
        }
    }
}
