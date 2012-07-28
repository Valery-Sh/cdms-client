package org.cdms.auth;

import com.caucho.hessian.client.HessianProxyFactory;
import org.cdms.entities.User;
import org.cdms.remoting.UserService;

/**
 *
 * @author V. Shyshkin
 */
public class HessianAuthService {
        static  String url = "http://127.0.0.1:8084/cdms-server/remoting/UserService";

        public static User getUser(String userName, String password) {
            HessianProxyFactory factory = new HessianProxyFactory();
            UserService service;
            User user;
            try {
               service = (UserService) factory.create(UserService.class, url);        
               user = service.authenticate(userName, password);
               //user = (User)service.getUser(userName, password);
               //user = service.findById(10L); 
            } catch(Exception e) {
                user = null;
            }
            return user;
                    
        }
}
