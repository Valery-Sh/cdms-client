/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cdms.ui.common.dialog;

/**
 *
 * @author Valery
 */
public class ErrorOtherDetailsPane extends javax.swing.JPanel {
    Exception exception;
    /**
     * Creates new form ErrorOtherDetailsPane
     */
    public ErrorOtherDetailsPane() {
        initComponents();
    }

    public void setException(Exception exception) {
        this.exception = exception;
        if ( exception == null ) {
            return;
        }
/*        String identifier = exception.getIdentifier() != null ? exception.getIdentifier().toString() : "";
        jLabel_Identifier.setText(identifier);
        jLabel_EntityName.setText(exception.getEntityName());
        jLabel_EntityClass.setText(exception.getPersistentClassName());
        jLabel_ExceptionClass.setText(exception.getOriginalClassName());
        jLabel_Message.setText(exception.getMessage());
*/        
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 400, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 300, Short.MAX_VALUE)
        );
    }// </editor-fold>//GEN-END:initComponents
    // Variables declaration - do not modify//GEN-BEGIN:variables
    // End of variables declaration//GEN-END:variables
}
