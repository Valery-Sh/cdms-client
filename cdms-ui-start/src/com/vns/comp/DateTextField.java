/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.vns.comp;

import com.qt.datapicker.DatePicker;
import java.util.Calendar;
import java.util.Observable;
import java.util.Observer;
import javax.swing.JFormattedTextField;

/**
 *
 * @author Valery
 */
public class DateTextField extends JFormattedTextField implements Observer{
    
    public DateTextField() {
        initComponents();
    }
    @Override
    public void update(Observable o, Object arg) {
        Calendar calendar = (Calendar) arg;
        DatePicker dp = (DatePicker) o;
        this.setValue(calendar.getTime());
        //System.out.println("picked=" + dp.formatDate(calendar));
        //setText(dp.formatDate(calendar));
    }
 @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">
    private void initComponents() {
        setBorder(null);
    }// </editor-fold>

    
}
