package org.cdms.auth;

import org.openide.util.Lookup;
import org.openide.util.lookup.AbstractLookup;
import org.openide.util.lookup.InstanceContent;

/**
 *
 * @author V. Shyshkin
 */
public class UserLookup extends AbstractLookup {

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

    public static UserLookup getDefault() {
        
        if ( lookup == null ) {
             lookup = new UserLookup(content);
        }
        return lookup;
    }
    
}
