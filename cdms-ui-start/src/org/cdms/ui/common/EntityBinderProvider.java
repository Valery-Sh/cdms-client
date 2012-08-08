package org.cdms.ui.common;

import javax.swing.JComponent;

/**
 *
 * @author V. Shyshkin
 */
public interface EntityBinderProvider {
    EntityBinder getInstance(JComponent source);
}
