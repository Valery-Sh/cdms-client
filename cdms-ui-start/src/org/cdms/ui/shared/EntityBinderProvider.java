package org.cdms.ui.shared;

import javax.swing.JComponent;

/**
 *
 * @author V. Shyshkin
 */
public interface EntityBinderProvider {
    EntityBinder getInstance(JComponent source);
}
