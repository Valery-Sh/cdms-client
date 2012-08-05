/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cdms.remoting.services;

import java.util.List;
import org.cdms.entities.Customer;
import org.cdms.remoting.CustomerService;

/**
 *
 * @author Valery
 */
public class HessianCustomerService implements CustomerService {

    @Override
    public Customer findById(long l) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public List<Customer> findByExample(Customer cstmr, int i, int i1) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void insert(Customer cstmr) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void update(Customer cstmr) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void delete(long l) {
        throw new UnsupportedOperationException("Not supported yet.");
    }
    
}
