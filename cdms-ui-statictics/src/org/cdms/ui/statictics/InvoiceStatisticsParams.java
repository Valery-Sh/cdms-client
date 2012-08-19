package org.cdms.ui.statictics;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.Date;


/**
 * Used by Statistics module to keep parameters to statistics request 
 * methods.
 * @author V. Shyshkin
 */
public class InvoiceStatisticsParams {
    
    private Long selectedEntityId;
    
    protected boolean onlySelectedEntity;
    private int selectedRow;
    
    private Date startDate;
    private Date endDate;
    
    public InvoiceStatisticsParams() {
        selectedRow = -1;
    }

    public InvoiceStatisticsParams(Long id, Boolean idSelected, Date startDate, Date endDate) {
        this.selectedEntityId = id;
        this.onlySelectedEntity = idSelected;
        this.startDate = startDate;
        this.endDate = endDate;
    }

    private transient PropertyChangeSupport changeSupport;

    public void addPropertyChangeListener(PropertyChangeListener listener) {
        if ( changeSupport == null ) {
            changeSupport = new PropertyChangeSupport(this);
        }
        changeSupport.addPropertyChangeListener(listener);
    }
    
    public void removePropertyChangeListener(PropertyChangeListener listener) {
        if ( changeSupport == null ) {
            return;
        }
        changeSupport.removePropertyChangeListener(listener);
    }    
    protected void fire(String propertyName,Object oldValue, Object newValue) {
        if ( changeSupport == null ) {
            return;
        }
        changeSupport.firePropertyChange(propertyName, oldValue, newValue);

    }

    public Long getSelectedEntityId() {
        return selectedEntityId;
    }

    public void setSelectedEntityId(Long selectedEntityId) {
        Long oldValue = this.selectedEntityId;
        this.selectedEntityId = selectedEntityId;
        fire("selectedEntityId",oldValue,this.selectedEntityId);
    }

    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        Date oldValue = this.startDate;
        this.startDate = startDate;
        fire("startDate",oldValue,this.startDate);
    }

    public Date getEndDate() {
        return endDate;
    }

    public void setEndDate(Date endDate) {
        Date oldValue = this.endDate;
        this.endDate = endDate;
        fire("endDate",oldValue,this.endDate);
    }

    public boolean isOnlySelectedEntity() {
        return onlySelectedEntity;
    }

    public void setOnlySelectedEntity(boolean onlySelectedEntity) {
        boolean oldValue = this.onlySelectedEntity;
        this.onlySelectedEntity = onlySelectedEntity;
        fire("onlySelectedEntity",oldValue,this.onlySelectedEntity);
    }

    public int getSelectedRow() {
        return selectedRow;
    }

    public void setSelectedRow(int selectedRow) {
        int oldValue = this.selectedRow;
        this.selectedRow = selectedRow;
        fire("selectedRow",oldValue,this.selectedRow);
    }
}
