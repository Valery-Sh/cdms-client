package org.cdms.remoting.services.hessian;

import com.caucho.hessian.client.HessianProxyFactory;
import com.caucho.hessian.client.HessianRuntimeException;
import java.net.MalformedURLException;
import java.util.prefs.Preferences;
import org.cdms.remoting.ConfigService;
import org.cdms.remoting.EntityService;
import org.cdms.remoting.QueryPage;
import org.cdms.remoting.UserInfo;
import org.cdms.remoting.exception.RemoteConnectionException;
import org.openide.util.Exceptions;
import org.openide.util.Lookup;
import org.openide.util.NbPreferences;

/**
 * Implements <code>EntityService</code> interface  to access it's remote methods
 * trough <code>Hessian Remote Protocol</code>.
 * 
 * Subclasses should implement the single method {@link #getServiceClass() }.  
 * @see HessianAuthService
 * @see HessianCustomerService
 * @see HessianInvoiceService
 * @see HessianInvoiceItemService
 * 
 * @author Valery
 */
public abstract class HessianEntityService<E> implements EntityService<E>{
    /**
     * Subclasses must implement this method  to provide an actual service class.
     * 
     * @return the actual service class  who is responsible for the maintenance
     *     of an entity of the concrete type.
     */
    protected abstract Class getServiceClass();
    /**
     * @return the Hessian <code>stub</code> for a class that the method 
     * <code>getServiceClass</code> returns
     * @throws MalformedURLException 
     */
    private EntityService getService() throws MalformedURLException {
        
        UserInfo info = ((ConfigService)Lookup.getDefault().lookup(ConfigService.class)).getConfig();
        HessianProxyFactory factory = new HessianProxyFactory();
        Preferences node = NbPreferences.root();
        String serverName = node.get("server.name", "localhost");
        int port = node.getInt("server.port", 8080);
        
        String url = "http://" + serverName + ":" + port + "/cdms-server/remoting/" + getServiceClass().getSimpleName();
//            factory.setUser(userName);
//            factory.setPassword(password);
        factory.setUser(info.getUserName());
        factory.setPassword(info.getTicket()); // TODO in production
        return (EntityService) factory.create(getServiceClass(), url);
    }
    
    @Override
    public QueryPage<E> findByExample(QueryPage<E> queryPage) {
        try {
            return getService().findByExample(queryPage);
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
    public E insert(E customer) {
        E result = null;
//        User u = new User();
//        u.setId(10L);
//        customer.setCreatedBy(u);
        try {
            result = (E)getService().insert(customer);
        } catch (MalformedURLException ex) {
            Exceptions.printStackTrace(ex);
        } catch(HessianRuntimeException hre) {
            throwHesianTranslated(hre, "insert");
        }
        return result;

    }

    @Override
    public E update(E customer) {
        E result = null;
        try {
            result = (E)getService().update(customer);
        } catch (MalformedURLException ex) {
            Exceptions.printStackTrace(ex);
        } catch(HessianRuntimeException hre) {
            throwHesianTranslated(hre, "update");
        }
        return result;
    }

    @Override
    public E delete(E entity) {
        E result = null;
        try {
            EntityService es = getService();
            result = (E)getService().delete(entity);
        } catch (MalformedURLException ex) {
            Exceptions.printStackTrace(ex);
        } catch(HessianRuntimeException hre) {
            throwHesianTranslated(hre, "delete");
        }
        return result;

    }

    @Override
    public E deleteById(Long id) {
        E result = null;
        try {
            result = (E)getService().delete(id);
        } catch (MalformedURLException ex) {
            Exceptions.printStackTrace(ex);
        } catch(HessianRuntimeException hre) {
            throwHesianTranslated(hre, "delete");
        }
        return result;
    }
        
}
