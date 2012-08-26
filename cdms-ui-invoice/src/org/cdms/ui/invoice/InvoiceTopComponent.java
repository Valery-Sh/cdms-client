package org.cdms.ui.invoice;


import java.awt.EventQueue;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import org.cdms.shared.entities.Customer;
import org.cdms.shared.entities.Invoice;
import org.cdms.shared.entities.InvoiceItem;
import org.cdms.shared.entities.ProductItem;
import org.cdms.shared.entities.User;
import org.cdms.shared.remoting.ConfigService;
import org.cdms.shared.remoting.QueryPage;
import org.cdms.shared.remoting.UserInfo;
import org.cdms.shared.remoting.WindowInfo;
import org.cdms.ui.common.EntityBinder;
import org.cdms.ui.common.EntityBinderImpl;
import org.cdms.ui.common.ErrorMessageBuilder;
import org.cdms.ui.common.TableBinder;
import org.cdms.ui.common.dialog.DeleteConfirmDialog;
import org.cdms.ui.common.dialog.ErrorDetailsHandler;
import org.jdesktop.beansbinding.BindingGroup;
import org.jdesktop.observablecollections.ObservableCollections;
import org.netbeans.api.settings.ConvertAsProperties;
import org.openide.util.Lookup;
import org.openide.util.NbBundle;
import org.openide.util.NbBundle.Messages;
import org.openide.util.Task;
import org.openide.util.TaskListener;
import org.openide.util.lookup.Lookups;
import org.openide.windows.TopComponent;

/**
 * Top component which displays something.
 */
@ConvertAsProperties(
    dtd = "-//org.cdms.ui.invoice//Invoice//EN",
autostore = false)
@TopComponent.Description(
    preferredID = "invoiceTopComponent",
iconBase = "org/cdms/ui/invoice/invoice16x16.png",
persistenceType = TopComponent.PERSISTENCE_ONLY_OPENED)
//persistenceType = TopComponent.PERSISTENCE_ALWAYS)
@TopComponent.Registration(mode = "editor", openAtStartup = false)
@Messages({
    "CTL_InvoiceAction=Invoice",
    "CTL_InvoiceTopComponent=Invoice Window",
    "HINT_InvoiceTopComponent=This is a Invoice window"
})
public final class InvoiceTopComponent extends TopComponent {
    
    private WindowInfo windowInfo;

    
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
    InvoiceAsyncService invoiceAsyncFilter = new InvoiceAsyncService();
    QueryPage<Invoice> invoiceQueryPage;
    /*-------------------------------------------------------------
     * InvoiceItem fields, properties and variables
     --------------------------------------------------------------*/
    InvoiceItemAsyncService invoiceItemAsyncSave;
    InvoiceItemAsyncService invoiceItemAsyncDelete;
    InvoiceItemAsyncService invoiceItemAsyncInsert;
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
    BindingGroup productItemToInsertBindingGroup;
    ProductItemAsyncService productItemAsyncFilter = new ProductItemAsyncService();
    QueryPage<ProductItem> productItemQueryPage;
    
/*    @Override
    public boolean isDisplayable(){
        return false;
    }
*/
    public InvoiceTopComponent() {
        initComponents();

        initInvoiceFilterComponents();
        initProductItemFilterComponents();
        hideErrors();
        invoiceQueryPage = new QueryPage<Invoice>();
        productItemQueryPage = new QueryPage<ProductItem>();
        initPageNavigator();
        initProductItemPageNavigator();
        initInvoiceTableComponents();
        initProductItemTableComponents();

//        initDateFields();
        
        //prohibitEditOperations();
        windowInfo = new WindowInfo(new ProhibitEditHandler());
        
        checkEditRole();
        
        setName(Bundle.CTL_InvoiceTopComponent());
        setToolTipText(Bundle.HINT_InvoiceTopComponent());

    }
    protected class ProhibitEditHandler implements WindowInfo.OperationHandler {

        @Override
        public void process(WindowInfo wi) {
            if ( ! windowInfo.getUserInfo().inRole("edit") ) {
                prohibitEditOperations();
            }
        }
        
    }
    @Override
    public void componentOpened() {
        windowInfo.getRoles().add("view");
        windowInfo.getRoles().add("edit");        
        //windowInfo.getRoles().add("view statistics");
        associateLookup(Lookups.singleton(windowInfo));

    }
    
    protected void checkEditRole() {
        UserInfo info = ((ConfigService) Lookup.getDefault().lookup(ConfigService.class)).getConfig();
        if ( info == null ) {
            return;
        }
        if ( !info.inRole("edit") ) {
            prohibitEditOperations();
        } 
    }
    
    
    boolean editProhibited = false;
    
    protected void prohibitEditOperations() {
            editProhibited = true;
            jButton_InvoiceItem_Edit_Save_.setEnabled(false);
            jButton_InvoiceItem_Edit_Delete_.setEnabled(false);
            jButton_InvoiceItem_Add_To_Invoice.setEnabled(false);
    }
    
    public Invoice getInvoiceAsFilter() {
        return invoiceAsFilter;
    }

