package org.cdms.ui.shared;

import javax.swing.JComponent;
import org.openide.util.Lookup;

    
/**
 *
 * @author V. Shyshkin
 */
public abstract class AbstractEntityBinderProvider implements EntityBinderProvider{
    
    @Override
    public abstract EntityBinder getInstance(JComponent source);
    
    public static AbstractEntityBinderProvider getDefault() {

        AbstractEntityBinderProvider services =
                Lookup.getDefault().lookup(AbstractEntityBinderProvider.class);
        if (services == null) {
            services = new DefaultEntityBinderProvider();
        }
        return services;
    }

    protected static class DefaultEntityBinderProvider extends AbstractEntityBinderProvider {

        @Override
        public EntityBinder getInstance(JComponent source) {
            return new EntityBinderImpl(source);
        }
    }

}
