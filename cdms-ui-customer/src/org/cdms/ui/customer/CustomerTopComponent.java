package org.cdms.ui.customer;

import java.awt.EventQueue;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.swing.JTextField;
import javax.swing.text.DateFormatter;
import javax.swing.text.DefaultFormatterFactory;
import org.cdms.auth.UserLookup;
import org.cdms.entities.Customer;
import org.cdms.entities.User;
import org.cdms.remoting.QueryPage;
import org.cdms.remoting.UserInfo;
import org.cdms.ui.common.EntityBinder;
import org.cdms.ui.common.EntityBinderImpl;
import org.cdms.ui.common.ErrorMessageBuilder;
import org.cdms.ui.common.TableBinder;
import org.cdms.ui.common.dialog.DeleteConfirmDialog;
import org.cdms.ui.common.dialog.ErrorDetailsHandler;
import org.jdesktop.beansbinding.BindingGroup;
import org.jdesktop.observablecollections.ObservableCollections;
import org.netbeans.api.settings.ConvertAsProperties;
import org.openide.util.NbBundle;
import org.openide.util.NbBundle.Messages;
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

    private EntityBinder entityFilterBinder;
    private Customer entityAsFilter = new Customer();
    private EntityBinder userFilterBinder;
    private User userAsFilter = new User();
    private BindingGroup entityBindingGroup = new BindingGroup();
    private BindingGroup userBindingGroup = new BindingGroup();
    private TableBinder tableBinder;

    List<Customer> filterResult = null;
    CustomerAsyncService entityAsyncFilter = new CustomerAsyncService();
    CustomerAsyncService entityAsyncSave = new CustomerAsyncService();
    CustomerAsyncService entityAsyncDelete = new CustomerAsyncService();
    // For Insert operations
    Customer entityToInsert;
    EntityBinder entityToInsertBinder;
    BindingGroup entityToInsertBindingGroup;
    CustomerAsyncService entityAsyncInsert = new CustomerAsyncService();
    private QueryPage<Customer> queryPage;

    public CustomerTopComponent() {
        initComponents();

        initFilterComponents();
        showGruidErrors(false);
        queryPage = new QueryPage<Customer>();

        initPageNavigator();

        //hideCRUIDPanel();
        setName(Bundle.CTL_customerTopComponent());
        setToolTipText(Bundle.HINT_customerTopComponent());

    }

    protected void initNewCustomerComponents() {
        entityToInsertBindingGroup = new BindingGroup();
        entityToInsert = new Customer();
        entityToInsertBinder = new EntityBinderImpl(entityToInsertBindingGroup, this);
        entityToInsertBinder.addTextFieldBinder(jTextField_New_ID, "newCustomer.id");

        entityToInsertBinder.addTextFieldBinder(jTextField_New_FirstName, "newCustomer.firstName");
        entityToInsertBinder.addTextFieldBinder(jTextField_New_LastName, "newCustomer.lastName");
        entityToInsertBinder.addTextFieldBinder(jTextField_New_Email, "newCustomer.email");
        entityToInsertBinder.addTextFieldBinder(jTextField_New_Phone, "newCustomer.phone");

        entityToInsertBinder.addFormattedTextFieldBinder(jFormattedTextField_New_CreatedAt, "newCustomer.createdAt");

        entityToInsertBinder.addConcatTextFieldBinder(jTextField_New_CreatedBy, "newCustomer.createdBy.firstName", "newCustomer.createdBy.lastName");

        entityToInsertBindingGroup.bind();

    }

    protected void initPageNavigator() {
        int pageSize = Integer.parseInt(jFormattedTextField_PageSize.getText());
        queryPage.setPageSize(pageSize);

        jTextField_PageNo.setText("" + queryPage.getPageNo());
        jTextField_RowCount.setText("" + queryPage.getRowCount());


        if (queryPage.getRowCount() == 0) {
            jButton_FirstPage_.setEnabled(false);
            jButton_LastPage_.setEnabled(false);
            jButton_NextPage_.setEnabled(false);
            jButton_PriorPage_.setEnabled(false);
            return;
        }


        int lastPage = (int) (queryPage.getRowCount() / queryPage.getPageSize() - 1);
        if (queryPage.getRowCount() % queryPage.getPageSize() != 0) {
            lastPage++;
        }
        if (queryPage.getPageNo() == lastPage) {
            jButton_LastPage_.setEnabled(false);
            jButton_NextPage_.setEnabled(false);
            jButton_FirstPage_.setEnabled(true);
            jButton_PriorPage_.setEnabled(true);
        } else if (queryPage.getPageNo() == 0) {
            jButton_LastPage_.setEnabled(true);
            jButton_NextPage_.setEnabled(true);
            jButton_FirstPage_.setEnabled(false);
            jButton_PriorPage_.setEnabled(false);
        } else {
            jButton_LastPage_.setEnabled(true);
            jButton_NextPage_.setEnabled(true);
            jButton_FirstPage_.setEnabled(true);
            jButton_PriorPage_.setEnabled(true);
        }
    }

    protected void initFilterComponents() {
        entityFilterBinder = new EntityBinderImpl(entityBindingGroup, this);
        entityFilterBinder.addTextFieldBinder(jTextField_ID_Filter, "customerAsFilter.idFilter");
        entityFilterBinder.addTextFieldBinder(jTextField_FirstName_Filter, "customerAsFilter.firstName");
        entityFilterBinder.addTextFieldBinder(jTextField_LastName_Filter, "customerAsFilter.lastName");
        entityFilterBinder.addTextFieldBinder(jTextField_Email_Filter, "customerAsFilter.email");
        entityFilterBinder.addTextFieldBinder(jTextField_Phone_Filter, "customerAsFilter.phone");
        entityFilterBinder.addCalendarBinder(dateField_createDate_From, "customerAsFilter.createdAt");
        entityFilterBinder.addCalendarBinder(dateField_createDate_To, "customerAsFilter.createdAtEnd");

        entityAsFilter.setCreatedBy(userAsFilter);

        userFilterBinder = new EntityBinderImpl(userBindingGroup, this);
        userFilterBinder.addTextFieldBinder(jTextField_User_firstName, "userAsFilter.firstName");
        userFilterBinder.addTextFieldBinder(jTextField_User_lastName, "userAsFilter.lastName");

        entityBindingGroup.bind();
        userBindingGroup.bind();

        dateField_createDate_From.getFormattedTextField().setHorizontalAlignment(JTextField.CENTER);
        dateField_createDate_From.getFormattedTextField()
                .setFormatterFactory(
                new DefaultFormatterFactory(
                new DateFormatter(DateFormat.getDateInstance(DateFormat.MEDIUM))));

        dateField_createDate_To.getFormattedTextField().setHorizontalAlignment(JTextField.CENTER);
        dateField_createDate_To.getFormattedTextField()
                .setFormatterFactory(
                new DefaultFormatterFactory(
                new DateFormatter(DateFormat.getDateInstance(DateFormat.MEDIUM))));

    }

    protected void showGruidErrors(boolean visible) {
        jLabel_Cruid_Errors.setVisible(visible);
        jButton_Gruid_Errors_Details.setVisible(visible);

    }

    protected void emptyEditComponents() {
        jTextField_ID.setText(null);
        jTextField_Email.setText(null);
        jTextField_FirstName.setText(null);
        jTextField_LastName.setText(null);
        jTextField_Phone.setText(null);
        jFormattedTextField_CreatedAt.setValue(null);
        jTextField_CreatedBy.setText(null);
    }

    protected void initTableComponents() {
        //userList = new ArrayList<User>();
        //jTable_Customer.clearSelection();
        if (tableBinder != null) {
            tableBinder.getBindingGroup().unbind();
        }
        //jTable_Customer.setModel(new DefaultTableModel());
        filterResult = ObservableCollections.observableList(
                filterResult);



        tableBinder = new TableBinder(jTable_Customer, filterResult);

        tableBinder.addColumn("id", Long.class);
        tableBinder.addColumn("version", Long.class);

        tableBinder.addColumn("firstName", String.class, "First Name");
        tableBinder.addColumn("lastName", String.class);
        tableBinder.addColumn("phone", String.class);
        tableBinder.addColumn("email", String.class);
        tableBinder.addColumn("createdAt", Date.class);
        tableBinder.addColumn("createdBy.firstName", String.class);
        tableBinder.addColumn("createdBy.lastName", String.class);


        tableBinder.bindTable();

        tableBinder.addTextFieldBinder(jTextField_ID, "id");
        tableBinder.addTextFieldBinder(jTextField_Email, "email");
        tableBinder.addTextFieldBinder(jTextField_FirstName, "firstName");
        tableBinder.addTextFieldBinder(jTextField_LastName, "lastName");
        tableBinder.addTextFieldBinder(jTextField_Phone, "phone");
        tableBinder.addFormattedTextFieldBinder(jFormattedTextField_CreatedAt, "createdAt");
        tableBinder.addConcatTextFieldBinder(jTextField_CreatedBy, "createdBy.firstName", "createdBy.lastName");



        //tableBinder.addTextFieldBinder(jTextField1_LastName, "lastName");
        tableBinder.refresh();
        if (!filterResult.isEmpty()) {
            jTable_Customer.setRowSelectionInterval(0, 0);
        }
        tableBinder.updateMasterColumnModel();
    }

    protected void hideCRUIDPanel() {
        UserInfo info = UserLookup.getDefault().lookup(UserInfo.class);
        if (!info.inRole("edit")) {
            //this.JPanel_CruidOp.setVisible(false);
        }
    }

    public Customer getNewCustomer() {
        return entityToInsert;
    }

    public void setNewCustomer(Customer newCustomer) {
        this.entityToInsert = newCustomer;
    }

    public Customer getCustomerAsFilter() {
        return entityAsFilter;
    }

    public void setCustomerAsFilter(Customer customerFilter) {
        this.entityAsFilter = customerFilter;
    }

    public User getUserAsFilter() {
        return userAsFilter;
    }

    public void setUserAsFilter(User userAsFilter) {
        this.userAsFilter = userAsFilter;
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
        jLabel_FilterError = new javax.swing.JLabel();
        jTextField_User_firstName = new javax.swing.JTextField();
        jTextField_User_lastName = new javax.swing.JTextField();
        jLabel1 = new javax.swing.JLabel();
        jPanel_Table = new javax.swing.JPanel();
        jPanel1 = new javax.swing.JPanel();
        jButton_FirstPage_ = new javax.swing.JButton();
        jButton_PriorPage_ = new javax.swing.JButton();
        jButton_NextPage_ = new javax.swing.JButton();
        jButton_LastPage_ = new javax.swing.JButton();
        jButton_Refresh_Table = new javax.swing.JButton();
        jLabel18 = new javax.swing.JLabel();
        jFormattedTextField_PageSize = new javax.swing.JFormattedTextField();
        jLabel_PageNo = new javax.swing.JLabel();
        jLabel_PrintResult1 = new javax.swing.JLabel();
        jTextField_PageNo = new javax.swing.JTextField();
        jTextField_RowCount = new javax.swing.JTextField();
        jScrollPane1 = new javax.swing.JScrollPane();
        jTable_Customer = new javax.swing.JTable();
        jTabbedPane1 = new javax.swing.JTabbedPane();
        jPanel2 = new javax.swing.JPanel();
        jPanel_Gruid_Data = new javax.swing.JPanel();
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
        jLabel16 = new javax.swing.JLabel();
        jFormattedTextField_CreatedAt = new javax.swing.JFormattedTextField();
        JPanel_CruidOp1 = new javax.swing.JPanel();
        jButton_Save_ = new javax.swing.JButton();
        jButton_Cancel1 = new javax.swing.JButton();
        jButton_Delete_ = new javax.swing.JButton();
        jPanel3 = new javax.swing.JPanel();
        jPanel_Gruid_Data1 = new javax.swing.JPanel();
        jLabel25 = new javax.swing.JLabel();
        jTextField_New_ID = new javax.swing.JTextField();
        jTextField_New_FirstName = new javax.swing.JTextField();
        jTextField_New_LastName = new javax.swing.JTextField();
        jTextField_New_Email = new javax.swing.JTextField();
        jTextField_New_Phone = new javax.swing.JTextField();
        jLabel26 = new javax.swing.JLabel();
        jLabel27 = new javax.swing.JLabel();
        jLabel28 = new javax.swing.JLabel();
        jLabel29 = new javax.swing.JLabel();
        jLabel30 = new javax.swing.JLabel();
        jTextField_New_CreatedBy = new javax.swing.JTextField();
        jLabel31 = new javax.swing.JLabel();
        jFormattedTextField_New_CreatedAt = new javax.swing.JFormattedTextField();
        jPanel5 = new javax.swing.JPanel();
        jButton_New_New_ = new javax.swing.JButton();
        jButton_New_Save_ = new javax.swing.JButton();
        jButton_New_Cancel = new javax.swing.JButton();
        jPanel_Error_Msg = new javax.swing.JPanel();
        jLabel_Cruid_Errors = new javax.swing.JLabel();
        jButton_Gruid_Errors_Details = new javax.swing.JButton();
        jPanel4 = new javax.swing.JPanel();

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

        dateField_createDate_From.setMinimumSize(new java.awt.Dimension(52, 20));
        dateField_createDate_From.setPreferredSize(new java.awt.Dimension(52, 20));

        dateField_createDate_To.setMinimumSize(new java.awt.Dimension(52, 20));
        dateField_createDate_To.setPreferredSize(new java.awt.Dimension(52, 20));

        org.openide.awt.Mnemonics.setLocalizedText(jLabel7, org.openide.util.NbBundle.getMessage(CustomerTopComponent.class, "CustomerTopComponent.jLabel7.text")); // NOI18N
        jLabel7.setMinimumSize(new java.awt.Dimension(24, 20));
        jLabel7.setPreferredSize(new java.awt.Dimension(24, 20));

        org.openide.awt.Mnemonics.setLocalizedText(jLabel9, org.openide.util.NbBundle.getMessage(CustomerTopComponent.class, "CustomerTopComponent.jLabel9.text")); // NOI18N
        jLabel9.setMaximumSize(new java.awt.Dimension(10, 20));
        jLabel9.setPreferredSize(new java.awt.Dimension(10, 20));

        jLabel_FilterError.setForeground(new java.awt.Color(255, 51, 0));
        org.openide.awt.Mnemonics.setLocalizedText(jLabel_FilterError, org.openide.util.NbBundle.getMessage(CustomerTopComponent.class, "CustomerTopComponent.jLabel_FilterError.text")); // NOI18N

        jTextField_User_firstName.setText(org.openide.util.NbBundle.getMessage(CustomerTopComponent.class, "CustomerTopComponent.jTextField_User_firstName.text")); // NOI18N

        jTextField_User_lastName.setText(org.openide.util.NbBundle.getMessage(CustomerTopComponent.class, "CustomerTopComponent.jTextField_User_lastName.text")); // NOI18N

        org.openide.awt.Mnemonics.setLocalizedText(jLabel1, org.openide.util.NbBundle.getMessage(CustomerTopComponent.class, "CustomerTopComponent.jLabel1.text")); // NOI18N

        javax.swing.GroupLayout jPanel_FilterLayout = new javax.swing.GroupLayout(jPanel_Filter);
        jPanel_Filter.setLayout(jPanel_FilterLayout);
        jPanel_FilterLayout.setHorizontalGroup(
            jPanel_FilterLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel_FilterLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel_FilterLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel_FilterLayout.createSequentialGroup()
                        .addGroup(jPanel_FilterLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel5)
                            .addComponent(jLabel7, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(30, 30, 30)
                        .addGroup(jPanel_FilterLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel_FilterLayout.createSequentialGroup()
                                .addComponent(dateField_createDate_From, javax.swing.GroupLayout.PREFERRED_SIZE, 90, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(jLabel9, javax.swing.GroupLayout.PREFERRED_SIZE, 12, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(dateField_createDate_To, javax.swing.GroupLayout.PREFERRED_SIZE, 89, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(jLabel1)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(jTextField_User_firstName, javax.swing.GroupLayout.PREFERRED_SIZE, 141, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(jTextField_User_lastName, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(jPanel_FilterLayout.createSequentialGroup()
                                .addGroup(jPanel_FilterLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                    .addGroup(jPanel_FilterLayout.createSequentialGroup()
                                        .addComponent(jTextField_ID_Filter, javax.swing.GroupLayout.PREFERRED_SIZE, 74, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addGap(18, 18, 18)
                                        .addComponent(jLabel3)
                                        .addGap(18, 18, 18)
                                        .addComponent(jTextField_FirstName_Filter, javax.swing.GroupLayout.PREFERRED_SIZE, 210, javax.swing.GroupLayout.PREFERRED_SIZE))
                                    .addComponent(jTextField_Email_Filter))
                                .addGap(18, 18, 18)
                                .addGroup(jPanel_FilterLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jLabel4)
                                    .addComponent(jLabel6))
                                .addGap(18, 18, 18)
                                .addGroup(jPanel_FilterLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jTextField_LastName_Filter)
                                    .addComponent(jTextField_Phone_Filter)))))
                    .addComponent(jLabel2)
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
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(jPanel_FilterLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel_FilterLayout.createSequentialGroup()
                        .addGroup(jPanel_FilterLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel_FilterLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                .addComponent(jTextField_ID_Filter, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(jLabel2)
                                .addComponent(jLabel3))
                            .addComponent(jTextField_FirstName_Filter, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel_FilterLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel5)
                            .addComponent(jTextField_Email_Filter, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel_FilterLayout.createSequentialGroup()
                        .addGroup(jPanel_FilterLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jTextField_LastName_Filter, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel4))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel_FilterLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jTextField_Phone_Filter, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel6))))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel_FilterLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(jPanel_FilterLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addComponent(dateField_createDate_To, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(dateField_createDate_From, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(jLabel9, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(jPanel_FilterLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addComponent(jTextField_User_lastName, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGroup(jPanel_FilterLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jTextField_User_firstName, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel1)))
                    .addComponent(jLabel7, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(8, 8, 8)
                .addGroup(jPanel_FilterLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jButton_Search_)
                    .addComponent(jButton_Clear_)
                    .addComponent(jLabel_FilterError)))
        );

        jPanel_Table.setBorder(javax.swing.BorderFactory.createTitledBorder(org.openide.util.NbBundle.getMessage(CustomerTopComponent.class, "CustomerTopComponent.jPanel_Table.border.title"))); // NOI18N

        jPanel1.setBorder(javax.swing.BorderFactory.createTitledBorder(""));
        jPanel1.setMinimumSize(new java.awt.Dimension(0, 37));
        jPanel1.setPreferredSize(new java.awt.Dimension(788, 37));
        jPanel1.setRequestFocusEnabled(false);

        jButton_FirstPage_.setIcon(new javax.swing.ImageIcon(getClass().getResource("/org/cdms/ui/images/navigate_beginning.png"))); // NOI18N
        org.openide.awt.Mnemonics.setLocalizedText(jButton_FirstPage_, org.openide.util.NbBundle.getMessage(CustomerTopComponent.class, "CustomerTopComponent.jButton_FirstPage_.text")); // NOI18N
        jButton_FirstPage_.setToolTipText(org.openide.util.NbBundle.getMessage(CustomerTopComponent.class, "CustomerTopComponent.jButton_FirstPage_.toolTipText")); // NOI18N
        jButton_FirstPage_.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 1, 1, 1));
        jButton_FirstPage_.setContentAreaFilled(false);
        jButton_FirstPage_.setMargin(new java.awt.Insets(0, 0, 0, 0));
        jButton_FirstPage_.setMaximumSize(new java.awt.Dimension(16, 16));
        jButton_FirstPage_.setMinimumSize(new java.awt.Dimension(16, 16));
        jButton_FirstPage_.setPreferredSize(new java.awt.Dimension(16, 16));
        jButton_FirstPage_.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton_FirstPage_ActionPerformed(evt);
            }
        });

        jButton_PriorPage_.setIcon(new javax.swing.ImageIcon(getClass().getResource("/org/cdms/ui/images/navigate_left.png"))); // NOI18N
        org.openide.awt.Mnemonics.setLocalizedText(jButton_PriorPage_, org.openide.util.NbBundle.getMessage(CustomerTopComponent.class, "CustomerTopComponent.jButton_PriorPage_.text")); // NOI18N
        jButton_PriorPage_.setToolTipText(org.openide.util.NbBundle.getMessage(CustomerTopComponent.class, "CustomerTopComponent.jButton_PriorPage_.toolTipText")); // NOI18N
        jButton_PriorPage_.setBorder(null);
        jButton_PriorPage_.setContentAreaFilled(false);
        jButton_PriorPage_.setMaximumSize(new java.awt.Dimension(16, 16));
        jButton_PriorPage_.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton_PriorPage_ActionPerformed(evt);
            }
        });

        jButton_NextPage_.setIcon(new javax.swing.ImageIcon(getClass().getResource("/org/cdms/ui/images/navigate_right.png"))); // NOI18N
        org.openide.awt.Mnemonics.setLocalizedText(jButton_NextPage_, org.openide.util.NbBundle.getMessage(CustomerTopComponent.class, "CustomerTopComponent.jButton_NextPage_.text")); // NOI18N
        jButton_NextPage_.setToolTipText(org.openide.util.NbBundle.getMessage(CustomerTopComponent.class, "CustomerTopComponent.jButton_NextPage_.toolTipText")); // NOI18N
        jButton_NextPage_.setContentAreaFilled(false);
        jButton_NextPage_.setMaximumSize(new java.awt.Dimension(16, 16));
        jButton_NextPage_.setMinimumSize(new java.awt.Dimension(16, 16));
        jButton_NextPage_.setPreferredSize(new java.awt.Dimension(16, 16));
        jButton_NextPage_.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton_NextPage_ActionPerformed(evt);
            }
        });

        jButton_LastPage_.setIcon(new javax.swing.ImageIcon(getClass().getResource("/org/cdms/ui/images/navigate_end.png"))); // NOI18N
        org.openide.awt.Mnemonics.setLocalizedText(jButton_LastPage_, org.openide.util.NbBundle.getMessage(CustomerTopComponent.class, "CustomerTopComponent.jButton_LastPage_.text")); // NOI18N
        jButton_LastPage_.setToolTipText(org.openide.util.NbBundle.getMessage(CustomerTopComponent.class, "CustomerTopComponent.jButton_LastPage_.toolTipText")); // NOI18N
        jButton_LastPage_.setContentAreaFilled(false);
        jButton_LastPage_.setMaximumSize(new java.awt.Dimension(16, 16));
        jButton_LastPage_.setMinimumSize(new java.awt.Dimension(16, 16));
        jButton_LastPage_.setPreferredSize(new java.awt.Dimension(16, 16));
        jButton_LastPage_.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton_LastPage_ActionPerformed(evt);
            }
        });

        jButton_Refresh_Table.setIcon(new javax.swing.ImageIcon(getClass().getResource("/org/cdms/ui/images/refresh.png"))); // NOI18N
        org.openide.awt.Mnemonics.setLocalizedText(jButton_Refresh_Table, org.openide.util.NbBundle.getMessage(CustomerTopComponent.class, "CustomerTopComponent.jButton_Refresh_Table.text")); // NOI18N
        jButton_Refresh_Table.setToolTipText(org.openide.util.NbBundle.getMessage(CustomerTopComponent.class, "CustomerTopComponent.jButton_Refresh_Table.toolTipText")); // NOI18N
        jButton_Refresh_Table.setContentAreaFilled(false);
        jButton_Refresh_Table.setMaximumSize(new java.awt.Dimension(16, 16));
        jButton_Refresh_Table.setMinimumSize(new java.awt.Dimension(16, 16));
        jButton_Refresh_Table.setPreferredSize(new java.awt.Dimension(16, 16));
        jButton_Refresh_Table.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton_Refresh_TableActionPerformed(evt);
            }
        });

        org.openide.awt.Mnemonics.setLocalizedText(jLabel18, org.openide.util.NbBundle.getMessage(CustomerTopComponent.class, "CustomerTopComponent.jLabel18.text")); // NOI18N

        jFormattedTextField_PageSize.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.NumberFormatter(new java.text.DecimalFormat("######"))));
        jFormattedTextField_PageSize.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        jFormattedTextField_PageSize.setText(org.openide.util.NbBundle.getMessage(CustomerTopComponent.class, "CustomerTopComponent.jFormattedTextField_PageSize.text")); // NOI18N

        org.openide.awt.Mnemonics.setLocalizedText(jLabel_PageNo, org.openide.util.NbBundle.getMessage(CustomerTopComponent.class, "CustomerTopComponent.jLabel_PageNo.text")); // NOI18N

        org.openide.awt.Mnemonics.setLocalizedText(jLabel_PrintResult1, org.openide.util.NbBundle.getMessage(CustomerTopComponent.class, "CustomerTopComponent.jLabel_PrintResult1.text")); // NOI18N

        jTextField_PageNo.setEditable(false);
        jTextField_PageNo.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        jTextField_PageNo.setText(org.openide.util.NbBundle.getMessage(CustomerTopComponent.class, "CustomerTopComponent.jTextField_PageNo.text")); // NOI18N

        jTextField_RowCount.setEditable(false);
        jTextField_RowCount.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        jTextField_RowCount.setText(org.openide.util.NbBundle.getMessage(CustomerTopComponent.class, "CustomerTopComponent.jTextField_RowCount.text")); // NOI18N

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
                .addComponent(jButton_NextPage_, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jButton_LastPage_, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(33, 33, 33)
                .addComponent(jLabel18)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jFormattedTextField_PageSize, javax.swing.GroupLayout.PREFERRED_SIZE, 36, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(jLabel_PageNo)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jTextField_PageNo, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jTextField_RowCount, javax.swing.GroupLayout.PREFERRED_SIZE, 84, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jLabel_PrintResult1, javax.swing.GroupLayout.PREFERRED_SIZE, 58, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(141, 141, 141))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jFormattedTextField_PageSize)
                        .addComponent(jLabel18)
                        .addComponent(jLabel_PageNo)
                        .addComponent(jLabel_PrintResult1)
                        .addComponent(jTextField_PageNo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jTextField_RowCount, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(jButton_LastPage_, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jButton_NextPage_, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
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
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel_TableLayout.createSequentialGroup()
                .addGroup(jPanel_TableLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jPanel1, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 829, Short.MAX_VALUE)
                    .addComponent(jScrollPane1))
                .addContainerGap())
        );
        jPanel_TableLayout.setVerticalGroup(
            jPanel_TableLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel_TableLayout.createSequentialGroup()
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 154, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

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

        org.openide.awt.Mnemonics.setLocalizedText(jLabel16, org.openide.util.NbBundle.getMessage(CustomerTopComponent.class, "CustomerTopComponent.jLabel16.text")); // NOI18N

        jFormattedTextField_CreatedAt.setEditable(false);
        jFormattedTextField_CreatedAt.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.DateFormatter(java.text.DateFormat.getDateInstance(java.text.DateFormat.MEDIUM))));
        jFormattedTextField_CreatedAt.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        jFormattedTextField_CreatedAt.setText(org.openide.util.NbBundle.getMessage(CustomerTopComponent.class, "CustomerTopComponent.jFormattedTextField_CreatedAt.text")); // NOI18N
        jFormattedTextField_CreatedAt.setFocusable(false);

        JPanel_CruidOp1.setEnabled(false);

        org.openide.awt.Mnemonics.setLocalizedText(jButton_Save_, org.openide.util.NbBundle.getMessage(CustomerTopComponent.class, "CustomerTopComponent.jButton_Save_.text")); // NOI18N
        jButton_Save_.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton_Save_ActionPerformed(evt);
            }
        });

        org.openide.awt.Mnemonics.setLocalizedText(jButton_Cancel1, org.openide.util.NbBundle.getMessage(CustomerTopComponent.class, "CustomerTopComponent.jButton_Cancel.text")); // NOI18N

        org.openide.awt.Mnemonics.setLocalizedText(jButton_Delete_, org.openide.util.NbBundle.getMessage(CustomerTopComponent.class, "CustomerTopComponent.jButton_Delete_.text")); // NOI18N
        jButton_Delete_.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton_Delete_ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout JPanel_CruidOp1Layout = new javax.swing.GroupLayout(JPanel_CruidOp1);
        JPanel_CruidOp1.setLayout(JPanel_CruidOp1Layout);
        JPanel_CruidOp1Layout.setHorizontalGroup(
            JPanel_CruidOp1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(JPanel_CruidOp1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jButton_Save_)
                .addGap(18, 18, 18)
                .addComponent(jButton_Cancel1)
                .addGap(18, 18, 18)
                .addComponent(jButton_Delete_)
                .addContainerGap(527, Short.MAX_VALUE))
        );
        JPanel_CruidOp1Layout.setVerticalGroup(
            JPanel_CruidOp1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(JPanel_CruidOp1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                .addComponent(jButton_Save_)
                .addComponent(jButton_Cancel1)
                .addComponent(jButton_Delete_))
        );

        javax.swing.GroupLayout jPanel_Gruid_DataLayout = new javax.swing.GroupLayout(jPanel_Gruid_Data);
        jPanel_Gruid_Data.setLayout(jPanel_Gruid_DataLayout);
        jPanel_Gruid_DataLayout.setHorizontalGroup(
            jPanel_Gruid_DataLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel_Gruid_DataLayout.createSequentialGroup()
                .addGroup(jPanel_Gruid_DataLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel13)
                    .addComponent(jLabel10)
                    .addComponent(jLabel15))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel_Gruid_DataLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel_Gruid_DataLayout.createSequentialGroup()
                        .addGroup(jPanel_Gruid_DataLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel_Gruid_DataLayout.createSequentialGroup()
                                .addComponent(jTextField_ID, javax.swing.GroupLayout.PREFERRED_SIZE, 76, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(18, 18, 18)
                                .addComponent(jLabel14)
                                .addGap(17, 17, 17)
                                .addComponent(jTextField_FirstName, javax.swing.GroupLayout.DEFAULT_SIZE, 308, Short.MAX_VALUE))
                            .addComponent(jTextField_Email))
                        .addGap(18, 18, 18)
                        .addGroup(jPanel_Gruid_DataLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel11)
                            .addComponent(jLabel12))
                        .addGap(18, 18, 18)
                        .addGroup(jPanel_Gruid_DataLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(jTextField_LastName)
                            .addComponent(jTextField_Phone, javax.swing.GroupLayout.PREFERRED_SIZE, 209, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addGroup(jPanel_Gruid_DataLayout.createSequentialGroup()
                        .addComponent(jTextField_CreatedBy, javax.swing.GroupLayout.PREFERRED_SIZE, 179, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jLabel16, javax.swing.GroupLayout.PREFERRED_SIZE, 54, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(jFormattedTextField_CreatedAt, javax.swing.GroupLayout.PREFERRED_SIZE, 76, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(290, 290, 290))))
            .addGroup(jPanel_Gruid_DataLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(JPanel_CruidOp1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );
        jPanel_Gruid_DataLayout.setVerticalGroup(
            jPanel_Gruid_DataLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel_Gruid_DataLayout.createSequentialGroup()
                .addGroup(jPanel_Gruid_DataLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel10)
                    .addComponent(jTextField_ID, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel14)
                    .addComponent(jLabel11)
                    .addComponent(jTextField_LastName, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jTextField_FirstName, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel_Gruid_DataLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jTextField_Email, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel12)
                    .addComponent(jTextField_Phone, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel15))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel_Gruid_DataLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel13)
                    .addComponent(jTextField_CreatedBy, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel16)
                    .addComponent(jFormattedTextField_CreatedAt, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(JPanel_CruidOp1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(22, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel_Gruid_Data, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel_Gruid_Data, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jTabbedPane1.addTab(org.openide.util.NbBundle.getMessage(CustomerTopComponent.class, "CustomerTopComponent.jPanel2.TabConstraints.tabTitle"), jPanel2); // NOI18N

        org.openide.awt.Mnemonics.setLocalizedText(jLabel25, org.openide.util.NbBundle.getMessage(CustomerTopComponent.class, "CustomerTopComponent.jLabel25.text")); // NOI18N

        jTextField_New_ID.setEditable(false);
        jTextField_New_ID.setText(org.openide.util.NbBundle.getMessage(CustomerTopComponent.class, "CustomerTopComponent.jTextField_New_ID.text")); // NOI18N

        jTextField_New_FirstName.setText(org.openide.util.NbBundle.getMessage(CustomerTopComponent.class, "CustomerTopComponent.jTextField_New_FirstName.text")); // NOI18N
        jTextField_New_FirstName.setEnabled(false);

        jTextField_New_LastName.setText(org.openide.util.NbBundle.getMessage(CustomerTopComponent.class, "CustomerTopComponent.jTextField_New_LastName.text")); // NOI18N
        jTextField_New_LastName.setEnabled(false);

        jTextField_New_Email.setText(org.openide.util.NbBundle.getMessage(CustomerTopComponent.class, "CustomerTopComponent.jTextField_New_Email.text")); // NOI18N
        jTextField_New_Email.setEnabled(false);

        jTextField_New_Phone.setText(org.openide.util.NbBundle.getMessage(CustomerTopComponent.class, "CustomerTopComponent.jTextField_New_Phone.text")); // NOI18N
        jTextField_New_Phone.setEnabled(false);

        org.openide.awt.Mnemonics.setLocalizedText(jLabel26, org.openide.util.NbBundle.getMessage(CustomerTopComponent.class, "CustomerTopComponent.jLabel26.text")); // NOI18N

        org.openide.awt.Mnemonics.setLocalizedText(jLabel27, org.openide.util.NbBundle.getMessage(CustomerTopComponent.class, "CustomerTopComponent.jLabel27.text")); // NOI18N

        org.openide.awt.Mnemonics.setLocalizedText(jLabel28, org.openide.util.NbBundle.getMessage(CustomerTopComponent.class, "CustomerTopComponent.jLabel28.text")); // NOI18N

        org.openide.awt.Mnemonics.setLocalizedText(jLabel29, org.openide.util.NbBundle.getMessage(CustomerTopComponent.class, "CustomerTopComponent.jLabel29.text")); // NOI18N

        org.openide.awt.Mnemonics.setLocalizedText(jLabel30, org.openide.util.NbBundle.getMessage(CustomerTopComponent.class, "CustomerTopComponent.jLabel30.text")); // NOI18N

        jTextField_New_CreatedBy.setEditable(false);
        jTextField_New_CreatedBy.setText(org.openide.util.NbBundle.getMessage(CustomerTopComponent.class, "CustomerTopComponent.jTextField_New_CreatedBy.text")); // NOI18N

        org.openide.awt.Mnemonics.setLocalizedText(jLabel31, org.openide.util.NbBundle.getMessage(CustomerTopComponent.class, "CustomerTopComponent.jLabel31.text")); // NOI18N

        jFormattedTextField_New_CreatedAt.setEditable(false);
        jFormattedTextField_New_CreatedAt.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.DateFormatter(java.text.DateFormat.getDateInstance(java.text.DateFormat.MEDIUM))));
        jFormattedTextField_New_CreatedAt.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        jFormattedTextField_New_CreatedAt.setText(org.openide.util.NbBundle.getMessage(CustomerTopComponent.class, "CustomerTopComponent.jFormattedTextField_New_CreatedAt.text")); // NOI18N
        jFormattedTextField_New_CreatedAt.setFocusable(false);

        javax.swing.GroupLayout jPanel_Gruid_Data1Layout = new javax.swing.GroupLayout(jPanel_Gruid_Data1);
        jPanel_Gruid_Data1.setLayout(jPanel_Gruid_Data1Layout);
        jPanel_Gruid_Data1Layout.setHorizontalGroup(
            jPanel_Gruid_Data1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel_Gruid_Data1Layout.createSequentialGroup()
                .addGroup(jPanel_Gruid_Data1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel28)
                    .addComponent(jLabel25)
                    .addComponent(jLabel30))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel_Gruid_Data1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel_Gruid_Data1Layout.createSequentialGroup()
                        .addGroup(jPanel_Gruid_Data1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel_Gruid_Data1Layout.createSequentialGroup()
                                .addComponent(jTextField_New_ID, javax.swing.GroupLayout.PREFERRED_SIZE, 76, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(18, 18, 18)
                                .addComponent(jLabel29)
                                .addGap(17, 17, 17)
                                .addComponent(jTextField_New_FirstName, javax.swing.GroupLayout.DEFAULT_SIZE, 308, Short.MAX_VALUE))
                            .addComponent(jTextField_New_Email))
                        .addGap(18, 18, 18)
                        .addGroup(jPanel_Gruid_Data1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel26)
                            .addComponent(jLabel27))
                        .addGap(18, 18, 18)
                        .addGroup(jPanel_Gruid_Data1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(jTextField_New_LastName)
                            .addComponent(jTextField_New_Phone, javax.swing.GroupLayout.PREFERRED_SIZE, 209, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addGroup(jPanel_Gruid_Data1Layout.createSequentialGroup()
                        .addComponent(jTextField_New_CreatedBy, javax.swing.GroupLayout.PREFERRED_SIZE, 179, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jLabel31, javax.swing.GroupLayout.PREFERRED_SIZE, 54, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(jFormattedTextField_New_CreatedAt, javax.swing.GroupLayout.PREFERRED_SIZE, 76, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(290, 290, 290))))
        );
        jPanel_Gruid_Data1Layout.setVerticalGroup(
            jPanel_Gruid_Data1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel_Gruid_Data1Layout.createSequentialGroup()
                .addGroup(jPanel_Gruid_Data1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel25)
                    .addComponent(jTextField_New_ID, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel29)
                    .addComponent(jLabel26)
                    .addComponent(jTextField_New_LastName, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jTextField_New_FirstName, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel_Gruid_Data1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jTextField_New_Email, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel27)
                    .addComponent(jTextField_New_Phone, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel30))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel_Gruid_Data1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel28)
                    .addComponent(jTextField_New_CreatedBy, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel31)
                    .addComponent(jFormattedTextField_New_CreatedAt, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(0, 0, Short.MAX_VALUE))
        );

        org.openide.awt.Mnemonics.setLocalizedText(jButton_New_New_, org.openide.util.NbBundle.getMessage(CustomerTopComponent.class, "CustomerTopComponent.jButton_New_New_.text")); // NOI18N
        jButton_New_New_.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton_New_New_ActionPerformed(evt);
            }
        });

        org.openide.awt.Mnemonics.setLocalizedText(jButton_New_Save_, org.openide.util.NbBundle.getMessage(CustomerTopComponent.class, "CustomerTopComponent.jButton_New_Save_.text")); // NOI18N
        jButton_New_Save_.setEnabled(false);
        jButton_New_Save_.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton_New_Save_ActionPerformed(evt);
            }
        });

        org.openide.awt.Mnemonics.setLocalizedText(jButton_New_Cancel, org.openide.util.NbBundle.getMessage(CustomerTopComponent.class, "CustomerTopComponent.jButton_New_Cancel.text")); // NOI18N

        javax.swing.GroupLayout jPanel5Layout = new javax.swing.GroupLayout(jPanel5);
        jPanel5.setLayout(jPanel5Layout);
        jPanel5Layout.setHorizontalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel5Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jButton_New_New_)
                .addGap(20, 20, 20)
                .addComponent(jButton_New_Save_)
                .addGap(18, 18, 18)
                .addComponent(jButton_New_Cancel)
                .addContainerGap(399, Short.MAX_VALUE))
        );
        jPanel5Layout.setVerticalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jButton_New_Save_)
                    .addComponent(jButton_New_New_)
                    .addComponent(jButton_New_Cancel))
                .addGap(0, 0, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel3Layout.createSequentialGroup()
                .addContainerGap(58, Short.MAX_VALUE)
                .addComponent(jPanel5, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(166, 166, 166))
            .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(jPanel3Layout.createSequentialGroup()
                    .addContainerGap()
                    .addComponent(jPanel_Gruid_Data1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addContainerGap()))
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel3Layout.createSequentialGroup()
                .addContainerGap(95, Short.MAX_VALUE)
                .addComponent(jPanel5, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
            .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(jPanel3Layout.createSequentialGroup()
                    .addContainerGap()
                    .addComponent(jPanel_Gruid_Data1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addContainerGap(46, Short.MAX_VALUE)))
        );

        jTabbedPane1.addTab(org.openide.util.NbBundle.getMessage(CustomerTopComponent.class, "CustomerTopComponent.jPanel3.TabConstraints.tabTitle"), jPanel3); // NOI18N

        jLabel_Cruid_Errors.setForeground(new java.awt.Color(255, 51, 0));
        org.openide.awt.Mnemonics.setLocalizedText(jLabel_Cruid_Errors, org.openide.util.NbBundle.getMessage(CustomerTopComponent.class, "CustomerTopComponent.jLabel_Cruid_Errors.text")); // NOI18N

        jButton_Gruid_Errors_Details.setIcon(new javax.swing.ImageIcon(getClass().getResource("/org/cdms/ui/images/action_stop.gif"))); // NOI18N
        org.openide.awt.Mnemonics.setLocalizedText(jButton_Gruid_Errors_Details, org.openide.util.NbBundle.getMessage(CustomerTopComponent.class, "CustomerTopComponent.jButton_Gruid_Errors_Details.text")); // NOI18N
        jButton_Gruid_Errors_Details.setContentAreaFilled(false);
        jButton_Gruid_Errors_Details.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton_Gruid_Errors_DetailsActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel_Error_MsgLayout = new javax.swing.GroupLayout(jPanel_Error_Msg);
        jPanel_Error_Msg.setLayout(jPanel_Error_MsgLayout);
        jPanel_Error_MsgLayout.setHorizontalGroup(
            jPanel_Error_MsgLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel_Error_MsgLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel_Cruid_Errors, javax.swing.GroupLayout.DEFAULT_SIZE, 303, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jButton_Gruid_Errors_Details)
                .addGap(0, 210, Short.MAX_VALUE))
        );
        jPanel_Error_MsgLayout.setVerticalGroup(
            jPanel_Error_MsgLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel_Error_MsgLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                .addComponent(jButton_Gruid_Errors_Details, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                .addComponent(jLabel_Cruid_Errors, javax.swing.GroupLayout.DEFAULT_SIZE, 23, Short.MAX_VALUE))
        );

        jPanel4.setBorder(javax.swing.BorderFactory.createTitledBorder(""));

        javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 0, Short.MAX_VALUE)
        );
        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 9, Short.MAX_VALUE)
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jPanel_Table, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel_Filter, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jTabbedPane1, javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jPanel_Error_Msg, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jPanel4, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addGap(0, 0, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel_Filter, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel_Table, javax.swing.GroupLayout.PREFERRED_SIZE, 209, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel_Error_Msg, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jTabbedPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 157, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jPanel4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(23, 23, 23))
        );

        //initFilterComponents();
        //filterBindingGroup.bind();
    }// </editor-fold>//GEN-END:initComponents

    protected void doFilter() {
        jLabel_FilterError.setText("");
        showGruidErrors(false);
        //jLabel_Cruid_Errors.setText("");
        entityAsyncFilter = new CustomerAsyncService();
        System.out.println("FILTER ID=" + entityAsFilter.getId()
                + "FirstName=" + entityAsFilter.getFirstName());
        jButton_Search_.setEnabled(false);

        try {
            queryPage.setEntityAsExample(entityAsFilter);
            queryPage.setQueryResult(new ArrayList<Customer>());
            entityAsyncFilter.findByExample(new FilterSeachHandler(), queryPage); // TODO paging
        } catch (Exception e) {
            System.out.println("ERROR");
        }
        System.out.println("RRRRRRRRRRRRRRRRRRRRRRRRRRRRR");


    }
    private void jButton_Search_ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton_Search_ActionPerformed
        int pageSize = Integer.parseInt(jFormattedTextField_PageSize.getText());
        queryPage.setPageSize(pageSize);
        queryPage.setPageNo(0);

        doFilter();
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

    private void jButton_Save_ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton_Save_ActionPerformed
        clearEditErrorMessages();
        int row = jTable_Customer.getSelectedRow();
        if (row < 0) {
            jLabel_Cruid_Errors.setText(NbBundle.getMessage(CustomerTopComponent.class, "CustomerTopComponent.Internal.NoSelectedRow"));
            showGruidErrors(true);
            return;
        }
        enableCruidOperations(false);
        updateCustomer();
    }//GEN-LAST:event_jButton_Save_ActionPerformed

    private void jButton_New_Save_ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton_New_Save_ActionPerformed
        clearInserErrorMessages();
        insertCustomer();
    }//GEN-LAST:event_jButton_New_Save_ActionPerformed

    private void jButton_New_New_ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton_New_New_ActionPerformed
        enableInserOperations(true);
        if (entityToInsertBindingGroup == null) {
            initNewCustomerComponents();
            
        } else {
            entityToInsertBindingGroup.unbind();
            entityToInsert = new Customer();
            entityToInsertBindingGroup.bind();
        }


    }//GEN-LAST:event_jButton_New_New_ActionPerformed

    private void jButton_NextPage_ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton_NextPage_ActionPerformed
        queryPage.setPageNo(queryPage.getPageNo() + 1);
        doFilter();
    }//GEN-LAST:event_jButton_NextPage_ActionPerformed

    private void jButton_PriorPage_ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton_PriorPage_ActionPerformed
        queryPage.setPageNo(queryPage.getPageNo() - 1);
        doFilter();
    }//GEN-LAST:event_jButton_PriorPage_ActionPerformed

    private void jButton_FirstPage_ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton_FirstPage_ActionPerformed
        queryPage.setPageNo(0);
        doFilter();
    }//GEN-LAST:event_jButton_FirstPage_ActionPerformed

    private void jButton_LastPage_ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton_LastPage_ActionPerformed
        int lastPage = (int) (queryPage.getRowCount() / queryPage.getPageSize() - 1);
        if (queryPage.getRowCount() % queryPage.getPageSize() != 0) {
            lastPage++;
        }

        queryPage.setPageNo(lastPage);
        doFilter();

    }//GEN-LAST:event_jButton_LastPage_ActionPerformed
    private ErrorDetailsHandler errorDetailsHandler = new ErrorDetailsHandler();
    private void jButton_Gruid_Errors_DetailsActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton_Gruid_Errors_DetailsActionPerformed
        errorDetailsHandler.show();
    }//GEN-LAST:event_jButton_Gruid_Errors_DetailsActionPerformed

    private void jButton_Refresh_TableActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton_Refresh_TableActionPerformed
        //int pageSize = Integer.parseInt(jFormattedTextField_PageSize.getText());
        //queryPage.setPageSize(pageSize);
        //
        //queryPage.setPageNo(0);

        //TODO in production. A user may change page size. 
        //     So we must  keep it im mind
        doFilter();

    }//GEN-LAST:event_jButton_Refresh_TableActionPerformed

    private void jButton_Delete_ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton_Delete_ActionPerformed
        clearEditErrorMessages();
        int row = jTable_Customer.getSelectedRow();
        if (row < 0) {
            jLabel_Cruid_Errors.setText(NbBundle.getMessage(CustomerTopComponent.class, "CustomerTopComponent.Internal.NoSelectedRow"));
            showGruidErrors(true);
            return;
        }
        if ( ! DeleteConfirmDialog.confirm("Customer", filterResult.get(row).getId())) {
            return;
        }
        enableCruidOperations(false);
        deleteCustomer();

    }//GEN-LAST:event_jButton_Delete_ActionPerformed
    private void insertCustomer() {
        entityAsyncInsert = new CustomerAsyncService();
        entityAsyncInsert.insert(new InsertHandler(), entityToInsert); // TODO paging
/*        try {
         entityAsyncInsert.insert(new InsertHandler(), entityToInsert); // TODO paging
         } catch (Exception e) {
         System.out.println("ERROR: ");
         }
         */
    }

    private void updateCustomer() {
        entityAsyncSave = new CustomerAsyncService();
        jPanel_Gruid_Data.setEnabled(false);

        int r = jTable_Customer.getSelectedRow();
        if (r < 0) {
            return;
        }
        Customer toUpdate = filterResult.get(r);
        try {
            entityAsyncSave.update(new SaveHandler(), toUpdate); // TODO paging
        } catch (Exception e) {
            System.out.println("ERROR");
        }
        System.out.println("MMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMM");

    }

    private void deleteCustomer() {
        entityAsyncDelete = new CustomerAsyncService();
        //jPanel_Gruid_Data.setEnabled(false);

        int r = jTable_Customer.getSelectedRow();
        if (r < 0) {
            return;
        }
        Customer c = filterResult.get(r);
        try {
            entityAsyncDelete.delete(new DeleteHandler(), c); // TODO paging
        } catch (Exception e) {
            System.out.println("ERROR");
        }
        System.out.println("MMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMM");

    }
    
    public void clearEditErrorMessages() {
        jLabel_Cruid_Errors.setText("");
        showGruidErrors(false);
    }

    public void clearInserErrorMessages() {
        
    }

    public void enableCruidOperations(boolean enabled) {

        jButton_New_New_.setEnabled(enabled);
        jButton_Refresh_Table.setEnabled(enabled);
        jButton_New_Save_.setEnabled(enabled);
        jButton_Search_.setEnabled(enabled);
        jTable_Customer.setEnabled(enabled);
    }
    public void enableInserOperations(boolean enabled) {

        jButton_New_New_.setEnabled(enabled);
        jButton_New_Save_.setEnabled(enabled);
        jTextField_New_Email.setEnabled(enabled);
        jTextField_New_FirstName.setEnabled(enabled);
        jTextField_New_LastName.setEnabled(enabled);
        jTextField_New_Phone.setEnabled(enabled);
        jTextField_New_Phone.setEnabled(enabled);
        
        
    }
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPanel JPanel_CruidOp1;
    private net.sf.nachocalendar.components.DateField dateField_createDate_From;
    private net.sf.nachocalendar.components.DateField dateField_createDate_To;
    private javax.swing.JButton jButton_Cancel1;
    private javax.swing.JButton jButton_Clear_;
    private javax.swing.JButton jButton_Delete_;
    private javax.swing.JButton jButton_FirstPage_;
    private javax.swing.JButton jButton_Gruid_Errors_Details;
    private javax.swing.JButton jButton_LastPage_;
    private javax.swing.JButton jButton_New_Cancel;
    private javax.swing.JButton jButton_New_New_;
    private javax.swing.JButton jButton_New_Save_;
    private javax.swing.JButton jButton_NextPage_;
    private javax.swing.JButton jButton_PriorPage_;
    private javax.swing.JButton jButton_Refresh_Table;
    private javax.swing.JButton jButton_Save_;
    private javax.swing.JButton jButton_Search_;
    private javax.swing.JFormattedTextField jFormattedTextField_CreatedAt;
    private javax.swing.JFormattedTextField jFormattedTextField_New_CreatedAt;
    private javax.swing.JFormattedTextField jFormattedTextField_PageSize;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel14;
    private javax.swing.JLabel jLabel15;
    private javax.swing.JLabel jLabel16;
    private javax.swing.JLabel jLabel18;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel25;
    private javax.swing.JLabel jLabel26;
    private javax.swing.JLabel jLabel27;
    private javax.swing.JLabel jLabel28;
    private javax.swing.JLabel jLabel29;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel30;
    private javax.swing.JLabel jLabel31;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JLabel jLabel_Cruid_Errors;
    private javax.swing.JLabel jLabel_FilterError;
    private javax.swing.JLabel jLabel_PageNo;
    private javax.swing.JLabel jLabel_PrintResult1;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JPanel jPanel_Error_Msg;
    private javax.swing.JPanel jPanel_Filter;
    private javax.swing.JPanel jPanel_Gruid_Data;
    private javax.swing.JPanel jPanel_Gruid_Data1;
    private javax.swing.JPanel jPanel_Table;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTabbedPane jTabbedPane1;
    private javax.swing.JTable jTable_Customer;
    private javax.swing.JTextField jTextField_CreatedBy;
    private javax.swing.JTextField jTextField_Email;
    private javax.swing.JTextField jTextField_Email_Filter;
    private javax.swing.JTextField jTextField_FirstName;
    private javax.swing.JTextField jTextField_FirstName_Filter;
    private javax.swing.JTextField jTextField_ID;
    private javax.swing.JTextField jTextField_ID_Filter;
    private javax.swing.JTextField jTextField_LastName;
    private javax.swing.JTextField jTextField_LastName_Filter;
    private javax.swing.JTextField jTextField_New_CreatedBy;
    private javax.swing.JTextField jTextField_New_Email;
    private javax.swing.JTextField jTextField_New_FirstName;
    private javax.swing.JTextField jTextField_New_ID;
    private javax.swing.JTextField jTextField_New_LastName;
    private javax.swing.JTextField jTextField_New_Phone;
    private javax.swing.JTextField jTextField_PageNo;
    private javax.swing.JTextField jTextField_Phone;
    private javax.swing.JTextField jTextField_Phone_Filter;
    private javax.swing.JTextField jTextField_RowCount;
    private javax.swing.JTextField jTextField_User_firstName;
    private javax.swing.JTextField jTextField_User_lastName;
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

