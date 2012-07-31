
package org.cdms.hessian;

import org.cdms.remoting.AuthService;
import org.cdms.remoting.services.AuthServiceProvider;
import org.openide.util.lookup.ServiceProvider;

/**
 *
 * @author V. Shyshkin
 */
@ServiceProvider(service=AuthServiceProvider.class)
public class HessianAuthserviceProvider extends AuthServiceProvider{

    @Override
    public AuthService getInstance() {
        return new HessianAuthService();
    }
    
}
