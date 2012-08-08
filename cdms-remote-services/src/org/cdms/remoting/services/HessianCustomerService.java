/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cdms.remoting.services;

import com.caucho.hessian.client.HessianProxyFactory;
import com.caucho.hessian.client.HessianRuntimeException;
import java.net.MalformedURLException;
import java.util.List;
import java.util.prefs.Preferences;
import org.cdms.connection.exception.RemoteConnectionException;
import org.cdms.entities.Customer;
import org.cdms.remoting.ConfigService;
import org.cdms.remoting.CustomerService;
import org.cdms.remoting.UserInfo;
import org.openide.util.Exceptions;
import org.openide.util.Lookup;
import org.openide.util.NbPreferences;

/**
 *
 * @author V.Shyshkin
 */
public class HessianCustomerService implements CustomerService {

    @Override
    public Customer findById(long l) {
        return null;
    }

    private CustomerService getService() throws MalformedURLException {
        
        UserInfo info = ((ConfigService)Lookup.getDefault().lookup(ConfigService.class)).getConfig();
        HessianProxyFactory factory = new HessianProxyFactory();
        Preferences node = NbPreferences.root();
        String serverName = node.get("server.name", "localhost");
        int port = node.getInt("server.port", 8080);
        
        String url = "http://" + serverName + ":" + port + "/cdms-server/remoting/CustomerService";
//            factory.setUser(userName);
//            factory.setPassword(password);
        factory.setUser(info.getUserName());
        factory.setPassword(info.getTicket()); // TODO in production
        return (CustomerService) factory.create(CustomerService.class, url);
    }
    
    @Override
    public List<Customer> findByExample(Customer customerFilter, int start, int pageSize) {
        try {
            return getService().findByExample(customerFilter, start, pageSize);
        } catch (MalformedURLException ex) {
            Exceptions.printStackTrace(ex);
        } catch(HessianRuntimeException hre) {
            throwHesianTranslated(hre, "findByExample");
        }
        return null;
        
    }
    public void throwHesianTranslated(Exception e,String methodName) {
        RemoteConnectionException re = new RemoteConnectionException(e.getMessage());
        re.setOriginalClassName(e.getClass().getName());
        re.setEntityName("Customer");
        re.setServiceName("HessianCustomerService");
        re.setServiceMethodName(methodName);
        if ( re.getCause() != null ) {
            re.setCauseClassName(e.getCause().getClass().getName());
            re.setCauseMessage(e.getCause().getMessage());
        } else {
            re.setCauseMessage(e.getMessage());
        }
        
        throw re;
    }
    
    @Override
    public void insert(Customer cstmr) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void update(Customer customer) {
        try {
            getService().update(customer);
        } catch (MalformedURLException ex) {
            Exceptions.printStackTrace(ex);
        } catch(HessianRuntimeException hre) {
            throwHesianTranslated(hre, "findByExample");
        }
    }

    @Override
    public void delete(long l) {
        throw new UnsupportedOperationException("Not supported yet.");
    }
    
}
