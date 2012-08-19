package org.cdms.remoting.services;


import org.cdms.remoting.AuthService;
import org.cdms.remoting.services.hessian.HessianAuthService;
import org.openide.util.Lookup;

/**
 * The base class for all authentication service provider implementations. 
 * Looks up in the <code>Glbal Lookup</code> all registered providers of type <code>AuthServiceProvide</code>
 * and if there is no such provider returns the default one. 
 * The method <code>getInstance</code> of the default service provider returns
 * an object of type 
 * {@link org.cdms.remoting.services.hessian.HessianAuthService }.
 * If we decide to use a different protocol to communicate with the server
 * ( for example web services or Spring HttpInvoker) we should implement
 * a subclass and register it in the Global Lookup as a Service Provider.
 * @see org.cdms.remoting.AuthService
 * @author V. Shyshkin
 */
public abstract class AuthServiceProvider {
    /**
     * Subclasses of the class must implement this method.
     * @return the instance of {@link org.cdms.remoting.AuthService }
     */
    public abstract AuthService getInstance();
    
    /**
     * Looks up in the <code>Glbal Lookup</code> all registered providers of type <code>AuthServiceProvide</code>
     * and if there is no such provider returns the default one. 
     * The method <code>getInstance</code> of the default service provider returns
     * an object of type 
     * {@link org.cdms.remoting.services.hessian.HessianAuthService }
     * @return the object of type <code>AuthServiceProvider</code>. 
     */
    public static AuthServiceProvider getDefault() {

        AuthServiceProvider services =
                Lookup.getDefault().lookup(AuthServiceProvider.class);
        if (services == null) {
            services = new DefaultAuthServiceProvider();
        }
        return services;
    }

    protected static class DefaultAuthServiceProvider extends AuthServiceProvider {
        /**
         * @return an instance of type 
         * {@link org.cdms.remoting.services.hessian.HessianAuthService }
         */
        @Override
        public AuthService getInstance() {
            return new HessianAuthService();
        }
    }
}
