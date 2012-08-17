package org.cdms.ui.statictics;

import java.awt.EventQueue;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import org.cdms.entities.Customer;
import org.cdms.entities.Invoice;
import org.cdms.entities.InvoiceItem;
import org.cdms.entities.InvoiceStatView;
import org.cdms.entities.ProductItem;
import org.cdms.entities.User;
import org.cdms.remoting.QueryPage;
import org.cdms.ui.common.CustomerAsyncServiceProvider;
import org.cdms.ui.common.EntityAsyncService;
import org.cdms.ui.common.EntityBinder;
import org.cdms.ui.common.EntityBinderImpl;
import org.cdms.ui.common.ErrorMessageBuilder;
import org.cdms.ui.common.TableBinder;
import org.cdms.ui.common.dialog.ErrorDetailsHandler;
import org.jdesktop.beansbinding.BindingGroup;
import org.jdesktop.observablecollections.ObservableCollections;
import org.netbeans.api.settings.ConvertAsProperties;
import org.openide.util.Lookup;
import org.openide.util.NbBundle.Messages;
import org.openide.util.Task;
import org.openide.util.TaskListener;
import org.openide.windows.TopComponent;

/**
 * Top component which displays something.
 */
@ConvertAsProperties(
    dtd = "-//org.cdms.ui.statictics//Statistics//EN",
autostore = false)
@TopComponent.Description(
    preferredID = "statisticsTopComponent",
    iconBase = "org/cdms/ui/statictics/statistics16x16.png",
persistenceType = TopComponent.PERSISTENCE_ALWAYS)
@TopComponent.Registration(mode = "editor", openAtStartup = false)
@Messages({
    "CTL_StatisticsAction=Statistics",
    "CTL_StatisticsTopComponent=Statistics Window",
    "HINT_StatisticsTopComponent=Open a Statistics window"
})
public final class StatisticsTopComponent extends TopComponent {
    private EntityAsyncService customerService;
    private EntityAsyncService customerAsyncFilter;    
    private EntityAsyncService invoiceService;    
    private ErrorDetailsHandler errorDetailsHandler = new ErrorDetailsHandler();

  /*-------------------------------------------------------------
     * Invoice entities fields, properties and variables
     --------------------------------------------------------------*/
    private EntityBinder invoiceFilterBinder;
    private Invoice invoiceAsFilter = new Invoice();
    private EntityBinder userFilterBinder;
    private EntityBinder customerFilterBinder;
    private User userAsFilter = new User();
    
    private Customer customerAsFilter = new Customer();
    private BindingGroup invoiceFilterBindingGroup = new BindingGroup();
    private BindingGroup userBindingGroup = new BindingGroup();
    private BindingGroup customerBindingGroup = new BindingGroup();
    private TableBinder invoiceTableBinder;
    private TableBinder invoiceItemAsChildTableBinder;
    List<Invoice> invoiceFilterResult = null;
    
    
    //QueryPage<Invoice> invoiceQueryPage;
    QueryPage<InvoiceStatView> statisticsQueryPage;
    StatisticsAsyncService statisticsAsyncService;
    
    private List<Customer> customerFilterResult = null;
    private TableBinder customerTableBinder;
    private QueryPage<Customer> customerQueryPage;
    
    /*-------------------------------------------------------------
     * ProductItem fields, properties and variables
     --------------------------------------------------------------*/
    EntityBinder productItemFilterBinder;
    BindingGroup productItemFilterBindingGroup = new BindingGroup();
    ProductItem productItemAsFilter = new ProductItem();
    List<ProductItem> productItemFilterResult;
    TableBinder productItemTableBinder;
    ProductItem productItemToInsert;
    EntityBinder productItemToInsertBinder;
//    BindingGroup productItemToInsertBindingGroup;
    
//    ProductItemAsyncService productItemAsyncFilter = new ProductItemAsyncService();
    QueryPage<Object[]> productItemQueryPage;    
    public StatisticsTopComponent() {
        initComponents();
        statisticsQueryPage = new QueryPage<InvoiceStatView>();
        customerQueryPage = new QueryPage<Customer>();
        
        initCustomerFilterComponents();        
        initPageNavigator();
        initInvoiceTableComponents();
        initCustomerTableComponents();
        
        setName(Bundle.CTL_StatisticsTopComponent());
        setToolTipText(Bundle.HINT_StatisticsTopComponent());

    }
    protected void initCustomerFilterComponents() {
        customerFilterBinder = new EntityBinderImpl(customerBindingGroup, this);
        customerFilterBinder.addTextFieldBinder(jTextField_FirstName_Filter, "customerAsFilter.firstName");
        customerFilterBinder.addTextFieldBinder(jTextField_LastName_Filter, "customerAsFilter.lastName");
        customerFilterBinder.addTextFieldBinder(jTextField_Email_Filter, "customerAsFilter.email");
        customerFilterBinder.addTextFieldBinder(jTextField_Phone_Filter, "customerAsFilter.phone");

        customerAsFilter.setCreatedBy(userAsFilter);
        customerBindingGroup.bind();


    }
    protected void hideErrors() {
        jLabel_Errors.setVisible(false);
        jButton_Errors_Details.setVisible(false);

    }

