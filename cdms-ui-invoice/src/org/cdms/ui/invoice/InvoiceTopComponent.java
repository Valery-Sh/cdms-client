/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cdms.ui.invoice;


import java.awt.EventQueue;
import java.math.BigDecimal;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.swing.JTextField;
import javax.swing.text.DateFormatter;
import javax.swing.text.DefaultFormatterFactory;
import org.cdms.entities.Customer;
import org.cdms.entities.Invoice;
import org.cdms.entities.InvoiceItem;
import org.cdms.entities.User;
import org.cdms.remoting.QueryPage;
import org.cdms.ui.common.EntityBinder;
import org.cdms.ui.common.EntityBinderImpl;
import org.cdms.ui.common.ErrorMessageBuilder;
import org.cdms.ui.common.TableBinder;
import org.jdesktop.beansbinding.BindingGroup;
import org.jdesktop.observablecollections.ObservableCollections;
import org.netbeans.api.settings.ConvertAsProperties;
import org.openide.windows.TopComponent;
import org.openide.util.NbBundle.Messages;
import org.openide.util.Task;
import org.openide.util.TaskListener;
/**
 * Top component which displays something.
 */
@ConvertAsProperties(
    dtd = "-//org.cdms.ui.invoice//Invoice//EN",
autostore = false)
@TopComponent.Description(
    preferredID = "invoiceTopComponent",
iconBase = "org/cdms/ui/invoice/invoice16x16.png",
persistenceType = TopComponent.PERSISTENCE_ALWAYS)
@TopComponent.Registration(mode = "editor", openAtStartup = false)
//@ActionID(category = "Window", id = "org.cdms.ui.invoice.InvoiceTopComponent")
//@ActionReference(path = "Menu/Window" /*, position = 333 */)
//@TopComponent.OpenActionRegistration(
//    displayName = "#CTL_InvoiceAction",
//preferredID = "InvoiceTopComponent")
@Messages({
    "CTL_InvoiceAction=Invoice",
    "CTL_InvoiceTopComponent=Invoice Window",
    "HINT_InvoiceTopComponent=This is a Invoice window"
})
public final class InvoiceTopComponent extends TopComponent {
    private EntityBinder entityFilterBinder;
    private Invoice entityAsFilter = new Invoice();
    private EntityBinder userFilterBinder;
    private EntityBinder customerFilterBinder;
    private User userAsFilter = new User();
    private Customer customerAsFilter = new Customer();    
    private BindingGroup entityBindingGroup = new BindingGroup();
    private BindingGroup userBindingGroup = new BindingGroup();
    private BindingGroup customerBindingGroup = new BindingGroup();
    private TableBinder tableBinder;
    private TableBinder childTableBinder;

    List<Invoice> filterResult = null;
    InvoiceAsyncService entityAsyncFilter = new InvoiceAsyncService();
    InvoiceAsyncService entityAsyncSave = new InvoiceAsyncService();
    InvoiceAsyncService entityAsyncDelete = new InvoiceAsyncService();
    // For Insert operations
    Invoice entityToInsert;
    EntityBinder entityToInsertBinder;
    BindingGroup entityToInsertBindingGroup;
    InvoiceAsyncService entityAsyncInsert = new InvoiceAsyncService();
    private QueryPage<Invoice> queryPage;

    public InvoiceTopComponent() {
        initComponents();

        initFilterComponents();
        showGruidErrors(false);
        queryPage = new QueryPage<Invoice>();

        initPageNavigator();
        
        //EntityTablePanel tablePanel = new EntityTablePanel();
        //this.jPanel_LoadEntityPanel.add(tablePanel,BorderLayout.CENTER);
        setName(Bundle.CTL_InvoiceTopComponent());
        setToolTipText(Bundle.HINT_InvoiceTopComponent());

    }
    public Invoice getEntityAsFilter() {
        return entityAsFilter;
    }

    public void setEntityAsFilter(Invoice entityAsFilter) {
        this.entityAsFilter = entityAsFilter;
    }

    public User getUserAsFilter() {
        return userAsFilter;
    }

