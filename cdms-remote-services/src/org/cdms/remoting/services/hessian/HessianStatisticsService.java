/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cdms.remoting.services.hessian;

import com.caucho.hessian.client.HessianProxyFactory;
import com.caucho.hessian.client.HessianRuntimeException;
import java.net.MalformedURLException;
import java.util.List;
import java.util.prefs.Preferences;
import org.cdms.entities.InvoiceStatView;
import org.cdms.remoting.ConfigService;
import org.cdms.remoting.QueryPage;
import org.cdms.remoting.StatisticsService;
import org.cdms.remoting.UserInfo;
import org.cdms.remoting.exception.RemoteConnectionException;
import org.openide.util.Exceptions;
import org.openide.util.Lookup;
import org.openide.util.NbPreferences;

/**
 *
 * @author Valery
 */
public  abstract class HessianStatisticsService implements StatisticsService {
    
    protected abstract Class getServiceClass();

    private StatisticsService getService() throws MalformedURLException {
        
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
        return (StatisticsService) factory.create(getServiceClass(), url);
    }

    @Override
    public QueryPage<InvoiceStatView> requestInvoice(QueryPage<InvoiceStatView> queryPage) {
        try {
            return getService().requestInvoice(queryPage);
        } catch (MalformedURLException ex) {
            Exceptions.printStackTrace(ex);
        } catch(HessianRuntimeException hre) {
            throwHesianTranslated(hre, "requestInvoice");
        }
        return null;
        
    }


    public void throwHesianTranslated(Exception e,String methodName) {
        RemoteConnectionException re = new RemoteConnectionException(e.getMessage());
        re.setOriginalClassName(e.getClass().getName());
        re.setEntityName("Statistics");
        re.setServiceName("HessianStatisticsService");
        re.setServiceMethodName(methodName);
        if ( re.getCause() != null ) {
            re.setCauseClassName(e.getCause().getClass().getName());
            re.setCauseMessage(e.getCause().getMessage());
        } else {
            re.setCauseMessage(e.getMessage());
        }
        
        throw re;
    }
    
}
