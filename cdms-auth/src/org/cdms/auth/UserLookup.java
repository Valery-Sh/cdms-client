package org.cdms.auth;

import org.openide.util.lookup.AbstractLookup;
import org.openide.util.lookup.InstanceContent;

/**
 * Convenient class that is used internally to handle authentication info.
 * Provided the static method {@link #getDefault() ) to get the instance 
 * of the class {@link org.cdms.remoting.UserInfo }
 * @author V. Shyshkin
 */
class UserLookup extends AbstractLookup {

    protected final static InstanceContent content = new InstanceContent();
    protected static UserLookup lookup = null;
    
    public UserLookup(InstanceContent c) {
        super(c);
    }
    public void set(Object instance) {
        content.remove(instance);
        
        content.add(instance);
        
    }

    public void remove(Object instance) {
        content.remove(instance);
    }
    /**
     * @return the instance of {@link org.cdms.remoting.UserInfo }
     */
    public static UserLookup getDefault() {
        
        if ( lookup == null ) {
             lookup = new UserLookup(content);
        }
        return lookup;
    }
    
}