    public void setInvoiceAsFilter(Invoice invoiceAsFilter) {
        this.invoiceAsFilter = invoiceAsFilter;
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

    public ProductItem getProductItemAsFilter() {
        return productItemAsFilter;
    }

    public void setProductItemAsFilter(ProductItem productItemAsFilter) {
        this.productItemAsFilter = productItemAsFilter;
    }

    protected void initPageNavigator() {
        int pageSize = Integer.parseInt(jFormattedTextField_Invoice_PageSize.getText());
        invoiceQueryPage.setPageSize(pageSize);

        jTextField_Invoice_PageNo.setText("" + invoiceQueryPage.getPageNo());
        jTextField_Invoice_RowCount.setText("" + invoiceQueryPage.getRowCount());


        if (invoiceQueryPage.getRowCount() == 0) {
            jButton_Invoice_FirstPage_.setEnabled(false);
            jButton_Invoice_LastPage_.setEnabled(false);
            jButton_Invoice_NextPage_.setEnabled(false);
            jButton_Invoice_PriorPage_.setEnabled(false);
            return;
        }


        int lastPage = (int) (invoiceQueryPage.getRowCount() / invoiceQueryPage.getPageSize() - 1);
        if (invoiceQueryPage.getRowCount() % invoiceQueryPage.getPageSize() != 0) {
            lastPage++;
        }
        if (invoiceQueryPage.getPageNo() == lastPage) {
            jButton_Invoice_LastPage_.setEnabled(false);
            jButton_Invoice_NextPage_.setEnabled(false);

            boolean b = true;
            if (lastPage == 0) {
                b = false;
            }

            jButton_Invoice_FirstPage_.setEnabled(b);
            jButton_Invoice_PriorPage_.setEnabled(b);
        } else if (invoiceQueryPage.getPageNo() == 0) {
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

    protected void initProductItemPageNavigator() {

        int pageSize = Integer.parseInt(jFormattedTextField_PageItem_PageSize.getText());
        productItemQueryPage.setPageSize(pageSize);

        jTextField_PageItem_Item_PageNo.setText("" + productItemQueryPage.getPageNo());
        jTextField_PageItem_Item_RowCount.setText("" + productItemQueryPage.getRowCount());


        if (productItemQueryPage.getRowCount() == 0) {
            jButton_PageItem_FirstPage_.setEnabled(false);
            jButton_PageItem_LastPage_.setEnabled(false);
            jButton_PageItem_NextPage_.setEnabled(false);
            jButton_PageItem_PriorPage_.setEnabled(false);
            return;
        }


        int lastPage = (int) (productItemQueryPage.getRowCount() / productItemQueryPage.getPageSize() - 1);
        if (productItemQueryPage.getRowCount() % productItemQueryPage.getPageSize() != 0) {
            lastPage++;
        }
        if (productItemQueryPage.getPageNo() == lastPage) {
            jButton_PageItem_LastPage_.setEnabled(false);
            jButton_PageItem_NextPage_.setEnabled(false);
            boolean b = true;
            if (lastPage == 0) {
                b = false;
            }

            jButton_PageItem_FirstPage_.setEnabled(b);
            jButton_PageItem_PriorPage_.setEnabled(b);
        } else if (productItemQueryPage.getPageNo() == 0) {
            boolean b = true;
            if (lastPage == 0) {
                b = false;
            }
            jButton_PageItem_LastPage_.setEnabled(b);
            jButton_PageItem_NextPage_.setEnabled(b);
            jButton_PageItem_FirstPage_.setEnabled(false);
            jButton_PageItem_PriorPage_.setEnabled(false);
        } else {
            jButton_PageItem_LastPage_.setEnabled(true);
            jButton_PageItem_NextPage_.setEnabled(true);
            jButton_PageItem_FirstPage_.setEnabled(true);
            jButton_PageItem_PriorPage_.setEnabled(true);
        }
    }

/*    protected void initDateFields() {
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
*/
    protected void initInvoiceFilterComponents() {
        invoiceFilterBinder = new EntityBinderImpl(invoiceFilterBindingGroup, this);
        invoiceFilterBinder.addTextFieldBinder(jTextField_ID_Filter, "invoiceAsFilter.idFilter");

        invoiceFilterBinder.addDatePickerBinder(dateField_createDate_From.getDateField(), "invoiceAsFilter.createdAt");
        invoiceFilterBinder.addCalendarBinder(dateField_createDate_To.getDateField(), "invoiceAsFilter.createdAtEnd");

        invoiceAsFilter.setCreatedBy(userAsFilter);

        userFilterBinder = new EntityBinderImpl(userBindingGroup, this);
        userFilterBinder.addTextFieldBinder(jTextField_User_firstName, "userAsFilter.firstName");
        userFilterBinder.addTextFieldBinder(jTextField_User_lastName, "userAsFilter.lastName");

        invoiceAsFilter.setCustomer(customerAsFilter);

        customerFilterBinder = new EntityBinderImpl(customerBindingGroup, this);
        customerFilterBinder.addTextFieldBinder(jTextField_CustomerId_Filter, "customerAsFilter.idFilter");
        customerFilterBinder.addTextFieldBinder(jTextField_FirstName_Filter, "customerAsFilter.firstName");
        customerFilterBinder.addTextFieldBinder(jTextField_LastName_Filter, "customerAsFilter.lastName");
        customerFilterBinder.addTextFieldBinder(jTextField_Email_Filter, "customerAsFilter.email");
        customerFilterBinder.addTextFieldBinder(jTextField_Phone_Filter, "customerAsFilter.phone");


        invoiceFilterBindingGroup.bind();
        userBindingGroup.bind();
        customerBindingGroup.bind();

    }

    protected void initProductItemFilterComponents() {
        productItemFilterBinder = new EntityBinderImpl(productItemFilterBindingGroup, this);
        productItemFilterBinder.addTextFieldBinder(jTextField_Item_Id_Filter, "productItemAsFilter.idFilter");
        productItemFilterBinder.addTextFieldBinder(jTextField_Item_Barcode_Filter, "productItemAsFilter.barcode");
        productItemFilterBinder.addTextFieldBinder(jTextField_Item_ItemName_Filter, "productItemAsFilter.itemName");

        productItemFilterBindingGroup.bind();
    }

    protected void initInvoiceTableComponents() {
        if (invoiceTableBinder != null) {
            invoiceTableBinder.getBindingGroup().unbind();
        }
        if (invoiceFilterResult == null) {
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
            invoiceItemAsChildTableBinder = invoiceTableBinder.addChild(jTable_InvoiceItem, "invoiceItems");
        } else {
            List<InvoiceItem> ol = ObservableCollections.observableList(
                    new ArrayList<InvoiceItem>());
            invoiceItemAsChildTableBinder = new TableBinder(jTable_InvoiceItem, ol);

        }
        invoiceItemAsChildTableBinder.addColumn("id", Long.class, "Id");

        invoiceItemAsChildTableBinder.addColumn("productItem.itemName", String.class, "Name");
        invoiceItemAsChildTableBinder.addColumn("productItem.barcode", String.class, "Barcode");
        invoiceItemAsChildTableBinder.addColumn("productItem.price", BigDecimal.class, "Price");
        invoiceItemAsChildTableBinder.addColumn("itemCount", Integer.class, "Count");

        invoiceItemAsChildTableBinder.addTextFieldBinder(jTextField_InvoiceItem_Id_Edit, "id");
        invoiceItemAsChildTableBinder.addTextFieldBinder(jTextField_InvoiceItem_ItemName_Edit, "productItem.itemName");
        invoiceItemAsChildTableBinder.addTextFieldBinder(jTextField_InvoiceItem_Barcode_Edit, "productItem.barcode");
        invoiceItemAsChildTableBinder.addTextFieldBinder(jTextField_InvoiceItem_Price_Edit, "productItem.price");
        invoiceItemAsChildTableBinder.addTextFieldBinder(jTextField_InvoiceItem_ItemCount_Edit, "itemCount");

        invoiceTableBinder.bindTable();

        invoiceTableBinder.refresh();

        emptyInvoiceItemEditComponents();

        if (!invoiceFilterResult.isEmpty()) {
            jTable_Invoice.setRowSelectionInterval(0, 0);
            if (!invoiceFilterResult.get(0).getInvoiceItems().isEmpty()) {
                jTable_InvoiceItem.setRowSelectionInterval(0, 0);
            }
        }

        invoiceTableBinder.updateColumnModel(jTable_Invoice);
        if (invoiceFilterResult == null || invoiceFilterResult.isEmpty()) {
            invoiceItemAsChildTableBinder.bindTable();
            invoiceItemAsChildTableBinder.refresh();
            invoiceItemAsChildTableBinder.updateColumnModel(jTable_InvoiceItem);
        } else {
            invoiceTableBinder.updateColumnModel(jTable_InvoiceItem);
        }

    }

    protected void initProductItemTableComponents() {

        if (productItemTableBinder != null) {
            productItemTableBinder.getBindingGroup().unbind();
        }
        if (productItemFilterResult == null || productItemFilterResult.isEmpty()) {
            productItemFilterResult = ObservableCollections.observableList(
                    new ArrayList<ProductItem>());

        } else {
            productItemFilterResult = ObservableCollections.observableList(
                    productItemFilterResult);
        }

        productItemTableBinder = new TableBinder(jTable_ProductItems, productItemFilterResult);

        productItemTableBinder.addColumn("id", Long.class, "Id");
        productItemTableBinder.addColumn("itemName", String.class, "Name");
        productItemTableBinder.addColumn("barcode", String.class, "Barcode");
        productItemTableBinder.addColumn("price", BigDecimal.class, "Price");

        productItemTableBinder.bindTable();

        productItemTableBinder.addTextFieldBinder(jTextField_ProductItem_Id_Add, "id");
        productItemTableBinder.addTextFieldBinder(jTextField_ProductItem_ItemName_Add, "itemName");
        productItemTableBinder.addTextFieldBinder(jTextField_ProductItem_Barcode_Add, "barcode");
        productItemTableBinder.addTextFieldBinder(jTextField_ProductItem_Price_Add, "price");

        productItemTableBinder.refresh();
        emptyProductItemInsertComponents();
        if (!productItemFilterResult.isEmpty()) {
            jTable_ProductItems.setRowSelectionInterval(0, 0);
        }
        productItemTableBinder.updateMasterColumnModel();
    }

    protected void hideErrors() {
        jLabel_Errors.setVisible(false);
        jButton_Errors_Details.setVisible(false);
    }

    protected void showErrors(String message) {
        jLabel_Errors.setText(message);
        jLabel_Errors.setVisible(true);
//        jButton_Errors_Details.setVisible(true);

    }
    protected void showErrors(Exception e) {
        String msg = ErrorMessageBuilder.get(e);
        jLabel_Errors.setText(msg);
        jLabel_Errors.setVisible(true);
        jButton_Errors_Details.setVisible(true);
    }

    protected void emptyInvoiceItemEditComponents() {
        jTextField_InvoiceItem_Barcode_Edit.setText(null);
        jTextField_InvoiceItem_Id_Edit.setText(null);
        jTextField_InvoiceItem_ItemCount_Edit.setText(null);
        jTextField_InvoiceItem_ItemName_Edit.setText(null);
        jTextField_InvoiceItem_Price_Edit.setText(null);
    }

    protected void emptyProductItemInsertComponents() {
        jTextField_ProductItem_Barcode_Add.setText(null);
        jTextField_ProductItem_Id_Add.setText(null);
        jTextField_ProductItem_ItemName_Add.setText(null);
        jTextField_ProductItem_Price_Add.setText(null);
        jFormattedTextField_InvoiceItem_itemCount_Add.setText(null);
    }

    private void enableInvoiceOperations(boolean enabled) {
        jTable_Invoice.setEnabled(enabled);
        jTable_InvoiceItem.setEnabled(enabled);


        jButton_Search_.setEnabled(enabled);
        //jButton_InvoiceItem_Add_To_Invoice.setEnabled(enabled);
        enableNavigateOperations(enabled);
        if ( editProhibited && enabled ) {
            return;
        }
        jButton_InvoiceItem_Edit_Save_.setEnabled(enabled);

        jButton_InvoiceItem_Edit_Delete_.setEnabled(enabled);
        jButton_InvoiceItem_Add_To_Invoice.setEnabled(enabled);
        
    }

    private void enableProductItemOperations(boolean enabled) {
        jTable_ProductItems.setEnabled(enabled);
        jButton_PageItem_Search_Filter.setEnabled(enabled);
        enableProductItemNavigateOperations(enabled);
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

    public void enableProductItemNavigateOperations(boolean enabled) {
        jButton_PageItem_FirstPage_.setEnabled(enabled);
        jButton_PageItem_LastPage_.setEnabled(enabled);
        jButton_PageItem_NextPage_.setEnabled(enabled);
        jButton_PageItem_PriorPage_.setEnabled(enabled);
        jButton_PageItem_Refresh_Table.setEnabled(enabled);
        if (enabled) {
            initProductItemPageNavigator();
        }

    }

    protected void doInvoiceFilter() {
        hideErrors();
        enableInvoiceOperations(false);
        invoiceAsyncFilter = new InvoiceAsyncService();
        try {
            invoiceQueryPage.setEntityAsExample(invoiceAsFilter);
            invoiceQueryPage.setQueryResult(new ArrayList<Invoice>());
            invoiceAsyncFilter.findByExample(new InvoiceFilterSeachHandler(), invoiceQueryPage); // TODO paging
        } catch (Exception e) {
            System.out.println("ERROR");
        }
    }

    protected void doProductItemFilter() {

        hideErrors();
        enableProductItemOperations(false);
        productItemAsyncFilter = new ProductItemAsyncService();
        try {
            productItemQueryPage.setEntityAsExample(productItemAsFilter);
            productItemQueryPage.setQueryResult(new ArrayList<ProductItem>());
            productItemAsyncFilter.findByExample(new ProductItemFilterSeachHandler(), productItemQueryPage); // TODO paging
        } catch (Exception e) {
            System.out.println("ERROR");
        }

    }

    private void insertInvoiceItem() {
        invoiceItemAsyncInsert = new InvoiceItemAsyncService<InvoiceItem>();
        enableInvoiceOperations(false);

        int invoiceRow = jTable_Invoice.getSelectedRow();

        Invoice invoice = invoiceFilterResult.get(invoiceRow);
        int productRow = jTable_ProductItems.getSelectedRow();
        ProductItem productItem = productItemFilterResult.get(productRow);

        InvoiceItem invoiceItem = new InvoiceItem();
        invoiceItem.setInvoice(invoice);
        invoiceItem.setProductItem(productItem);

        Integer c = 0;
        Object o = jFormattedTextField_InvoiceItem_itemCount_Add.getValue();
        if (o != null) {
            c = ((Long) o).intValue();
        }
        invoiceItem.setItemCount(c);

        try {
            invoiceItemAsyncInsert.insert(new InvoiceItemInsertHandler(), invoiceItem); // TODO paging
        } catch (Exception e) {
            System.out.println("ERROR insertInvoiceItem");
        }
    }

    private void updateInvoiceItem() {
        enableInvoiceOperations(false);
        invoiceItemAsyncSave = new InvoiceItemAsyncService();

        int r = jTable_InvoiceItem.getSelectedRow();
        if (r < 0) {
            return;
        }
        int r1 = jTable_Invoice.getSelectedRow();
        Invoice selectedInvoice = invoiceFilterResult.get(r1);
        InvoiceItem item = selectedInvoice.getInvoiceItems().get(r);
        item.getProductItem().setStringPrice(item.getProductItem().getPrice().toPlainString());

        try {
            invoiceItemAsyncSave.update(new InvoiceItemSaveHandler(), item); // TODO paging
        } catch (Exception e) {
            System.out.println("ERROR updateInvoiceItem");
        }
    }

    private void deleteInvoiceItem() {
        enableInvoiceOperations(false);
        invoiceItemAsyncDelete = new InvoiceItemAsyncService();

        int r = jTable_InvoiceItem.getSelectedRow();
        if (r < 0) {
            return;
        }
        int r1 = jTable_Invoice.getSelectedRow();
        Invoice selectedInvoice = invoiceFilterResult.get(r1);
        InvoiceItem item = selectedInvoice.getInvoiceItems().get(r);
        try {
            invoiceItemAsyncDelete.delete(new InvoiceItemDeleteHandler(), item); // TODO paging
        } catch (Exception e) {
            System.out.println("DELETE ERROR");
        }
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel_Table = new javax.swing.JPanel();
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
        jScrollPane1 = new javax.swing.JScrollPane();
        jTable_Invoice = new javax.swing.JTable();
        jScrollPane2 = new javax.swing.JScrollPane();
        jTable_InvoiceItem = new javax.swing.JTable();
        jPanel3 = new javax.swing.JPanel();
        jLabel20 = new javax.swing.JLabel();
        jTextField_InvoiceItem_Id_Edit = new javax.swing.JTextField();
        jLabel21 = new javax.swing.JLabel();
        jLabel22 = new javax.swing.JLabel();
        jLabel23 = new javax.swing.JLabel();
        jLabel24 = new javax.swing.JLabel();
        jTextField_InvoiceItem_Barcode_Edit = new javax.swing.JTextField();
        jTextField_InvoiceItem_ItemName_Edit = new javax.swing.JTextField();
        jTextField_InvoiceItem_Price_Edit = new javax.swing.JTextField();
        jTextField_InvoiceItem_ItemCount_Edit = new javax.swing.JTextField();
        jButton_InvoiceItem_Edit_Save_ = new javax.swing.JButton();
        jButton_InvoiceItem_Edit_Delete_ = new javax.swing.JButton();
        jPanel2 = new javax.swing.JPanel();
        jLabel2 = new javax.swing.JLabel();
        jTextField_ID_Filter = new javax.swing.JTextField();
        jPanel4 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        jLabel10 = new javax.swing.JLabel();
        jTextField_User_firstName = new javax.swing.JTextField();
        jLabel11 = new javax.swing.JLabel();
        jTextField_User_lastName = new javax.swing.JTextField();
        jPanel5 = new javax.swing.JPanel();
        jLabel7 = new javax.swing.JLabel();
        jLabel9 = new javax.swing.JLabel();
        dateField_createDate_From = new com.vns.comp.DatePickerEx();
        dateField_createDate_To = new com.vns.comp.DatePickerEx();
        jLabel8 = new javax.swing.JLabel();
        jTextField_CustomerId_Filter = new javax.swing.JTextField();
        jLabel3 = new javax.swing.JLabel();
        jTextField_FirstName_Filter = new javax.swing.JTextField();
        jLabel4 = new javax.swing.JLabel();
        jTextField_LastName_Filter = new javax.swing.JTextField();
        jLabel6 = new javax.swing.JLabel();
        jTextField_Email_Filter = new javax.swing.JTextField();
        jLabel5 = new javax.swing.JLabel();
        jButton_Search_ = new javax.swing.JButton();
        jButton_Clear_ = new javax.swing.JButton();
        jPanel_Error_Msg = new javax.swing.JPanel();
        jLabel_Errors = new javax.swing.JLabel();
        jButton_Errors_Details = new javax.swing.JButton();
        jTextField_Phone_Filter = new javax.swing.JTextField();
        jPanel_ProductItems = new javax.swing.JPanel();
        jPanel_ProductItem_Navigator = new javax.swing.JPanel();
        jButton_PageItem_FirstPage_ = new javax.swing.JButton();
        jButton_PageItem_PriorPage_ = new javax.swing.JButton();
        jButton_PageItem_NextPage_ = new javax.swing.JButton();
        jButton_PageItem_LastPage_ = new javax.swing.JButton();
        jButton_PageItem_Refresh_Table = new javax.swing.JButton();
        jLabel1_PageItem_PageSize = new javax.swing.JLabel();
        jFormattedTextField_PageItem_PageSize = new javax.swing.JFormattedTextField();
        jLabel_PageItem_PageNo = new javax.swing.JLabel();
        jTextField_PageItem_Item_PageNo = new javax.swing.JTextField();
        jTextField_PageItem_Item_RowCount = new javax.swing.JTextField();
        jScrollPane3 = new javax.swing.JScrollPane();
        jTable_ProductItems = new javax.swing.JTable();
        jPanel1 = new javax.swing.JPanel();
        jLabel28 = new javax.swing.JLabel();
        jTextField_ProductItem_Id_Add = new javax.swing.JTextField();
        jLabel29 = new javax.swing.JLabel();
        jTextField_ProductItem_Barcode_Add = new javax.swing.JTextField();
        jLabel30 = new javax.swing.JLabel();
        jTextField_ProductItem_ItemName_Add = new javax.swing.JTextField();
        jLabel31 = new javax.swing.JLabel();
        jTextField_ProductItem_Price_Add = new javax.swing.JTextField();
        jLabel32 = new javax.swing.JLabel();
        jButton_InvoiceItem_Add_To_Invoice = new javax.swing.JButton();
        jFormattedTextField_InvoiceItem_itemCount_Add = new javax.swing.JFormattedTextField();
        jPanel6 = new javax.swing.JPanel();
        jLabel25 = new javax.swing.JLabel();
        jLabel26 = new javax.swing.JLabel();
        jLabel27 = new javax.swing.JLabel();
        jTextField_Item_Id_Filter = new javax.swing.JTextField();
        jTextField_Item_Barcode_Filter = new javax.swing.JTextField();
        jTextField_Item_ItemName_Filter = new javax.swing.JTextField();
        jButton_PageItem_Search_Filter = new javax.swing.JButton();

        jPanel_Table.setBorder(javax.swing.BorderFactory.createTitledBorder(org.openide.util.NbBundle.getMessage(InvoiceTopComponent.class, "InvoiceTopComponent.jPanel_Table.border.title"))); // NOI18N

        jPanel_Invoice_Navigator.setBorder(javax.swing.BorderFactory.createTitledBorder(""));
        jPanel_Invoice_Navigator.setMinimumSize(new java.awt.Dimension(0, 37));
        jPanel_Invoice_Navigator.setPreferredSize(new java.awt.Dimension(788, 37));
        jPanel_Invoice_Navigator.setRequestFocusEnabled(false);

        jButton_Invoice_FirstPage_.setIcon(new javax.swing.ImageIcon(getClass().getResource("/org/cdms/ui/images/navigate_beginning.png"))); // NOI18N
        org.openide.awt.Mnemonics.setLocalizedText(jButton_Invoice_FirstPage_, org.openide.util.NbBundle.getMessage(InvoiceTopComponent.class, "InvoiceTopComponent.jButton_Invoice_FirstPage_.text")); // NOI18N
        jButton_Invoice_FirstPage_.setToolTipText(org.openide.util.NbBundle.getMessage(InvoiceTopComponent.class, "InvoiceTopComponent.jButton_Invoice_FirstPage_.toolTipText")); // NOI18N
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
        org.openide.awt.Mnemonics.setLocalizedText(jButton_Invoice_PriorPage_, org.openide.util.NbBundle.getMessage(InvoiceTopComponent.class, "InvoiceTopComponent.jButton_Invoice_PriorPage_.text")); // NOI18N
        jButton_Invoice_PriorPage_.setToolTipText(org.openide.util.NbBundle.getMessage(InvoiceTopComponent.class, "InvoiceTopComponent.jButton_Invoice_PriorPage_.toolTipText")); // NOI18N
        jButton_Invoice_PriorPage_.setBorder(null);
        jButton_Invoice_PriorPage_.setContentAreaFilled(false);
        jButton_Invoice_PriorPage_.setMaximumSize(new java.awt.Dimension(16, 16));
        jButton_Invoice_PriorPage_.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton_Invoice_PriorPage_ActionPerformed(evt);
            }
        });

        jButton_Invoice_NextPage_.setIcon(new javax.swing.ImageIcon(getClass().getResource("/org/cdms/ui/images/navigate_right.png"))); // NOI18N
        org.openide.awt.Mnemonics.setLocalizedText(jButton_Invoice_NextPage_, org.openide.util.NbBundle.getMessage(InvoiceTopComponent.class, "InvoiceTopComponent.jButton_Invoice_NextPage_.text")); // NOI18N
        jButton_Invoice_NextPage_.setToolTipText(org.openide.util.NbBundle.getMessage(InvoiceTopComponent.class, "InvoiceTopComponent.jButton_Invoice_NextPage_.toolTipText")); // NOI18N
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
        org.openide.awt.Mnemonics.setLocalizedText(jButton_Invoice_LastPage_, org.openide.util.NbBundle.getMessage(InvoiceTopComponent.class, "InvoiceTopComponent.jButton_Invoice_LastPage_.text")); // NOI18N
        jButton_Invoice_LastPage_.setToolTipText(org.openide.util.NbBundle.getMessage(InvoiceTopComponent.class, "InvoiceTopComponent.jButton_Invoice_LastPage_.toolTipText")); // NOI18N
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
        org.openide.awt.Mnemonics.setLocalizedText(jButton_Refresh_Invoice_Table, org.openide.util.NbBundle.getMessage(InvoiceTopComponent.class, "InvoiceTopComponent.jButton_Refresh_Invoice_Table.text")); // NOI18N
        jButton_Refresh_Invoice_Table.setToolTipText(org.openide.util.NbBundle.getMessage(InvoiceTopComponent.class, "InvoiceTopComponent.jButton_Refresh_Invoice_Table.toolTipText")); // NOI18N
        jButton_Refresh_Invoice_Table.setContentAreaFilled(false);
        jButton_Refresh_Invoice_Table.setMaximumSize(new java.awt.Dimension(16, 16));
        jButton_Refresh_Invoice_Table.setMinimumSize(new java.awt.Dimension(16, 16));
        jButton_Refresh_Invoice_Table.setPreferredSize(new java.awt.Dimension(16, 16));
        jButton_Refresh_Invoice_Table.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton_Refresh_Invoice_TableActionPerformed(evt);
            }
        });

        org.openide.awt.Mnemonics.setLocalizedText(jLabel_Invoice_PageSize, org.openide.util.NbBundle.getMessage(InvoiceTopComponent.class, "InvoiceTopComponent.jLabel_Invoice_PageSize.text")); // NOI18N

        jFormattedTextField_Invoice_PageSize.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.NumberFormatter(new java.text.DecimalFormat("######"))));
        jFormattedTextField_Invoice_PageSize.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        jFormattedTextField_Invoice_PageSize.setText(org.openide.util.NbBundle.getMessage(InvoiceTopComponent.class, "InvoiceTopComponent.jFormattedTextField_Invoice_PageSize.text")); // NOI18N

        org.openide.awt.Mnemonics.setLocalizedText(jLabel_Invoice_PageNo, org.openide.util.NbBundle.getMessage(InvoiceTopComponent.class, "InvoiceTopComponent.jLabel_Invoice_PageNo.text")); // NOI18N

        jTextField_Invoice_PageNo.setEditable(false);
        jTextField_Invoice_PageNo.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        jTextField_Invoice_PageNo.setText(org.openide.util.NbBundle.getMessage(InvoiceTopComponent.class, "InvoiceTopComponent.jTextField_Invoice_PageNo.text")); // NOI18N

        jTextField_Invoice_RowCount.setEditable(false);
        jTextField_Invoice_RowCount.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        jTextField_Invoice_RowCount.setText(org.openide.util.NbBundle.getMessage(InvoiceTopComponent.class, "InvoiceTopComponent.jTextField_Invoice_RowCount.text")); // NOI18N

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
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel_Invoice_NavigatorLayout.setVerticalGroup(
            jPanel_Invoice_NavigatorLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel_Invoice_NavigatorLayout.createSequentialGroup()
                .addGap(3, 3, 3)
                .addGroup(jPanel_Invoice_NavigatorLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addGroup(jPanel_Invoice_NavigatorLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jTextField_Invoice_PageNo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jTextField_Invoice_RowCount, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
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
        jScrollPane1.setViewportView(jTable_Invoice);

        jTable_InvoiceItem.setAutoCreateRowSorter(true);
        jTable_InvoiceItem.setModel(new javax.swing.table.DefaultTableModel(
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
        jScrollPane2.setViewportView(jTable_InvoiceItem);

        jPanel3.setBorder(javax.swing.BorderFactory.createTitledBorder(org.openide.util.NbBundle.getMessage(InvoiceTopComponent.class, "InvoiceTopComponent.jPanel3.border.title"))); // NOI18N

        org.openide.awt.Mnemonics.setLocalizedText(jLabel20, org.openide.util.NbBundle.getMessage(InvoiceTopComponent.class, "InvoiceTopComponent.jLabel20.text")); // NOI18N

        jTextField_InvoiceItem_Id_Edit.setEditable(false);
        jTextField_InvoiceItem_Id_Edit.setText(org.openide.util.NbBundle.getMessage(InvoiceTopComponent.class, "InvoiceTopComponent.jTextField_InvoiceItem_Id_Edit.text")); // NOI18N

        org.openide.awt.Mnemonics.setLocalizedText(jLabel21, org.openide.util.NbBundle.getMessage(InvoiceTopComponent.class, "InvoiceTopComponent.jLabel21.text")); // NOI18N

        org.openide.awt.Mnemonics.setLocalizedText(jLabel22, org.openide.util.NbBundle.getMessage(InvoiceTopComponent.class, "InvoiceTopComponent.jLabel22.text")); // NOI18N

        org.openide.awt.Mnemonics.setLocalizedText(jLabel23, org.openide.util.NbBundle.getMessage(InvoiceTopComponent.class, "InvoiceTopComponent.jLabel23.text")); // NOI18N

        org.openide.awt.Mnemonics.setLocalizedText(jLabel24, org.openide.util.NbBundle.getMessage(InvoiceTopComponent.class, "InvoiceTopComponent.jLabel24.text")); // NOI18N

        jTextField_InvoiceItem_Barcode_Edit.setEditable(false);
        jTextField_InvoiceItem_Barcode_Edit.setText(org.openide.util.NbBundle.getMessage(InvoiceTopComponent.class, "InvoiceTopComponent.jTextField_InvoiceItem_Barcode_Edit.text")); // NOI18N

        jTextField_InvoiceItem_ItemName_Edit.setEditable(false);
        jTextField_InvoiceItem_ItemName_Edit.setText(org.openide.util.NbBundle.getMessage(InvoiceTopComponent.class, "InvoiceTopComponent.jTextField_InvoiceItem_ItemName_Edit.text")); // NOI18N

        jTextField_InvoiceItem_Price_Edit.setEditable(false);
        jTextField_InvoiceItem_Price_Edit.setText(org.openide.util.NbBundle.getMessage(InvoiceTopComponent.class, "InvoiceTopComponent.jTextField_InvoiceItem_Price_Edit.text")); // NOI18N

        jTextField_InvoiceItem_ItemCount_Edit.setText(org.openide.util.NbBundle.getMessage(InvoiceTopComponent.class, "InvoiceTopComponent.jTextField_InvoiceItem_ItemCount_Edit.text")); // NOI18N

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addComponent(jLabel22)
                        .addGap(18, 18, 18)
                        .addComponent(jTextField_InvoiceItem_ItemName_Edit, javax.swing.GroupLayout.PREFERRED_SIZE, 96, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel20)
                            .addComponent(jLabel21)
                            .addComponent(jLabel24)
                            .addComponent(jLabel23))
                        .addGap(28, 28, 28)
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jTextField_InvoiceItem_Price_Edit, javax.swing.GroupLayout.PREFERRED_SIZE, 96, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jTextField_InvoiceItem_ItemCount_Edit, javax.swing.GroupLayout.PREFERRED_SIZE, 96, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jTextField_InvoiceItem_Barcode_Edit, javax.swing.GroupLayout.PREFERRED_SIZE, 96, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jTextField_InvoiceItem_Id_Edit, javax.swing.GroupLayout.PREFERRED_SIZE, 96, javax.swing.GroupLayout.PREFERRED_SIZE))))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel20)
                    .addComponent(jTextField_InvoiceItem_Id_Edit, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(4, 4, 4)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel21)
                    .addComponent(jTextField_InvoiceItem_Barcode_Edit, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel22)
                    .addComponent(jTextField_InvoiceItem_ItemName_Edit, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel23)
                    .addComponent(jTextField_InvoiceItem_Price_Edit, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel24)
                    .addComponent(jTextField_InvoiceItem_ItemCount_Edit, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(0, 0, Short.MAX_VALUE))
        );

        org.openide.awt.Mnemonics.setLocalizedText(jButton_InvoiceItem_Edit_Save_, org.openide.util.NbBundle.getMessage(InvoiceTopComponent.class, "InvoiceTopComponent.jButton_InvoiceItem_Edit_Save_.text")); // NOI18N
        jButton_InvoiceItem_Edit_Save_.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton_InvoiceItem_Edit_Save_ActionPerformed(evt);
            }
        });

        org.openide.awt.Mnemonics.setLocalizedText(jButton_InvoiceItem_Edit_Delete_, org.openide.util.NbBundle.getMessage(InvoiceTopComponent.class, "InvoiceTopComponent.jButton_InvoiceItem_Edit_Delete_.text")); // NOI18N
        jButton_InvoiceItem_Edit_Delete_.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton_InvoiceItem_Edit_Delete_ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel_TableLayout = new javax.swing.GroupLayout(jPanel_Table);
        jPanel_Table.setLayout(jPanel_TableLayout);
        jPanel_TableLayout.setHorizontalGroup(
            jPanel_TableLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel_TableLayout.createSequentialGroup()
                .addGroup(jPanel_TableLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                    .addComponent(jPanel_Invoice_Navigator, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 677, Short.MAX_VALUE)
                    .addGroup(jPanel_TableLayout.createSequentialGroup()
                        .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 250, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 421, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGroup(jPanel_TableLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel_TableLayout.createSequentialGroup()
                        .addGap(34, 34, 34)
                        .addComponent(jButton_InvoiceItem_Edit_Save_)
                        .addGap(49, 49, 49)
                        .addComponent(jButton_InvoiceItem_Edit_Delete_)
                        .addContainerGap())
                    .addGroup(jPanel_TableLayout.createSequentialGroup()
                        .addGap(18, 18, 18)
                        .addComponent(jPanel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))))
        );
        jPanel_TableLayout.setVerticalGroup(
            jPanel_TableLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel_TableLayout.createSequentialGroup()
                .addComponent(jPanel_Invoice_Navigator, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel_TableLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 148, Short.MAX_VALUE)
                    .addComponent(jScrollPane2, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            .addGroup(jPanel_TableLayout.createSequentialGroup()
                .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(jPanel_TableLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jButton_InvoiceItem_Edit_Save_, javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jButton_InvoiceItem_Edit_Delete_, javax.swing.GroupLayout.Alignment.TRAILING))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jPanel2.setBorder(javax.swing.BorderFactory.createTitledBorder(org.openide.util.NbBundle.getMessage(InvoiceTopComponent.class, "InvoiceTopComponent.jPanel2.border.title"))); // NOI18N

        org.openide.awt.Mnemonics.setLocalizedText(jLabel2, org.openide.util.NbBundle.getMessage(InvoiceTopComponent.class, "InvoiceTopComponent.jLabel2.text")); // NOI18N

        jTextField_ID_Filter.setText(org.openide.util.NbBundle.getMessage(InvoiceTopComponent.class, "InvoiceTopComponent.jTextField_ID_Filter.text")); // NOI18N

        jPanel4.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(204, 204, 204)));

        org.openide.awt.Mnemonics.setLocalizedText(jLabel1, org.openide.util.NbBundle.getMessage(InvoiceTopComponent.class, "InvoiceTopComponent.jLabel1.text_1")); // NOI18N

        org.openide.awt.Mnemonics.setLocalizedText(jLabel10, org.openide.util.NbBundle.getMessage(InvoiceTopComponent.class, "InvoiceTopComponent.jLabel10.text")); // NOI18N

        jTextField_User_firstName.setText(org.openide.util.NbBundle.getMessage(InvoiceTopComponent.class, "InvoiceTopComponent.jTextField_User_firstName.text")); // NOI18N

        org.openide.awt.Mnemonics.setLocalizedText(jLabel11, org.openide.util.NbBundle.getMessage(InvoiceTopComponent.class, "InvoiceTopComponent.jLabel11.text")); // NOI18N

        jTextField_User_lastName.setText(org.openide.util.NbBundle.getMessage(InvoiceTopComponent.class, "InvoiceTopComponent.jTextField_User_lastName.text")); // NOI18N

        javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addGap(6, 6, 6)
                .addComponent(jLabel1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jLabel10)
                .addGap(18, 18, 18)
                .addComponent(jTextField_User_firstName, javax.swing.GroupLayout.PREFERRED_SIZE, 82, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jLabel11)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jTextField_User_lastName, javax.swing.GroupLayout.PREFERRED_SIZE, 92, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
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
                    .addComponent(jTextField_User_lastName, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap())
        );

        jPanel5.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(204, 204, 204)));

        org.openide.awt.Mnemonics.setLocalizedText(jLabel7, org.openide.util.NbBundle.getMessage(InvoiceTopComponent.class, "InvoiceTopComponent.jLabel7.text")); // NOI18N
        jLabel7.setMinimumSize(new java.awt.Dimension(24, 20));
        jLabel7.setPreferredSize(new java.awt.Dimension(24, 20));

        org.openide.awt.Mnemonics.setLocalizedText(jLabel9, org.openide.util.NbBundle.getMessage(InvoiceTopComponent.class, "InvoiceTopComponent.jLabel9.text")); // NOI18N
        jLabel9.setMaximumSize(new java.awt.Dimension(10, 20));
        jLabel9.setPreferredSize(new java.awt.Dimension(10, 20));

        javax.swing.GroupLayout jPanel5Layout = new javax.swing.GroupLayout(jPanel5);
        jPanel5.setLayout(jPanel5Layout);
        jPanel5Layout.setHorizontalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel7, javax.swing.GroupLayout.PREFERRED_SIZE, 43, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(dateField_createDate_From, javax.swing.GroupLayout.PREFERRED_SIZE, 95, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jLabel9, javax.swing.GroupLayout.PREFERRED_SIZE, 12, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(dateField_createDate_To, javax.swing.GroupLayout.PREFERRED_SIZE, 96, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(100, Short.MAX_VALUE))
        );
        jPanel5Layout.setVerticalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(dateField_createDate_To, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel9, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(dateField_createDate_From, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel7, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        org.openide.awt.Mnemonics.setLocalizedText(jLabel8, org.openide.util.NbBundle.getMessage(InvoiceTopComponent.class, "InvoiceTopComponent.jLabel8.text")); // NOI18N

        jTextField_CustomerId_Filter.setText(org.openide.util.NbBundle.getMessage(InvoiceTopComponent.class, "InvoiceTopComponent.jTextField_CustomerId_Filter.text")); // NOI18N

        org.openide.awt.Mnemonics.setLocalizedText(jLabel3, org.openide.util.NbBundle.getMessage(InvoiceTopComponent.class, "InvoiceTopComponent.jLabel3.text")); // NOI18N

        org.openide.awt.Mnemonics.setLocalizedText(jLabel4, org.openide.util.NbBundle.getMessage(InvoiceTopComponent.class, "InvoiceTopComponent.jLabel4.text")); // NOI18N

        jTextField_LastName_Filter.setText(org.openide.util.NbBundle.getMessage(InvoiceTopComponent.class, "InvoiceTopComponent.jTextField_LastName_Filter.text")); // NOI18N

        org.openide.awt.Mnemonics.setLocalizedText(jLabel6, org.openide.util.NbBundle.getMessage(InvoiceTopComponent.class, "InvoiceTopComponent.jLabel6.text")); // NOI18N

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

        jLabel_Errors.setForeground(new java.awt.Color(255, 51, 0));
        org.openide.awt.Mnemonics.setLocalizedText(jLabel_Errors, org.openide.util.NbBundle.getMessage(InvoiceTopComponent.class, "InvoiceTopComponent.jLabel_Errors.text")); // NOI18N

        jButton_Errors_Details.setIcon(new javax.swing.ImageIcon(getClass().getResource("/org/cdms/ui/images/action_stop.gif"))); // NOI18N
        org.openide.awt.Mnemonics.setLocalizedText(jButton_Errors_Details, org.openide.util.NbBundle.getMessage(InvoiceTopComponent.class, "InvoiceTopComponent.jButton_Errors_Details.text")); // NOI18N
        jButton_Errors_Details.setContentAreaFilled(false);
        jButton_Errors_Details.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton_Errors_DetailsActionPerformed(evt);
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
                .addComponent(jButton_Errors_Details)
                .addContainerGap())
        );
        jPanel_Error_MsgLayout.setVerticalGroup(
            jPanel_Error_MsgLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel_Error_MsgLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                .addComponent(jButton_Errors_Details, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                .addComponent(jLabel_Errors, javax.swing.GroupLayout.DEFAULT_SIZE, 23, Short.MAX_VALUE))
        );

        jTextField_Phone_Filter.setText(org.openide.util.NbBundle.getMessage(InvoiceTopComponent.class, "InvoiceTopComponent.jTextField_Phone_Filter.text")); // NOI18N

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
                        .addComponent(jButton_Clear_, javax.swing.GroupLayout.PREFERRED_SIZE, 72, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel8)
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                                .addComponent(jLabel2)
                                .addGap(17, 17, 17)))
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jTextField_CustomerId_Filter, javax.swing.GroupLayout.PREFERRED_SIZE, 65, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jTextField_ID_Filter, javax.swing.GroupLayout.PREFERRED_SIZE, 64, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel2Layout.createSequentialGroup()
                                .addGap(9, 9, 9)
                                .addComponent(jPanel4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(jPanel5, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(0, 0, Short.MAX_VALUE))
                            .addGroup(jPanel2Layout.createSequentialGroup()
                                .addGap(81, 81, 81)
                                .addComponent(jLabel3)
                                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(jPanel2Layout.createSequentialGroup()
                                        .addGap(20, 20, 20)
                                        .addComponent(jPanel_Error_Msg, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                                    .addGroup(jPanel2Layout.createSequentialGroup()
                                        .addGap(18, 18, 18)
                                        .addComponent(jTextField_FirstName_Filter, javax.swing.GroupLayout.PREFERRED_SIZE, 82, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addGap(12, 12, 12)
                                        .addComponent(jLabel4)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                        .addComponent(jTextField_LastName_Filter, javax.swing.GroupLayout.PREFERRED_SIZE, 90, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addGap(32, 32, 32)
                                        .addComponent(jLabel6)
                                        .addGap(15, 15, 15)
                                        .addComponent(jTextField_Phone_Filter, javax.swing.GroupLayout.PREFERRED_SIZE, 89, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                        .addComponent(jLabel5)))
                                .addGap(18, 18, 18)
                                .addComponent(jTextField_Email_Filter)))))
                .addContainerGap())
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
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
                            .addComponent(jLabel3)
                            .addComponent(jTextField_FirstName_Filter, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel4)
                            .addComponent(jTextField_LastName_Filter, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel6)
                            .addComponent(jTextField_Email_Filter, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel5)
                            .addComponent(jTextField_Phone_Filter, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addComponent(jTextField_CustomerId_Filter, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jButton_Search_)
                        .addComponent(jButton_Clear_))
                    .addComponent(jPanel_Error_Msg, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jPanel_ProductItems.setBorder(javax.swing.BorderFactory.createTitledBorder(org.openide.util.NbBundle.getMessage(InvoiceTopComponent.class, "InvoiceTopComponent.jPanel_ProductItems.border.title"))); // NOI18N

        jPanel_ProductItem_Navigator.setBorder(javax.swing.BorderFactory.createTitledBorder(""));
        jPanel_ProductItem_Navigator.setMinimumSize(new java.awt.Dimension(0, 37));
        jPanel_ProductItem_Navigator.setPreferredSize(new java.awt.Dimension(788, 37));
        jPanel_ProductItem_Navigator.setRequestFocusEnabled(false);

        jButton_PageItem_FirstPage_.setIcon(new javax.swing.ImageIcon(getClass().getResource("/org/cdms/ui/images/navigate_beginning.png"))); // NOI18N
        org.openide.awt.Mnemonics.setLocalizedText(jButton_PageItem_FirstPage_, org.openide.util.NbBundle.getMessage(InvoiceTopComponent.class, "InvoiceTopComponent.jButton_PageItem_FirstPage_.text")); // NOI18N
        jButton_PageItem_FirstPage_.setToolTipText(org.openide.util.NbBundle.getMessage(InvoiceTopComponent.class, "InvoiceTopComponent.jButton_PageItem_FirstPage_.toolTipText")); // NOI18N
        jButton_PageItem_FirstPage_.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 1, 1, 1));
        jButton_PageItem_FirstPage_.setContentAreaFilled(false);
        jButton_PageItem_FirstPage_.setMargin(new java.awt.Insets(0, 0, 0, 0));
        jButton_PageItem_FirstPage_.setMaximumSize(new java.awt.Dimension(16, 16));
        jButton_PageItem_FirstPage_.setMinimumSize(new java.awt.Dimension(16, 16));
        jButton_PageItem_FirstPage_.setPreferredSize(new java.awt.Dimension(16, 16));
        jButton_PageItem_FirstPage_.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton_PageItem_FirstPage_ActionPerformed(evt);
            }
        });

        jButton_PageItem_PriorPage_.setIcon(new javax.swing.ImageIcon(getClass().getResource("/org/cdms/ui/images/navigate_left.png"))); // NOI18N
        org.openide.awt.Mnemonics.setLocalizedText(jButton_PageItem_PriorPage_, org.openide.util.NbBundle.getMessage(InvoiceTopComponent.class, "InvoiceTopComponent.jButton_PageItem_PriorPage_.text")); // NOI18N
        jButton_PageItem_PriorPage_.setToolTipText(org.openide.util.NbBundle.getMessage(InvoiceTopComponent.class, "InvoiceTopComponent.jButton_PageItem_PriorPage_.toolTipText")); // NOI18N
        jButton_PageItem_PriorPage_.setBorder(null);
        jButton_PageItem_PriorPage_.setContentAreaFilled(false);
        jButton_PageItem_PriorPage_.setMaximumSize(new java.awt.Dimension(16, 16));
        jButton_PageItem_PriorPage_.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton_PageItem_PriorPage_ActionPerformed(evt);
            }
        });

        jButton_PageItem_NextPage_.setIcon(new javax.swing.ImageIcon(getClass().getResource("/org/cdms/ui/images/navigate_right.png"))); // NOI18N
        org.openide.awt.Mnemonics.setLocalizedText(jButton_PageItem_NextPage_, org.openide.util.NbBundle.getMessage(InvoiceTopComponent.class, "InvoiceTopComponent.jButton_PageItem_NextPage_.text")); // NOI18N
        jButton_PageItem_NextPage_.setToolTipText(org.openide.util.NbBundle.getMessage(InvoiceTopComponent.class, "InvoiceTopComponent.jButton_PageItem_NextPage_.toolTipText")); // NOI18N
        jButton_PageItem_NextPage_.setContentAreaFilled(false);
        jButton_PageItem_NextPage_.setMaximumSize(new java.awt.Dimension(16, 16));
        jButton_PageItem_NextPage_.setMinimumSize(new java.awt.Dimension(16, 16));
        jButton_PageItem_NextPage_.setPreferredSize(new java.awt.Dimension(16, 16));
        jButton_PageItem_NextPage_.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton_PageItem_NextPage_ActionPerformed(evt);
            }
        });

        jButton_PageItem_LastPage_.setIcon(new javax.swing.ImageIcon(getClass().getResource("/org/cdms/ui/images/navigate_end.png"))); // NOI18N
        org.openide.awt.Mnemonics.setLocalizedText(jButton_PageItem_LastPage_, org.openide.util.NbBundle.getMessage(InvoiceTopComponent.class, "InvoiceTopComponent.jButton_PageItem_LastPage_.text")); // NOI18N
        jButton_PageItem_LastPage_.setToolTipText(org.openide.util.NbBundle.getMessage(InvoiceTopComponent.class, "InvoiceTopComponent.jButton_PageItem_LastPage_.toolTipText")); // NOI18N
        jButton_PageItem_LastPage_.setContentAreaFilled(false);
        jButton_PageItem_LastPage_.setMaximumSize(new java.awt.Dimension(16, 16));
        jButton_PageItem_LastPage_.setMinimumSize(new java.awt.Dimension(16, 16));
        jButton_PageItem_LastPage_.setPreferredSize(new java.awt.Dimension(16, 16));
        jButton_PageItem_LastPage_.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton_PageItem_LastPage_ActionPerformed(evt);
            }
        });

        jButton_PageItem_Refresh_Table.setIcon(new javax.swing.ImageIcon(getClass().getResource("/org/cdms/ui/images/refresh.png"))); // NOI18N
        org.openide.awt.Mnemonics.setLocalizedText(jButton_PageItem_Refresh_Table, org.openide.util.NbBundle.getMessage(InvoiceTopComponent.class, "InvoiceTopComponent.jButton_PageItem_Refresh_Table.text")); // NOI18N
        jButton_PageItem_Refresh_Table.setToolTipText(org.openide.util.NbBundle.getMessage(InvoiceTopComponent.class, "InvoiceTopComponent.jButton_PageItem_Refresh_Table.toolTipText")); // NOI18N
        jButton_PageItem_Refresh_Table.setContentAreaFilled(false);
        jButton_PageItem_Refresh_Table.setMaximumSize(new java.awt.Dimension(16, 16));
        jButton_PageItem_Refresh_Table.setMinimumSize(new java.awt.Dimension(16, 16));
        jButton_PageItem_Refresh_Table.setPreferredSize(new java.awt.Dimension(16, 16));
        jButton_PageItem_Refresh_Table.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton_PageItem_Refresh_TableActionPerformed(evt);
            }
        });

        org.openide.awt.Mnemonics.setLocalizedText(jLabel1_PageItem_PageSize, org.openide.util.NbBundle.getMessage(InvoiceTopComponent.class, "InvoiceTopComponent.jLabel1_PageItem_PageSize.text")); // NOI18N

        jFormattedTextField_PageItem_PageSize.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.NumberFormatter(new java.text.DecimalFormat("######"))));
        jFormattedTextField_PageItem_PageSize.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        jFormattedTextField_PageItem_PageSize.setText(org.openide.util.NbBundle.getMessage(InvoiceTopComponent.class, "InvoiceTopComponent.jFormattedTextField_PageItem_PageSize.text")); // NOI18N

        org.openide.awt.Mnemonics.setLocalizedText(jLabel_PageItem_PageNo, org.openide.util.NbBundle.getMessage(InvoiceTopComponent.class, "InvoiceTopComponent.jLabel_PageItem_PageNo.text")); // NOI18N

        jTextField_PageItem_Item_PageNo.setEditable(false);
        jTextField_PageItem_Item_PageNo.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        jTextField_PageItem_Item_PageNo.setText(org.openide.util.NbBundle.getMessage(InvoiceTopComponent.class, "InvoiceTopComponent.jTextField_PageItem_Item_PageNo.text")); // NOI18N

        jTextField_PageItem_Item_RowCount.setEditable(false);
        jTextField_PageItem_Item_RowCount.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        jTextField_PageItem_Item_RowCount.setText(org.openide.util.NbBundle.getMessage(InvoiceTopComponent.class, "InvoiceTopComponent.jTextField_PageItem_Item_RowCount.text")); // NOI18N

        javax.swing.GroupLayout jPanel_ProductItem_NavigatorLayout = new javax.swing.GroupLayout(jPanel_ProductItem_Navigator);
        jPanel_ProductItem_Navigator.setLayout(jPanel_ProductItem_NavigatorLayout);
        jPanel_ProductItem_NavigatorLayout.setHorizontalGroup(
            jPanel_ProductItem_NavigatorLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel_ProductItem_NavigatorLayout.createSequentialGroup()
                .addComponent(jButton_PageItem_Refresh_Table, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jButton_PageItem_FirstPage_, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jButton_PageItem_PriorPage_, javax.swing.GroupLayout.PREFERRED_SIZE, 21, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jButton_PageItem_NextPage_, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jButton_PageItem_LastPage_, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(jLabel1_PageItem_PageSize)
                .addGap(18, 18, 18)
                .addComponent(jFormattedTextField_PageItem_PageSize, javax.swing.GroupLayout.PREFERRED_SIZE, 36, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(jLabel_PageItem_PageNo)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jTextField_PageItem_Item_PageNo, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jTextField_PageItem_Item_RowCount, javax.swing.GroupLayout.PREFERRED_SIZE, 84, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel_ProductItem_NavigatorLayout.setVerticalGroup(
            jPanel_ProductItem_NavigatorLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel_ProductItem_NavigatorLayout.createSequentialGroup()
                .addGroup(jPanel_ProductItem_NavigatorLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addGroup(jPanel_ProductItem_NavigatorLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jTextField_PageItem_Item_PageNo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jTextField_PageItem_Item_RowCount, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel_ProductItem_NavigatorLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jFormattedTextField_PageItem_PageSize)
                        .addComponent(jLabel1_PageItem_PageSize)
                        .addComponent(jLabel_PageItem_PageNo))
                    .addComponent(jButton_PageItem_LastPage_, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jButton_PageItem_NextPage_, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jButton_PageItem_PriorPage_, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jButton_PageItem_FirstPage_, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jButton_PageItem_Refresh_Table, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addGap(0, 0, Short.MAX_VALUE))
        );

        jTable_ProductItems.setAutoCreateRowSorter(true);
        jTable_ProductItems.setModel(new javax.swing.table.DefaultTableModel(
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
        jTable_ProductItems.setGridColor(new java.awt.Color(204, 204, 204));
        jScrollPane3.setViewportView(jTable_ProductItems);

        jPanel1.setBorder(javax.swing.BorderFactory.createTitledBorder(org.openide.util.NbBundle.getMessage(InvoiceTopComponent.class, "InvoiceTopComponent.jPanel1.border.title"))); // NOI18N

        org.openide.awt.Mnemonics.setLocalizedText(jLabel28, org.openide.util.NbBundle.getMessage(InvoiceTopComponent.class, "InvoiceTopComponent.jLabel28.text")); // NOI18N

        jTextField_ProductItem_Id_Add.setEditable(false);
        jTextField_ProductItem_Id_Add.setText(org.openide.util.NbBundle.getMessage(InvoiceTopComponent.class, "InvoiceTopComponent.jTextField_ProductItem_Id_Add.text")); // NOI18N

        org.openide.awt.Mnemonics.setLocalizedText(jLabel29, org.openide.util.NbBundle.getMessage(InvoiceTopComponent.class, "InvoiceTopComponent.jLabel29.text")); // NOI18N

        jTextField_ProductItem_Barcode_Add.setEditable(false);
        jTextField_ProductItem_Barcode_Add.setText(org.openide.util.NbBundle.getMessage(InvoiceTopComponent.class, "InvoiceTopComponent.jTextField_ProductItem_Barcode_Add.text")); // NOI18N

        org.openide.awt.Mnemonics.setLocalizedText(jLabel30, org.openide.util.NbBundle.getMessage(InvoiceTopComponent.class, "InvoiceTopComponent.jLabel30.text")); // NOI18N

        jTextField_ProductItem_ItemName_Add.setEditable(false);
        jTextField_ProductItem_ItemName_Add.setText(org.openide.util.NbBundle.getMessage(InvoiceTopComponent.class, "InvoiceTopComponent.jTextField_ProductItem_ItemName_Add.text")); // NOI18N

        org.openide.awt.Mnemonics.setLocalizedText(jLabel31, org.openide.util.NbBundle.getMessage(InvoiceTopComponent.class, "InvoiceTopComponent.jLabel31.text")); // NOI18N

        jTextField_ProductItem_Price_Add.setEditable(false);
        jTextField_ProductItem_Price_Add.setText(org.openide.util.NbBundle.getMessage(InvoiceTopComponent.class, "InvoiceTopComponent.jTextField_ProductItem_Price_Add.text")); // NOI18N

        org.openide.awt.Mnemonics.setLocalizedText(jLabel32, org.openide.util.NbBundle.getMessage(InvoiceTopComponent.class, "InvoiceTopComponent.jLabel32.text")); // NOI18N

        org.openide.awt.Mnemonics.setLocalizedText(jButton_InvoiceItem_Add_To_Invoice, org.openide.util.NbBundle.getMessage(InvoiceTopComponent.class, "InvoiceTopComponent.jButton_InvoiceItem_Add_To_Invoice.text")); // NOI18N
        jButton_InvoiceItem_Add_To_Invoice.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton_InvoiceItem_Add_To_InvoiceActionPerformed(evt);
            }
        });

        jFormattedTextField_InvoiceItem_itemCount_Add.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.NumberFormatter(new java.text.DecimalFormat("######"))));
        jFormattedTextField_InvoiceItem_itemCount_Add.setText(org.openide.util.NbBundle.getMessage(InvoiceTopComponent.class, "InvoiceTopComponent.jFormattedTextField_InvoiceItem_itemCount_Add.text")); // NOI18N

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(jPanel1Layout.createSequentialGroup()
                            .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                .addComponent(jLabel28)
                                .addComponent(jLabel32)
                                .addComponent(jLabel31))
                            .addGap(28, 28, 28)
                            .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                .addComponent(jTextField_ProductItem_Price_Add)
                                .addComponent(jFormattedTextField_InvoiceItem_itemCount_Add)
                                .addComponent(jTextField_ProductItem_Id_Add)
                                .addComponent(jTextField_ProductItem_Barcode_Add, javax.swing.GroupLayout.PREFERRED_SIZE, 96, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addGroup(jPanel1Layout.createSequentialGroup()
                            .addComponent(jLabel30)
                            .addGap(18, 18, 18)
                            .addComponent(jTextField_ProductItem_ItemName_Add, javax.swing.GroupLayout.PREFERRED_SIZE, 96, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addComponent(jButton_InvoiceItem_Add_To_Invoice, javax.swing.GroupLayout.Alignment.TRAILING))
                    .addComponent(jLabel29))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel28)
                    .addComponent(jTextField_ProductItem_Id_Add, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel29)
                    .addComponent(jTextField_ProductItem_Barcode_Add, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(9, 9, 9)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel30)
                    .addComponent(jTextField_ProductItem_ItemName_Add, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel31)
                    .addComponent(jTextField_ProductItem_Price_Add, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel32)
                    .addComponent(jFormattedTextField_InvoiceItem_itemCount_Add, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jButton_InvoiceItem_Add_To_Invoice)
                .addGap(8, 8, 8))
        );

        jPanel6.setBorder(javax.swing.BorderFactory.createTitledBorder(org.openide.util.NbBundle.getMessage(InvoiceTopComponent.class, "InvoiceTopComponent.jPanel6.border.title"))); // NOI18N

        org.openide.awt.Mnemonics.setLocalizedText(jLabel25, org.openide.util.NbBundle.getMessage(InvoiceTopComponent.class, "InvoiceTopComponent.jLabel25.text")); // NOI18N

        org.openide.awt.Mnemonics.setLocalizedText(jLabel26, org.openide.util.NbBundle.getMessage(InvoiceTopComponent.class, "InvoiceTopComponent.jLabel26.text")); // NOI18N

        org.openide.awt.Mnemonics.setLocalizedText(jLabel27, org.openide.util.NbBundle.getMessage(InvoiceTopComponent.class, "InvoiceTopComponent.jLabel27.text")); // NOI18N

        jTextField_Item_Id_Filter.setText(org.openide.util.NbBundle.getMessage(InvoiceTopComponent.class, "InvoiceTopComponent.jTextField_Item_Id_Filter.text")); // NOI18N

        jTextField_Item_Barcode_Filter.setText(org.openide.util.NbBundle.getMessage(InvoiceTopComponent.class, "InvoiceTopComponent.jTextField_Item_Barcode_Filter.text")); // NOI18N

        jTextField_Item_ItemName_Filter.setText(org.openide.util.NbBundle.getMessage(InvoiceTopComponent.class, "InvoiceTopComponent.jTextField_Item_ItemName_Filter.text")); // NOI18N

        org.openide.awt.Mnemonics.setLocalizedText(jButton_PageItem_Search_Filter, org.openide.util.NbBundle.getMessage(InvoiceTopComponent.class, "InvoiceTopComponent.jButton_PageItem_Search_Filter.text")); // NOI18N
        jButton_PageItem_Search_Filter.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton_PageItem_Search_FilterActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel6Layout = new javax.swing.GroupLayout(jPanel6);
        jPanel6.setLayout(jPanel6Layout);
        jPanel6Layout.setHorizontalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel6Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jButton_PageItem_Search_Filter)
                    .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(jPanel6Layout.createSequentialGroup()
                            .addComponent(jLabel27)
                            .addGap(18, 18, 18)
                            .addComponent(jTextField_Item_ItemName_Filter, javax.swing.GroupLayout.PREFERRED_SIZE, 96, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGroup(jPanel6Layout.createSequentialGroup()
                            .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                .addComponent(jLabel25)
                                .addComponent(jLabel26))
                            .addGap(31, 31, 31)
                            .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                .addComponent(jTextField_Item_Barcode_Filter, javax.swing.GroupLayout.PREFERRED_SIZE, 96, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(jTextField_Item_Id_Filter, javax.swing.GroupLayout.PREFERRED_SIZE, 96, javax.swing.GroupLayout.PREFERRED_SIZE)))))
                .addContainerGap(20, Short.MAX_VALUE))
        );
        jPanel6Layout.setVerticalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel6Layout.createSequentialGroup()
                .addGap(28, 28, 28)
                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel25)
                    .addComponent(jTextField_Item_Id_Filter, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel26)
                    .addComponent(jTextField_Item_Barcode_Filter, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel27)
                    .addComponent(jTextField_Item_ItemName_Filter, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jButton_PageItem_Search_Filter)
                .addContainerGap())
        );

        javax.swing.GroupLayout jPanel_ProductItemsLayout = new javax.swing.GroupLayout(jPanel_ProductItems);
        jPanel_ProductItems.setLayout(jPanel_ProductItemsLayout);
        jPanel_ProductItemsLayout.setHorizontalGroup(
            jPanel_ProductItemsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel_ProductItemsLayout.createSequentialGroup()
                .addGroup(jPanel_ProductItemsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jPanel_ProductItem_Navigator, javax.swing.GroupLayout.DEFAULT_SIZE, 463, Short.MAX_VALUE)
                    .addComponent(jScrollPane3))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jPanel6, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel_ProductItemsLayout.setVerticalGroup(
            jPanel_ProductItemsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel_ProductItemsLayout.createSequentialGroup()
                .addGroup(jPanel_ProductItemsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, jPanel_ProductItemsLayout.createSequentialGroup()
                        .addComponent(jPanel_ProductItem_Navigator, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jScrollPane3, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE))
                    .addComponent(jPanel6, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel1, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addGap(0, 0, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel_Table, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel_ProductItems, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addGap(0, 0, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap(24, Short.MAX_VALUE)
                .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel_Table, javax.swing.GroupLayout.PREFERRED_SIZE, 208, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel_ProductItems, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );
    }// </editor-fold>//GEN-END:initComponents

    private void jButton_Invoice_FirstPage_ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton_Invoice_FirstPage_ActionPerformed
        invoiceQueryPage.setPageNo(0);
        doInvoiceFilter();
    }//GEN-LAST:event_jButton_Invoice_FirstPage_ActionPerformed

    private void jButton_Invoice_PriorPage_ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton_Invoice_PriorPage_ActionPerformed
        invoiceQueryPage.setPageNo(invoiceQueryPage.getPageNo() - 1);
        doInvoiceFilter();
    }//GEN-LAST:event_jButton_Invoice_PriorPage_ActionPerformed

    private void jButton_Invoice_NextPage_ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton_Invoice_NextPage_ActionPerformed
        invoiceQueryPage.setPageNo(invoiceQueryPage.getPageNo() + 1);
        doInvoiceFilter();
    }//GEN-LAST:event_jButton_Invoice_NextPage_ActionPerformed

    private void jButton_Invoice_LastPage_ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton_Invoice_LastPage_ActionPerformed
        int lastPage = (int) (invoiceQueryPage.getRowCount() / invoiceQueryPage.getPageSize() - 1);
        if (invoiceQueryPage.getRowCount() % invoiceQueryPage.getPageSize() != 0) {
            lastPage++;
        }

        invoiceQueryPage.setPageNo(lastPage);
        doInvoiceFilter();
    }//GEN-LAST:event_jButton_Invoice_LastPage_ActionPerformed

    private void jButton_Refresh_Invoice_TableActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton_Refresh_Invoice_TableActionPerformed
        //TODO in production. A user may change page size.
        //     So we must  keep it im mind
        doInvoiceFilter();
    }//GEN-LAST:event_jButton_Refresh_Invoice_TableActionPerformed

    private void jButton_Search_ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton_Search_ActionPerformed
        int pageSize = Integer.parseInt(jFormattedTextField_Invoice_PageSize.getText());
        invoiceQueryPage.setPageSize(pageSize);
        invoiceQueryPage.setPageNo(0);

        doInvoiceFilter();
    }//GEN-LAST:event_jButton_Search_ActionPerformed

    private void jButton_Clear_ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton_Clear_ActionPerformed
        jTextField_Email_Filter.setText("");
        jTextField_FirstName_Filter.setText("");
        jTextField_ID_Filter.setText("");
        jTextField_LastName_Filter.setText("");
        jTextField_Phone_Filter.setText("");
        dateField_createDate_From.setValue(null);
        dateField_createDate_To.setValue(null);
    }//GEN-LAST:event_jButton_Clear_ActionPerformed

    private void jButton_PageItem_FirstPage_ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton_PageItem_FirstPage_ActionPerformed
        productItemQueryPage.setPageNo(0);
        doProductItemFilter();
    }//GEN-LAST:event_jButton_PageItem_FirstPage_ActionPerformed

    private void jButton_PageItem_PriorPage_ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton_PageItem_PriorPage_ActionPerformed
        productItemQueryPage.setPageNo(productItemQueryPage.getPageNo() - 1);
        doProductItemFilter();
    }//GEN-LAST:event_jButton_PageItem_PriorPage_ActionPerformed

    private void jButton_PageItem_NextPage_ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton_PageItem_NextPage_ActionPerformed
        productItemQueryPage.setPageNo(productItemQueryPage.getPageNo() + 1);
        doProductItemFilter();
    }//GEN-LAST:event_jButton_PageItem_NextPage_ActionPerformed

    private void jButton_PageItem_LastPage_ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton_PageItem_LastPage_ActionPerformed
        int lastPage = (int) (productItemQueryPage.getRowCount() / productItemQueryPage.getPageSize() - 1);
        if (productItemQueryPage.getRowCount() % productItemQueryPage.getPageSize() != 0) {
            lastPage++;
        }

        productItemQueryPage.setPageNo(lastPage);
        doProductItemFilter();

    }//GEN-LAST:event_jButton_PageItem_LastPage_ActionPerformed

    private void jButton_PageItem_Refresh_TableActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton_PageItem_Refresh_TableActionPerformed
        doProductItemFilter();
    }//GEN-LAST:event_jButton_PageItem_Refresh_TableActionPerformed

    private void jButton_PageItem_Search_FilterActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton_PageItem_Search_FilterActionPerformed
        int pageSize = Integer.parseInt(jFormattedTextField_PageItem_PageSize.getText());
        productItemQueryPage.setPageSize(pageSize);
        productItemQueryPage.setPageNo(0);

        doProductItemFilter();
    }//GEN-LAST:event_jButton_PageItem_Search_FilterActionPerformed

    private void jButton_Errors_DetailsActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton_Errors_DetailsActionPerformed
        errorDetailsHandler.show();
    }//GEN-LAST:event_jButton_Errors_DetailsActionPerformed

    private void jButton_InvoiceItem_Edit_Save_ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton_InvoiceItem_Edit_Save_ActionPerformed
        hideErrors();

        int row = jTable_InvoiceItem.getSelectedRow();
        if (row < 0) {
            jLabel_Errors.setText(NbBundle.getMessage(InvoiceTopComponent.class, "InvoiceTopComponent.Internal.NoSelectedRow"));
            showErrors(NbBundle.getMessage(InvoiceTopComponent.class, "InvoiceTopComponent.Internal.NoSelectedRow"));
            return;
        }
        updateInvoiceItem();

    }//GEN-LAST:event_jButton_InvoiceItem_Edit_Save_ActionPerformed

    private void jButton_InvoiceItem_Edit_Delete_ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton_InvoiceItem_Edit_Delete_ActionPerformed
        hideErrors();

        int row = jTable_InvoiceItem.getSelectedRow();
        if (row < 0) {
            showErrors(NbBundle.getMessage(InvoiceTopComponent.class, "InvoiceTopComponent.Internal.NoSelectedRow"));
            return;
        }
        Long id = Long.parseLong(jTextField_InvoiceItem_Id_Edit.getText());
        if (!DeleteConfirmDialog.confirm("InvoiceItem", id)) {
            return;
        }
        deleteInvoiceItem();

    }//GEN-LAST:event_jButton_InvoiceItem_Edit_Delete_ActionPerformed

    private void jButton_InvoiceItem_Add_To_InvoiceActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton_InvoiceItem_Add_To_InvoiceActionPerformed
        hideErrors();
        int row = jTable_Invoice.getSelectedRow();
        int row1 = jTable_ProductItems.getSelectedRow();

        if (row < 0 || row1 < 0) {
            showErrors(NbBundle.getMessage(InvoiceTopComponent.class, "InvoiceTopComponent.Internal.NoSelectedRow"));
            return;
        }
        insertInvoiceItem();
    }//GEN-LAST:event_jButton_InvoiceItem_Add_To_InvoiceActionPerformed
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private com.vns.comp.DatePickerEx dateField_createDate_From;
    private com.vns.comp.DatePickerEx dateField_createDate_To;
    private javax.swing.JButton jButton_Clear_;
    private javax.swing.JButton jButton_Errors_Details;
    private javax.swing.JButton jButton_InvoiceItem_Add_To_Invoice;
    private javax.swing.JButton jButton_InvoiceItem_Edit_Delete_;
    private javax.swing.JButton jButton_InvoiceItem_Edit_Save_;
    private javax.swing.JButton jButton_Invoice_FirstPage_;
    private javax.swing.JButton jButton_Invoice_LastPage_;
    private javax.swing.JButton jButton_Invoice_NextPage_;
    private javax.swing.JButton jButton_Invoice_PriorPage_;
    private javax.swing.JButton jButton_PageItem_FirstPage_;
    private javax.swing.JButton jButton_PageItem_LastPage_;
    private javax.swing.JButton jButton_PageItem_NextPage_;
    private javax.swing.JButton jButton_PageItem_PriorPage_;
    private javax.swing.JButton jButton_PageItem_Refresh_Table;
    private javax.swing.JButton jButton_PageItem_Search_Filter;
    private javax.swing.JButton jButton_Refresh_Invoice_Table;
    private javax.swing.JButton jButton_Search_;
    private javax.swing.JFormattedTextField jFormattedTextField_InvoiceItem_itemCount_Add;
    private javax.swing.JFormattedTextField jFormattedTextField_Invoice_PageSize;
    private javax.swing.JFormattedTextField jFormattedTextField_PageItem_PageSize;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel1_PageItem_PageSize;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel20;
    private javax.swing.JLabel jLabel21;
    private javax.swing.JLabel jLabel22;
    private javax.swing.JLabel jLabel23;
    private javax.swing.JLabel jLabel24;
    private javax.swing.JLabel jLabel25;
    private javax.swing.JLabel jLabel26;
    private javax.swing.JLabel jLabel27;
    private javax.swing.JLabel jLabel28;
    private javax.swing.JLabel jLabel29;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel30;
    private javax.swing.JLabel jLabel31;
    private javax.swing.JLabel jLabel32;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JLabel jLabel_Errors;
    private javax.swing.JLabel jLabel_Invoice_PageNo;
    private javax.swing.JLabel jLabel_Invoice_PageSize;
    private javax.swing.JLabel jLabel_PageItem_PageNo;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JPanel jPanel6;
    private javax.swing.JPanel jPanel_Error_Msg;
    private javax.swing.JPanel jPanel_Invoice_Navigator;
    private javax.swing.JPanel jPanel_ProductItem_Navigator;
    private javax.swing.JPanel jPanel_ProductItems;
    private javax.swing.JPanel jPanel_Table;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JTable jTable_Invoice;
    private javax.swing.JTable jTable_InvoiceItem;
    private javax.swing.JTable jTable_ProductItems;
    private javax.swing.JTextField jTextField_CustomerId_Filter;
    private javax.swing.JTextField jTextField_Email_Filter;
    private javax.swing.JTextField jTextField_FirstName_Filter;
    private javax.swing.JTextField jTextField_ID_Filter;
    private javax.swing.JTextField jTextField_InvoiceItem_Barcode_Edit;
    private javax.swing.JTextField jTextField_InvoiceItem_Id_Edit;
    private javax.swing.JTextField jTextField_InvoiceItem_ItemCount_Edit;
    private javax.swing.JTextField jTextField_InvoiceItem_ItemName_Edit;
    private javax.swing.JTextField jTextField_InvoiceItem_Price_Edit;
    private javax.swing.JTextField jTextField_Invoice_PageNo;
    private javax.swing.JTextField jTextField_Invoice_RowCount;
    private javax.swing.JTextField jTextField_Item_Barcode_Filter;
    private javax.swing.JTextField jTextField_Item_Id_Filter;
    private javax.swing.JTextField jTextField_Item_ItemName_Filter;
    private javax.swing.JTextField jTextField_LastName_Filter;
    private javax.swing.JTextField jTextField_PageItem_Item_PageNo;
    private javax.swing.JTextField jTextField_PageItem_Item_RowCount;
    private javax.swing.JTextField jTextField_Phone_Filter;
    private javax.swing.JTextField jTextField_ProductItem_Barcode_Add;
    private javax.swing.JTextField jTextField_ProductItem_Id_Add;
    private javax.swing.JTextField jTextField_ProductItem_ItemName_Add;
    private javax.swing.JTextField jTextField_ProductItem_Price_Add;
    private javax.swing.JTextField jTextField_User_firstName;
    private javax.swing.JTextField jTextField_User_lastName;
    // End of variables declaration//GEN-END:variables


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

    protected void fixInvoiceResult() {

        for (Invoice invoice : invoiceFilterResult) {
            for (InvoiceItem it : invoice.getInvoiceItems()) {
                String s = it.getProductItem().getStringPrice();
                if (s != null) {
                    it.getProductItem().setPrice(new BigDecimal(s));
                }
            }
            List<InvoiceItem> l = invoice.getInvoiceItems();
            invoice.setInvoiceItems(ObservableCollections.observableList(l));
        }

    }

    protected void fixProductItemResult() {

        for (ProductItem item : productItemFilterResult) {
            String s = item.getStringPrice();
            if (s != null) {
                item.setPrice(new BigDecimal(s));
            }
        }

    }

    protected class InvoiceFilterSeachHandler implements TaskListener {

        @Override
        public void taskFinished(Task task) {
            EventQueue.invokeLater(new Runnable() {
                @Override
                public void run() {
                    //It's Swing
                    if (invoiceAsyncFilter.getResult() instanceof Exception) {
                        Exception e = (Exception) invoiceAsyncFilter.getResult();
                        showErrors(e);
                    } else {

                        QueryPage<Invoice> q = (QueryPage<Invoice>) invoiceAsyncFilter.getResult();
                        if (q != null) {
                            invoiceQueryPage = q;
                        }
                        invoiceFilterResult = q.getQueryResult();
                        // 
                        // fix BigDecimal and replace invoiceItems with ObservableList
                        //
                        fixInvoiceResult();
                        initInvoiceTableComponents();
                        initPageNavigator();
                    }
                    enableInvoiceOperations(true);

                }
            });

        }
    }//inner FilterSearchHandler

    protected class InvoiceItemSaveHandler implements TaskListener {

        @Override
        public void taskFinished(Task task) {
            //
            // Its Swing !
            //
            EventQueue.invokeLater(new Runnable() {
                @Override
                public void run() {
                    if (invoiceItemAsyncSave.getResult() instanceof Exception) {
                        Exception e = (Exception) invoiceItemAsyncSave.getResult();
                        errorDetailsHandler.setException(e);
                        showErrors(e);

                    } else {
                        InvoiceItem it = (InvoiceItem) invoiceItemAsyncSave.getResult();
                        jTextField_InvoiceItem_ItemCount_Edit.setText(it.getItemCount() + "");
                    }
                    enableInvoiceOperations(true);
                }
            });

        }
    }//class InvoiceItemSaveHandler

    protected class InvoiceItemDeleteHandler implements TaskListener {

        @Override
        public void taskFinished(Task task) {
            EventQueue.invokeLater(new Runnable() {
                @Override
                public void run() {
                    //this code can work with Swing
                    if (invoiceItemAsyncDelete.getResult() instanceof Exception) {
                        Exception e = (Exception) invoiceItemAsyncDelete.getResult();
                        errorDetailsHandler.setException(e);
                        showErrors(ErrorMessageBuilder.get(e));
                    } else {

                        int itemRow = jTable_InvoiceItem.getSelectedRow();
                        int invoiceRow = jTable_Invoice.getSelectedRow();
                        Invoice invoice = invoiceFilterResult.get(invoiceRow);
                        invoice.getInvoiceItems().remove(itemRow);

                        if (!invoice.getInvoiceItems().isEmpty()) {

                            if (itemRow >= invoice.getInvoiceItems().size()) {
                                itemRow = invoice.getInvoiceItems().size() - 1;
                            }
                            jTable_InvoiceItem.setRowSelectionInterval(itemRow, itemRow);
                        }
                    }
                    enableInvoiceOperations(true);
                }
            });

        }
    }//class InvoiceItemDeleteHandler    

    protected class InvoiceItemInsertHandler implements TaskListener {

        @Override
        public void taskFinished(Task task) {
            EventQueue.invokeLater(new Runnable() {
                @Override
                public void run() {
                    //this code can work with Swing
                    if (invoiceItemAsyncInsert.getResult() instanceof Exception) {
                        Exception e = (Exception) invoiceItemAsyncInsert.getResult();
                        errorDetailsHandler.setException(e);
                        showErrors(e);
                    } else {
                        InvoiceItem item = (InvoiceItem) invoiceItemAsyncInsert.getResult();
                        item.getProductItem().setPrice(new BigDecimal(item.getProductItem().getStringPrice()));
                        Invoice invoice = invoiceFilterResult.get(jTable_Invoice.getSelectedRow());
                        invoice.getInvoiceItems().add(item);
                        int row = invoice.getInvoiceItems().size() - 1;
                        jTable_InvoiceItem.setRowSelectionInterval(row, row);

                    }
                    enableInvoiceOperations(true);
                }
            });

        }
    }//InsertHandler

    protected class ProductItemFilterSeachHandler implements TaskListener {

        @Override
        public void taskFinished(Task task) {
            EventQueue.invokeLater(new Runnable() {
                @Override
                public void run() {
                    //It's Swing
                    if (productItemAsyncFilter.getResult() instanceof Exception) {
                        Exception e = (Exception) productItemAsyncFilter.getResult();
                        showErrors(e);
                    } else {

                        QueryPage<ProductItem> q = (QueryPage<ProductItem>) productItemAsyncFilter.getResult();
                        if (q != null) {
                            productItemQueryPage = q;
                        }
                        productItemFilterResult = q.getQueryResult();
                        fixProductItemResult();
                        initProductItemTableComponents();
                        initProductItemPageNavigator();
                        if (productItemFilterResult.isEmpty()) {
                            //emptyProductItemEditComponents();
                        }
                    }
                    enableProductItemOperations(true);

                }
            });

        }
    }//inner FilterSearchHandler
}