    protected void showErrors(String message) {
        jLabel_Errors.setText(message);
        jLabel_Errors.setVisible(true);
        jButton_Errors_Details.setVisible(true);

    }
    protected void initPageNavigator() {
        int pageSize = Integer.parseInt(jFormattedTextField_Invoice_PageSize.getText());
        statisticsQueryPage.setPageSize(pageSize);

        jTextField_Invoice_PageNo.setText("" + statisticsQueryPage.getPageNo());
        jTextField_Invoice_RowCount.setText("" + statisticsQueryPage.getRowCount());


        if (statisticsQueryPage.getRowCount() == 0) {
            jButton_Invoice_FirstPage_.setEnabled(false);
            jButton_Invoice_LastPage_.setEnabled(false);
            jButton_Invoice_NextPage_.setEnabled(false);
            jButton_Invoice_PriorPage_.setEnabled(false);
            return;
        }


        int lastPage = (int) (statisticsQueryPage.getRowCount() / statisticsQueryPage.getPageSize() - 1);
        if (statisticsQueryPage.getRowCount() % statisticsQueryPage.getPageSize() != 0) {
            lastPage++;
        }
        if (statisticsQueryPage.getPageNo() == lastPage) {
            jButton_Invoice_LastPage_.setEnabled(false);
            jButton_Invoice_NextPage_.setEnabled(false);
            jButton_Invoice_FirstPage_.setEnabled(true);
            jButton_Invoice_PriorPage_.setEnabled(true);
        } else if (statisticsQueryPage.getPageNo() == 0) {
            jButton_Invoice_LastPage_.setEnabled(true);
            jButton_Invoice_NextPage_.setEnabled(true);
            jButton_Invoice_FirstPage_.setEnabled(false);
            jButton_Invoice_PriorPage_.setEnabled(false);
        } else {
            jButton_Invoice_LastPage_.setEnabled(true);
            jButton_Invoice_NextPage_.setEnabled(true);
            jButton_Invoice_FirstPage_.setEnabled(true);
            jButton_Invoice_PriorPage_.setEnabled(true);
        }
    }
    
    private void enableInvoiceOperations(boolean enabled) {
        jTable_Invoice.setEnabled(enabled);

//        jButton_Search_.setEnabled(enabled);
//        jButton_InvoiceItem_Add_To_Invoice.setEnabled(enabled);
        enableNavigateOperations(enabled);
    }

  
    public void enableNavigateOperations(boolean enabled) {
        jButton_Invoice_FirstPage_.setEnabled(enabled);
        jButton_Invoice_LastPage_.setEnabled(enabled);
        jButton_Invoice_NextPage_.setEnabled(enabled);
        jButton_Invoice_PriorPage_.setEnabled(enabled);
        jButton_Refresh_Invoice_Table.setEnabled(enabled);
        if (enabled) {
            initPageNavigator();
        }
    }
    public Customer getCustomerAsFilter() {
        return customerAsFilter;
    }

    public void setCustomerAsFilter(Customer customerFilter) {
        this.customerAsFilter = customerFilter;
    }
    
    protected void initCustomerTableComponents() {
        if (customerTableBinder != null) {
            customerTableBinder.getBindingGroup().unbind();
        }
        if ( customerFilterResult == null ) {
            customerFilterResult = ObservableCollections.observableList(
                    new ArrayList<Customer>());
            
        } else {
            customerFilterResult = ObservableCollections.observableList(
                    customerFilterResult);
        }

        customerTableBinder = new TableBinder(jTable_Customer, customerFilterResult);

        customerTableBinder.addColumn("id", Long.class);
        //customerTableBinder.addColumn("version", Long.class);

        customerTableBinder.addColumn("firstName", String.class, "First Name");
        customerTableBinder.addColumn("lastName", String.class);
        //customerTableBinder.addColumn("phone", String.class);
        customerTableBinder.addColumn("email", String.class);
        //customerTableBinder.addColumn("createdAt", Date.class);
        //customerTableBinder.addColumn("createdBy.firstName", String.class);
        //customerTableBinder.addColumn("createdBy.lastName", String.class);


        customerTableBinder.bindTable();


        customerTableBinder.refresh();
        if (!customerFilterResult.isEmpty()) {
            jTable_Customer.setRowSelectionInterval(0, 0);
        }
        customerTableBinder.updateMasterColumnModel();
    }
    
    protected void initInvoiceTableComponents() {
        
        if (invoiceTableBinder != null) {
            invoiceTableBinder.getBindingGroup().unbind();
        }
        if ( invoiceFilterResult == null ) {
            invoiceFilterResult = ObservableCollections.observableList(
                new ArrayList<Invoice>());

        }
        invoiceFilterResult = ObservableCollections.observableList(
                invoiceFilterResult);

        invoiceTableBinder = new TableBinder(jTable_Invoice, invoiceFilterResult);

        invoiceTableBinder.addColumn("id", Long.class);

        invoiceTableBinder.addColumn("customer.firstName", String.class, "First Name");
        invoiceTableBinder.addColumn("customer.lastName", String.class, "Last Name");
        
        
        
        if (!(invoiceFilterResult == null || invoiceFilterResult.isEmpty())) {
        } else {
            List<InvoiceItem> ol = ObservableCollections.observableList(
                new ArrayList<InvoiceItem>());

        }

        invoiceTableBinder.bindTable();

        invoiceTableBinder.refresh();
        
        if (!invoiceFilterResult.isEmpty()) {
            jTable_Invoice.setRowSelectionInterval(0, 0);
        }

        //invoiceTableBinder.updateMasterColumnModel();
        invoiceTableBinder.updateColumnModel(jTable_Invoice);

    }

    protected void doCustomerFilter() {
        hideErrors();
        customerAsyncFilter = getCustomerService();
        enableNavigateOperations(false);
        try {
            customerQueryPage.setEntityAsExample(customerAsFilter);
            customerQueryPage.setQueryResult(new ArrayList<Customer>());
            customerAsyncFilter.findByExample(new FilterSeachHandler(), customerQueryPage); // TODO paging
        } catch (Exception e) {
            System.out.println("ERROR");
        }

    }

