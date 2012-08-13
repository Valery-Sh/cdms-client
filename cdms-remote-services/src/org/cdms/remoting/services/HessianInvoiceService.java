/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cdms.remoting.services;

import com.caucho.hessian.client.HessianProxyFactory;
import com.caucho.hessian.client.HessianRuntimeException;
import java.math.BigDecimal;
import java.net.MalformedURLException;
import java.util.List;
import java.util.prefs.Preferences;
import org.cdms.entities.Invoice;
import org.cdms.entities.InvoiceItem;
import org.cdms.entities.User;
import org.cdms.remoting.ConfigService;
import org.cdms.remoting.InvoiceService;
import org.cdms.remoting.QueryPage;
import org.cdms.remoting.UserInfo;
import org.cdms.remoting.exception.RemoteConnectionException;
import org.openide.util.Exceptions;
import org.openide.util.Lookup;
import org.openide.util.NbPreferences;

/**
 *
 * @author V.Shyshkin
 */
public class HessianInvoiceService implements InvoiceService {

    @Override
    public Invoice findById(long l) {
        return null;
    }

    private InvoiceService getService() throws MalformedURLException {
        
        UserInfo info = ((ConfigService)Lookup.getDefault().lookup(ConfigService.class)).getConfig();
        HessianProxyFactory factory = new HessianProxyFactory();
        Preferences node = NbPreferences.root();
        String serverName = node.get("server.name", "localhost");
        int port = node.getInt("server.port", 8080);
        
        String url = "http://" + serverName + ":" + port + "/cdms-server/remoting/InvoiceService";
//            factory.setUser(userName);
//            factory.setPassword(password);
        factory.setUser(info.getUserName());
        factory.setPassword(info.getTicket()); // TODO in production
        
        return (InvoiceService) factory.create(InvoiceService.class, url);
    }
    
    
    @Override
    public QueryPage<Invoice> findByExample(QueryPage<Invoice> queryPage) {
        try {
             return getService().findByExample(queryPage);
             //convertPrice(p);
             //return p;
        } catch (MalformedURLException ex) {
            Exceptions.printStackTrace(ex);
        } catch(HessianRuntimeException hre) {
            throwHesianTranslated(hre, "findByExample");
        }
        return null;
        
    }
    
  /*  protected void convertPrice(QueryPage<Invoice> p) {
        List<Invoice> l = p.getQueryResult();
        for ( Invoice inv : l) {
            if ( inv != null ) {
                for ( InvoiceItem ii : inv.getInvoiceItems()){
                    if ( ii != null ) {
                        String s = ii.getProductItem().getStringPrice();
                        if ( s != null ) {
                            ii.getProductItem().setPrice(new BigDecimal(s));
                        }
                    }
                }
            }
        }
        
    }
    */ 
    public void throwHesianTranslated(Exception e,String methodName) {
        RemoteConnectionException re = new RemoteConnectionException(e.getMessage());
        re.setOriginalClassName(e.getClass().getName());
        re.setEntityName("Invoice");
        re.setServiceName("HessianInvoiceService");
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
    public Invoice insert(Invoice entity) {
        Invoice result = null;
        User u = new User();
        u.setId(10L);
        entity.setCreatedBy(u);
        try {
            result = getService().insert(entity);
        } catch (MalformedURLException ex) {
            Exceptions.printStackTrace(ex);
        } catch(HessianRuntimeException hre) {
            throwHesianTranslated(hre, "insert");
        }
        return result;

    }

    @Override
    public Invoice update(Invoice entity) {
        Invoice result = null;
        try {
            result = getService().update(entity);
        } catch (MalformedURLException ex) {
            Exceptions.printStackTrace(ex);
        } catch(HessianRuntimeException hre) {
            throwHesianTranslated(hre, "update");
        }
        return result;
    }

    @Override
    public Invoice delete(long id) {
        Invoice result = null;
        try {
            result = getService().delete(id);
        } catch (MalformedURLException ex) {
            Exceptions.printStackTrace(ex);
        } catch(HessianRuntimeException hre) {
            throwHesianTranslated(hre, "delete");
        }
        return result;

    }
    
}