/*    protected String buildValidationMessageFor(Exception exception) {
        RemoteValidationException e = (RemoteValidationException) exception;
        String m = "";
        if (e.getViolations() == null || e.getViolations().isEmpty()) {
            m = ""; //TODO
            return m;
        }
        for (RemoteConstraintViolation v : e.getViolations()) {
            if (v.getPropertyPath() != null) {
                m += "'" + v.getPropertyPath() + "': ";
                if ("javax.validation.constraints.Size".equals(v.getAnnotationClassName())
                        || "javax.validation.constraints.Min".equals(v.getAnnotationClassName())
                        || "javax.validation.constraints.Max".equals(v.getAnnotationClassName())) {
                    m += NbBundle.getMessage(CustomerTopComponent.class, "CustomerTopComponent.Validation.Size"); // NOI             
                    if (v.getSizeExpression() != null) {
                        m += v.getSizeExpression();
                    }
                } else if ("javax.validation.constraints.NotNull".equals(v.getAnnotationClassName())) {
                    m += NbBundle.getMessage(CustomerTopComponent.class, "CustomerTopComponent.Validation.NotNull");
                }
            }
        }

        return m;
    }

    protected String buildConnectionMessageFor(Exception exception) {
        RemoteConnectionException e = (RemoteConnectionException) exception;
        String m = NbBundle.getMessage(CustomerTopComponent.class, "CustomerTopComponent.RemoteConnection.Refused"); // NOI             
        return m;
    }

    protected String buildOtherMessageFor(Exception exception) {
        String m = NbBundle.getMessage(CustomerTopComponent.class, "CustomerTopComponent.Exception.Other"); // NOI             
        return m;
    }

    protected String buildDataAccessMessageFor(Exception exception) {
        RemoteDataAccessException e = (RemoteDataAccessException) exception;
        String m = NbBundle.getMessage(CustomerTopComponent.class, "CustomerTopComponent.Exception.Other"); // NOI                     
        switch (e.getErrorCode() ) {
            case RemoteDataAccessException.OPTIMISTIC_LOCKING :
                m = NbBundle.getMessage(CustomerTopComponent.class, "CustomerTopComponent.RemoteDataAccessException.OPTIMISTIC_LOCKING");
                break;
            case RemoteDataAccessException.OBJECT_RETRIEVAL :
                m = NbBundle.getMessage(CustomerTopComponent.class, "CustomerTopComponent.RemoteDataAccessException.OBJECT_RETRIEVAL");
                m += " " + e.getIdentifier();
                break;
            case RemoteDataAccessException.QUERY :
                m = NbBundle.getMessage(CustomerTopComponent.class, "CustomerTopComponent.RemoteDataAccessException.QUERY");
                break;
            case RemoteDataAccessException.SYSTEM :
                m = NbBundle.getMessage(CustomerTopComponent.class, "CustomerTopComponent.RemoteDataAccessException.SYSTEM");
                break;
            case RemoteDataAccessException.JDBC :
                m = NbBundle.getMessage(CustomerTopComponent.class, "CustomerTopComponent.RemoteDataAccessException.JDBC");
                break;
                
        }
        return m;
    }

    protected String buildMessageFor(Exception e) {
        String m;
        if (e instanceof RemoteConnectionException) {
            m = buildConnectionMessageFor(e);
        } else if (e instanceof RemoteValidationException) {
            return buildValidationMessageFor(e);
        } else  if (e instanceof RemoteDataAccessException) {
             m = buildDataAccessMessageFor(e);
        } else {
            m = buildOtherMessageFor(e);
        }

        return m;
    }
*/
    protected class FilterSeachHandler implements TaskListener {

        @Override
        public void taskFinished(Task task) {
            EventQueue.invokeLater(new Runnable() {
                @Override
                public void run() {
                    //this code can work with Swing
                    if (entityAsyncFilter.getResult() instanceof Exception) {
                        Exception e = (Exception) entityAsyncFilter.getResult();
                        jLabel_FilterError.setText(ErrorMessageBuilder.get(e));
                    } else {

                        QueryPage<Customer> q = (QueryPage<Customer>) entityAsyncFilter.getResult();
                        if (q != null) {
                            queryPage = q;
                        }
                        filterResult = q.getQueryResult();
                        initTableComponents();
                        initPageNavigator();
                        if (filterResult.isEmpty()) {
                            emptyEditComponents();
                        }
                    }
                    jButton_Search_.setEnabled(true);
                }
            });

        }

    }//inner FilterSearchHandler

    protected class SaveHandler implements TaskListener {

        @Override
        public void taskFinished(Task task) {
            //
            // Its Swing !
            //
            EventQueue.invokeLater(new Runnable() {
                @Override
                public void run() {
                    if (entityAsyncSave.getResult() instanceof Exception) {
                        Exception e = (Exception) entityAsyncSave.getResult();
                        //jLabel_Cruid_Errors.setText(buildMessageFor(e));
                        jLabel_Cruid_Errors.setText(ErrorMessageBuilder.get(e));
                        errorDetailsHandler.setException(e);
                        showGruidErrors(true);

                    } else {
                        Customer c = (Customer) entityAsyncSave.getResult();
                        filterResult.set(jTable_Customer.getSelectedRow(), c);
                    }
                    enableCruidOperations(true);
                }
            });

        }
    }//class SaveHandler

    protected class InsertHandler implements TaskListener {

        @Override
        public void taskFinished(Task task) {
            EventQueue.invokeLater(new Runnable() {
                @Override
                public void run() {
                    //this code can work with Swing
                    if (entityAsyncInsert.getResult() instanceof Exception) {
                        Exception e = (Exception) entityAsyncInsert.getResult();
                        jLabel_Cruid_Errors.setText(ErrorMessageBuilder.get(e));
                        errorDetailsHandler.setException(e);
                        showGruidErrors(true);
                    } else {
                        entityToInsertBindingGroup.unbind();
                        entityToInsert = (Customer) entityAsyncInsert.getResult();
                        entityToInsertBindingGroup.bind();
                    }
                    enableCruidOperations(true);
                }
            });

        }
    }//InsertHandler
    
    protected class DeleteHandler implements TaskListener {

        @Override
        public void taskFinished(Task task) {
            EventQueue.invokeLater(new Runnable() {
                @Override
                public void run() {
                    //this code can work with Swing
                    if (entityAsyncDelete.getResult() instanceof Exception) {
                        Exception e = (Exception) entityAsyncDelete.getResult();
                        jLabel_Cruid_Errors.setText(ErrorMessageBuilder.get(e));
                        errorDetailsHandler.setException(e);
                        showGruidErrors(true);
                    } else {
                        //Customer c = (Customer) entityAsyncDelete.getResult();
                        int row = jTable_Customer.getSelectedRow();
                        filterResult.remove(jTable_Customer.getSelectedRow());
                        if ( ! filterResult.isEmpty() ) {
                            
                            if ( row >= filterResult.size() ) {
                                row = filterResult.size()-1 ;
                            } 
                            jTable_Customer.setRowSelectionInterval(row, row);
                        }
                    }
                    enableCruidOperations(true);
                }
            });

        }
    }
    
}