    public void setUserAsFilter(User userAsFilter) {
        this.userAsFilter = userAsFilter;
    }
    public Customer getCustomerAsFilter() {
        return customerAsFilter;
    }

    public void setCustomerAsFilter(Customer customerAsFilter) {
        this.customerAsFilter = customerAsFilter;
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
        entityFilterBinder.addTextFieldBinder(jTextField_ID_Filter, "entityAsFilter.idFilter");
        
        entityFilterBinder.addCalendarBinder(dateField_createDate_From, "entityAsFilter.createdAt");
        entityFilterBinder.addCalendarBinder(dateField_createDate_To, "entityAsFilter.createdAtEnd");

        entityAsFilter.setCreatedBy(userAsFilter);

        userFilterBinder = new EntityBinderImpl(userBindingGroup, this);
        userFilterBinder.addTextFieldBinder(jTextField_User_firstName, "userAsFilter.firstName");
        userFilterBinder.addTextFieldBinder(jTextField_User_lastName, "userAsFilter.lastName");

        entityAsFilter.setCustomer(customerAsFilter);
        
        customerFilterBinder = new EntityBinderImpl(customerBindingGroup, this);
        customerFilterBinder.addTextFieldBinder(jTextField_FirstName_Filter, "customerAsFilter.firstName");
        customerFilterBinder.addTextFieldBinder(jTextField_LastName_Filter, "entityAsFilter.lastName");
        customerFilterBinder.addTextFieldBinder(jTextField_Email_Filter, "entityAsFilter.email");
        customerFilterBinder.addTextFieldBinder(jTextField_Phone_Filter, "entityAsFilter.phone");
                
        entityBindingGroup.bind();
        userBindingGroup.bind();
        customerBindingGroup.bind();
        
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
//    List<InvoiceItem> childFilterResult;
    protected void initTableComponents() {
        //userList = new ArrayList<User>();
        //jTable_Customer.clearSelection();
        if (tableBinder != null) {
            tableBinder.getBindingGroup().unbind();
        }
        //jTable_Customer.setModel(new DefaultTableModel());
        filterResult = ObservableCollections.observableList(
                filterResult);
//        childFilterResult = ObservableCollections.observableList(
//                child);


        tableBinder = new TableBinder(jTable_MasterEntity, filterResult);

        tableBinder.addColumn("id", Long.class);
        //tableBinder.addColumn("version", Long.class);

        tableBinder.addColumn("customer.firstName", String.class, "First Name");
        tableBinder.addColumn("customer.lastName", String.class, "Last Name");        
        
//        tableBinder.addColumn("createdAt", Date.class);
        
/*        tableBinder.addColumn("lastName", String.class);
        tableBinder.addColumn("phone", String.class);
        tableBinder.addColumn("email", String.class);
        tableBinder.addColumn("createdAt", Date.class);
        tableBinder.addColumn("createdBy.firstName", String.class);
        tableBinder.addColumn("createdBy.lastName", String.class);
*/
        TableBinder child = tableBinder.addChild(jTable_ChildEntity, "invoiceItems");
        child.addColumn("id", Long.class,"Id");
        child.addColumn("productItem.itemName", String.class,"Name");
        child.addColumn("productItem.barcode", String.class,"Barcode");
        child.addColumn("productItem.price", BigDecimal.class, "Price");
        child.addColumn("itemCount", Integer.class,"Number");        
        //BigDecimal b = new BigDecimal("123.45");
        
        tableBinder.bindTable();

/*        tableBinder.addTextFieldBinder(jTextField_ID, "id");
        tableBinder.addTextFieldBinder(jTextField_Email, "email");
        tableBinder.addTextFieldBinder(jTextField_FirstName, "firstName");
        tableBinder.addTextFieldBinder(jTextField_LastName, "lastName");
        tableBinder.addTextFieldBinder(jTextField_Phone, "phone");
        tableBinder.addFormattedTextFieldBinder(jFormattedTextField_CreatedAt, "createdAt");
        tableBinder.addConcatTextFieldBinder(jTextField_CreatedBy, "createdBy.firstName", "createdBy.lastName");
*/


        //tableBinder.addTextFieldBinder(jTextField1_LastName, "lastName");
        tableBinder.refresh();
        if (!filterResult.isEmpty()) {
            jTable_MasterEntity.setRowSelectionInterval(0, 0);
        }
        tableBinder.updateMasterColumnModel();
    }
    
    protected void showGruidErrors(boolean visible) {
        //jLabel_Cruid_Errors.setVisible(visible);
        //jButton_Gruid_Errors_Details.setVisible(visible);

    }
    protected void emptyEditComponents() {
/*        jTextField_ID.setText(null);
        jTextField_Email.setText(null);
        jTextField_FirstName.setText(null);
        jTextField_LastName.setText(null);
        jTextField_Phone.setText(null);
        jFormattedTextField_CreatedAt.setValue(null);
        jTextField_CreatedBy.setText(null);
        */ 
    }
    public void enableNavigateOperations(boolean enabled) {
        jButton_Search_.setEnabled(enabled);
        jButton_FirstPage_.setEnabled(enabled);
        jButton_LastPage_.setEnabled(enabled);
        jButton_NextPage_.setEnabled(enabled);
        jButton_PriorPage_.setEnabled(enabled);
        jButton_Refresh_Table.setEnabled(enabled);
        if ( enabled ) {
            initPageNavigator();
        }
        
    }
    
    protected void doFilter() {
        jLabel_FilterError.setText("");
        showGruidErrors(false);
        //jLabel_Cruid_Errors.setText("");
        entityAsyncFilter = new InvoiceAsyncService();
        System.out.println("FILTER ID=" + entityAsFilter.getId()
                + "FirstName=" + entityAsFilter.getCustomer().getFirstName());
        //jButton_Search_.setEnabled(false);
        enableNavigateOperations(false);

        try {
            queryPage.setEntityAsExample(entityAsFilter);
            queryPage.setQueryResult(new ArrayList<Invoice>());
            entityAsyncFilter.findByExample(new FilterSeachHandler(), queryPage); // TODO paging
        } catch (Exception e) {
            System.out.println("ERROR");
        }
        System.out.println("RRRRRRRRRRRRRRRRRRRRRRRRRRRRR");


    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

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
        jTextField_PageNo = new javax.swing.JTextField();
        jTextField_RowCount = new javax.swing.JTextField();
        jScrollPane1 = new javax.swing.JScrollPane();
        jTable_MasterEntity = new javax.swing.JTable();
        jScrollPane2 = new javax.swing.JScrollPane();
        jTable_ChildEntity = new javax.swing.JTable();
        jPanel2 = new javax.swing.JPanel();
        jLabel2 = new javax.swing.JLabel();
        jTextField_ID_Filter = new javax.swing.JTextField();
        jPanel4 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        jLabel10 = new javax.swing.JLabel();
        jTextField_User_firstName = new javax.swing.JTextField();
        jLabel11 = new javax.swing.JLabel();
        jTextField_Phone_Filter = new javax.swing.JTextField();
        jPanel5 = new javax.swing.JPanel();
        jLabel12 = new javax.swing.JLabel();
        jLabel7 = new javax.swing.JLabel();
        dateField_createDate_From = new net.sf.nachocalendar.components.DateField();
        jLabel9 = new javax.swing.JLabel();
        dateField_createDate_To = new net.sf.nachocalendar.components.DateField();
        jLabel8 = new javax.swing.JLabel();
        jTextField1 = new javax.swing.JTextField();
        jLabel3 = new javax.swing.JLabel();
        jTextField_FirstName_Filter = new javax.swing.JTextField();
        jLabel4 = new javax.swing.JLabel();
        jTextField_LastName_Filter = new javax.swing.JTextField();
        jLabel6 = new javax.swing.JLabel();
        jTextField_User_lastName = new javax.swing.JTextField();
        jTextField_Email_Filter = new javax.swing.JTextField();
        jLabel5 = new javax.swing.JLabel();
        jButton_Search_ = new javax.swing.JButton();
        jButton_Clear_ = new javax.swing.JButton();
        jLabel_FilterError = new javax.swing.JLabel();

        jPanel_Table.setBorder(javax.swing.BorderFactory.createTitledBorder(org.openide.util.NbBundle.getMessage(InvoiceTopComponent.class, "InvoiceTopComponent.jPanel_Table.border.title"))); // NOI18N

        jPanel1.setBorder(javax.swing.BorderFactory.createTitledBorder(""));
        jPanel1.setMinimumSize(new java.awt.Dimension(0, 37));
        jPanel1.setPreferredSize(new java.awt.Dimension(788, 37));
        jPanel1.setRequestFocusEnabled(false);

        jButton_FirstPage_.setIcon(new javax.swing.ImageIcon(getClass().getResource("/org/cdms/ui/images/navigate_beginning.png"))); // NOI18N
        org.openide.awt.Mnemonics.setLocalizedText(jButton_FirstPage_, org.openide.util.NbBundle.getMessage(InvoiceTopComponent.class, "InvoiceTopComponent.jButton_FirstPage_.text")); // NOI18N
        jButton_FirstPage_.setToolTipText(org.openide.util.NbBundle.getMessage(InvoiceTopComponent.class, "InvoiceTopComponent.jButton_FirstPage_.toolTipText")); // NOI18N
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
        org.openide.awt.Mnemonics.setLocalizedText(jButton_PriorPage_, org.openide.util.NbBundle.getMessage(InvoiceTopComponent.class, "InvoiceTopComponent.jButton_PriorPage_.text")); // NOI18N
        jButton_PriorPage_.setToolTipText(org.openide.util.NbBundle.getMessage(InvoiceTopComponent.class, "InvoiceTopComponent.jButton_PriorPage_.toolTipText")); // NOI18N
        jButton_PriorPage_.setBorder(null);
        jButton_PriorPage_.setContentAreaFilled(false);
        jButton_PriorPage_.setMaximumSize(new java.awt.Dimension(16, 16));
        jButton_PriorPage_.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton_PriorPage_ActionPerformed(evt);
            }
        });

        jButton_NextPage_.setIcon(new javax.swing.ImageIcon(getClass().getResource("/org/cdms/ui/images/navigate_right.png"))); // NOI18N
        org.openide.awt.Mnemonics.setLocalizedText(jButton_NextPage_, org.openide.util.NbBundle.getMessage(InvoiceTopComponent.class, "InvoiceTopComponent.jButton_NextPage_.text")); // NOI18N
        jButton_NextPage_.setToolTipText(org.openide.util.NbBundle.getMessage(InvoiceTopComponent.class, "InvoiceTopComponent.jButton_NextPage_.toolTipText")); // NOI18N
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
        org.openide.awt.Mnemonics.setLocalizedText(jButton_LastPage_, org.openide.util.NbBundle.getMessage(InvoiceTopComponent.class, "InvoiceTopComponent.jButton_LastPage_.text")); // NOI18N
        jButton_LastPage_.setToolTipText(org.openide.util.NbBundle.getMessage(InvoiceTopComponent.class, "InvoiceTopComponent.jButton_LastPage_.toolTipText")); // NOI18N
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
        org.openide.awt.Mnemonics.setLocalizedText(jButton_Refresh_Table, org.openide.util.NbBundle.getMessage(InvoiceTopComponent.class, "InvoiceTopComponent.jButton_Refresh_Table.text")); // NOI18N
        jButton_Refresh_Table.setToolTipText(org.openide.util.NbBundle.getMessage(InvoiceTopComponent.class, "InvoiceTopComponent.jButton_Refresh_Table.toolTipText")); // NOI18N
        jButton_Refresh_Table.setContentAreaFilled(false);
        jButton_Refresh_Table.setMaximumSize(new java.awt.Dimension(16, 16));
        jButton_Refresh_Table.setMinimumSize(new java.awt.Dimension(16, 16));
        jButton_Refresh_Table.setPreferredSize(new java.awt.Dimension(16, 16));
        jButton_Refresh_Table.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton_Refresh_TableActionPerformed(evt);
            }
        });

        org.openide.awt.Mnemonics.setLocalizedText(jLabel18, org.openide.util.NbBundle.getMessage(InvoiceTopComponent.class, "InvoiceTopComponent.jLabel18.text")); // NOI18N

        jFormattedTextField_PageSize.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.NumberFormatter(new java.text.DecimalFormat("######"))));
        jFormattedTextField_PageSize.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        jFormattedTextField_PageSize.setText(org.openide.util.NbBundle.getMessage(InvoiceTopComponent.class, "InvoiceTopComponent.jFormattedTextField_PageSize.text")); // NOI18N

        org.openide.awt.Mnemonics.setLocalizedText(jLabel_PageNo, org.openide.util.NbBundle.getMessage(InvoiceTopComponent.class, "InvoiceTopComponent.jLabel_PageNo.text")); // NOI18N

        jTextField_PageNo.setEditable(false);
        jTextField_PageNo.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        jTextField_PageNo.setText(org.openide.util.NbBundle.getMessage(InvoiceTopComponent.class, "InvoiceTopComponent.jTextField_PageNo.text")); // NOI18N

        jTextField_RowCount.setEditable(false);
        jTextField_RowCount.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        jTextField_RowCount.setText(org.openide.util.NbBundle.getMessage(InvoiceTopComponent.class, "InvoiceTopComponent.jTextField_RowCount.text")); // NOI18N

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
                .addGap(18, 18, 18)
                .addComponent(jLabel18)
                .addGap(18, 18, 18)
                .addComponent(jFormattedTextField_PageSize, javax.swing.GroupLayout.PREFERRED_SIZE, 36, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(jLabel_PageNo)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jTextField_PageNo, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jTextField_RowCount, javax.swing.GroupLayout.PREFERRED_SIZE, 84, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jTextField_PageNo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jTextField_RowCount, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jFormattedTextField_PageSize)
                        .addComponent(jLabel18)
                        .addComponent(jLabel_PageNo))
                    .addComponent(jButton_LastPage_, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jButton_NextPage_, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jButton_PriorPage_, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jButton_FirstPage_, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jButton_Refresh_Table, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addGap(0, 0, Short.MAX_VALUE))
        );

        jTable_MasterEntity.setAutoCreateRowSorter(true);
        jTable_MasterEntity.setModel(new javax.swing.table.DefaultTableModel(
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
        jTable_MasterEntity.setGridColor(new java.awt.Color(204, 204, 204));
        jScrollPane1.setViewportView(jTable_MasterEntity);

        jTable_ChildEntity.setModel(new javax.swing.table.DefaultTableModel(
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
        jScrollPane2.setViewportView(jTable_ChildEntity);

        javax.swing.GroupLayout jPanel_TableLayout = new javax.swing.GroupLayout(jPanel_Table);
        jPanel_Table.setLayout(jPanel_TableLayout);
        jPanel_TableLayout.setHorizontalGroup(
            jPanel_TableLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel_TableLayout.createSequentialGroup()
                .addGroup(jPanel_TableLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel_TableLayout.createSequentialGroup()
                        .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 250, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(jScrollPane2))
                    .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, 1028, Short.MAX_VALUE))
                .addContainerGap())
        );
        jPanel_TableLayout.setVerticalGroup(
            jPanel_TableLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel_TableLayout.createSequentialGroup()
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel_TableLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 154, Short.MAX_VALUE)
                    .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE))
                .addContainerGap(47, Short.MAX_VALUE))
        );

        jPanel2.setBorder(javax.swing.BorderFactory.createTitledBorder(org.openide.util.NbBundle.getMessage(InvoiceTopComponent.class, "InvoiceTopComponent.jPanel2.border.title"))); // NOI18N

        org.openide.awt.Mnemonics.setLocalizedText(jLabel2, org.openide.util.NbBundle.getMessage(InvoiceTopComponent.class, "InvoiceTopComponent.jLabel2.text")); // NOI18N

        jTextField_ID_Filter.setText(org.openide.util.NbBundle.getMessage(InvoiceTopComponent.class, "InvoiceTopComponent.jTextField_ID_Filter.text")); // NOI18N

        jPanel4.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(204, 204, 204)));

        org.openide.awt.Mnemonics.setLocalizedText(jLabel1, org.openide.util.NbBundle.getMessage(InvoiceTopComponent.class, "InvoiceTopComponent.jLabel1.text_1")); // NOI18N

        org.openide.awt.Mnemonics.setLocalizedText(jLabel10, org.openide.util.NbBundle.getMessage(InvoiceTopComponent.class, "InvoiceTopComponent.jLabel10.text")); // NOI18N

        jTextField_User_firstName.setText(org.openide.util.NbBundle.getMessage(InvoiceTopComponent.class, "InvoiceTopComponent.jTextField_User_firstName.text")); // NOI18N

        org.openide.awt.Mnemonics.setLocalizedText(jLabel11, org.openide.util.NbBundle.getMessage(InvoiceTopComponent.class, "InvoiceTopComponent.jLabel11.text")); // NOI18N

        jTextField_Phone_Filter.setText(org.openide.util.NbBundle.getMessage(InvoiceTopComponent.class, "InvoiceTopComponent.jTextField_Phone_Filter.text")); // NOI18N

        javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel1)
                .addGap(18, 18, 18)
                .addComponent(jLabel10)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jTextField_User_firstName, javax.swing.GroupLayout.PREFERRED_SIZE, 82, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jLabel11)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jTextField_Phone_Filter, javax.swing.GroupLayout.PREFERRED_SIZE, 89, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(20, Short.MAX_VALUE))
        );
        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel4Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1)
                    .addComponent(jTextField_User_firstName, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel10)
                    .addComponent(jLabel11)
                    .addComponent(jTextField_Phone_Filter, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap())
        );

        jPanel5.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(204, 204, 204)));

        org.openide.awt.Mnemonics.setLocalizedText(jLabel12, org.openide.util.NbBundle.getMessage(InvoiceTopComponent.class, "InvoiceTopComponent.jLabel12.text")); // NOI18N

        org.openide.awt.Mnemonics.setLocalizedText(jLabel7, org.openide.util.NbBundle.getMessage(InvoiceTopComponent.class, "InvoiceTopComponent.jLabel7.text")); // NOI18N
        jLabel7.setMinimumSize(new java.awt.Dimension(24, 20));
        jLabel7.setPreferredSize(new java.awt.Dimension(24, 20));

        dateField_createDate_From.setMinimumSize(new java.awt.Dimension(52, 20));
        dateField_createDate_From.setPreferredSize(new java.awt.Dimension(52, 20));

        org.openide.awt.Mnemonics.setLocalizedText(jLabel9, org.openide.util.NbBundle.getMessage(InvoiceTopComponent.class, "InvoiceTopComponent.jLabel9.text")); // NOI18N
        jLabel9.setMaximumSize(new java.awt.Dimension(10, 20));
        jLabel9.setPreferredSize(new java.awt.Dimension(10, 20));

        dateField_createDate_To.setMinimumSize(new java.awt.Dimension(52, 20));
        dateField_createDate_To.setPreferredSize(new java.awt.Dimension(52, 20));

        javax.swing.GroupLayout jPanel5Layout = new javax.swing.GroupLayout(jPanel5);
        jPanel5.setLayout(jPanel5Layout);
        jPanel5Layout.setHorizontalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel12)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jLabel7, javax.swing.GroupLayout.PREFERRED_SIZE, 43, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(dateField_createDate_From, javax.swing.GroupLayout.PREFERRED_SIZE, 82, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(jLabel9, javax.swing.GroupLayout.PREFERRED_SIZE, 12, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(dateField_createDate_To, javax.swing.GroupLayout.PREFERRED_SIZE, 89, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(34, Short.MAX_VALUE))
        );
        jPanel5Layout.setVerticalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jLabel7, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jLabel12))
                    .addComponent(dateField_createDate_From, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel9, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(dateField_createDate_To, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        org.openide.awt.Mnemonics.setLocalizedText(jLabel8, org.openide.util.NbBundle.getMessage(InvoiceTopComponent.class, "InvoiceTopComponent.jLabel8.text")); // NOI18N

        jTextField1.setText(org.openide.util.NbBundle.getMessage(InvoiceTopComponent.class, "InvoiceTopComponent.jTextField1.text")); // NOI18N

        org.openide.awt.Mnemonics.setLocalizedText(jLabel3, org.openide.util.NbBundle.getMessage(InvoiceTopComponent.class, "InvoiceTopComponent.jLabel3.text")); // NOI18N

        org.openide.awt.Mnemonics.setLocalizedText(jLabel4, org.openide.util.NbBundle.getMessage(InvoiceTopComponent.class, "InvoiceTopComponent.jLabel4.text")); // NOI18N

        jTextField_LastName_Filter.setText(org.openide.util.NbBundle.getMessage(InvoiceTopComponent.class, "InvoiceTopComponent.jTextField_LastName_Filter.text")); // NOI18N

        org.openide.awt.Mnemonics.setLocalizedText(jLabel6, org.openide.util.NbBundle.getMessage(InvoiceTopComponent.class, "InvoiceTopComponent.jLabel6.text")); // NOI18N

        jTextField_User_lastName.setText(org.openide.util.NbBundle.getMessage(InvoiceTopComponent.class, "InvoiceTopComponent.jTextField_User_lastName.text")); // NOI18N

        jTextField_Email_Filter.setText(org.openide.util.NbBundle.getMessage(InvoiceTopComponent.class, "InvoiceTopComponent.jTextField_Email_Filter.text")); // NOI18N

        org.openide.awt.Mnemonics.setLocalizedText(jLabel5, org.openide.util.NbBundle.getMessage(InvoiceTopComponent.class, "InvoiceTopComponent.jLabel5.text")); // NOI18N

        org.openide.awt.Mnemonics.setLocalizedText(jButton_Search_, org.openide.util.NbBundle.getMessage(InvoiceTopComponent.class, "InvoiceTopComponent.jButton_Search_.text")); // NOI18N
        jButton_Search_.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton_Search_ActionPerformed(evt);
            }
        });

        org.openide.awt.Mnemonics.setLocalizedText(jButton_Clear_, org.openide.util.NbBundle.getMessage(InvoiceTopComponent.class, "InvoiceTopComponent.jButton_Clear_.text")); // NOI18N
        jButton_Clear_.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton_Clear_ActionPerformed(evt);
            }
        });

        jLabel_FilterError.setForeground(new java.awt.Color(255, 51, 0));
        org.openide.awt.Mnemonics.setLocalizedText(jLabel_FilterError, org.openide.util.NbBundle.getMessage(InvoiceTopComponent.class, "InvoiceTopComponent.jLabel_FilterError.text")); // NOI18N

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(jButton_Search_)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jButton_Clear_, javax.swing.GroupLayout.PREFERRED_SIZE, 72, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jLabel_FilterError, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                            .addGroup(jPanel2Layout.createSequentialGroup()
                                .addComponent(jLabel8)
                                .addGap(36, 36, 36)
                                .addComponent(jTextField1, javax.swing.GroupLayout.PREFERRED_SIZE, 65, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(91, 91, 91)
                                .addComponent(jLabel3)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(jTextField_FirstName_Filter, javax.swing.GroupLayout.PREFERRED_SIZE, 82, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(jLabel4)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(jTextField_LastName_Filter, javax.swing.GroupLayout.PREFERRED_SIZE, 90, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(18, 18, 18)
                                .addComponent(jLabel6)
                                .addGap(18, 18, 18)
                                .addComponent(jTextField_User_lastName, javax.swing.GroupLayout.PREFERRED_SIZE, 92, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(18, 18, 18)
                                .addComponent(jLabel5)
                                .addGap(18, 18, 18)
                                .addComponent(jTextField_Email_Filter))
                            .addGroup(jPanel2Layout.createSequentialGroup()
                                .addComponent(jLabel2)
                                .addGap(35, 35, 35)
                                .addComponent(jTextField_ID_Filter, javax.swing.GroupLayout.PREFERRED_SIZE, 64, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(jPanel4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(jPanel5, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addGap(0, 61, Short.MAX_VALUE)))
                .addContainerGap())
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jPanel5, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGap(12, 12, 12)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel2)
                            .addComponent(jTextField_ID_Filter, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addComponent(jPanel4, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel8)
                    .addComponent(jTextField1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel3)
                    .addComponent(jTextField_FirstName_Filter, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel4)
                    .addComponent(jTextField_LastName_Filter, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel6)
                    .addComponent(jTextField_User_lastName, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jTextField_Email_Filter, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel5))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 16, Short.MAX_VALUE)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jButton_Search_)
                    .addComponent(jButton_Clear_)
                    .addComponent(jLabel_FilterError)))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel_Table, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel_Table, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
    }// </editor-fold>//GEN-END:initComponents

    private void jButton_FirstPage_ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton_FirstPage_ActionPerformed
        queryPage.setPageNo(0);
        doFilter();
    }//GEN-LAST:event_jButton_FirstPage_ActionPerformed

    private void jButton_PriorPage_ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton_PriorPage_ActionPerformed
        queryPage.setPageNo(queryPage.getPageNo() - 1);
        doFilter();
    }//GEN-LAST:event_jButton_PriorPage_ActionPerformed

    private void jButton_NextPage_ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton_NextPage_ActionPerformed
        queryPage.setPageNo(queryPage.getPageNo() + 1);
        doFilter();
    }//GEN-LAST:event_jButton_NextPage_ActionPerformed

    private void jButton_LastPage_ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton_LastPage_ActionPerformed
        int lastPage = (int) (queryPage.getRowCount() / queryPage.getPageSize() - 1);
        if (queryPage.getRowCount() % queryPage.getPageSize() != 0) {
            lastPage++;
        }

        queryPage.setPageNo(lastPage);
        doFilter();
    }//GEN-LAST:event_jButton_LastPage_ActionPerformed

    private void jButton_Refresh_TableActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton_Refresh_TableActionPerformed
        //int pageSize = Integer.parseInt(jFormattedTextField_PageSize.getText());
        //queryPage.setPageSize(pageSize);
        //
        //queryPage.setPageNo(0);

        //TODO in production. A user may change page size.
        //     So we must  keep it im mind
        doFilter();
    }//GEN-LAST:event_jButton_Refresh_TableActionPerformed

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

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private net.sf.nachocalendar.components.DateField dateField_createDate_From;
    private net.sf.nachocalendar.components.DateField dateField_createDate_To;
    private javax.swing.JButton jButton_Clear_;
    private javax.swing.JButton jButton_FirstPage_;
    private javax.swing.JButton jButton_LastPage_;
    private javax.swing.JButton jButton_NextPage_;
    private javax.swing.JButton jButton_PriorPage_;
    private javax.swing.JButton jButton_Refresh_Table;
    private javax.swing.JButton jButton_Search_;
    private javax.swing.JFormattedTextField jFormattedTextField_PageSize;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel18;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JLabel jLabel_FilterError;
    private javax.swing.JLabel jLabel_PageNo;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JPanel jPanel_Table;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JTable jTable_ChildEntity;
    private javax.swing.JTable jTable_MasterEntity;
    private javax.swing.JTextField jTextField1;
    private javax.swing.JTextField jTextField_Email_Filter;
    private javax.swing.JTextField jTextField_FirstName_Filter;
    private javax.swing.JTextField jTextField_ID_Filter;
    private javax.swing.JTextField jTextField_LastName_Filter;
    private javax.swing.JTextField jTextField_PageNo;
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

                        QueryPage<Invoice> q = (QueryPage<Invoice>) entityAsyncFilter.getResult();
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
//                    jButton_Search_.setEnabled(true);
                    enableNavigateOperations(true);
                    
                }
            });

        }

    }//inner FilterSearchHandler
    
}
