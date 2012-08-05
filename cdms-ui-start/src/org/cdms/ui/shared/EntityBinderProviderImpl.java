package org.cdms.ui.shared;

import javax.swing.JComponent;
import org.openide.util.lookup.ServiceProvider;

/**
 *
 * @author V. Shyshkin
 */
@ServiceProvider(service=EntityBinderProvider.class)
public class EntityBinderProviderImpl extends AbstractEntityBinderProvider {

    @Override
    public EntityBinder getInstance(JComponent source) {
        return new EntityBinderImpl(source);
    }
    
}