    protected void doStatistics() {
        hideErrors();
        statisticsAsyncService = new StatisticsAsyncService();
        enableNavigateOperations(false);
        try {
            //statisticsQueryPage.setEntityAsExample(customerAsFilter);
            statisticsQueryPage.setQueryResult(new ArrayList());
//            statisticsAsyncService.requestInvoice(new InvoiceStatisticsHandler(), statisticsQueryPage, (Date)dateField_createDate_From.getValue(), (Date)dateField_createDate_To.getValue()); // TODO paging
            Object[] param = new Object[] {new Long(10),dateField_createDate_From.getValue(),dateField_createDate_To.getValue() };
            statisticsQueryPage.setParam(param);
            statisticsAsyncService.requestInvoice(new InvoiceStatisticsHandler(), statisticsQueryPage); // TODO paging            
        } catch (Exception e) {
            System.out.println("ERROR");
        }

    }
    
    public EntityAsyncService getCustomerService() {
        CustomerAsyncServiceProvider  eas = Lookup.getDefault().lookup(CustomerAsyncServiceProvider.class);
        return (EntityAsyncService)eas.getInstance();
    }
            
    public EntityAsyncService getInvoiceService() {
        return invoiceService;
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel2 = new javax.swing.JPanel();
        jPanel_Error_Msg1 = new javax.swing.JPanel();
        jButton_Errors_Details = new javax.swing.JButton();
        jPanel_Error_Msg = new javax.swing.JPanel();
        jLabel_Errors = new javax.swing.JLabel();
        jButton_Errors_Details1 = new javax.swing.JButton();
        jScrollPane1 = new javax.swing.JScrollPane();
        jTable_Customer = new javax.swing.JTable();
        jPanel_Filter = new javax.swing.JPanel();
        jLabel8 = new javax.swing.JLabel();
        jTextField_CustomerId_Filter = new javax.swing.JTextField();
        jLabel3 = new javax.swing.JLabel();
        jTextField_FirstName_Filter = new javax.swing.JTextField();
        jLabel4 = new javax.swing.JLabel();
        jTextField_LastName_Filter = new javax.swing.JTextField();
        jLabel6 = new javax.swing.JLabel();
        jTextField_Phone_Filter = new javax.swing.JTextField();
        jLabel5 = new javax.swing.JLabel();
        jTextField_Email_Filter = new javax.swing.JTextField();
        jButton_Customer_Search_Filter_ = new javax.swing.JButton();
        jButton_Customer_Clear_Filter = new javax.swing.JButton();
        jPanel5 = new javax.swing.JPanel();
        jLabel7 = new javax.swing.JLabel();
        dateField_createDate_From = new net.sf.nachocalendar.components.DateField();
        jLabel9 = new javax.swing.JLabel();
        dateField_createDate_To = new net.sf.nachocalendar.components.DateField();
        jCheckBox_OnlySelectedCustomer = new javax.swing.JCheckBox();
        jPanel1 = new javax.swing.JPanel();
        jPanel_Invoice_Navigator = new javax.swing.JPanel();
        jButton_Invoice_FirstPage_ = new javax.swing.JButton();
        jButton_Invoice_PriorPage_ = new javax.swing.JButton();
        jButton_Invoice_NextPage_ = new javax.swing.JButton();
        jButton_Invoice_LastPage_ = new javax.swing.JButton();
        jButton_Refresh_Invoice_Table = new javax.swing.JButton();
        jLabel_Invoice_PageSize = new javax.swing.JLabel();
        jFormattedTextField_Invoice_PageSize = new javax.swing.JFormattedTextField();
        jLabel_Invoice_PageNo = new javax.swing.JLabel();
        jTextField_Invoice_PageNo = new javax.swing.JTextField();
        jTextField_Invoice_RowCount = new javax.swing.JTextField();
        jButton_doStatistics = new javax.swing.JButton();
        jScrollPane2 = new javax.swing.JScrollPane();
        jTable_Invoice = new javax.swing.JTable();

        jPanel2.setBorder(javax.swing.BorderFactory.createTitledBorder(org.openide.util.NbBundle.getMessage(StatisticsTopComponent.class, "StatisticsTopComponent.jPanel2.border.title"))); // NOI18N

        org.openide.awt.Mnemonics.setLocalizedText(jButton_Errors_Details, org.openide.util.NbBundle.getMessage(StatisticsTopComponent.class, "StatisticsTopComponent.jButton_Errors_Details.text")); // NOI18N
        jButton_Errors_Details.setContentAreaFilled(false);
        jButton_Errors_Details.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton_Errors_DetailsActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel_Error_Msg1Layout = new javax.swing.GroupLayout(jPanel_Error_Msg1);
        jPanel_Error_Msg1.setLayout(jPanel_Error_Msg1Layout);
        jPanel_Error_Msg1Layout.setHorizontalGroup(
            jPanel_Error_Msg1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel_Error_Msg1Layout.createSequentialGroup()
                .addGap(166, 166, 166)
                .addComponent(jButton_Errors_Details)
                .addGap(0, 59, Short.MAX_VALUE))
        );
        jPanel_Error_Msg1Layout.setVerticalGroup(
            jPanel_Error_Msg1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jButton_Errors_Details, javax.swing.GroupLayout.PREFERRED_SIZE, 0, javax.swing.GroupLayout.PREFERRED_SIZE)
        );

        jLabel_Errors.setForeground(new java.awt.Color(255, 51, 0));
        org.openide.awt.Mnemonics.setLocalizedText(jLabel_Errors, org.openide.util.NbBundle.getMessage(StatisticsTopComponent.class, "StatisticsTopComponent.jLabel_Errors.text")); // NOI18N

        org.openide.awt.Mnemonics.setLocalizedText(jButton_Errors_Details1, org.openide.util.NbBundle.getMessage(StatisticsTopComponent.class, "StatisticsTopComponent.jButton_Errors_Details1.text")); // NOI18N
        jButton_Errors_Details1.setContentAreaFilled(false);
        jButton_Errors_Details1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton_Errors_Details1ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel_Error_MsgLayout = new javax.swing.GroupLayout(jPanel_Error_Msg);
        jPanel_Error_Msg.setLayout(jPanel_Error_MsgLayout);
        jPanel_Error_MsgLayout.setHorizontalGroup(
            jPanel_Error_MsgLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel_Error_MsgLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel_Errors, javax.swing.GroupLayout.DEFAULT_SIZE, 152, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jButton_Errors_Details1)
                .addGap(0, 59, Short.MAX_VALUE))
        );
        jPanel_Error_MsgLayout.setVerticalGroup(
            jPanel_Error_MsgLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel_Error_MsgLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                .addComponent(jButton_Errors_Details1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jLabel_Errors, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
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

        jPanel_Filter.setBorder(javax.swing.BorderFactory.createTitledBorder(org.openide.util.NbBundle.getMessage(StatisticsTopComponent.class, "StatisticsTopComponent.jPanel_Filter.border.title"))); // NOI18N

        org.openide.awt.Mnemonics.setLocalizedText(jLabel8, org.openide.util.NbBundle.getMessage(StatisticsTopComponent.class, "StatisticsTopComponent.jLabel8.text")); // NOI18N

        jTextField_CustomerId_Filter.setText(org.openide.util.NbBundle.getMessage(StatisticsTopComponent.class, "StatisticsTopComponent.jTextField_CustomerId_Filter.text")); // NOI18N

        org.openide.awt.Mnemonics.setLocalizedText(jLabel3, org.openide.util.NbBundle.getMessage(StatisticsTopComponent.class, "StatisticsTopComponent.jLabel3.text")); // NOI18N

        org.openide.awt.Mnemonics.setLocalizedText(jLabel4, org.openide.util.NbBundle.getMessage(StatisticsTopComponent.class, "StatisticsTopComponent.jLabel4.text")); // NOI18N

        jTextField_LastName_Filter.setText(org.openide.util.NbBundle.getMessage(StatisticsTopComponent.class, "StatisticsTopComponent.jTextField_LastName_Filter.text")); // NOI18N

        org.openide.awt.Mnemonics.setLocalizedText(jLabel6, org.openide.util.NbBundle.getMessage(StatisticsTopComponent.class, "StatisticsTopComponent.jLabel6.text")); // NOI18N

        jTextField_Phone_Filter.setText(org.openide.util.NbBundle.getMessage(StatisticsTopComponent.class, "StatisticsTopComponent.jTextField_Phone_Filter.text")); // NOI18N

        org.openide.awt.Mnemonics.setLocalizedText(jLabel5, org.openide.util.NbBundle.getMessage(StatisticsTopComponent.class, "StatisticsTopComponent.jLabel5.text")); // NOI18N

        jTextField_Email_Filter.setText(org.openide.util.NbBundle.getMessage(StatisticsTopComponent.class, "StatisticsTopComponent.jTextField_Email_Filter.text")); // NOI18N

        org.openide.awt.Mnemonics.setLocalizedText(jButton_Customer_Search_Filter_, org.openide.util.NbBundle.getMessage(StatisticsTopComponent.class, "StatisticsTopComponent.jButton_Customer_Search_Filter_.text")); // NOI18N
        jButton_Customer_Search_Filter_.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton_Customer_Search_Filter_ActionPerformed(evt);
            }
        });

        org.openide.awt.Mnemonics.setLocalizedText(jButton_Customer_Clear_Filter, org.openide.util.NbBundle.getMessage(StatisticsTopComponent.class, "StatisticsTopComponent.jButton_Customer_Clear_Filter.text")); // NOI18N
        jButton_Customer_Clear_Filter.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton_Customer_Clear_FilterActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel_FilterLayout = new javax.swing.GroupLayout(jPanel_Filter);
        jPanel_Filter.setLayout(jPanel_FilterLayout);
        jPanel_FilterLayout.setHorizontalGroup(
            jPanel_FilterLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel_FilterLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel_FilterLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel_FilterLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                        .addGroup(jPanel_FilterLayout.createSequentialGroup()
                            .addComponent(jLabel8)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                            .addComponent(jTextField_CustomerId_Filter, javax.swing.GroupLayout.PREFERRED_SIZE, 65, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGroup(jPanel_FilterLayout.createSequentialGroup()
                            .addComponent(jLabel3)
                            .addGap(18, 18, 18)
                            .addComponent(jTextField_FirstName_Filter, javax.swing.GroupLayout.PREFERRED_SIZE, 82, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGroup(jPanel_FilterLayout.createSequentialGroup()
                            .addComponent(jLabel4)
                            .addGap(18, 18, 18)
                            .addComponent(jTextField_LastName_Filter, javax.swing.GroupLayout.PREFERRED_SIZE, 90, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel_FilterLayout.createSequentialGroup()
                            .addComponent(jLabel6)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jTextField_Phone_Filter, javax.swing.GroupLayout.PREFERRED_SIZE, 89, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addGroup(jPanel_FilterLayout.createSequentialGroup()
                        .addComponent(jLabel5)
                        .addGap(41, 41, 41)
                        .addComponent(jTextField_Email_Filter, javax.swing.GroupLayout.PREFERRED_SIZE, 143, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel_FilterLayout.createSequentialGroup()
                        .addComponent(jButton_Customer_Search_Filter_)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jButton_Customer_Clear_Filter, javax.swing.GroupLayout.PREFERRED_SIZE, 72, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel_FilterLayout.setVerticalGroup(
            jPanel_FilterLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel_FilterLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel_FilterLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel8)
                    .addComponent(jTextField_CustomerId_Filter, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel_FilterLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jLabel3)
                    .addComponent(jTextField_FirstName_Filter, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel_FilterLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel4)
                    .addComponent(jTextField_LastName_Filter, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel_FilterLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jTextField_Phone_Filter, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel6))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel_FilterLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jTextField_Email_Filter, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel5))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 9, Short.MAX_VALUE)
                .addGroup(jPanel_FilterLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jButton_Customer_Search_Filter_)
                    .addComponent(jButton_Customer_Clear_Filter)))
        );

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 318, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(jPanel_Filter, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, Short.MAX_VALUE))
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jPanel_Error_Msg, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(151, 151, 151))
            .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(jPanel2Layout.createSequentialGroup()
                    .addGap(301, 301, 301)
                    .addComponent(jPanel_Error_Msg1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addComponent(jPanel_Error_Msg, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                    .addComponent(jPanel_Filter, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(jPanel2Layout.createSequentialGroup()
                    .addGap(47, 47, 47)
                    .addComponent(jPanel_Error_Msg1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addContainerGap(183, Short.MAX_VALUE)))
        );

        jPanel5.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(204, 204, 204)));

        org.openide.awt.Mnemonics.setLocalizedText(jLabel7, org.openide.util.NbBundle.getMessage(StatisticsTopComponent.class, "StatisticsTopComponent.jLabel7.text")); // NOI18N
        jLabel7.setMinimumSize(new java.awt.Dimension(24, 20));
        jLabel7.setPreferredSize(new java.awt.Dimension(24, 20));

        dateField_createDate_From.setMinimumSize(new java.awt.Dimension(52, 20));
        dateField_createDate_From.setPreferredSize(new java.awt.Dimension(52, 20));

        org.openide.awt.Mnemonics.setLocalizedText(jLabel9, org.openide.util.NbBundle.getMessage(StatisticsTopComponent.class, "StatisticsTopComponent.jLabel9.text")); // NOI18N
        jLabel9.setMaximumSize(new java.awt.Dimension(10, 20));
        jLabel9.setPreferredSize(new java.awt.Dimension(10, 20));

        dateField_createDate_To.setMinimumSize(new java.awt.Dimension(52, 20));
        dateField_createDate_To.setPreferredSize(new java.awt.Dimension(52, 20));

        org.openide.awt.Mnemonics.setLocalizedText(jCheckBox_OnlySelectedCustomer, org.openide.util.NbBundle.getMessage(StatisticsTopComponent.class, "StatisticsTopComponent.jCheckBox_OnlySelectedCustomer.text")); // NOI18N

        javax.swing.GroupLayout jPanel5Layout = new javax.swing.GroupLayout(jPanel5);
        jPanel5.setLayout(jPanel5Layout);
        jPanel5Layout.setHorizontalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addGap(73, 73, 73)
                .addComponent(jLabel7, javax.swing.GroupLayout.PREFERRED_SIZE, 43, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(dateField_createDate_From, javax.swing.GroupLayout.PREFERRED_SIZE, 82, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(jLabel9, javax.swing.GroupLayout.PREFERRED_SIZE, 12, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(dateField_createDate_To, javax.swing.GroupLayout.PREFERRED_SIZE, 89, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(29, 29, 29)
                .addComponent(jCheckBox_OnlySelectedCustomer)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel5Layout.setVerticalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(dateField_createDate_From, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(jPanel5Layout.createSequentialGroup()
                        .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(jCheckBox_OnlySelectedCustomer, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jLabel7, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel9, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(dateField_createDate_To, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addContainerGap())
        );

        jPanel1.setBorder(javax.swing.BorderFactory.createTitledBorder(org.openide.util.NbBundle.getMessage(StatisticsTopComponent.class, "StatisticsTopComponent.jPanel1.border.title"))); // NOI18N

        jPanel_Invoice_Navigator.setBorder(javax.swing.BorderFactory.createTitledBorder(""));
        jPanel_Invoice_Navigator.setMinimumSize(new java.awt.Dimension(0, 37));
        jPanel_Invoice_Navigator.setPreferredSize(new java.awt.Dimension(788, 37));
        jPanel_Invoice_Navigator.setRequestFocusEnabled(false);

        jButton_Invoice_FirstPage_.setIcon(new javax.swing.ImageIcon(getClass().getResource("/org/cdms/ui/images/navigate_beginning.png"))); // NOI18N
        org.openide.awt.Mnemonics.setLocalizedText(jButton_Invoice_FirstPage_, org.openide.util.NbBundle.getMessage(StatisticsTopComponent.class, "StatisticsTopComponent.jButton_Invoice_FirstPage_.text")); // NOI18N
        jButton_Invoice_FirstPage_.setToolTipText(org.openide.util.NbBundle.getMessage(StatisticsTopComponent.class, "StatisticsTopComponent.jButton_Invoice_FirstPage_.toolTipText")); // NOI18N
        jButton_Invoice_FirstPage_.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 1, 1, 1));
        jButton_Invoice_FirstPage_.setContentAreaFilled(false);
        jButton_Invoice_FirstPage_.setMargin(new java.awt.Insets(0, 0, 0, 0));
        jButton_Invoice_FirstPage_.setMaximumSize(new java.awt.Dimension(16, 16));
        jButton_Invoice_FirstPage_.setMinimumSize(new java.awt.Dimension(16, 16));
        jButton_Invoice_FirstPage_.setPreferredSize(new java.awt.Dimension(16, 16));
        jButton_Invoice_FirstPage_.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton_Invoice_FirstPage_ActionPerformed(evt);
            }
        });

        jButton_Invoice_PriorPage_.setIcon(new javax.swing.ImageIcon(getClass().getResource("/org/cdms/ui/images/navigate_left.png"))); // NOI18N
        org.openide.awt.Mnemonics.setLocalizedText(jButton_Invoice_PriorPage_, org.openide.util.NbBundle.getMessage(StatisticsTopComponent.class, "StatisticsTopComponent.jButton_Invoice_PriorPage_.text")); // NOI18N
        jButton_Invoice_PriorPage_.setToolTipText(org.openide.util.NbBundle.getMessage(StatisticsTopComponent.class, "StatisticsTopComponent.jButton_Invoice_PriorPage_.toolTipText")); // NOI18N
        jButton_Invoice_PriorPage_.setBorder(null);
        jButton_Invoice_PriorPage_.setContentAreaFilled(false);
        jButton_Invoice_PriorPage_.setMaximumSize(new java.awt.Dimension(16, 16));
        jButton_Invoice_PriorPage_.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton_Invoice_PriorPage_ActionPerformed(evt);
            }
        });

        jButton_Invoice_NextPage_.setIcon(new javax.swing.ImageIcon(getClass().getResource("/org/cdms/ui/images/navigate_right.png"))); // NOI18N
        org.openide.awt.Mnemonics.setLocalizedText(jButton_Invoice_NextPage_, org.openide.util.NbBundle.getMessage(StatisticsTopComponent.class, "StatisticsTopComponent.jButton_Invoice_NextPage_.text")); // NOI18N
        jButton_Invoice_NextPage_.setToolTipText(org.openide.util.NbBundle.getMessage(StatisticsTopComponent.class, "StatisticsTopComponent.jButton_Invoice_NextPage_.toolTipText")); // NOI18N
        jButton_Invoice_NextPage_.setContentAreaFilled(false);
        jButton_Invoice_NextPage_.setMaximumSize(new java.awt.Dimension(16, 16));
        jButton_Invoice_NextPage_.setMinimumSize(new java.awt.Dimension(16, 16));
        jButton_Invoice_NextPage_.setPreferredSize(new java.awt.Dimension(16, 16));
        jButton_Invoice_NextPage_.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton_Invoice_NextPage_ActionPerformed(evt);
            }
        });

        jButton_Invoice_LastPage_.setIcon(new javax.swing.ImageIcon(getClass().getResource("/org/cdms/ui/images/navigate_end.png"))); // NOI18N
        org.openide.awt.Mnemonics.setLocalizedText(jButton_Invoice_LastPage_, org.openide.util.NbBundle.getMessage(StatisticsTopComponent.class, "StatisticsTopComponent.jButton_Invoice_LastPage_.text")); // NOI18N
        jButton_Invoice_LastPage_.setToolTipText(org.openide.util.NbBundle.getMessage(StatisticsTopComponent.class, "StatisticsTopComponent.jButton_Invoice_LastPage_.toolTipText")); // NOI18N
        jButton_Invoice_LastPage_.setContentAreaFilled(false);
        jButton_Invoice_LastPage_.setMaximumSize(new java.awt.Dimension(16, 16));
        jButton_Invoice_LastPage_.setMinimumSize(new java.awt.Dimension(16, 16));
        jButton_Invoice_LastPage_.setPreferredSize(new java.awt.Dimension(16, 16));
        jButton_Invoice_LastPage_.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton_Invoice_LastPage_ActionPerformed(evt);
            }
        });

        jButton_Refresh_Invoice_Table.setIcon(new javax.swing.ImageIcon(getClass().getResource("/org/cdms/ui/images/refresh.png"))); // NOI18N
        org.openide.awt.Mnemonics.setLocalizedText(jButton_Refresh_Invoice_Table, org.openide.util.NbBundle.getMessage(StatisticsTopComponent.class, "StatisticsTopComponent.jButton_Refresh_Invoice_Table.text")); // NOI18N
        jButton_Refresh_Invoice_Table.setToolTipText(org.openide.util.NbBundle.getMessage(StatisticsTopComponent.class, "StatisticsTopComponent.jButton_Refresh_Invoice_Table.toolTipText")); // NOI18N
        jButton_Refresh_Invoice_Table.setContentAreaFilled(false);
        jButton_Refresh_Invoice_Table.setMaximumSize(new java.awt.Dimension(16, 16));
        jButton_Refresh_Invoice_Table.setMinimumSize(new java.awt.Dimension(16, 16));
        jButton_Refresh_Invoice_Table.setPreferredSize(new java.awt.Dimension(16, 16));
        jButton_Refresh_Invoice_Table.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton_Refresh_Invoice_TableActionPerformed(evt);
            }
        });

        org.openide.awt.Mnemonics.setLocalizedText(jLabel_Invoice_PageSize, org.openide.util.NbBundle.getMessage(StatisticsTopComponent.class, "StatisticsTopComponent.jLabel_Invoice_PageSize.text")); // NOI18N

        jFormattedTextField_Invoice_PageSize.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.NumberFormatter(new java.text.DecimalFormat("######"))));
        jFormattedTextField_Invoice_PageSize.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        jFormattedTextField_Invoice_PageSize.setText(org.openide.util.NbBundle.getMessage(StatisticsTopComponent.class, "StatisticsTopComponent.jFormattedTextField_Invoice_PageSize.text")); // NOI18N

        org.openide.awt.Mnemonics.setLocalizedText(jLabel_Invoice_PageNo, org.openide.util.NbBundle.getMessage(StatisticsTopComponent.class, "StatisticsTopComponent.jLabel_Invoice_PageNo.text")); // NOI18N

        jTextField_Invoice_PageNo.setEditable(false);
        jTextField_Invoice_PageNo.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        jTextField_Invoice_PageNo.setText(org.openide.util.NbBundle.getMessage(StatisticsTopComponent.class, "StatisticsTopComponent.jTextField_Invoice_PageNo.text")); // NOI18N

        jTextField_Invoice_RowCount.setEditable(false);
        jTextField_Invoice_RowCount.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        jTextField_Invoice_RowCount.setText(org.openide.util.NbBundle.getMessage(StatisticsTopComponent.class, "StatisticsTopComponent.jTextField_Invoice_RowCount.text")); // NOI18N

        org.openide.awt.Mnemonics.setLocalizedText(jButton_doStatistics, org.openide.util.NbBundle.getMessage(StatisticsTopComponent.class, "StatisticsTopComponent.jButton_doStatistics.text")); // NOI18N
        jButton_doStatistics.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton_doStatisticsActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel_Invoice_NavigatorLayout = new javax.swing.GroupLayout(jPanel_Invoice_Navigator);
        jPanel_Invoice_Navigator.setLayout(jPanel_Invoice_NavigatorLayout);
        jPanel_Invoice_NavigatorLayout.setHorizontalGroup(
            jPanel_Invoice_NavigatorLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel_Invoice_NavigatorLayout.createSequentialGroup()
                .addComponent(jButton_Refresh_Invoice_Table, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jButton_Invoice_FirstPage_, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jButton_Invoice_PriorPage_, javax.swing.GroupLayout.PREFERRED_SIZE, 21, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jButton_Invoice_NextPage_, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jButton_Invoice_LastPage_, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(jLabel_Invoice_PageSize)
                .addGap(18, 18, 18)
                .addComponent(jFormattedTextField_Invoice_PageSize, javax.swing.GroupLayout.PREFERRED_SIZE, 36, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(jLabel_Invoice_PageNo)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jTextField_Invoice_PageNo, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jTextField_Invoice_RowCount, javax.swing.GroupLayout.PREFERRED_SIZE, 84, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jButton_doStatistics)
                .addContainerGap(153, Short.MAX_VALUE))
        );
        jPanel_Invoice_NavigatorLayout.setVerticalGroup(
            jPanel_Invoice_NavigatorLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel_Invoice_NavigatorLayout.createSequentialGroup()
                .addGap(2, 2, 2)
                .addGroup(jPanel_Invoice_NavigatorLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addGroup(jPanel_Invoice_NavigatorLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jTextField_Invoice_PageNo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jTextField_Invoice_RowCount, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jButton_doStatistics))
                    .addGroup(jPanel_Invoice_NavigatorLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jFormattedTextField_Invoice_PageSize)
                        .addComponent(jLabel_Invoice_PageSize)
                        .addComponent(jLabel_Invoice_PageNo))
                    .addComponent(jButton_Invoice_LastPage_, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jButton_Invoice_NextPage_, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jButton_Invoice_PriorPage_, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jButton_Invoice_FirstPage_, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jButton_Refresh_Invoice_Table, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addGap(0, 0, Short.MAX_VALUE))
        );

        jTable_Invoice.setAutoCreateRowSorter(true);
        jTable_Invoice.setModel(new javax.swing.table.DefaultTableModel(
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
        jTable_Invoice.setGridColor(new java.awt.Color(204, 204, 204));
        jScrollPane2.setViewportView(jTable_Invoice);

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                        .addComponent(jPanel_Invoice_Navigator, javax.swing.GroupLayout.PREFERRED_SIZE, 677, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addContainerGap())
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                        .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 564, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(123, 123, 123))))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addComponent(jPanel_Invoice_Navigator, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 298, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, 612, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jPanel5, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap(237, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel5, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );
    }// </editor-fold>//GEN-END:initComponents

    private void jButton_Customer_Search_Filter_ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton_Customer_Search_Filter_ActionPerformed
        int pageSize = Integer.parseInt(jFormattedTextField_Invoice_PageSize.getText());
        customerQueryPage.setPageSize(pageSize);
        customerQueryPage.setPageNo(0);

        doCustomerFilter();
    }//GEN-LAST:event_jButton_Customer_Search_Filter_ActionPerformed

    private void jButton_Customer_Clear_FilterActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton_Customer_Clear_FilterActionPerformed
        //        jLabel_FilterError.setText("");

        jTextField_Email_Filter.setText("");
        jTextField_FirstName_Filter.setText("");
        jTextField_LastName_Filter.setText("");
        jTextField_Phone_Filter.setText("");
        dateField_createDate_From.setValue(null);
        dateField_createDate_To.setValue(null);
    }//GEN-LAST:event_jButton_Customer_Clear_FilterActionPerformed

    private void jButton_Errors_DetailsActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton_Errors_DetailsActionPerformed
        errorDetailsHandler.show();
    }//GEN-LAST:event_jButton_Errors_DetailsActionPerformed

    private void jButton_Errors_Details1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton_Errors_Details1ActionPerformed
        errorDetailsHandler.show();
    }//GEN-LAST:event_jButton_Errors_Details1ActionPerformed

    private void jButton_Invoice_FirstPage_ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton_Invoice_FirstPage_ActionPerformed
        //invoiceQueryPage.setPageNo(0);
        //doInvoiceFilter();
    }//GEN-LAST:event_jButton_Invoice_FirstPage_ActionPerformed

    private void jButton_Invoice_PriorPage_ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton_Invoice_PriorPage_ActionPerformed
        //invoiceQueryPage.setPageNo(invoiceQueryPage.getPageNo() - 1);
        //doInvoiceFilter();
    }//GEN-LAST:event_jButton_Invoice_PriorPage_ActionPerformed

    private void jButton_Invoice_NextPage_ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton_Invoice_NextPage_ActionPerformed
