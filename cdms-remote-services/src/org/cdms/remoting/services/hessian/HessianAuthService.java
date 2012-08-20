package org.cdms.remoting.services.hessian;

import com.caucho.hessian.client.HessianProxyFactory;
import java.util.prefs.Preferences;
import org.cdms.remoting.AuthService;
import org.cdms.remoting.UserInfo;
import org.openide.util.NbPreferences;

/**
 * Implements the <code>AuthService</code> interface to access it's remote methods
 * trough <code>Hessian Remote Protocol</code>.
 * @see org.cdms.remoting.AuthService
 * @author V. Shyshkin
 */
public class HessianAuthService implements AuthService {

    // TEMPLATE: static String url = "http://127.0.0.1:8084/cdms-server/remoting/AuthService";
    
    /**
     * Performs authentication  with the given <code>userName</code>
     * and <code>password</code>.
     * @param userName the name of the user to be authenticated
     * @param password the password of the user to be authenticated
     * @return the object of type {@link org.cdms.remoting.UserInfo } when 
     *    authentication success or <code>null</code> otherwise
     * @see org.cdms.remoting.AuthService
     */
    @Override
    public UserInfo authenticate(String userName, String password) {
        HessianProxyFactory factory = new HessianProxyFactory();
        Preferences node = NbPreferences.root();
        String name = node.get("server.name", "localhost");
        int port = node.getInt("server.port", 8080);
        String url = "http://" + name + ":" + port + "/cdms-server/remoting/AuthService";
//            factory.setUser(userName);
//            factory.setPassword(password);
            factory.setUser(userName);
            factory.setPassword(password);
            //factory.se
        
        AuthService service;
        UserInfo userInfo;
        try {
//               static  String url = "http://127.0.0.1:8084/cdms-server/remoting/AuthService"; 
            service = (AuthService) factory.create(AuthService.class, url);
            //factory.setUser(userName);
            //factory.setPassword(password);

            userInfo = service.authenticate(userName, password);
            userInfo.setProxyFactory(factory);
            //user = (User)service.getUser(userName, password);
            //user = service.findById(10L); 
        } catch (Exception e) {
            userInfo = null;
        }
        return userInfo;

    }
}
