/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cdms.ui.customer;

import java.util.Date;
import java.util.List;
import javax.swing.Action;
import org.cdms.auth.UserLookup;
import org.cdms.connection.exception.RemoteConnectionException;
import org.cdms.entities.Customer;
import org.cdms.remoting.ConfigService;
import org.cdms.remoting.CustomerService;
import org.cdms.remoting.UserInfo;
import org.cdms.ui.shared.EntityBinder;
import org.cdms.ui.shared.EntityBinderImpl;
import org.cdms.ui.shared.TableBinder;
import org.jdesktop.beansbinding.BindingGroup;
import org.jdesktop.observablecollections.ObservableCollections;
import org.netbeans.api.progress.ProgressHandle;
import org.netbeans.api.progress.ProgressHandleFactory;
import org.netbeans.api.settings.ConvertAsProperties;
import org.openide.awt.Actions;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileUtil;
import org.openide.util.Lookup;
import org.openide.util.NbBundle;
import org.openide.util.NbBundle.Messages;
import org.openide.util.RequestProcessor;
import org.openide.util.Task;
import org.openide.util.TaskListener;
import org.openide.windows.TopComponent;

/**
 * Top component which displays something.
 */
@ConvertAsProperties(
    dtd = "-//org.cdms.ui.customer//customer//EN",
autostore = false)
@TopComponent.Description(
    preferredID = "customerTopComponent",
iconBase = "org/cdms/ui/customer/customers.png",
persistenceType = TopComponent.PERSISTENCE_ALWAYS)
@TopComponent.Registration(mode = "editor", openAtStartup = false)
@Messages({
    "CTL_customerAction=customer",
    "CTL_customerTopComponent=Customer Window",
    "FER_ConnectionRefused=Connection refused",
    "HINT_customerTopComponent=Opens a Customer window"
})
public final class CustomerTopComponent extends TopComponent {

    private EntityBinder filterBinder;
    private Customer customerFilter = new Customer();
    private BindingGroup filterBindingGroup = new BindingGroup();
    List<Customer> filterResult = null;
    CustomerAsyncService customerAsyncFilter = new CustomerAsyncService();

    public CustomerTopComponent() {
        initComponents();
        initFilterComponents();

        //hideCRUIDPanel();
        setName(Bundle.CTL_customerTopComponent());
        setToolTipText(Bundle.HINT_customerTopComponent());
    }

    protected void initFilterComponents() {
        filterBinder = new EntityBinderImpl(filterBindingGroup, this);
        filterBinder.addTextFieldBinder(jTextField_ID_Filter, "customerFilter.idFilter");
        filterBinder.addTextFieldBinder(jTextField_FirstName_Filter, "customerFilter.firstName");
        filterBinder.addTextFieldBinder(jTextField_LastName_Filter, "customerFilter.lastName");
        filterBinder.addTextFieldBinder(jTextField_Email_Filter, "customerFilter.email");
        filterBinder.addTextFieldBinder(jTextField_Phone_Filter, "customerFilter.phone");
        filterBinder.addCalendarBinder(dateField_createDate_From, "customerFilter.createdAt");
        filterBinder.addCalendarBinder(dateField_createDate_To, "customerFilter.createdAtEnd");
        filterBindingGroup.bind();
    }
    
    TableBinder tableBinder;

    protected void initTableComponents() {
        //userList = new ArrayList<User>();
        filterResult = ObservableCollections.observableList(
                filterResult);


        tableBinder = new TableBinder(jTable_Customer, filterResult);

        tableBinder.addColumn("id", Long.class);
        tableBinder.addColumn("version", Long.class);
        
        tableBinder.addColumn("firstName", String.class,"First Name");
        tableBinder.addColumn("lastName", String.class);
        tableBinder.addColumn("phone", String.class);
        tableBinder.addColumn("email", String.class);
        tableBinder.addColumn("createdAt", Date.class);
        //tableBinder.addColumn("createdBy", Date.class);
        
        tableBinder.bindTable();
        

        //tableBinder.addTextFieldBinder(jTextField1_LastName, "lastName");
        tableBinder.refresh();
        if ( ! filterResult.isEmpty()) {
            jTable_Customer.setRowSelectionInterval(0, 0);
        }
        tableBinder.updateMasterColumnModel();
    }

    protected void hideCRUIDPanel() {
        UserInfo info = UserLookup.getDefault().lookup(UserInfo.class);
        if (!info.inRole("edit")) {
            this.GRUIDPanel.setVisible(false);
        }
    }

    public Customer getCustomerFilter() {
        return customerFilter;
    }

    public void setCustomerFilter(Customer customerFilter) {
        this.customerFilter = customerFilter;
    }