//        invoiceQueryPage.setPageNo(invoiceQueryPage.getPageNo() + 1);
        //doInvoiceFilter();
    }//GEN-LAST:event_jButton_Invoice_NextPage_ActionPerformed

    private void jButton_Invoice_LastPage_ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton_Invoice_LastPage_ActionPerformed
/*        int lastPage = (int) (invoiceQueryPage.getRowCount() / invoiceQueryPage.getPageSize() - 1);
        if (invoiceQueryPage.getRowCount() % invoiceQueryPage.getPageSize() != 0) {
            lastPage++;
        }

        invoiceQueryPage.setPageNo(lastPage);
        */ 
//        doInvoiceFilter();
    }//GEN-LAST:event_jButton_Invoice_LastPage_ActionPerformed

    private void jButton_Refresh_Invoice_TableActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton_Refresh_Invoice_TableActionPerformed
        //int pageSize = Integer.parseInt(jFormattedTextField_PageSize.getText());
        //queryPage.setPageSize(pageSize);
        //
        //queryPage.setPageNo(0);

        //TODO in production. A user may change page size.
        //     So we must  keep it im mind
//        doInvoiceFilter();
    }//GEN-LAST:event_jButton_Refresh_Invoice_TableActionPerformed

    private void jButton_doStatisticsActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton_doStatisticsActionPerformed
        doStatistics();
    }//GEN-LAST:event_jButton_doStatisticsActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private net.sf.nachocalendar.components.DateField dateField_createDate_From;
    private net.sf.nachocalendar.components.DateField dateField_createDate_To;
    private javax.swing.JButton jButton_Customer_Clear_Filter;
    private javax.swing.JButton jButton_Customer_Search_Filter_;
    private javax.swing.JButton jButton_Errors_Details;
    private javax.swing.JButton jButton_Errors_Details1;
    private javax.swing.JButton jButton_Invoice_FirstPage_;
    private javax.swing.JButton jButton_Invoice_LastPage_;
    private javax.swing.JButton jButton_Invoice_NextPage_;
    private javax.swing.JButton jButton_Invoice_PriorPage_;
    private javax.swing.JButton jButton_Refresh_Invoice_Table;
    private javax.swing.JButton jButton_doStatistics;
    private javax.swing.JCheckBox jCheckBox_OnlySelectedCustomer;
    private javax.swing.JFormattedTextField jFormattedTextField_Invoice_PageSize;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JLabel jLabel_Errors;
    private javax.swing.JLabel jLabel_Invoice_PageNo;
    private javax.swing.JLabel jLabel_Invoice_PageSize;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JPanel jPanel_Error_Msg;
    private javax.swing.JPanel jPanel_Error_Msg1;
    private javax.swing.JPanel jPanel_Filter;
    private javax.swing.JPanel jPanel_Invoice_Navigator;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JTable jTable_Customer;
    private javax.swing.JTable jTable_Invoice;
    private javax.swing.JTextField jTextField_CustomerId_Filter;
    private javax.swing.JTextField jTextField_Email_Filter;
    private javax.swing.JTextField jTextField_FirstName_Filter;
    private javax.swing.JTextField jTextField_Invoice_PageNo;
    private javax.swing.JTextField jTextField_Invoice_RowCount;
    private javax.swing.JTextField jTextField_LastName_Filter;
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
            EventQueue.invokeLater(new Runnable() {
                @Override
                public void run() {
                    //this code can work with Swing
                    if (customerAsyncFilter.getResult() instanceof Exception) {
                        Exception e = (Exception) customerAsyncFilter.getResult();
                        showErrors(ErrorMessageBuilder.get(e));
                    } else {

                        QueryPage<Customer> q = (QueryPage<Customer>) customerAsyncFilter.getResult();
                        if (q != null) {
                            customerQueryPage = q;
                        }
                        customerFilterResult = q.getQueryResult();
                        initCustomerTableComponents();
                        initPageNavigator();
                    }
//                    jButton_Search_.setEnabled(true);
                    enableNavigateOperations(true);
                    
                }
            });

        }

    }//inner FilterSearchHandler
    protected class InvoiceStatisticsHandler implements TaskListener {

        @Override
        public void taskFinished(Task task) {
            EventQueue.invokeLater(new Runnable() {
                @Override
                public void run() {
                    //this code can work with Swing
                    if (statisticsAsyncService.getResult() instanceof Exception) {
                        Exception e = (Exception) customerAsyncFilter.getResult();
                        showErrors(ErrorMessageBuilder.get(e));
                    } else {

                        //QueryPage<Object[]> q = (QueryPage<Object[]>) statisticsAsyncFilter.getResult();
/*                        if (q != null) {
                            customerQueryPage = q;
                        }
                        customerFilterResult = q.getQueryResult();
                        */ 
                        initCustomerTableComponents();
                        initPageNavigator();
                    }
//                    jButton_Search_.setEnabled(true);
                    enableNavigateOperations(true);
                    
                }
            });

        }

    }//inner FilterSearchHandler
    
}
