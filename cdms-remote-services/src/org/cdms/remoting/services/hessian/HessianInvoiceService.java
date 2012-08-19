package org.cdms.remoting.services.hessian;

import org.cdms.entities.Invoice;
import org.cdms.remoting.InvoiceService;

/**
 * Implements the <code>InvoiceService</code> interface to access it's remote
 * methods trough <code>Hessian Remote Protocol</code>.
 * Actually the class extends <code>HessianEntityService</code> and overrides 
 * a single method {@link #getServiceClass() }
 * 
 * @see org.cdms.remoting.InvoiceService
 * @see HessianEntityService
 * @author V. Shyshkin
 */
public class HessianInvoiceService <E extends Invoice> extends HessianEntityService<E>
        implements InvoiceService<E> {
    /**
     * @return the type <code>InvoiceService.class</code> of the service class 
     *    who is responsible for the maintenance of the entity of 
     *    type {@link org.cdms.entities.Invoice }
     * @see org.cdms.remoting.InvoiceService   
     */
    @Override
    protected Class getServiceClass() {
        return InvoiceService.class;
    }

}
