package org.cdms.auth;

import org.openide.util.lookup.AbstractLookup;
import org.openide.util.lookup.InstanceContent;

/**
 *
 * @author V. Shyshkin
 */
public class UserLookup extends AbstractLookup {

    protected static UserLookup lookup = new UserLookup();
    protected InstanceContent content = new InstanceContent();

    public void set(Object instance) {
        content.remove(instance);
        content.add(instance);
    }

    public void remove(Object instance) {
        content.remove(instance);
    }

    public static UserLookup getDefault() {
        return lookup;
    }
}