    /**
     * TODO For testing only. Need to remove
     *
     * @param result
     */
    public void printResult(List<Customer> result) {
        if (result == null) {
            jLabel_PrintResult.setText("NULL result");
        } else if (result.isEmpty()) {
            jLabel_PrintResult.setText("Empty result");
        } else {
            Customer c = result.get(0);
            jLabel_PrintResult.setText(c.getFirstName() + " " + c.getLastName());
        }
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel_Filter = new javax.swing.JPanel();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        jTextField_ID_Filter = new javax.swing.JTextField();
        jTextField_FirstName_Filter = new javax.swing.JTextField();
        jTextField_LastName_Filter = new javax.swing.JTextField();
        jTextField_Email_Filter = new javax.swing.JTextField();
        jTextField_Phone_Filter = new javax.swing.JTextField();
        jButton_Search_ = new javax.swing.JButton();
        jButton_Clear_ = new javax.swing.JButton();
        dateField_createDate_From = new net.sf.nachocalendar.components.DateField();
        dateField_createDate_To = new net.sf.nachocalendar.components.DateField();
        jLabel7 = new javax.swing.JLabel();
        jLabel9 = new javax.swing.JLabel();
        jLabel_PrintResult = new javax.swing.JLabel();
        jLabel_FilterError = new javax.swing.JLabel();
        jPanel_Table = new javax.swing.JPanel();
        jPanel1 = new javax.swing.JPanel();
        jButton_FirstPage_ = new javax.swing.JButton();
        jButton_PriorPage_ = new javax.swing.JButton();
        jButton_NextPage = new javax.swing.JButton();
        jButton_LastPage = new javax.swing.JButton();
        jButton_Refresh_Table = new javax.swing.JButton();
        jLabel18 = new javax.swing.JLabel();
        jFormattedTextField_PageSize = new javax.swing.JFormattedTextField();
        jLabel_PageNo = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        jTable_Customer = new javax.swing.JTable();
        jPanel_Gruid = new javax.swing.JPanel();
        jLabel10 = new javax.swing.JLabel();
        jTextField_ID = new javax.swing.JTextField();
        jTextField_FirstName = new javax.swing.JTextField();
        jTextField_LastName = new javax.swing.JTextField();
        jTextField_Email = new javax.swing.JTextField();
        jTextField_Phone = new javax.swing.JTextField();
        jLabel11 = new javax.swing.JLabel();
        jLabel12 = new javax.swing.JLabel();
        jLabel13 = new javax.swing.JLabel();
        jLabel14 = new javax.swing.JLabel();
        jLabel15 = new javax.swing.JLabel();
        jTextField_CreatedBy = new javax.swing.JTextField();
        jTextField_CreatedBy1 = new javax.swing.JTextField();
        jLabel16 = new javax.swing.JLabel();
        GRUIDPanel = new javax.swing.JPanel();
        jButton_New_ = new javax.swing.JButton();
        jButton_Save_ = new javax.swing.JButton();
        jButton_Cancel = new javax.swing.JButton();
        jButton1 = new javax.swing.JButton();
        jButton_Find_Action = new javax.swing.JButton();
        jButton_FindByExam = new javax.swing.JButton();
        jButton3 = new javax.swing.JButton();
        jButton2 = new javax.swing.JButton();

        //bindingGroup1 = new BindingGroup();
        //org.jdesktop.beansbinding.Binding binding1 = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, this, org.jdesktop.beansbinding.ELProperty.create("${customerFilter.createdAtEnd}"), dateField_createDate_From, org.jdesktop.beansbinding.BeanProperty.create("value"));
        //bindingGroup.addBinding(binding1);

        //initFilterComponents();
        jPanel_Filter.setBorder(javax.swing.BorderFactory.createTitledBorder(org.openide.util.NbBundle.getMessage(CustomerTopComponent.class, "CustomerTopComponent.jPanel_Filter.border.title"))); // NOI18N

        org.openide.awt.Mnemonics.setLocalizedText(jLabel2, org.openide.util.NbBundle.getMessage(CustomerTopComponent.class, "CustomerTopComponent.jLabel2.text")); // NOI18N

        org.openide.awt.Mnemonics.setLocalizedText(jLabel3, org.openide.util.NbBundle.getMessage(CustomerTopComponent.class, "CustomerTopComponent.jLabel3.text")); // NOI18N

        org.openide.awt.Mnemonics.setLocalizedText(jLabel4, org.openide.util.NbBundle.getMessage(CustomerTopComponent.class, "CustomerTopComponent.jLabel4.text")); // NOI18N

        org.openide.awt.Mnemonics.setLocalizedText(jLabel5, org.openide.util.NbBundle.getMessage(CustomerTopComponent.class, "CustomerTopComponent.jLabel5.text")); // NOI18N

        org.openide.awt.Mnemonics.setLocalizedText(jLabel6, org.openide.util.NbBundle.getMessage(CustomerTopComponent.class, "CustomerTopComponent.jLabel6.text")); // NOI18N

        jTextField_ID_Filter.setText(org.openide.util.NbBundle.getMessage(CustomerTopComponent.class, "CustomerTopComponent.jTextField_ID_Filter.text")); // NOI18N

        jTextField_LastName_Filter.setText(org.openide.util.NbBundle.getMessage(CustomerTopComponent.class, "CustomerTopComponent.jTextField_LastName_Filter.text")); // NOI18N

        jTextField_Email_Filter.setText(org.openide.util.NbBundle.getMessage(CustomerTopComponent.class, "CustomerTopComponent.jTextField_Email_Filter.text")); // NOI18N

        jTextField_Phone_Filter.setText(org.openide.util.NbBundle.getMessage(CustomerTopComponent.class, "CustomerTopComponent.jTextField_Phone_Filter.text")); // NOI18N

        org.openide.awt.Mnemonics.setLocalizedText(jButton_Search_, org.openide.util.NbBundle.getMessage(CustomerTopComponent.class, "CustomerTopComponent.jButton_Search_.text")); // NOI18N
        jButton_Search_.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton_Search_ActionPerformed(evt);
            }
        });

        org.openide.awt.Mnemonics.setLocalizedText(jButton_Clear_, org.openide.util.NbBundle.getMessage(CustomerTopComponent.class, "CustomerTopComponent.jButton_Clear_.text")); // NOI18N
        jButton_Clear_.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton_Clear_ActionPerformed(evt);
            }
        });

        org.openide.awt.Mnemonics.setLocalizedText(jLabel7, org.openide.util.NbBundle.getMessage(CustomerTopComponent.class, "CustomerTopComponent.jLabel7.text")); // NOI18N

        org.openide.awt.Mnemonics.setLocalizedText(jLabel9, org.openide.util.NbBundle.getMessage(CustomerTopComponent.class, "CustomerTopComponent.jLabel9.text")); // NOI18N

        org.openide.awt.Mnemonics.setLocalizedText(jLabel_PrintResult, org.openide.util.NbBundle.getMessage(CustomerTopComponent.class, "CustomerTopComponent.jLabel_PrintResult.text")); // NOI18N

        jLabel_FilterError.setForeground(new java.awt.Color(255, 51, 0));
        org.openide.awt.Mnemonics.setLocalizedText(jLabel_FilterError, org.openide.util.NbBundle.getMessage(CustomerTopComponent.class, "CustomerTopComponent.jLabel_FilterError.text")); // NOI18N

        javax.swing.GroupLayout jPanel_FilterLayout = new javax.swing.GroupLayout(jPanel_Filter);
        jPanel_Filter.setLayout(jPanel_FilterLayout);
        jPanel_FilterLayout.setHorizontalGroup(
            jPanel_FilterLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel_FilterLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel_FilterLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel_FilterLayout.createSequentialGroup()
                        .addGroup(jPanel_FilterLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel2)
                            .addGroup(jPanel_FilterLayout.createSequentialGroup()
                                .addGroup(jPanel_FilterLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jLabel5)
                                    .addComponent(jLabel7))
                                .addGap(30, 30, 30)
                                .addGroup(jPanel_FilterLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                    .addComponent(jTextField_Email_Filter, javax.swing.GroupLayout.PREFERRED_SIZE, 350, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addGroup(jPanel_FilterLayout.createSequentialGroup()
                                        .addGroup(jPanel_FilterLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel_FilterLayout.createSequentialGroup()
                                                .addComponent(jTextField_ID_Filter, javax.swing.GroupLayout.PREFERRED_SIZE, 74, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                .addGap(18, 18, 18)
                                                .addComponent(jLabel3))
                                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel_FilterLayout.createSequentialGroup()
                                                .addComponent(dateField_createDate_From, javax.swing.GroupLayout.PREFERRED_SIZE, 121, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                .addGap(12, 12, 12)
                                                .addComponent(jLabel9)))
                                        .addGap(18, 18, 18)
                                        .addGroup(jPanel_FilterLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                            .addGroup(jPanel_FilterLayout.createSequentialGroup()
                                                .addComponent(dateField_createDate_To, javax.swing.GroupLayout.PREFERRED_SIZE, 102, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                                .addComponent(jLabel_PrintResult, javax.swing.GroupLayout.PREFERRED_SIZE, 58, javax.swing.GroupLayout.PREFERRED_SIZE))
                                            .addComponent(jTextField_FirstName_Filter, javax.swing.GroupLayout.PREFERRED_SIZE, 189, javax.swing.GroupLayout.PREFERRED_SIZE))))))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 30, Short.MAX_VALUE)
                        .addGroup(jPanel_FilterLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel4)
                            .addComponent(jLabel6))
                        .addGap(18, 18, 18)
                        .addGroup(jPanel_FilterLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jTextField_LastName_Filter, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 184, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jTextField_Phone_Filter, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 184, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addGroup(jPanel_FilterLayout.createSequentialGroup()
                        .addComponent(jButton_Search_)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jButton_Clear_, javax.swing.GroupLayout.PREFERRED_SIZE, 72, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jLabel_FilterError, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                .addContainerGap())
        );
        jPanel_FilterLayout.setVerticalGroup(
            jPanel_FilterLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel_FilterLayout.createSequentialGroup()
                .addGap(0, 9, Short.MAX_VALUE)
                .addGroup(jPanel_FilterLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(jPanel_FilterLayout.createSequentialGroup()
                        .addGroup(jPanel_FilterLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel_FilterLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                .addComponent(jTextField_ID_Filter, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(jLabel2)
                                .addComponent(jLabel3))
                            .addGroup(jPanel_FilterLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                .addComponent(jTextField_LastName_Filter, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(jLabel4)
                                .addComponent(jTextField_FirstName_Filter, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel_FilterLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jTextField_Phone_Filter, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGroup(jPanel_FilterLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                .addComponent(jLabel5)
                                .addComponent(jTextField_Email_Filter, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(jLabel6)))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(jPanel_FilterLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jLabel7)
                            .addGroup(jPanel_FilterLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                .addComponent(dateField_createDate_To, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(dateField_createDate_From, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addComponent(jLabel9)))
                    .addComponent(jLabel_PrintResult))
                .addGap(11, 11, 11)
                .addGroup(jPanel_FilterLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jButton_Search_)
                    .addComponent(jButton_Clear_)
                    .addComponent(jLabel_FilterError)))
        );

        jPanel_Table.setBorder(javax.swing.BorderFactory.createTitledBorder(org.openide.util.NbBundle.getMessage(CustomerTopComponent.class, "CustomerTopComponent.jPanel_Table.border.title"))); // NOI18N

        jPanel1.setBorder(javax.swing.BorderFactory.createTitledBorder(""));

        jButton_FirstPage_.setIcon(new javax.swing.ImageIcon(getClass().getResource("/org/cdms/ui/images/navigate_beginning.png"))); // NOI18N
        org.openide.awt.Mnemonics.setLocalizedText(jButton_FirstPage_, org.openide.util.NbBundle.getMessage(CustomerTopComponent.class, "CustomerTopComponent.jButton_FirstPage_.text")); // NOI18N
        jButton_FirstPage_.setToolTipText(org.openide.util.NbBundle.getMessage(CustomerTopComponent.class, "CustomerTopComponent.jButton_FirstPage_.toolTipText")); // NOI18N
        jButton_FirstPage_.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 1, 1, 1));
        jButton_FirstPage_.setContentAreaFilled(false);
        jButton_FirstPage_.setEnabled(false);
        jButton_FirstPage_.setMargin(new java.awt.Insets(0, 0, 0, 0));
        jButton_FirstPage_.setMaximumSize(new java.awt.Dimension(16, 16));
        jButton_FirstPage_.setMinimumSize(new java.awt.Dimension(16, 16));
        jButton_FirstPage_.setPreferredSize(new java.awt.Dimension(16, 16));

        jButton_PriorPage_.setIcon(new javax.swing.ImageIcon(getClass().getResource("/org/cdms/ui/images/navigate_left.png"))); // NOI18N
        org.openide.awt.Mnemonics.setLocalizedText(jButton_PriorPage_, org.openide.util.NbBundle.getMessage(CustomerTopComponent.class, "CustomerTopComponent.jButton_PriorPage_.text")); // NOI18N
        jButton_PriorPage_.setToolTipText(org.openide.util.NbBundle.getMessage(CustomerTopComponent.class, "CustomerTopComponent.jButton_PriorPage_.toolTipText")); // NOI18N
        jButton_PriorPage_.setBorder(null);
        jButton_PriorPage_.setContentAreaFilled(false);
        jButton_PriorPage_.setEnabled(false);
        jButton_PriorPage_.setMaximumSize(new java.awt.Dimension(16, 16));

        jButton_NextPage.setIcon(new javax.swing.ImageIcon(getClass().getResource("/org/cdms/ui/images/navigate_right.png"))); // NOI18N
        org.openide.awt.Mnemonics.setLocalizedText(jButton_NextPage, org.openide.util.NbBundle.getMessage(CustomerTopComponent.class, "CustomerTopComponent.jButton_NextPage.text")); // NOI18N
        jButton_NextPage.setToolTipText(org.openide.util.NbBundle.getMessage(CustomerTopComponent.class, "CustomerTopComponent.jButton_NextPage.toolTipText")); // NOI18N
        jButton_NextPage.setContentAreaFilled(false);
        jButton_NextPage.setEnabled(false);
        jButton_NextPage.setMaximumSize(new java.awt.Dimension(16, 16));
        jButton_NextPage.setMinimumSize(new java.awt.Dimension(16, 16));
        jButton_NextPage.setPreferredSize(new java.awt.Dimension(16, 16));

        jButton_LastPage.setIcon(new javax.swing.ImageIcon(getClass().getResource("/org/cdms/ui/images/navigate_end.png"))); // NOI18N
        org.openide.awt.Mnemonics.setLocalizedText(jButton_LastPage, org.openide.util.NbBundle.getMessage(CustomerTopComponent.class, "CustomerTopComponent.jButton_LastPage.text")); // NOI18N
        jButton_LastPage.setToolTipText(org.openide.util.NbBundle.getMessage(CustomerTopComponent.class, "CustomerTopComponent.jButton_LastPage.toolTipText")); // NOI18N
        jButton_LastPage.setContentAreaFilled(false);
        jButton_LastPage.setEnabled(false);
        jButton_LastPage.setMaximumSize(new java.awt.Dimension(16, 16));
        jButton_LastPage.setMinimumSize(new java.awt.Dimension(16, 16));
        jButton_LastPage.setPreferredSize(new java.awt.Dimension(16, 16));

        jButton_Refresh_Table.setIcon(new javax.swing.ImageIcon(getClass().getResource("/org/cdms/ui/images/refresh.png"))); // NOI18N
        org.openide.awt.Mnemonics.setLocalizedText(jButton_Refresh_Table, org.openide.util.NbBundle.getMessage(CustomerTopComponent.class, "CustomerTopComponent.jButton_Refresh_Table.text")); // NOI18N
        jButton_Refresh_Table.setToolTipText(org.openide.util.NbBundle.getMessage(CustomerTopComponent.class, "CustomerTopComponent.jButton_Refresh_Table.toolTipText")); // NOI18N
        jButton_Refresh_Table.setContentAreaFilled(false);
        jButton_Refresh_Table.setMaximumSize(new java.awt.Dimension(16, 16));
        jButton_Refresh_Table.setMinimumSize(new java.awt.Dimension(16, 16));
        jButton_Refresh_Table.setPreferredSize(new java.awt.Dimension(16, 16));

        org.openide.awt.Mnemonics.setLocalizedText(jLabel18, org.openide.util.NbBundle.getMessage(CustomerTopComponent.class, "CustomerTopComponent.jLabel18.text")); // NOI18N

        jFormattedTextField_PageSize.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.NumberFormatter(new java.text.DecimalFormat("######"))));
        jFormattedTextField_PageSize.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        jFormattedTextField_PageSize.setText(org.openide.util.NbBundle.getMessage(CustomerTopComponent.class, "CustomerTopComponent.jFormattedTextField_PageSize.text")); // NOI18N

        org.openide.awt.Mnemonics.setLocalizedText(jLabel_PageNo, org.openide.util.NbBundle.getMessage(CustomerTopComponent.class, "CustomerTopComponent.jLabel_PageNo.text")); // NOI18N

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addComponent(jButton_Refresh_Table, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jButton_FirstPage_, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jButton_PriorPage_, javax.swing.GroupLayout.PREFERRED_SIZE, 21, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jButton_NextPage, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jButton_LastPage, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(33, 33, 33)
                .addComponent(jLabel18)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jFormattedTextField_PageSize, javax.swing.GroupLayout.PREFERRED_SIZE, 36, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(jLabel_PageNo)
                .addGap(0, 0, Short.MAX_VALUE))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jFormattedTextField_PageSize)
                        .addComponent(jLabel18)
                        .addComponent(jLabel_PageNo))
                    .addComponent(jButton_LastPage, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jButton_NextPage, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jButton_PriorPage_, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jButton_FirstPage_, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jButton_Refresh_Table, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addGap(0, 0, Short.MAX_VALUE))
        );

        jTable_Customer.setAutoCreateRowSorter(true);
        jTable_Customer.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String [] {
                "Title 1", "Title 2", "Title 3", "Title 4"
            }
        ));
        jTable_Customer.setGridColor(new java.awt.Color(204, 204, 204));
        jScrollPane1.setViewportView(jTable_Customer);

        javax.swing.GroupLayout jPanel_TableLayout = new javax.swing.GroupLayout(jPanel_Table);
        jPanel_Table.setLayout(jPanel_TableLayout);
        jPanel_TableLayout.setHorizontalGroup(
            jPanel_TableLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 710, Short.MAX_VALUE)
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        jPanel_TableLayout.setVerticalGroup(
            jPanel_TableLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel_TableLayout.createSequentialGroup()
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 154, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        jPanel_Gruid.setBorder(javax.swing.BorderFactory.createTitledBorder(org.openide.util.NbBundle.getMessage(CustomerTopComponent.class, "CustomerTopComponent.jPanel_Gruid.border.title"))); // NOI18N

        org.openide.awt.Mnemonics.setLocalizedText(jLabel10, org.openide.util.NbBundle.getMessage(CustomerTopComponent.class, "CustomerTopComponent.jLabel10.text")); // NOI18N

        jTextField_ID.setEditable(false);
        jTextField_ID.setText(org.openide.util.NbBundle.getMessage(CustomerTopComponent.class, "CustomerTopComponent.jTextField_ID.text")); // NOI18N

        jTextField_FirstName.setText(org.openide.util.NbBundle.getMessage(CustomerTopComponent.class, "CustomerTopComponent.jTextField_FirstName.text")); // NOI18N

        jTextField_LastName.setText(org.openide.util.NbBundle.getMessage(CustomerTopComponent.class, "CustomerTopComponent.jTextField_LastName.text")); // NOI18N

        jTextField_Email.setText(org.openide.util.NbBundle.getMessage(CustomerTopComponent.class, "CustomerTopComponent.jTextField_Email.text")); // NOI18N

        jTextField_Phone.setText(org.openide.util.NbBundle.getMessage(CustomerTopComponent.class, "CustomerTopComponent.jTextField_Phone.text")); // NOI18N

        org.openide.awt.Mnemonics.setLocalizedText(jLabel11, org.openide.util.NbBundle.getMessage(CustomerTopComponent.class, "CustomerTopComponent.jLabel11.text")); // NOI18N

        org.openide.awt.Mnemonics.setLocalizedText(jLabel12, org.openide.util.NbBundle.getMessage(CustomerTopComponent.class, "CustomerTopComponent.jLabel12.text")); // NOI18N

        org.openide.awt.Mnemonics.setLocalizedText(jLabel13, org.openide.util.NbBundle.getMessage(CustomerTopComponent.class, "CustomerTopComponent.jLabel13.text")); // NOI18N

        org.openide.awt.Mnemonics.setLocalizedText(jLabel14, org.openide.util.NbBundle.getMessage(CustomerTopComponent.class, "CustomerTopComponent.jLabel14.text")); // NOI18N

        org.openide.awt.Mnemonics.setLocalizedText(jLabel15, org.openide.util.NbBundle.getMessage(CustomerTopComponent.class, "CustomerTopComponent.jLabel15.text")); // NOI18N

        jTextField_CreatedBy.setEditable(false);
        jTextField_CreatedBy.setText(org.openide.util.NbBundle.getMessage(CustomerTopComponent.class, "CustomerTopComponent.jTextField_CreatedBy.text")); // NOI18N

        jTextField_CreatedBy1.setEditable(false);
        jTextField_CreatedBy1.setText(org.openide.util.NbBundle.getMessage(CustomerTopComponent.class, "CustomerTopComponent.jTextField_CreatedBy1.text")); // NOI18N

        org.openide.awt.Mnemonics.setLocalizedText(jLabel16, org.openide.util.NbBundle.getMessage(CustomerTopComponent.class, "CustomerTopComponent.jLabel16.text")); // NOI18N

        javax.swing.GroupLayout jPanel_GruidLayout = new javax.swing.GroupLayout(jPanel_Gruid);
        jPanel_Gruid.setLayout(jPanel_GruidLayout);
        jPanel_GruidLayout.setHorizontalGroup(
            jPanel_GruidLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel_GruidLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel_GruidLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel13)
                    .addComponent(jLabel10)
                    .addComponent(jLabel15))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel_GruidLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addGroup(jPanel_GruidLayout.createSequentialGroup()
                        .addComponent(jTextField_ID, javax.swing.GroupLayout.PREFERRED_SIZE, 76, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 30, Short.MAX_VALUE)
                        .addComponent(jLabel14)
                        .addGap(17, 17, 17)
                        .addComponent(jTextField_FirstName, javax.swing.GroupLayout.PREFERRED_SIZE, 192, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel_GruidLayout.createSequentialGroup()
                        .addComponent(jTextField_CreatedBy, javax.swing.GroupLayout.PREFERRED_SIZE, 179, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jLabel16, javax.swing.GroupLayout.PREFERRED_SIZE, 54, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 6, Short.MAX_VALUE)
                        .addComponent(jTextField_CreatedBy1, javax.swing.GroupLayout.PREFERRED_SIZE, 117, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(jTextField_Email))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 25, Short.MAX_VALUE)
                .addGroup(jPanel_GruidLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel11)
                    .addComponent(jLabel12))
                .addGap(18, 18, 18)
                .addGroup(jPanel_GruidLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jTextField_LastName, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 170, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jTextField_Phone, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 170, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap())
        );
        jPanel_GruidLayout.setVerticalGroup(
            jPanel_GruidLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel_GruidLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel_GruidLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel10)
                    .addComponent(jTextField_ID, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel14)
                    .addComponent(jLabel11)
                    .addComponent(jTextField_LastName, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jTextField_FirstName, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel_GruidLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jTextField_Email, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel12)
                    .addComponent(jTextField_Phone, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel15))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel_GruidLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel13)
                    .addComponent(jTextField_CreatedBy, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jTextField_CreatedBy1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel16))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        org.openide.awt.Mnemonics.setLocalizedText(jButton_New_, org.openide.util.NbBundle.getMessage(CustomerTopComponent.class, "CustomerTopComponent.jButton_New_.text")); // NOI18N

        org.openide.awt.Mnemonics.setLocalizedText(jButton_Save_, org.openide.util.NbBundle.getMessage(CustomerTopComponent.class, "CustomerTopComponent.jButton_Save_.text")); // NOI18N

        org.openide.awt.Mnemonics.setLocalizedText(jButton_Cancel, org.openide.util.NbBundle.getMessage(CustomerTopComponent.class, "CustomerTopComponent.jButton_Cancel.text")); // NOI18N

        org.openide.awt.Mnemonics.setLocalizedText(jButton1, org.openide.util.NbBundle.getMessage(CustomerTopComponent.class, "CustomerTopComponent.jButton1.text")); // NOI18N
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });

        org.openide.awt.Mnemonics.setLocalizedText(jButton_Find_Action, org.openide.util.NbBundle.getMessage(CustomerTopComponent.class, "CustomerTopComponent.jButton_Find_Action.text")); // NOI18N
        jButton_Find_Action.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton_Find_ActionActionPerformed(evt);
            }
        });

        org.openide.awt.Mnemonics.setLocalizedText(jButton_FindByExam, org.openide.util.NbBundle.getMessage(CustomerTopComponent.class, "CustomerTopComponent.jButton_FindByExam.text")); // NOI18N

        org.openide.awt.Mnemonics.setLocalizedText(jButton3, org.openide.util.NbBundle.getMessage(CustomerTopComponent.class, "CustomerTopComponent.jButton3.text")); // NOI18N
        jButton3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton3ActionPerformed(evt);
            }
        });

        org.openide.awt.Mnemonics.setLocalizedText(jButton2, org.openide.util.NbBundle.getMessage(CustomerTopComponent.class, "CustomerTopComponent.jButton2.text")); // NOI18N
        jButton2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton2ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout GRUIDPanelLayout = new javax.swing.GroupLayout(GRUIDPanel);
        GRUIDPanel.setLayout(GRUIDPanelLayout);
        GRUIDPanelLayout.setHorizontalGroup(
            GRUIDPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(GRUIDPanelLayout.createSequentialGroup()
                .addComponent(jButton_New_)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jButton_Save_)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jButton_Cancel)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jButton1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jButton_Find_Action)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jButton_FindByExam)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jButton3)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jButton2)
                .addGap(0, 0, Short.MAX_VALUE))
        );
        GRUIDPanelLayout.setVerticalGroup(
            GRUIDPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, GRUIDPanelLayout.createSequentialGroup()
                .addGap(0, 0, Short.MAX_VALUE)
                .addGroup(GRUIDPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jButton_New_)
                    .addComponent(jButton_Save_)
                    .addComponent(jButton_Cancel)
                    .addComponent(jButton1)
                    .addComponent(jButton_Find_Action)
                    .addComponent(jButton_FindByExam)
                    .addComponent(jButton3)
                    .addComponent(jButton2)))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel_Table, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(GRUIDPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel_Filter, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel_Gruid, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel_Filter, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel_Table, javax.swing.GroupLayout.PREFERRED_SIZE, 209, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel_Gruid, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(GRUIDPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        //initFilterComponents();
        //filterBindingGroup.bind();
    }// </editor-fold>//GEN-END:initComponents

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        UserInfo info = UserLookup.getDefault().lookup(UserInfo.class);
//        jLabel1.setText(info.getFirstName() + " " + info.getLastName() + " " + info.getUserName());
        UserInfo ui = ((ConfigService) Lookup.getDefault().lookup(ConfigService.class)).getConfig();


    }//GEN-LAST:event_jButton1ActionPerformed
    /**
     * TODO Nead to remove
     *
     * @param evt
     */
    private void jButton2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton2ActionPerformed
        for (FileObject fo : FileUtil.getConfigFile("Actions/Applications").getChildren()) {
            Action action = FileUtil.getConfigObject(fo.getPath(), Action.class);
            System.out.println("ACTION: " + action.toString() + "; CLASS=" + action.getClass().getName());
            if (action.getClass().getSimpleName().equals("OpenInvoiceAction")) {
                //jButton3.setAction(action); 
                System.out.println("INVOICE ACTION: " + action.toString());
                Actions.connect(jButton3, action);


            }
            //button.setPreferredSize(new Dimension(150,100));
            //add(button);
            //org.netbeans.modules.db.dataview.api.DataView dv;           
        }
    }//GEN-LAST:event_jButton2ActionPerformed

    private void jButton3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton3ActionPerformed
    }//GEN-LAST:event_jButton3ActionPerformed
    private void jButton_Find_ActionActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton_Find_ActionActionPerformed
    }//GEN-LAST:event_jButton_Find_ActionActionPerformed

    private void jButton_Search_ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton_Search_ActionPerformed
        jLabel_FilterError.setText("");
        customerAsyncFilter = new CustomerAsyncService();
        System.out.println("FILTER ID=" + customerFilter.getId()
                + "FirstName=" + customerFilter.getFirstName());

        try {
            customerAsyncFilter.findByExample(new FilterSeachHandler(), customerFilter, 0, 50); // TODO paging
        } catch (Exception e) {
            System.out.println("ERROR");
        }
        System.out.println("RRRRRRRRRRRRRRRRRRRRRRRRRRRRR");


    }//GEN-LAST:event_jButton_Search_ActionPerformed

    private void jButton_Clear_ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton_Clear_ActionPerformed
        jLabel_FilterError.setText("");
        jTextField_Email_Filter.setText("");
        jTextField_FirstName_Filter.setText("");
        jTextField_ID_Filter.setText("");
        jTextField_LastName_Filter.setText("");
        jTextField_Phone_Filter.setText("");
        dateField_createDate_From.setValue(null);
        dateField_createDate_To.setValue(null);
    }//GEN-LAST:event_jButton_Clear_ActionPerformed
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPanel GRUIDPanel;
    private net.sf.nachocalendar.components.DateField dateField_createDate_From;
    private net.sf.nachocalendar.components.DateField dateField_createDate_To;
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton2;
    private javax.swing.JButton jButton3;
    private javax.swing.JButton jButton_Cancel;
    private javax.swing.JButton jButton_Clear_;
    private javax.swing.JButton jButton_FindByExam;
    private javax.swing.JButton jButton_Find_Action;
    private javax.swing.JButton jButton_FirstPage_;
    private javax.swing.JButton jButton_LastPage;
    private javax.swing.JButton jButton_New_;
    private javax.swing.JButton jButton_NextPage;
    private javax.swing.JButton jButton_PriorPage_;
    private javax.swing.JButton jButton_Refresh_Table;
    private javax.swing.JButton jButton_Save_;
    private javax.swing.JButton jButton_Search_;
    private javax.swing.JFormattedTextField jFormattedTextField_PageSize;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel14;
    private javax.swing.JLabel jLabel15;
    private javax.swing.JLabel jLabel16;
    private javax.swing.JLabel jLabel18;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JLabel jLabel_FilterError;
    private javax.swing.JLabel jLabel_PageNo;
    private javax.swing.JLabel jLabel_PrintResult;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel_Filter;
    private javax.swing.JPanel jPanel_Gruid;
    private javax.swing.JPanel jPanel_Table;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTable jTable_Customer;
    private javax.swing.JTextField jTextField_CreatedBy;
    private javax.swing.JTextField jTextField_CreatedBy1;
    private javax.swing.JTextField jTextField_Email;
    private javax.swing.JTextField jTextField_Email_Filter;
    private javax.swing.JTextField jTextField_FirstName;
    private javax.swing.JTextField jTextField_FirstName_Filter;
    private javax.swing.JTextField jTextField_ID;
    private javax.swing.JTextField jTextField_ID_Filter;
    private javax.swing.JTextField jTextField_LastName;
    private javax.swing.JTextField jTextField_LastName_Filter;
    private javax.swing.JTextField jTextField_Phone;
    private javax.swing.JTextField jTextField_Phone_Filter;
    // End of variables declaration//GEN-END:variables

    @Override
    public void componentOpened() {
        // TODO add custom code on component opening
    }

    @Override
    public void componentClosed() {
        // TODO add custom code on component closing
    }

    void writeProperties(java.util.Properties p) {
        // better to version settings since initial version as advocated at
        // http://wiki.apidesign.org/wiki/PropertyFiles
        p.setProperty("version", "1.0");
        // TODO store your settings
    }

    void readProperties(java.util.Properties p) {
        String version = p.getProperty("version");

        // TODO read your settings according to their version
    }

    protected class FilterSeachHandler implements TaskListener {

        @Override
        public void taskFinished(Task task) {
            if (customerAsyncFilter.getResult() instanceof Exception) {
                Exception e = (Exception) customerAsyncFilter.getResult();
                //translate(e);
                if (e instanceof RemoteConnectionException) {
                    jLabel_FilterError.setText(NbBundle.getMessage(CustomerTopComponent.class, "CustomerTopComponent.jLabel_FilterError.msg.connectionrefused"));
                } else {
                    jLabel_FilterError.setText(e.getMessage());
                }

            } else {
                printResult((List<Customer>) customerAsyncFilter.getResult());
                filterResult = (List<Customer>) customerAsyncFilter.getResult();
                initTableComponents();
            }
        }

        /**
         * ClassName = com.caucho.hessian.client.HessianRuntimeException --- msg
         * = java.net.ConnectException: Connection refused: connect Cause
         * ClassName = java.net.ConnectException --- cause msg=Connection
         * refused: connect
         *
         * @param e
         */
        private void translate(Exception e) {
            System.out.println("ClassName = " + e.getClass().getName());
            System.out.println(" --- msg = " + e.getMessage());

            System.out.println("Cause ClassName = " + e.getCause().getClass().getName());
            System.out.println(" --- cause msg=" + e.getCause().getMessage());

        }
    }
}
