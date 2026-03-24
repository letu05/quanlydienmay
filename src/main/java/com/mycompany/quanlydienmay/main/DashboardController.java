package com.mycompany.quanlydienmay.main;

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import com.mycompany.quanlydienmay.dao.AccountDAO;
import com.mycompany.quanlydienmay.dao.CategoryDAO;
import com.mycompany.quanlydienmay.dao.OrderDAO;
import com.mycompany.quanlydienmay.dao.ProductDAO;
import com.mycompany.quanlydienmay.model.Account;
import com.mycompany.quanlydienmay.model.Category;
import com.mycompany.quanlydienmay.model.Order;
import com.mycompany.quanlydienmay.model.OrderItem;
import com.mycompany.quanlydienmay.model.Product;
import com.mycompany.quanlydienmay.utils.CartService;
import com.mycompany.quanlydienmay.utils.CartService.CartEntry;
import com.mycompany.quanlydienmay.utils.SessionManager;

import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.OverrunStyle;
import javafx.scene.control.PasswordField;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Separator;
import javafx.scene.control.Spinner;
import javafx.scene.control.SpinnerValueFactory;
import javafx.scene.control.Tab;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.Tooltip;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.XYChart;
import java.util.Map;
import java.util.TreeMap;

public class DashboardController {

    private static final int PAGE_SIZE = 10;
    private static final DateTimeFormatter DATE_TIME_FORMAT = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    @FXML private Label lblTotalProducts;
    @FXML private Label lblTotalCategories;
    @FXML private Label lblTotalOrders;
    @FXML private Label lblWelcome;
    @FXML private Label lblRevenue;
    @FXML private Label lblPendingOrders;
    @FXML private Label lblCompletedOrders;
    @FXML private Label lblLowStockProducts;
    @FXML private Label lblCartBadge;

    @FXML private HBox statsContainer;
    @FXML private HBox kpiStrip;

    @FXML private Tab tabCategory;
    @FXML private Tab tabProduct;
    @FXML private Tab tabOrder;
    @FXML private Tab tabAccount;
    @FXML private Tab tabCatalog;
    @FXML private Tab tabReport;

    @FXML private BarChart<String, Number> revenueChart;

    @FXML private TextField txtCatalogSearch;
    @FXML private VBox categoryFilterBox;
    @FXML private FlowPane productCardPane;
    @FXML private Label lblCatalogInfo;

    @FXML private TableView<Category> categoryTable;

    @FXML
    private TableColumn<Category, Integer> categoryIdCol;

    @FXML
    private TableColumn<Category, String> categoryNameCol;

    @FXML
    private TableView<Product> productTable;

    @FXML
    private TableColumn<Product, Integer> productIdCol;

    @FXML
    private TableColumn<Product, String> productNameCol;

    @FXML
    private TableColumn<Product, String> productCategoryCol;

    @FXML
    private TableColumn<Product, BigDecimal> productPriceCol;

    @FXML
    private TableColumn<Product, Integer> productStockCol;

    @FXML
    private TableView<Order> orderTable;

    @FXML
    private TableColumn<Order, Integer> orderIdCol;

    @FXML
    private TableColumn<Order, String> orderCustomerCol;

    @FXML
    private TableColumn<Order, String> orderPhoneCol;

    @FXML
    private TableColumn<Order, LocalDateTime> orderDateCol;

    @FXML
    private TableColumn<Order, String> orderStatusCol;

    @FXML
    private TableColumn<Order, BigDecimal> orderTotalCol;

    @FXML
    private TextField txtCategorySearch;

    @FXML
    private TextField txtProductSearch;

    @FXML
    private TextField txtOrderSearch;

    @FXML
    private TextField txtAccountSearch;

    @FXML
    private TableView<Account> accountTable;

    @FXML
    private TableColumn<Account, Integer> accountIdCol;

    @FXML
    private TableColumn<Account, String> accountFullNameCol;

    @FXML
    private TableColumn<Account, String> accountUsernameCol;

    @FXML
    private TableColumn<Account, String> accountPhoneCol;

    @FXML
    private TableColumn<Account, String> accountEmailCol;

    @FXML
    private TableColumn<Account, String> accountRoleCol;

    @FXML
    private Label lblCategoryPageInfo;

    @FXML
    private Label lblProductPageInfo;

    @FXML
    private Label lblOrderPageInfo;

    @FXML
    private Label lblAccountPageInfo;

    @FXML
    private Button btnCategoryPrev;

    @FXML
    private Button btnCategoryNext;

    @FXML
    private Button btnProductPrev;

    @FXML
    private Button btnProductNext;

    @FXML
    private Button btnOrderPrev;

    @FXML
    private Button btnOrderNext;

    @FXML
    private Button btnAccountPrev;

    @FXML
    private Button btnAccountNext;

    // Buttons hidden for CUSTOMER role
    @FXML
    private Button btnAddProduct;

    @FXML
    private Button btnEditProduct;

    @FXML
    private Button btnDeleteProduct;

    @FXML
    private Button btnAdvanceOrderStatus;

    private final ObservableList<Category> categories = FXCollections.observableArrayList();
    private final ObservableList<Product>  products   = FXCollections.observableArrayList();
    private final ObservableList<Order>    orders     = FXCollections.observableArrayList();
    private final ObservableList<Account>  accounts   = FXCollections.observableArrayList();

    private final ObservableList<Category> pagedCategories = FXCollections.observableArrayList();
    private final ObservableList<Product> pagedProducts = FXCollections.observableArrayList();
    private final ObservableList<Order> pagedOrders = FXCollections.observableArrayList();
    private final ObservableList<Account> pagedAccounts = FXCollections.observableArrayList();

    private int categoryPage = 0;
    private int productPage = 0;
    private int orderPage = 0;
    private int accountPage = 0;
    private Integer selectedCatalogCategoryId = null;

    private final FilteredList<Category> filteredCategories = new FilteredList<>(categories, item -> true);
    private final FilteredList<Product> filteredProducts = new FilteredList<>(products, item -> true);
    private final FilteredList<Order> filteredOrders = new FilteredList<>(orders, item -> true);
    private final FilteredList<Account> filteredAccounts = new FilteredList<>(accounts, item -> true);

    private final CategoryDAO categoryDAO = new CategoryDAO();
    private final ProductDAO  productDAO  = new ProductDAO();
    private final OrderDAO    orderDAO    = new OrderDAO();
    private final AccountDAO  accountDAO  = new AccountDAO();

    @FXML
    public void initialize() {
        try {
            setupColumns();
            setupTableItems();
            setupSearch();
            applyRolePermissions();
            loadData();
            updateSummary();
        } catch (Exception e) {
            System.err.println("[DASHBOARD] ERROR initialize: " + e.getMessage());
            showError("Lỗi giao diện dashboard", "Có lỗi khi khởi tạo dashboard: " + e.getMessage());
        }
    }

    private void setupTableItems() {
        if (categoryTable != null) {
            categoryTable.setItems(pagedCategories);
        }
        if (productTable != null) {
            productTable.setItems(pagedProducts);
        }
        if (orderTable != null) {
            orderTable.setItems(pagedOrders);
        }
        if (accountTable != null) {
            accountTable.setItems(pagedAccounts);
        }
    }

    // ── Phân quyền ───────────────────────────────────────────
    private void applyRolePermissions() {
        Account currentUser = SessionManager.getCurrentUser();
        String role = currentUser == null ? "GUEST" : currentUser.getRole();
        String fullName = currentUser == null ? "Khách" : currentUser.getFullName();
        lblWelcome.setText("Xin chào, " + fullName + "  (" + role + ")");
        boolean isAdmin = "ADMIN".equalsIgnoreCase(role);

        if (!isAdmin) {
            // Xóa hoàn toàn các tab chỉ dành cho Admin
            if (tabCategory != null && tabCategory.getTabPane() != null) {
                tabCategory.getTabPane().getTabs().remove(tabCategory);
            }
            if (tabProduct != null && tabProduct.getTabPane() != null) {
                tabProduct.getTabPane().getTabs().remove(tabProduct);
            }
            if (tabAccount != null && tabAccount.getTabPane() != null) {
                tabAccount.getTabPane().getTabs().remove(tabAccount);
            }
            if (tabReport != null && tabReport.getTabPane() != null) {
                tabReport.getTabPane().getTabs().remove(tabReport);
            }

            // Ẩn nút "Cập nhật trạng thái" trong tab Đơn hàng
            hideNode(btnAdvanceOrderStatus);
            
            // Ẩn vùng thống kê Dashboard
            hideNode(statsContainer);
            hideNode(kpiStrip);
        }
    }

    /** Ẩn hoàn toàn một node (không hiển thị, không chiếm layout space) */
    private void hideNode(javafx.scene.Node node) {
        if (node != null) {
            node.setVisible(false);
            node.setManaged(false);
        }
    }

    // ── Load từ DB ────────────────────────────────────────────
    private void loadData() {
        try {
            System.out.println("[DASHBOARD] Loading categories...");
            categories.setAll(categoryDAO.findAll());
            System.out.println("[DASHBOARD] Loaded " + categories.size() + " categories");
            
            System.out.println("[DASHBOARD] Loading products...");
            products.setAll(productDAO.findAll());
            System.out.println("[DASHBOARD] Loaded " + products.size() + " products");
            
            System.out.println("[DASHBOARD] Loading orders...");
            List<Order> ords = SessionManager.isAdmin()
                    ? orderDAO.findAll()
                    : orderDAO.findByUserId(SessionManager.getCurrentUser().getId());
            orders.setAll(ords);
            System.out.println("[DASHBOARD] Loaded " + orders.size() + " orders");

            System.out.println("[DASHBOARD] Loading accounts...");
            accounts.setAll(accountDAO.findAll());
            System.out.println("[DASHBOARD] Loaded " + accounts.size() + " accounts");

            categoryPage = 0;
            productPage = 0;
            orderPage = 0;
            accountPage = 0;
            refreshAllPages();
            refreshCatalogCategoryFilters();
            refreshCatalogView();
            System.out.println("[DASHBOARD] Tables refreshed successfully");
            
        } catch (Exception e) {
            System.err.println("[DASHBOARD] ERROR loading data: " + e.getMessage());
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Lỗi");
            alert.setHeaderText("Không thể tải dữ liệu từ cơ sở dữ liệu");
            alert.setContentText("Chi tiết: " + e.getMessage() + "\n\nVui lòng kiểm tra MySQL server có chạy không.");
            alert.showAndWait();
        }
    }

    private void setupSearch() {
        if (txtCategorySearch != null) {
            txtCategorySearch.textProperty().addListener((obs, oldValue, keyword) -> {
                String text = normalize(keyword);
                filteredCategories.setPredicate(c -> text.isEmpty()
                        || String.valueOf(c.getId()).contains(text)
                        || normalize(c.getName()).contains(text));
                categoryPage = 0;
                refreshCategoryPage();
            });
        }

        if (txtProductSearch != null) {
            txtProductSearch.textProperty().addListener((obs, oldValue, keyword) -> {
                String text = normalize(keyword);
                filteredProducts.setPredicate(p -> {
                    if (text.isEmpty()) {
                        return true;
                    }
                    String categoryName = p.getCategory() == null ? "" : p.getCategory().getName();
                    return String.valueOf(p.getId()).contains(text)
                            || normalize(p.getName()).contains(text)
                            || normalize(categoryName).contains(text);
                });
                productPage = 0;
                refreshProductPage();
            });
        }

        if (txtCatalogSearch != null) {
            txtCatalogSearch.textProperty().addListener((obs, oldValue, keyword) -> refreshCatalogView());
        }

        if (txtOrderSearch != null) {
            txtOrderSearch.textProperty().addListener((obs, oldValue, keyword) -> {
                String text = normalize(keyword);
                filteredOrders.setPredicate(o -> text.isEmpty()
                        || String.valueOf(o.getId()).contains(text)
                        || normalize(o.getCustomerName()).contains(text)
                        || normalize(o.getCustomerPhone()).contains(text)
                        || normalize(o.getStatus()).contains(text));
                orderPage = 0;
                refreshOrderPage();
            });
        }

        if (txtAccountSearch != null) {
            txtAccountSearch.textProperty().addListener((obs, oldValue, keyword) -> {
                String text = normalize(keyword);
                filteredAccounts.setPredicate(a -> text.isEmpty()
                        || String.valueOf(a.getId()).contains(text)
                        || normalize(a.getFullName()).contains(text)
                        || normalize(a.getUsername()).contains(text)
                        || normalize(a.getEmail()).contains(text)
                        || normalize(a.getPhone()).contains(text)
                        || normalize(a.getRole()).contains(text));
                accountPage = 0;
                refreshAccountPage();
            });
        }
    }

    private String normalize(String value) {
        return value == null ? "" : value.trim().toLowerCase();
    }

    @FXML
    private void handleRefresh() {
        loadData();
        updateSummary();
    }

    private void refreshAllPages() {
        refreshCategoryPage();
        refreshProductPage();
        refreshOrderPage();
        refreshAccountPage();
        refreshCatalogView();
    }

    private void refreshCatalogCategoryFilters() {
        if (categoryFilterBox == null) {
            return;
        }

        categoryFilterBox.getChildren().clear();

        Button btnAll = createCategoryChip("Tất cả", null);
        categoryFilterBox.getChildren().add(btnAll);

        for (Category category : categories) {
            categoryFilterBox.getChildren().add(createCategoryChip(category.getName(), category.getId()));
        }
    }

    private Button createCategoryChip(String text, Integer categoryId) {
        Button chip = new Button(text);
        chip.getStyleClass().add("sidebar-filter-btn");
        chip.setMaxWidth(Double.MAX_VALUE); // Cho phép nút dãn đầy chiều ngang sidebar
        
        if ((selectedCatalogCategoryId == null && categoryId == null)
                || (selectedCatalogCategoryId != null && selectedCatalogCategoryId.equals(categoryId))) {
            chip.setStyle("-fx-background-color: #f1f5f9; -fx-text-fill: #ee4d2d; -fx-font-weight: 700;");
        }

        chip.setOnAction(event -> {
            selectedCatalogCategoryId = categoryId;
            refreshCatalogCategoryFilters();
            refreshCatalogView();
        });

        return chip;
    }

    private void refreshCatalogView() {
        if (productCardPane == null) {
            return;
        }

        String keyword = normalize(txtCatalogSearch == null ? "" : txtCatalogSearch.getText());

        List<Product> filtered = products.stream()
                .filter(product -> {
                    if (selectedCatalogCategoryId == null) {
                        return true;
                    }
                    Category category = product.getCategory();
                    return category != null && category.getId() == selectedCatalogCategoryId;
                })
                .filter(product -> keyword.isEmpty()
                        || normalize(product.getName()).contains(keyword)
                        || normalize(product.getDescription()).contains(keyword)
                        || normalize(product.getCategory() == null ? "" : product.getCategory().getName()).contains(keyword))
                .collect(Collectors.toList());

        productCardPane.getChildren().clear();
        for (Product product : filtered) {
            productCardPane.getChildren().add(createProductCard(product));
        }

        if (lblCatalogInfo != null) {
            String categoryLabel = selectedCatalogCategoryId == null
                    ? "Tất cả danh mục"
                    : categories.stream()
                            .filter(c -> c.getId() == selectedCatalogCategoryId)
                            .map(Category::getName)
                            .findFirst()
                            .orElse("Danh mục");
            lblCatalogInfo.setText("Đang hiển thị " + filtered.size() + " sản phẩm • " + categoryLabel);
        }
    }

    private VBox createProductCard(Product product) {
        VBox card = new VBox(6);
        card.getStyleClass().add("product-card-web");
        card.setPrefWidth(180);
        card.setMinWidth(180);
        card.setMaxWidth(180);

        // Ảnh vuông
        StackPane imageContainer = new StackPane();
        imageContainer.setPrefHeight(180);
        imageContainer.setMinHeight(180);
        imageContainer.setStyle("-fx-background-color: #f8fafc; -fx-background-radius: 8 8 0 0;");

        ImageView imageView = createProductImageView(product.getImagePath());
        if (imageView != null) {
            imageView.setFitWidth(160);
            imageView.setFitHeight(160);
            imageContainer.getChildren().add(imageView);
        } else {
            Label noImage = new Label("No Image");
            noImage.setStyle("-fx-text-fill: #94a3b8;");
            imageContainer.getChildren().add(noImage);
        }

        // Thông tin dưới ảnh
        VBox infoBox = new VBox(4);
        infoBox.setPadding(new Insets(8));
        
        Label name = new Label(safe(product.getName()));
        name.getStyleClass().add("product-card-web-name");
        name.setWrapText(true);
        name.setPrefHeight(40); // Cố định chiều cao 2 dòng
        name.setAlignment(Pos.TOP_LEFT);
        Tooltip.install(name, new Tooltip(safe(product.getName())));

        Label price = new Label(formatMoney(product.getPrice()));
        price.getStyleClass().add("product-card-web-price");

        HBox statsBox = new HBox();
        statsBox.setAlignment(Pos.CENTER_LEFT);
        Label sold = new Label("Đã bán " + (int)(Math.random() * 500 + 10)); // Giả lập đã bán
        sold.getStyleClass().add("product-card-web-sold");
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        Label stock = new Label("Kho: " + product.getStock());
        stock.getStyleClass().add("product-card-web-sold");
        statsBox.getChildren().addAll(sold, spacer, stock);

        Button btnAddCart = new Button("🛒 Thêm vào giỏ");
        btnAddCart.setMaxWidth(Double.MAX_VALUE);
        btnAddCart.getStyleClass().add("btn-ecommerce-cart");
        btnAddCart.setOnAction(event -> addToCart(product, 1));
        
        infoBox.getChildren().addAll(name, price, statsBox, btnAddCart);
        
        card.getChildren().addAll(imageContainer, infoBox);
        
        // Mở dialog chi tiết khi click vào thẻ (trừ khi click nút Add to cart)
        card.setOnMouseClicked(e -> {
            if (e.getTarget() != btnAddCart && !(e.getTarget() instanceof javafx.scene.text.Text && ((javafx.scene.text.Text)e.getTarget()).getParent() == btnAddCart)) {
                showProductDetailDialog(product);
            }
        });

        return card;
    }

    // ═══════════════════════════════════════════════════════
    // GIỎ HÀNG
    // ═══════════════════════════════════════════════════════
    private final CartService cart = CartService.getInstance();

    /** Cập nhật badge số lượng trên nút giỏ hàng */
    private void updateCartBadge() {
        if (lblCartBadge == null) return;
        int total = cart.totalQuantity();
        if (total > 0) {
            lblCartBadge.setText(String.valueOf(total));
            lblCartBadge.setVisible(true);
            lblCartBadge.setManaged(true);
        } else {
            lblCartBadge.setVisible(false);
            lblCartBadge.setManaged(false);
        }
    }

    /** Thêm sản phẩm vào giỏ và cập nhật badge */
    private void addToCart(Product product, int qty) {
        cart.addProduct(product, qty);
        updateCartBadge();
        showInfo("✅ Đã thêm vào giỏ!",
                product.getName() + " (x" + qty + ") đã được thêm vào giỏ hàng.");
    }

    /** Nút giỏ hàng trong header */
    @FXML
    private void handleCartClick() {
        showCartDialog();
    }

    // ── Shopee-style Cart Stage ─────────────────────────────
    private Stage cartStage;

    /** Mở cửa sổ giỏ hàng kiểu Shopee */
    private void showCartDialog() {
        if (cartStage != null && cartStage.isShowing()) {
            cartStage.toFront();
            return;
        }

        cartStage = new Stage();
        cartStage.setTitle("Giỏ hàng");
        cartStage.setMinWidth(860);
        cartStage.setMinHeight(560);
        cartStage.setWidth(960);
        cartStage.setHeight(680);

        // ── ROOT LAYOUT ──
        BorderPane root = new BorderPane();
        root.setStyle("-fx-background-color: #f5f5f5;");

        // ── HEADER ORANGE (Shopee style) ──
        HBox header = new HBox(16);
        header.setAlignment(Pos.CENTER_LEFT);
        header.setPadding(new Insets(0, 24, 0, 24));
        header.setPrefHeight(60);
        header.setStyle("-fx-background-color: linear-gradient(from 0% 0% to 100% 0%, #ee4d2d, #f97316);"
                + "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.15), 8, 0.1, 0, 3);");

        Label cartIcon = new Label("🛒");
        cartIcon.setStyle("-fx-font-size: 22; -fx-text-fill: white;");
        Label cartTitle = new Label("Giỏ hàng");
        cartTitle.setStyle("-fx-font-size: 22; -fx-font-weight: 800; -fx-text-fill: white;");
        Label cartCount = new Label("(" + cart.totalQuantity() + " sản phẩm)");
        cartCount.setStyle("-fx-font-size: 15; -fx-text-fill: rgba(255,255,255,0.85);");
        header.getChildren().addAll(cartIcon, cartTitle, cartCount);
        root.setTop(header);

        // ── CENTER: product list ──
        VBox itemList = new VBox(10);
        itemList.setPadding(new Insets(16));
        itemList.setStyle("-fx-background-color: transparent;");

        // Live-updating total label (shared reference)
        Label liveTotalLabel = new Label(formatMoney(cart.totalAmount()));
        liveTotalLabel.setStyle("-fx-font-size: 26; -fx-font-weight: 800; -fx-text-fill: #ee4d2d;");

        if (cart.isEmpty()) {
            VBox emptyBox = new VBox(16);
            emptyBox.setAlignment(Pos.CENTER);
            emptyBox.setPadding(new Insets(80));
            Label emptyIcon = new Label("🛒");
            emptyIcon.setStyle("-fx-font-size: 64; -fx-text-fill: #d1d5db;");
            Label emptyTxt = new Label("Giỏ hàng của bạn đang trống");
            emptyTxt.setStyle("-fx-font-size: 18; -fx-text-fill: #9ca3af; -fx-font-weight: 600;");
            Label emptyHint = new Label("Hãy thêm sản phẩm vào giỏ để tiến hành mua hàng");
            emptyHint.setStyle("-fx-font-size: 14; -fx-text-fill: #d1d5db;");
            emptyBox.getChildren().addAll(emptyIcon, emptyTxt, emptyHint);
            itemList.getChildren().add(emptyBox);
        } else {
            // Column header
            HBox colHeader = new HBox();
            colHeader.setPadding(new Insets(8, 16, 8, 16));
            colHeader.setStyle("-fx-background-color: #fff8f6; -fx-border-color: transparent transparent #f0d0c0 transparent; -fx-border-width: 0 0 1 0;");
            Label colProduct = new Label("Sản phẩm");
            colProduct.setStyle("-fx-font-size: 14; -fx-font-weight: 700; -fx-text-fill: #6b7280;");
            HBox.setHgrow(colProduct, Priority.ALWAYS);
            Label colPrice = new Label("Đơn giá");
            colPrice.setStyle("-fx-font-size: 14; -fx-font-weight: 700; -fx-text-fill: #6b7280; -fx-pref-width: 150; -fx-alignment: CENTER;");
            Label colQty = new Label("Số lượng");
            colQty.setStyle("-fx-font-size: 14; -fx-font-weight: 700; -fx-text-fill: #6b7280; -fx-pref-width: 130; -fx-alignment: CENTER;");
            Label colSubtotal = new Label("Thành tiền");
            colSubtotal.setStyle("-fx-font-size: 14; -fx-font-weight: 700; -fx-text-fill: #6b7280; -fx-pref-width: 140; -fx-alignment: CENTER_RIGHT;");
            Label colAction = new Label("");
            colAction.setStyle("-fx-pref-width: 60;");
            colHeader.getChildren().addAll(colProduct, colPrice, colQty, colSubtotal, colAction);
            itemList.getChildren().add(colHeader);

            for (CartEntry entry : cart.getItems()) {
                HBox row = buildShopeeCartRow(entry, liveTotalLabel);
                itemList.getChildren().add(row);
            }
        }

        ScrollPane scrollPane = new ScrollPane(itemList);
        scrollPane.setFitToWidth(true);
        scrollPane.setStyle("-fx-background: #f5f5f5; -fx-background-color: #f5f5f5; -fx-border-color: transparent;");
        root.setCenter(scrollPane);

        // ── FOOTER: Tổng tiền + Đặt hàng ──
        VBox footer = new VBox();
        footer.setStyle("-fx-background-color: white;"
                + "-fx-border-color: #e5e7eb transparent transparent transparent;"
                + "-fx-border-width: 1 0 0 0;"
                + "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.08), 8, 0.05, 0, -3);");

        HBox footerInner = new HBox(20);
        footerInner.setAlignment(Pos.CENTER_RIGHT);
        footerInner.setPadding(new Insets(16, 24, 20, 24));

        VBox totalBox = new VBox(4);
        totalBox.setAlignment(Pos.CENTER_RIGHT);
        HBox totalRow = new HBox(8);
        totalRow.setAlignment(Pos.CENTER_RIGHT);
        Label totalLabel = new Label("Tổng thanh toán:");
        totalLabel.setStyle("-fx-font-size: 16; -fx-font-weight: 600; -fx-text-fill: #374151;");
        totalRow.getChildren().addAll(totalLabel, liveTotalLabel);
        Label itemCountLabel = new Label(cart.totalQuantity() + " sản phẩm");
        itemCountLabel.setStyle("-fx-font-size: 13; -fx-text-fill: #9ca3af;");
        totalBox.getChildren().addAll(totalRow, itemCountLabel);

        Button btnCheckout = new Button("Mua hàng (" + cart.totalQuantity() + ")");
        btnCheckout.setPrefHeight(50);
        btnCheckout.setPrefWidth(200);
        btnCheckout.setStyle("-fx-background-color: #ee4d2d; -fx-text-fill: white; -fx-font-size: 17;"
                + "-fx-font-weight: 800; -fx-background-radius: 4; -fx-cursor: hand;"
                + "-fx-effect: dropshadow(gaussian, rgba(238,77,45,0.4), 10, 0.15, 0, 3);");
        btnCheckout.setOnMouseEntered(e -> btnCheckout.setStyle("-fx-background-color: #d73211; -fx-text-fill: white; -fx-font-size: 17;"
                + "-fx-font-weight: 800; -fx-background-radius: 4; -fx-cursor: hand;"
                + "-fx-effect: dropshadow(gaussian, rgba(238,77,45,0.6), 14, 0.2, 0, 4);"));
        btnCheckout.setOnMouseExited(e -> btnCheckout.setStyle("-fx-background-color: #ee4d2d; -fx-text-fill: white; -fx-font-size: 17;"
                + "-fx-font-weight: 800; -fx-background-radius: 4; -fx-cursor: hand;"
                + "-fx-effect: dropshadow(gaussian, rgba(238,77,45,0.4), 10, 0.15, 0, 3);"));
        btnCheckout.setDisable(cart.isEmpty());
        btnCheckout.setOnAction(e -> {
            cartStage.close();
            showCheckoutDialog();
        });

        footerInner.getChildren().addAll(totalBox, btnCheckout);
        footer.getChildren().add(footerInner);
        root.setBottom(footer);

        Scene scene = new Scene(root);
        cartStage.setScene(scene);
        cartStage.show();
    }

    /** Một hàng sản phẩm trong giỏ hàng kiểu Shopee */
    private HBox buildShopeeCartRow(CartEntry entry, Label liveTotalLabel) {
        HBox row = new HBox(0);
        row.setAlignment(Pos.CENTER_LEFT);
        row.setPadding(new Insets(14, 16, 14, 16));
        row.setStyle("-fx-background-color: white; -fx-background-radius: 6;"
                + "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.05), 6, 0.05, 0, 2);");

        // Ảnh sản phẩm
        StackPane thumb = new StackPane();
        thumb.setPrefSize(90, 90);
        thumb.setMinSize(90, 90);
        thumb.setMaxSize(90, 90);
        thumb.setStyle("-fx-background-color: #f9fafb; -fx-background-radius: 6;"
                + "-fx-border-color: #f0f0f0; -fx-border-radius: 6; -fx-border-width: 1;");
        ImageView iv = createProductImageView(entry.product.getImagePath());
        if (iv != null) {
            iv.setFitWidth(84); iv.setFitHeight(84);
            iv.setPreserveRatio(true);
            thumb.getChildren().add(iv);
        } else {
            Label noImg = new Label("📦");
            noImg.setStyle("-fx-font-size: 32; -fx-text-fill: #d1d5db;");
            thumb.getChildren().add(noImg);
        }

        // Tên sản phẩm
        VBox nameBox = new VBox(6);
        nameBox.setPadding(new Insets(0, 0, 0, 14));
        HBox.setHgrow(nameBox, Priority.ALWAYS);
        Label name = new Label(safe(entry.product.getName()));
        name.setStyle("-fx-font-size: 16; -fx-font-weight: 700; -fx-text-fill: #111827; -fx-wrap-text: true;");
        name.setWrapText(true);
        name.setMaxWidth(270);
        String catName = entry.product.getCategory() != null ? entry.product.getCategory().getName() : "";
        Label category = new Label(catName);
        category.setStyle("-fx-font-size: 13; -fx-text-fill: #9ca3af;");
        nameBox.getChildren().addAll(name, category);

        // Đơn giá
        Label price = new Label(formatMoney(entry.product.getPrice()));
        price.setStyle("-fx-font-size: 17; -fx-font-weight: 800; -fx-text-fill: #ee4d2d; -fx-pref-width: 150; -fx-alignment: CENTER;");

        // Số lượng với nút +/-
        HBox qtyBox = new HBox(0);
        qtyBox.setAlignment(Pos.CENTER);
        qtyBox.setPrefWidth(130);

        Label qtyLabel = new Label(String.valueOf(entry.quantity));
        qtyLabel.setStyle("-fx-font-size: 16; -fx-font-weight: 700; -fx-text-fill: #111827;"
                + "-fx-min-width: 48; -fx-alignment: CENTER;");
        qtyLabel.setAlignment(Pos.CENTER);

        // Subtotal label (được tham chiếu trong listener)
        Label subtotal = new Label(formatMoney(entry.lineTotal()));
        subtotal.setStyle("-fx-font-size: 17; -fx-font-weight: 800; -fx-text-fill: #ee4d2d;"
                + "-fx-pref-width: 140; -fx-alignment: CENTER_RIGHT;");

        Button btnMinus = new Button("−");
        btnMinus.setPrefSize(32, 32);
        btnMinus.setStyle("-fx-background-color: white; -fx-text-fill: #374151; -fx-font-size: 18; -fx-font-weight: 700;"
                + "-fx-border-color: #d1d5db; -fx-border-width: 1; -fx-border-radius: 4 0 0 4;"
                + "-fx-background-radius: 4 0 0 4; -fx-cursor: hand; -fx-padding: 0;");
        Button btnPlus = new Button("+");
        btnPlus.setPrefSize(32, 32);
        btnPlus.setStyle("-fx-background-color: white; -fx-text-fill: #374151; -fx-font-size: 18; -fx-font-weight: 700;"
                + "-fx-border-color: #d1d5db; -fx-border-width: 1; -fx-border-radius: 0 4 4 0;"
                + "-fx-background-radius: 0 4 4 0; -fx-cursor: hand; -fx-padding: 0;");

        // Quantity display box
        HBox qtyDisplay = new HBox();
        qtyDisplay.setAlignment(Pos.CENTER);
        qtyDisplay.setStyle("-fx-border-color: #d1d5db; -fx-border-width: 1 0 1 0;");
        qtyLabel.setMinWidth(44);
        qtyLabel.setPrefWidth(44);
        qtyDisplay.getChildren().add(qtyLabel);

        qtyBox.getChildren().addAll(btnMinus, qtyDisplay, btnPlus);

        // Logic +/-
        int maxQty = Math.max(1, entry.product.getStock());
        btnMinus.setDisable(entry.quantity <= 1);
        btnPlus.setDisable(entry.quantity >= maxQty);

        btnMinus.setOnAction(ev -> {
            int cur = Integer.parseInt(qtyLabel.getText());
            if (cur <= 1) return;
            int nv = cur - 1;
            qtyLabel.setText(String.valueOf(nv));
            cart.updateQuantity(entry.product.getId(), nv);
            BigDecimal ns = entry.product.getPrice().multiply(BigDecimal.valueOf(nv));
            subtotal.setText(formatMoney(ns));
            liveTotalLabel.setText(formatMoney(cart.totalAmount()));
            updateCartBadge();
            btnMinus.setDisable(nv <= 1);
            btnPlus.setDisable(nv >= maxQty);
        });
        btnPlus.setOnAction(ev -> {
            int cur = Integer.parseInt(qtyLabel.getText());
            if (cur >= maxQty) return;
            int nv = cur + 1;
            qtyLabel.setText(String.valueOf(nv));
            cart.updateQuantity(entry.product.getId(), nv);
            BigDecimal ns = entry.product.getPrice().multiply(BigDecimal.valueOf(nv));
            subtotal.setText(formatMoney(ns));
            liveTotalLabel.setText(formatMoney(cart.totalAmount()));
            updateCartBadge();
            btnMinus.setDisable(nv <= 1);
            btnPlus.setDisable(nv >= maxQty);
        });

        // Nút xóa
        Button btnRemove = new Button("🗑");
        btnRemove.setPrefWidth(60);
        btnRemove.setStyle("-fx-background-color: transparent; -fx-text-fill: #9ca3af; -fx-font-size: 20;"
                + "-fx-cursor: hand; -fx-border-color: transparent;");
        btnRemove.setOnMouseEntered(e -> btnRemove.setStyle("-fx-background-color: #fee2e2; -fx-text-fill: #ee4d2d;"
                + "-fx-font-size: 20; -fx-cursor: hand; -fx-background-radius: 6; -fx-border-color: transparent;"));
        btnRemove.setOnMouseExited(e -> btnRemove.setStyle("-fx-background-color: transparent; -fx-text-fill: #9ca3af;"
                + "-fx-font-size: 20; -fx-cursor: hand; -fx-border-color: transparent;"));
        btnRemove.setOnAction(ev -> {
            cart.removeProduct(entry.product.getId());
            updateCartBadge();
            if (cartStage != null) cartStage.close();
            showCartDialog();
        });

        row.getChildren().addAll(thumb, nameBox, price, qtyBox, subtotal, btnRemove);
        return row;
    }

    /** Stage Checkout - nhập thông tin giao hàng */
    private void showCheckoutDialog() {
        Account currentUser = SessionManager.getCurrentUser();

        Stage checkoutStage = new Stage();
        checkoutStage.setTitle("Xác nhận đặt hàng");
        checkoutStage.setWidth(640);
        checkoutStage.setMinWidth(580);
        checkoutStage.setMinHeight(520);

        BorderPane root = new BorderPane();
        root.setStyle("-fx-background-color: #f5f5f5;");

        // ── Header ──
        HBox header = new HBox(12);
        header.setAlignment(Pos.CENTER_LEFT);
        header.setPadding(new Insets(0, 24, 0, 24));
        header.setPrefHeight(56);
        header.setStyle("-fx-background-color: linear-gradient(from 0% 0% to 100% 0%, #ee4d2d, #f97316);");
        Label hTitle = new Label("🚀 Thông tin đặt hàng");
        hTitle.setStyle("-fx-font-size: 20; -fx-font-weight: 800; -fx-text-fill: white;");
        header.getChildren().add(hTitle);
        root.setTop(header);

        // ── Form ──
        VBox form = new VBox(14);
        form.setPadding(new Insets(24));
        form.setStyle("-fx-background-color: white; -fx-background-radius: 8;");

        String fieldStyle = "-fx-font-size: 15; -fx-padding: 12 14; -fx-background-color: #f9fafb;"
                + "-fx-border-color: #d1d5db; -fx-border-width: 1; -fx-border-radius: 6;"
                + "-fx-background-radius: 6; -fx-text-fill: #111827;";
        String labelStyle = "-fx-font-size: 14; -fx-font-weight: 700; -fx-text-fill: #374151;";

        TextField tfName    = new TextField(currentUser != null ? safe(currentUser.getFullName()) : "");
        TextField tfPhone   = new TextField(currentUser != null ? safe(currentUser.getPhone()) : "");
        TextField tfAddress = new TextField();
        TextField tfEmail   = new TextField(currentUser != null ? safe(currentUser.getEmail()) : "");
        TextField tfNote    = new TextField();

        tfName.setPromptText("Họ và tên người nhận *");
        tfPhone.setPromptText("Số điện thoại liên lạc *");
        tfAddress.setPromptText("Địa chỉ giao hàng chi tiết *");
        tfEmail.setPromptText("Email (tùy chọn)");
        tfNote.setPromptText("Ghi chú cho đơn hàng (tùy chọn)");
        for (TextField tf : new TextField[]{tfName, tfPhone, tfAddress, tfEmail, tfNote}) {
            tf.setStyle(fieldStyle);
        }

        form.getChildren().addAll(
            buildFormRow("Họ và tên", tfName, labelStyle),
            buildFormRow("Số điện thoại", tfPhone, labelStyle),
            buildFormRow("Địa chỉ giao hàng", tfAddress, labelStyle),
            buildFormRow("Email", tfEmail, labelStyle),
            buildFormRow("Ghi chú", tfNote, labelStyle)
        );

        ScrollPane formScroll = new ScrollPane(form);
        formScroll.setFitToWidth(true);
        formScroll.setStyle("-fx-background: #f5f5f5; -fx-background-color: #f5f5f5; -fx-border-color: transparent;");
        formScroll.setPadding(new Insets(16));
        root.setCenter(formScroll);

        // ── Footer ──
        VBox footer = new VBox();
        footer.setStyle("-fx-background-color: white; -fx-border-color: #e5e7eb transparent transparent transparent;"
                + "-fx-border-width: 1 0 0 0;");
        HBox footerInner = new HBox(16);
        footerInner.setAlignment(Pos.CENTER_RIGHT);
        footerInner.setPadding(new Insets(16, 24, 20, 24));

        VBox totalBox = new VBox(4);
        totalBox.setAlignment(Pos.CENTER_RIGHT);
        Label totalHint = new Label("Tổng thanh toán:");
        totalHint.setStyle("-fx-font-size: 14; -fx-text-fill: #6b7280;");
        Label totalAmt = new Label(formatMoney(cart.totalAmount()));
        totalAmt.setStyle("-fx-font-size: 24; -fx-font-weight: 800; -fx-text-fill: #ee4d2d;");
        totalBox.getChildren().addAll(totalHint, totalAmt);

        Button btnBack = new Button("← Quay lại");
        btnBack.setPrefHeight(48);
        btnBack.setStyle("-fx-font-size: 15; -fx-font-weight: 700; -fx-background-color: white;"
                + "-fx-text-fill: #374151; -fx-border-color: #d1d5db; -fx-border-width: 1;"
                + "-fx-border-radius: 4; -fx-background-radius: 4; -fx-padding: 0 20; -fx-cursor: hand;");
        btnBack.setOnAction(ev -> {
            checkoutStage.close();
            showCartDialog();
        });

        Button btnConfirm = new Button("Đặt hàng");
        btnConfirm.setPrefHeight(48);
        btnConfirm.setPrefWidth(160);
        btnConfirm.setStyle("-fx-font-size: 17; -fx-font-weight: 800; -fx-background-color: #ee4d2d;"
                + "-fx-text-fill: white; -fx-border-radius: 4; -fx-background-radius: 4;"
                + "-fx-cursor: hand; -fx-effect: dropshadow(gaussian, rgba(238,77,45,0.4), 10, 0.15, 0, 3);");
        btnConfirm.setOnMouseEntered(e -> btnConfirm.setStyle("-fx-font-size: 17; -fx-font-weight: 800; -fx-background-color: #d73211;"
                + "-fx-text-fill: white; -fx-border-radius: 4; -fx-background-radius: 4; -fx-cursor: hand;"));
        btnConfirm.setOnMouseExited(e -> btnConfirm.setStyle("-fx-font-size: 17; -fx-font-weight: 800; -fx-background-color: #ee4d2d;"
                + "-fx-text-fill: white; -fx-border-radius: 4; -fx-background-radius: 4; -fx-cursor: hand;"
                + "-fx-effect: dropshadow(gaussian, rgba(238,77,45,0.4), 10, 0.15, 0, 3);"));
        btnConfirm.setOnAction(ev -> {
            String nameVal    = tfName.getText().trim();
            String phoneVal   = tfPhone.getText().trim();
            String addressVal = tfAddress.getText().trim();
            if (nameVal.isEmpty() || phoneVal.isEmpty() || addressVal.isEmpty()) {
                tfName.setStyle(fieldStyle + (nameVal.isEmpty() ? "-fx-border-color: #ee4d2d;" : ""));
                tfPhone.setStyle(fieldStyle + (phoneVal.isEmpty() ? "-fx-border-color: #ee4d2d;" : ""));
                tfAddress.setStyle(fieldStyle + (addressVal.isEmpty() ? "-fx-border-color: #ee4d2d;" : ""));
                return;
            }
            checkoutStage.close();
            placeOrder(nameVal, phoneVal, addressVal,
                    tfEmail.getText().trim(), tfNote.getText().trim());
        });

        footerInner.getChildren().addAll(totalBox, btnBack, btnConfirm);
        footer.getChildren().add(footerInner);
        root.setBottom(footer);

        Scene scene = new Scene(root);
        checkoutStage.setScene(scene);
        checkoutStage.show();
    }

    /** Helper: một dòng form label + input */
    private VBox buildFormRow(String labelText, TextField field, String labelStyle) {
        VBox row = new VBox(6);
        Label lbl = new Label(labelText);
        lbl.setStyle(labelStyle);
        field.setMaxWidth(Double.MAX_VALUE);
        row.getChildren().addAll(lbl, field);
        return row;
    }

    /** Lưu đơn hàng vào DB */
    private void placeOrder(String name, String phone, String address, String email, String note) {
        try {
            Order order = new Order();
            order.setUser(SessionManager.getCurrentUser());
            order.setCustomerName(name);
            order.setCustomerPhone(phone);
            order.setCustomerAddress(address);
            order.setEmail(email.isEmpty() ? "" : email);
            order.setNote(note);
            order.setStatus("PENDING");
            order.setPaymentMethod("CASH");

            List<OrderItem> items = new ArrayList<>();
            for (CartEntry entry : cart.getItems()) {
                OrderItem item = new OrderItem();
                item.setOrder(order);
                item.setProduct(entry.product);
                item.setQuantity(entry.quantity);
                item.setUnitPrice(entry.product.getPrice());
                items.add(item);
            }
            order.setItems(items);
            orderDAO.save(order);

            cart.clear();
            updateCartBadge();
            handleRefresh();
            showInfo("🎉 Đặt hàng thành công!",
                    "Đơn hàng của bạn đã được ghi nhận.\n"
                  + "Trạng thái hiện tại: PENDING\n"
                  + "Chúng tôi sẽ liên hệ sớm nhất!");
        } catch (Exception e) {
            showError("Lỗi đặt hàng", e.getMessage());
        }
    }

    // ═══════════════════════════════════════════════════════
    // PRODUCT IMAGE UTIL
    // ═══════════════════════════════════════════════════════
    private ImageView createProductImageView(String imagePath) {
        String path = safe(imagePath).trim();
        if (path.isEmpty()) {
            return null;
        }

        try {
            String normalized = path.replace("\\", "/");
            if (!normalized.startsWith("/")) {
                normalized = "/" + normalized;
            }

            Image image;
            var resource = getClass().getResource(normalized);
            if (resource != null) {
                image = new Image(resource.toExternalForm(), 180, 120, true, true);
            } else {
                File file = new File(path);
                if (!file.exists()) {
                    return null;
                }
                image = new Image(file.toURI().toString(), 180, 120, true, true);
            }

            if (image.isError()) {
                return null;
            }

            ImageView imageView = new ImageView(image);
            imageView.setFitWidth(180);
            imageView.setFitHeight(120);
            imageView.setPreserveRatio(true);
            imageView.setSmooth(true);
            return imageView;
        } catch (Exception e) {
            return null;
        }
    }

    private void showProductDetailDialog(Product product) {
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle("Chi tiết sản phẩm");
        dialog.setHeaderText(null);
        dialog.getDialogPane().getButtonTypes().add(ButtonType.CLOSE);
        dialog.getDialogPane().setPrefWidth(700);

        HBox root = new HBox(24);
        root.setPadding(new Insets(24));
        root.setStyle("-fx-background-color: white;");

        // LEFT: Image
        StackPane imageContainer = new StackPane();
        imageContainer.setStyle("-fx-border-color: #e2e8f0; -fx-border-radius: 8; -fx-background-radius: 8; -fx-padding: 8; -fx-background-color: #f8fafc;");
        imageContainer.setPrefSize(300, 300);
        imageContainer.setMinSize(300, 300);
        
        ImageView imageView = createProductImageView(product.getImagePath());
        if (imageView != null) {
            imageView.setFitWidth(280);
            imageView.setFitHeight(280);
            imageView.setPreserveRatio(true);
            imageContainer.getChildren().add(imageView);
        } else {
            Label noImage = new Label("📦");
            noImage.setStyle("-fx-font-size: 64; -fx-text-fill: #cbd5e1;");
            imageContainer.getChildren().add(noImage);
        }

        // RIGHT: Info
        VBox infoBox = new VBox(16);
        infoBox.setAlignment(Pos.TOP_LEFT);
        HBox.setHgrow(infoBox, Priority.ALWAYS);

        Label name = new Label(safe(product.getName()));
        name.setWrapText(true);
        name.setStyle("-fx-font-size: 22px; -fx-font-weight: bold; -fx-text-fill: #1e293b;");

        HBox priceBox = new HBox(10);
        priceBox.setAlignment(Pos.CENTER_LEFT);
        priceBox.setStyle("-fx-background-color: #f8faff; -fx-padding: 16; -fx-background-radius: 8;");
        Label price = new Label(formatMoney(product.getPrice()));
        price.setStyle("-fx-font-size: 28px; -fx-font-weight: bold; -fx-text-fill: #ee4d2d;");
        priceBox.getChildren().add(price);

        GridPane metaGrid = new GridPane();
        metaGrid.setHgap(20);
        metaGrid.setVgap(12);
        
        Label lblCategoryTag = new Label("Danh mục");
        lblCategoryTag.setStyle("-fx-text-fill: #64748b;");
        Label valCategory = new Label(product.getCategory() == null ? "Chưa phân loại" : safe(product.getCategory().getName()));
        valCategory.setStyle("-fx-font-weight: bold; -fx-text-fill: #334155;");

        Label lblStockTag = new Label("Tình trạng");
        lblStockTag.setStyle("-fx-text-fill: #64748b;");
        Label valStock = new Label(product.getStock() > 0 ? "Còn hàng (" + product.getStock() + ")" : "Hết hàng");
        valStock.setStyle("-fx-font-weight: bold; -fx-text-fill: " + (product.getStock() > 0 ? "#16a34a;" : "#dc2626;"));

        metaGrid.addRow(0, lblCategoryTag, valCategory);
        metaGrid.addRow(1, lblStockTag, valStock);

        VBox descBox = new VBox(8);
        Label descTitle = new Label("Mô tả sản phẩm");
        descTitle.setStyle("-fx-font-size: 14px; -fx-font-weight: bold; -fx-text-fill: #1e293b;");
        Label descContent = new Label(safe(product.getDescription()).isBlank() ? "Sản phẩm chưa có mô tả." : safe(product.getDescription()));
        descContent.setWrapText(true);
        descContent.setStyle("-fx-text-fill: #475569; -fx-line-spacing: 4px;");
        descBox.getChildren().addAll(descTitle, descContent);

        // Add to cart Box
        HBox actionBox = new HBox(16);
        actionBox.setAlignment(Pos.CENTER_LEFT);
        actionBox.setPadding(new Insets(16, 0, 0, 0));
        
        Spinner<Integer> spinner = new Spinner<>(1, Math.max(1, product.getStock()), 1);
        spinner.setPrefWidth(100);
        spinner.setStyle("-fx-font-size: 14px;");
        
        Button btnAddToCart = new Button("🛒 Thêm vào giỏ hàng");
        btnAddToCart.setStyle("-fx-background-color: #ee4d2d; -fx-text-fill: white; -fx-font-weight: bold; -fx-font-size: 14px; -fx-padding: 8 24; -fx-cursor: hand;");
        btnAddToCart.setOnAction(e -> {
            addToCart(product, spinner.getValue());
            dialog.setResult(ButtonType.CLOSE);
            dialog.close();
        });
        
        if (product.getStock() <= 0) {
            btnAddToCart.setDisable(true);
            btnAddToCart.setText("Hết hàng");
            spinner.setDisable(true);
        }

        actionBox.getChildren().addAll(spinner, btnAddToCart);

        infoBox.getChildren().addAll(name, priceBox, metaGrid, new Separator(), descBox, actionBox);

        root.getChildren().addAll(imageContainer, infoBox);
        
        ScrollPane scroll = new ScrollPane(root);
        scroll.setFitToWidth(true);
        scroll.setStyle("-fx-background: transparent; -fx-background-color: transparent;");
        
        dialog.getDialogPane().setContent(scroll);
        dialog.showAndWait();
    }

    private int totalPages(int size) {
        return Math.max(1, (int) Math.ceil(size / (double) PAGE_SIZE));
    }

    private <T> List<T> pageSlice(List<T> source, int pageIndex) {
        if (source.isEmpty()) {
            return List.of();
        }
        int from = Math.min(pageIndex * PAGE_SIZE, source.size());
        int to = Math.min(from + PAGE_SIZE, source.size());
        if (from >= to) {
            return List.of();
        }
        return source.subList(from, to);
    }

    private void refreshCategoryPage() {
        List<Category> source = new ArrayList<>(filteredCategories);
        int total = totalPages(source.size());
        categoryPage = Math.max(0, Math.min(categoryPage, total - 1));
        pagedCategories.setAll(pageSlice(source, categoryPage));
        if (lblCategoryPageInfo != null) {
            lblCategoryPageInfo.setText("Trang " + (categoryPage + 1) + "/" + total + " • " + source.size() + " bản ghi");
        }
        if (btnCategoryPrev != null) {
            btnCategoryPrev.setDisable(categoryPage <= 0);
        }
        if (btnCategoryNext != null) {
            btnCategoryNext.setDisable(categoryPage >= total - 1);
        }
    }

    private void refreshProductPage() {
        List<Product> source = new ArrayList<>(filteredProducts);
        int total = totalPages(source.size());
        productPage = Math.max(0, Math.min(productPage, total - 1));
        pagedProducts.setAll(pageSlice(source, productPage));
        if (lblProductPageInfo != null) {
            lblProductPageInfo.setText("Trang " + (productPage + 1) + "/" + total + " • " + source.size() + " bản ghi");
        }
        if (btnProductPrev != null) {
            btnProductPrev.setDisable(productPage <= 0);
        }
        if (btnProductNext != null) {
            btnProductNext.setDisable(productPage >= total - 1);
        }
    }

    private void refreshOrderPage() {
        List<Order> source = new ArrayList<>(filteredOrders);
        int total = totalPages(source.size());
        orderPage = Math.max(0, Math.min(orderPage, total - 1));
        pagedOrders.setAll(pageSlice(source, orderPage));
        if (lblOrderPageInfo != null) {
            lblOrderPageInfo.setText("Trang " + (orderPage + 1) + "/" + total + " • " + source.size() + " bản ghi");
        }
        if (btnOrderPrev != null) {
            btnOrderPrev.setDisable(orderPage <= 0);
        }
        if (btnOrderNext != null) {
            btnOrderNext.setDisable(orderPage >= total - 1);
        }
    }

    private void refreshAccountPage() {
        if (accountTable == null) {
            return;
        }
        List<Account> source = new ArrayList<>(filteredAccounts);
        int total = totalPages(source.size());
        accountPage = Math.max(0, Math.min(accountPage, total - 1));
        pagedAccounts.setAll(pageSlice(source, accountPage));
        if (lblAccountPageInfo != null) {
            lblAccountPageInfo.setText("Trang " + (accountPage + 1) + "/" + total + " • " + source.size() + " bản ghi");
        }
        if (btnAccountPrev != null) {
            btnAccountPrev.setDisable(accountPage <= 0);
        }
        if (btnAccountNext != null) {
            btnAccountNext.setDisable(accountPage >= total - 1);
        }
    }

    @FXML
    private void handleCategoryPrevPage() {
        if (categoryPage > 0) {
            categoryPage--;
            refreshCategoryPage();
        }
    }

    @FXML
    private void handleCategoryNextPage() {
        categoryPage++;
        refreshCategoryPage();
    }

    @FXML
    private void handleProductPrevPage() {
        if (productPage > 0) {
            productPage--;
            refreshProductPage();
        }
    }

    @FXML
    private void handleProductNextPage() {
        productPage++;
        refreshProductPage();
    }

    @FXML
    private void handleOrderPrevPage() {
        if (orderPage > 0) {
            orderPage--;
            refreshOrderPage();
        }
    }

    @FXML
    private void handleOrderNextPage() {
        orderPage++;
        refreshOrderPage();
    }

    @FXML
    private void handleAccountPrevPage() {
        if (accountPage > 0) {
            accountPage--;
            refreshAccountPage();
        }
    }

    @FXML
    private void handleAccountNextPage() {
        accountPage++;
        refreshAccountPage();
    }

    private void setupColumns() {
        if (categoryIdCol != null) {
            categoryIdCol.setCellValueFactory(data -> new javafx.beans.property.SimpleObjectProperty<>(data.getValue().getId()));
        }
        if (categoryNameCol != null) {
            categoryNameCol.setCellValueFactory(data -> new ReadOnlyStringWrapper(data.getValue().getName()));
        }

        if (productIdCol != null) {
            productIdCol.setCellValueFactory(data -> new javafx.beans.property.SimpleObjectProperty<>(data.getValue().getId()));
        }
        if (productNameCol != null) {
            productNameCol.setCellValueFactory(data -> new ReadOnlyStringWrapper(data.getValue().getName()));
        }
        if (productCategoryCol != null) {
            productCategoryCol.setCellValueFactory(data -> {
                Category category = data.getValue().getCategory();
                return new ReadOnlyStringWrapper(category == null ? "" : category.getName());
            });
        }
        if (productPriceCol != null) {
            productPriceCol.setCellValueFactory(data -> new javafx.beans.property.SimpleObjectProperty<>(data.getValue().getPrice()));
            productPriceCol.setCellFactory(col -> moneyCell());
        }
        if (productStockCol != null) {
            productStockCol.setCellValueFactory(data -> new javafx.beans.property.SimpleObjectProperty<>(data.getValue().getStock()));
        }

        if (orderIdCol != null) {
            orderIdCol.setCellValueFactory(data -> new javafx.beans.property.SimpleObjectProperty<>(data.getValue().getId()));
        }
        if (orderCustomerCol != null) {
            orderCustomerCol.setCellValueFactory(data -> new ReadOnlyStringWrapper(data.getValue().getCustomerName()));
        }
        if (orderPhoneCol != null) {
            orderPhoneCol.setCellValueFactory(data -> new ReadOnlyStringWrapper(data.getValue().getCustomerPhone()));
        }
        if (orderDateCol != null) {
            orderDateCol.setCellValueFactory(data -> new javafx.beans.property.SimpleObjectProperty<>(data.getValue().getOrderDate()));
        }
        if (orderStatusCol != null) {
            orderStatusCol.setCellValueFactory(data -> new ReadOnlyStringWrapper(data.getValue().getStatus()));
        }
        if (orderTotalCol != null) {
            orderTotalCol.setCellValueFactory(data -> new javafx.beans.property.SimpleObjectProperty<>(data.getValue().getTotalAmount()));
            orderTotalCol.setCellFactory(col -> moneyCell());
        }

        if (orderDateCol != null) {
            orderDateCol.setCellFactory(col -> new TableCell<Order, LocalDateTime>() {
                private final DateTimeFormatter format = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
                @Override
                protected void updateItem(LocalDateTime item, boolean empty) {
                    super.updateItem(item, empty);
                    setText(empty || item == null ? "" : item.format(format));
                }
            });
        }

        if (orderStatusCol != null) {
            orderStatusCol.setCellFactory(col -> new TableCell<Order, String>() {
                @Override
                protected void updateItem(String item, boolean empty) {
                    super.updateItem(item, empty);
                    getStyleClass().removeAll("status-pending", "status-confirmed", "status-done");
                    if (empty || item == null) { setText(""); return; }
                    setText(item);
                    switch (item.toUpperCase()) {
                        case "PENDING":   getStyleClass().add("status-pending");   break;
                        case "CONFIRMED": getStyleClass().add("status-confirmed"); break;
                        case "DONE": case "DELIVERED": getStyleClass().add("status-done"); break;
                    }
                }
            });
        }

        if (accountIdCol != null) {
            accountIdCol.setCellValueFactory(data -> new javafx.beans.property.SimpleObjectProperty<>(data.getValue().getId()));
            accountFullNameCol.setCellValueFactory(data -> new ReadOnlyStringWrapper(data.getValue().getFullName()));
            accountUsernameCol.setCellValueFactory(data -> new ReadOnlyStringWrapper(data.getValue().getUsername()));
            accountPhoneCol.setCellValueFactory(data -> new ReadOnlyStringWrapper(data.getValue().getPhone()));
            accountEmailCol.setCellValueFactory(data -> new ReadOnlyStringWrapper(data.getValue().getEmail()));
            accountRoleCol.setCellValueFactory(data -> new ReadOnlyStringWrapper(data.getValue().getRole()));
        }
    }

    private <T> TableCell<T, BigDecimal> moneyCell() {
        return new TableCell<T, BigDecimal>() {
            @Override
            protected void updateItem(BigDecimal item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText("");
                    return;
                }
                setText(String.format("%,.0f đ", item.doubleValue()));
            }
        };
    }

    private void updateSummary() {
        if (lblTotalCategories != null) {
            lblTotalCategories.setText(String.valueOf(categories.size()));
        }
        if (lblTotalProducts != null) {
            lblTotalProducts.setText(String.valueOf(products.size()));
        }
        if (lblTotalOrders != null) {
            lblTotalOrders.setText(String.valueOf(orders.size()));
        }

        BigDecimal totalRevenue = orders.stream()
                .map(Order::getTotalAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        long pending = orders.stream()
                .filter(o -> statusEquals(o, "PENDING"))
                .count();

        long completed = orders.stream()
                .filter(o -> statusEquals(o, "DONE") || statusEquals(o, "DELIVERED"))
                .count();

        long lowStock = products.stream()
                .filter(p -> p.getStock() <= 5)
                .count();

        if (lblRevenue != null) {
            lblRevenue.setText(formatMoney(totalRevenue));
        }
        if (lblPendingOrders != null) {
            lblPendingOrders.setText(String.valueOf(pending));
        }
        if (lblCompletedOrders != null) {
            lblCompletedOrders.setText(String.valueOf(completed));
        }
        if (lblLowStockProducts != null) {
            lblLowStockProducts.setText(String.valueOf(lowStock));
        }

        refreshRevenueChart();
    }

    private void refreshRevenueChart() {
        if (revenueChart == null) return;
        
        revenueChart.getData().clear();
        XYChart.Series<String, Number> series = new XYChart.Series<>();

        Map<String, BigDecimal> revenueByMonth = new TreeMap<>();
        for (Order o : orders) {
            if (statusEquals(o, "DONE") || statusEquals(o, "DELIVERED")) {
                if (o.getOrderDate() != null) {
                    String monthYear = o.getOrderDate().format(DateTimeFormatter.ofPattern("MM/yyyy"));
                    BigDecimal current = revenueByMonth.getOrDefault(monthYear, BigDecimal.ZERO);
                    revenueByMonth.put(monthYear, current.add(o.getTotalAmount()));
                }
            }
        }

        for (Map.Entry<String, BigDecimal> entry : revenueByMonth.entrySet()) {
            series.getData().add(new XYChart.Data<>(entry.getKey(), entry.getValue()));
        }

        revenueChart.getData().add(series);
    }

    private boolean statusEquals(Order order, String expected) {
        String status = order.getStatus();
        return status != null && status.trim().equalsIgnoreCase(expected);
    }

    // ── Category CRUD ───────────────────────────────────────
    @FXML
    private void handleAddCategory() {
        if (!requireAdmin()) {
            return;
        }
        Optional<String> nameOpt = showCategoryDialog(null);
        if (nameOpt.isEmpty()) {
            return;
        }
        try {
            categoryDAO.save(new Category(nameOpt.get()));
            handleRefresh();
            showInfo("Thành công", "Đã thêm danh mục mới.");
        } catch (Exception e) {
            showError("Lỗi thêm danh mục", e.getMessage());
        }
    }

    @FXML
    private void handleEditCategory() {
        if (!requireAdmin()) {
            return;
        }
        Category selected = categoryTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showWarning("Chưa chọn dữ liệu", "Vui lòng chọn danh mục cần sửa.");
            return;
        }
        Optional<String> nameOpt = showCategoryDialog(selected.getName());
        if (nameOpt.isEmpty()) {
            return;
        }
        try {
            selected.setName(nameOpt.get());
            categoryDAO.save(selected);
            handleRefresh();
            showInfo("Thành công", "Đã cập nhật danh mục.");
        } catch (Exception e) {
            showError("Lỗi sửa danh mục", e.getMessage());
        }
    }

    @FXML
    private void handleDeleteCategory() {
        if (!requireAdmin()) {
            return;
        }
        Category selected = categoryTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showWarning("Chưa chọn dữ liệu", "Vui lòng chọn danh mục cần xóa.");
            return;
        }
        if (!confirm("Xác nhận xóa", "Xóa danh mục: " + selected.getName() + " ?")) {
            return;
        }
        try {
            categoryDAO.delete(selected.getId());
            handleRefresh();
            showInfo("Thành công", "Đã xóa danh mục.");
        } catch (Exception e) {
            showError("Không thể xóa danh mục", "Danh mục có thể đang được sử dụng bởi sản phẩm.\n" + e.getMessage());
        }
    }

    // ── Product CRUD ────────────────────────────────────────
    @FXML
    private void handleAddProduct() {
        if (!requireAdmin()) {
            return;
        }
        Product product = new Product();
        if (!collectProductInput(product, true)) {
            return;
        }
        try {
            productDAO.save(product);
            handleRefresh();
            showInfo("Thành công", "Đã thêm sản phẩm.");
        } catch (Exception e) {
            showError("Lỗi thêm sản phẩm", e.getMessage());
        }
    }

    @FXML
    private void handleEditProduct() {
        if (!requireAdmin()) {
            return;
        }
        Product selected = productTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showWarning("Chưa chọn dữ liệu", "Vui lòng chọn sản phẩm cần sửa.");
            return;
        }
        if (!collectProductInput(selected, false)) {
            return;
        }
        try {
            productDAO.save(selected);
            handleRefresh();
            showInfo("Thành công", "Đã cập nhật sản phẩm.");
        } catch (Exception e) {
            showError("Lỗi sửa sản phẩm", e.getMessage());
        }
    }

    @FXML
    private void handleDeleteProduct() {
        if (!requireAdmin()) {
            return;
        }
        Product selected = productTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showWarning("Chưa chọn dữ liệu", "Vui lòng chọn sản phẩm cần xóa.");
            return;
        }
        if (!confirm("Xác nhận xóa", "Xóa sản phẩm: " + selected.getName() + " ?")) {
            return;
        }
        try {
            productDAO.delete(selected.getId());
            handleRefresh();
            showInfo("Thành công", "Đã xóa sản phẩm.");
        } catch (Exception e) {
            showError("Không thể xóa sản phẩm", "Sản phẩm có thể đang nằm trong đơn hàng/giỏ hàng.\n" + e.getMessage());
        }
    }

    // ── Order ───────────────────────────────────────────────
    @FXML
    private void handleAdvanceOrderStatus() {
        Order selected = orderTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showWarning("Chưa chọn dữ liệu", "Vui lòng chọn đơn hàng cần cập nhật.");
            return;
        }
        if (!SessionManager.isAdmin()) {
            showWarning("Không có quyền", "Chỉ ADMIN mới được cập nhật trạng thái đơn hàng.");
            return;
        }

        String current = selected.getStatus() == null ? "PENDING" : selected.getStatus().toUpperCase();
        String next;
        switch (current) {
            case "PENDING":
                next = "CONFIRMED";
                break;
            case "CONFIRMED":
                next = "DONE";
                break;
            default:
                next = "DONE";
                break;
        }

        if (!confirm("Xác nhận cập nhật", "Đổi trạng thái đơn #" + selected.getId() + " từ " + current + " -> " + next + " ?")) {
            return;
        }
        try {
            selected.setStatus(next);
            orderDAO.save(selected);
            handleRefresh();
            showInfo("Thành công", "Đã cập nhật trạng thái đơn hàng.");
        } catch (Exception e) {
            showError("Lỗi cập nhật trạng thái", e.getMessage());
        }
    }

    @FXML
    private void handleViewOrderDetails() {
        Order selected = orderTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showWarning("Chưa chọn dữ liệu", "Vui lòng chọn đơn hàng cần xem chi tiết.");
            return;
        }

        Order detailOrder = orderDAO.findByIdWithItems(selected.getId());
        if (detailOrder == null) {
            showWarning("Không tìm thấy", "Không thể tải chi tiết đơn hàng.");
            return;
        }

        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle("Chi tiết đơn hàng #" + detailOrder.getId());
        dialog.setHeaderText(null);
        dialog.getDialogPane().getButtonTypes().add(ButtonType.CLOSE);
        dialog.getDialogPane().setPrefWidth(900);
        dialog.getDialogPane().getStyleClass().add("modal-dialog-root");

        VBox root = new VBox(20);
        root.setStyle("-fx-background-color: transparent;");

        // HEADER: Status and ID
        HBox headerBox = new HBox();
        headerBox.setAlignment(Pos.CENTER_LEFT);
        Label lblOrderId = new Label("Đơn hàng #" + detailOrder.getId());
        lblOrderId.setStyle("-fx-font-size: 26px; -fx-font-weight: bold; -fx-text-fill: #1e293b;");
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        Label lblStatus = new Label(safe(detailOrder.getStatus()));
        lblStatus.setStyle("-fx-font-weight: bold; -fx-padding: 6 12; -fx-background-radius: 20; " 
                + getStatusStyle(detailOrder.getStatus()));
        headerBox.getChildren().addAll(lblOrderId, spacer, lblStatus);

        // CUSTOMER INFO
        GridPane infoGrid = new GridPane();
        infoGrid.setHgap(40);
        infoGrid.setVgap(12);
        infoGrid.setStyle("-fx-background-color: #f8fafc; -fx-padding: 16; -fx-background-radius: 8; -fx-border-color: #e2e8f0; -fx-border-radius: 8;");
        
        infoGrid.add(createDetailLabel("Khách hàng", safe(detailOrder.getCustomerName())), 0, 0);
        infoGrid.add(createDetailLabel("Điện thoại", safe(detailOrder.getCustomerPhone())), 1, 0);
        infoGrid.add(createDetailLabel("Địa chỉ", safe(detailOrder.getCustomerAddress())), 0, 1, 2, 1);
        infoGrid.add(createDetailLabel("Thời gian đặt", detailOrder.getOrderDate() != null ? detailOrder.getOrderDate().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")) : ""), 0, 2);
        infoGrid.add(createDetailLabel("Thanh toán", safe(detailOrder.getPaymentMethod())), 1, 2);
        if (detailOrder.getNote() != null && !detailOrder.getNote().isEmpty()) {
            infoGrid.add(createDetailLabel("Ghi chú", detailOrder.getNote()), 0, 3, 2, 1);
        }

        // ITEMS LIST (Modern View)
        Label lblItemsTitle = new Label("Danh sách sản phẩm");
        lblItemsTitle.setStyle("-fx-font-size: 20px; -fx-font-weight: bold; -fx-text-fill: #1e293b;");

        VBox itemsBox = new VBox(10);
        List<OrderItem> itemList = detailOrder.getItems() == null ? List.of() : detailOrder.getItems();
        for (OrderItem item : itemList) {
            HBox row = new HBox(16);
            row.setAlignment(Pos.CENTER_LEFT);
            row.setStyle("-fx-background-color: #ffffff; -fx-border-color: #e2e8f0; -fx-border-radius: 10; -fx-background-radius: 10; -fx-padding: 12;");

            StackPane thumb = new StackPane();
            thumb.setPrefSize(90, 90);
            thumb.setStyle("-fx-background-color: #eef2ff; -fx-background-radius: 8;");
            ImageView iv = item.getProduct() != null ? createProductImageView(item.getProduct().getImagePath()) : null;
            if (iv != null) {
                iv.setFitWidth(86); iv.setFitHeight(86);
                thumb.getChildren().add(iv);
            } else {
                Label noImg = new Label("📦");
                noImg.setStyle("-fx-font-size: 32;");
                thumb.getChildren().add(noImg);
            }

            VBox info = new VBox(6);
            HBox.setHgrow(info, Priority.ALWAYS);
            Label name = new Label(item.getProduct() == null ? "Sản phẩm không rõ" : safe(item.getProduct().getName()));
            name.setStyle("-fx-font-weight: bold; -fx-font-size: 18px; -fx-text-fill: #1e293b;");
            name.setWrapText(true);
            
            Label priceQty = new Label(formatMoney(item.getUnitPrice()) + "  x  " + item.getQuantity());
            priceQty.setStyle("-fx-text-fill: #64748b; -fx-font-size: 16px;");
            
            info.getChildren().addAll(name, priceQty);

            Label subtotal = new Label(formatMoney(item.getLineTotal()));
            subtotal.setStyle("-fx-font-weight: bold; -fx-text-fill: #dc2626; -fx-font-size: 20px;");

            row.getChildren().addAll(thumb, info, subtotal);
            itemsBox.getChildren().add(row);
        }

        // TOTAL SUMMARY
        HBox totalBox = new HBox(16);
        totalBox.setAlignment(Pos.CENTER_RIGHT);
        totalBox.setStyle("-fx-padding: 16 0 0 0;");
        Label lblTotalTitle = new Label("Tổng cộng:");
        lblTotalTitle.setStyle("-fx-font-size: 20px; -fx-font-weight: bold; -fx-text-fill: #475569;");
        Label lblTotalValue = new Label(formatMoney(detailOrder.getTotalAmount()));
        lblTotalValue.getStyleClass().add("modal-total-text");
        totalBox.getChildren().addAll(lblTotalTitle, lblTotalValue);

        root.getChildren().addAll(headerBox, infoGrid, lblItemsTitle, itemsBox, totalBox);

        ScrollPane scroll = new ScrollPane(root);
        scroll.setFitToWidth(true);
        scroll.setStyle("-fx-background: transparent; -fx-background-color: transparent;");
        dialog.getDialogPane().setContent(scroll);
        dialog.showAndWait();
    }

    private VBox createDetailLabel(String title, String value) {
        VBox box = new VBox(4);
        Label lblTitle = new Label(title);
        lblTitle.setStyle("-fx-font-size: 16px; -fx-text-fill: #64748b;");
        Label lblValue = new Label(value);
        lblValue.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: #1e293b;");
        lblValue.setWrapText(true);
        box.getChildren().addAll(lblTitle, lblValue);
        return box;
    }

    private Label createFormLabel(String title) {
        Label lbl = new Label(title);
        lbl.getStyleClass().add("checkout-form-label");
        lbl.setMinWidth(120);
        return lbl;
    }

    private String getStatusStyle(String status) {
        if (status == null) return "-fx-background-color: #f1f5f9; -fx-text-fill: #475569;";
        switch (status.toUpperCase()) {
            case "PENDING":   return "-fx-background-color: #fef3c7; -fx-text-fill: #d97706;";
            case "CONFIRMED": return "-fx-background-color: #e0e7ff; -fx-text-fill: #4f46e5;";
            case "DONE": 
            case "DELIVERED": return "-fx-background-color: #dcfce7; -fx-text-fill: #16a34a;";
            case "CANCELLED": return "-fx-background-color: #fee2e2; -fx-text-fill: #dc2626;";
            default:          return "-fx-background-color: #f1f5f9; -fx-text-fill: #475569;";
        }
    }

    // ── Account CRUD ────────────────────────────────────────
    @FXML
    private void handleAddAccount() {
        if (!requireAdmin()) {
            return;
        }
        Account account = new Account();
        if (!collectAccountInput(account, true)) {
            return;
        }
        try {
            accountDAO.save(account);
            handleRefresh();
            showInfo("Thành công", "Đã thêm tài khoản.");
        } catch (Exception e) {
            showError("Lỗi thêm tài khoản", e.getMessage());
        }
    }

    @FXML
    private void handleEditAccount() {
        if (!requireAdmin()) {
            return;
        }
        Account selected = accountTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showWarning("Chưa chọn dữ liệu", "Vui lòng chọn tài khoản cần sửa.");
            return;
        }
        if (!collectAccountInput(selected, false)) {
            return;
        }
        try {
            accountDAO.save(selected);
            handleRefresh();
            showInfo("Thành công", "Đã cập nhật tài khoản.");
        } catch (Exception e) {
            showError("Lỗi sửa tài khoản", e.getMessage());
        }
    }

    @FXML
    private void handleDeleteAccount() {
        if (!requireAdmin()) {
            return;
        }
        Account selected = accountTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showWarning("Chưa chọn dữ liệu", "Vui lòng chọn tài khoản cần xóa.");
            return;
        }
        Account current = SessionManager.getCurrentUser();
        if (current != null && current.getId() == selected.getId()) {
            showWarning("Không thể xóa", "Không thể xóa tài khoản đang đăng nhập.");
            return;
        }
        if (!confirm("Xác nhận xóa", "Xóa tài khoản: " + selected.getUsername() + " ?")) {
            return;
        }
        try {
            accountDAO.delete(selected.getId());
            handleRefresh();
            showInfo("Thành công", "Đã xóa tài khoản.");
        } catch (Exception e) {
            showError("Không thể xóa tài khoản", "Tài khoản có thể đang liên kết đơn hàng/giỏ hàng.\n" + e.getMessage());
        }
    }

    // ── Input Helpers ───────────────────────────────────────
    private boolean collectProductInput(Product product, boolean isCreate) {
        List<Category> allCategories = new ArrayList<>(categoryDAO.findAll());
        if (allCategories.isEmpty()) {
            showWarning("Thiếu dữ liệu", "Chưa có danh mục. Vui lòng tạo danh mục trước khi thêm sản phẩm.");
            return false;
        }

        Optional<ProductFormData> dataOpt = showProductDialog(product, allCategories, isCreate);
        if (dataOpt.isEmpty()) {
            return false;
        }

        ProductFormData data = dataOpt.get();
        product.setName(data.name);
        product.setCategory(data.category);
        product.setPrice(data.price);
        product.setStock(data.stock);
        product.setDescription(data.description);
        product.setImagePath(data.imagePath);
        return true;
    }

    private boolean collectAccountInput(Account account, boolean isCreate) {
        Optional<AccountFormData> dataOpt = showAccountDialog(account, isCreate);
        if (dataOpt.isEmpty()) {
            return false;
        }

        AccountFormData data = dataOpt.get();
        account.setFullName(data.fullName);
        account.setPhone(data.phone);
        account.setEmail(data.email);
        account.setUsername(data.username);
        if (isCreate || !data.password.isEmpty()) {
            account.setPassword(data.password);
        }
        account.setRole(data.role);
        return true;
    }

    private Optional<String> showCategoryDialog(String initialName) {
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle(initialName == null ? "Thêm danh mục" : "Sửa danh mục");
        dialog.setHeaderText("Thông tin danh mục");
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        TextField txtName = new TextField(safe(initialName));
        txtName.setPromptText("Tên danh mục");

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(12));
        grid.add(new Label("Tên danh mục:"), 0, 0);
        grid.add(txtName, 1, 0);
        GridPane.setHgrow(txtName, Priority.ALWAYS);

        dialog.getDialogPane().setContent(grid);
        Optional<ButtonType> result = dialog.showAndWait();
        if (result.isEmpty() || result.get() != ButtonType.OK) {
            return Optional.empty();
        }

        String name = safe(txtName.getText()).trim();
        if (name.isEmpty()) {
            showWarning("Dữ liệu không hợp lệ", "Tên danh mục không được để trống.");
            return Optional.empty();
        }
        return Optional.of(name);
    }

    private Optional<ProductFormData> showProductDialog(Product product, List<Category> categories, boolean isCreate) {
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle(isCreate ? "Thêm sản phẩm" : "Sửa sản phẩm");
        dialog.setHeaderText("Thông tin sản phẩm");
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        TextField txtName = new TextField(safe(product.getName()));
        TextField txtPrice = new TextField(product.getPrice() == null ? "" : product.getPrice().toPlainString());
        TextField txtStock = new TextField(String.valueOf(product.getStock()));
        TextField txtDescription = new TextField(safe(product.getDescription()));
        TextField txtImagePath = new TextField(safe(product.getImagePath()));
        txtImagePath.setPromptText("/static/images/products/ten-anh.jpg");
        Button btnUploadImage = new Button("Tải ảnh...");
        btnUploadImage.getStyleClass().add("ghost-button");

        ComboBox<Category> cboCategory = new ComboBox<>(FXCollections.observableArrayList(categories));
        cboCategory.setCellFactory(cb -> new javafx.scene.control.ListCell<>() {
            @Override
            protected void updateItem(Category item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? "" : item.getName());
            }
        });
        cboCategory.setButtonCell(new javafx.scene.control.ListCell<>() {
            @Override
            protected void updateItem(Category item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? "" : item.getName());
            }
        });
        if (product.getCategory() != null) {
            categories.stream()
                    .filter(c -> c.getId() == product.getCategory().getId())
                    .findFirst()
                    .ifPresent(cboCategory::setValue);
        }
        if (cboCategory.getValue() == null && !categories.isEmpty()) {
            cboCategory.setValue(categories.get(0));
        }

        btnUploadImage.setOnAction(event -> {
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Chọn ảnh sản phẩm");
            fileChooser.getExtensionFilters().addAll(
                    new FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg", "*.jpeg", "*.gif", "*.webp"),
                    new FileChooser.ExtensionFilter("All Files", "*.*")
            );

            Stage owner = null;
            if (dialog.getDialogPane().getScene() != null
                    && dialog.getDialogPane().getScene().getWindow() instanceof Stage) {
                owner = (Stage) dialog.getDialogPane().getScene().getWindow();
            }

            File selectedFile = fileChooser.showOpenDialog(owner);
            if (selectedFile == null) {
                return;
            }

            Optional<String> storedPath = copyProductImageToResources(selectedFile);
            storedPath.ifPresent(txtImagePath::setText);
        });

        HBox imagePathBox = new HBox(8, txtImagePath, btnUploadImage);
        HBox.setHgrow(txtImagePath, Priority.ALWAYS);

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(12));
        grid.add(new Label("Tên sản phẩm:"), 0, 0);
        grid.add(txtName, 1, 0);
        grid.add(new Label("Danh mục:"), 0, 1);
        grid.add(cboCategory, 1, 1);
        grid.add(new Label("Giá (VND):"), 0, 2);
        grid.add(txtPrice, 1, 2);
        grid.add(new Label("Tồn kho:"), 0, 3);
        grid.add(txtStock, 1, 3);
        grid.add(new Label("Mô tả:"), 0, 4);
        grid.add(txtDescription, 1, 4);
        grid.add(new Label("Ảnh sản phẩm:"), 0, 5);
        grid.add(imagePathBox, 1, 5);

        GridPane.setHgrow(txtName, Priority.ALWAYS);
        GridPane.setHgrow(cboCategory, Priority.ALWAYS);
        GridPane.setHgrow(txtPrice, Priority.ALWAYS);
        GridPane.setHgrow(txtStock, Priority.ALWAYS);
        GridPane.setHgrow(txtDescription, Priority.ALWAYS);
        GridPane.setHgrow(imagePathBox, Priority.ALWAYS);

        dialog.getDialogPane().setContent(grid);
        Optional<ButtonType> result = dialog.showAndWait();
        if (result.isEmpty() || result.get() != ButtonType.OK) {
            return Optional.empty();
        }

        String name = safe(txtName.getText()).trim();
        if (name.isEmpty()) {
            showWarning("Dữ liệu không hợp lệ", "Tên sản phẩm không được để trống.");
            return Optional.empty();
        }

        BigDecimal price;
        try {
            price = new BigDecimal(safe(txtPrice.getText()).trim());
            if (price.compareTo(BigDecimal.ZERO) < 0) {
                showWarning("Dữ liệu không hợp lệ", "Giá không được âm.");
                return Optional.empty();
            }
        } catch (NumberFormatException ex) {
            showWarning("Dữ liệu không hợp lệ", "Giá phải là số hợp lệ.");
            return Optional.empty();
        }

        int stock;
        try {
            stock = Integer.parseInt(safe(txtStock.getText()).trim());
            if (stock < 0) {
                showWarning("Dữ liệu không hợp lệ", "Tồn kho không được âm.");
                return Optional.empty();
            }
        } catch (NumberFormatException ex) {
            showWarning("Dữ liệu không hợp lệ", "Tồn kho phải là số nguyên.");
            return Optional.empty();
        }

        Category category = cboCategory.getValue();
        if (category == null) {
            showWarning("Dữ liệu không hợp lệ", "Vui lòng chọn danh mục.");
            return Optional.empty();
        }

        return Optional.of(new ProductFormData(
                name,
                category,
                price,
                stock,
                safe(txtDescription.getText()).trim(),
                safe(txtImagePath.getText()).trim()));
    }

    private Optional<String> copyProductImageToResources(File sourceFile) {
        try {
            String fileName = sourceFile.getName();
            String extension = "";
            int dotIndex = fileName.lastIndexOf('.');
            if (dotIndex >= 0) {
                extension = fileName.substring(dotIndex).toLowerCase();
            }

            String generatedName = "product_" + System.currentTimeMillis() + extension;
            Path targetDir = Path.of(System.getProperty("user.dir"),
                    "src", "main", "resources", "static", "images", "products");
            Files.createDirectories(targetDir);

            Path targetFile = targetDir.resolve(generatedName);
            Files.copy(sourceFile.toPath(), targetFile, StandardCopyOption.REPLACE_EXISTING);

            return Optional.of("/static/images/products/" + generatedName);
        } catch (IOException e) {
            showError("Không thể tải ảnh", "Lỗi khi lưu ảnh: " + e.getMessage());
            return Optional.empty();
        }
    }

    private Optional<AccountFormData> showAccountDialog(Account account, boolean isCreate) {
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle(isCreate ? "Thêm tài khoản" : "Sửa tài khoản");
        dialog.setHeaderText("Thông tin tài khoản");
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        TextField txtFullName = new TextField(safe(account.getFullName()));
        TextField txtPhone = new TextField(safe(account.getPhone()));
        TextField txtEmail = new TextField(safe(account.getEmail()));
        TextField txtUsername = new TextField(safe(account.getUsername()));
        PasswordField txtPassword = new PasswordField();
        txtPassword.setPromptText(isCreate ? "Nhập mật khẩu" : "Để trống nếu giữ nguyên");

        ComboBox<String> cboRole = new ComboBox<>(FXCollections.observableArrayList("ADMIN", "CUSTOMER"));
        String roleDefault = safe(account.getRole()).isBlank() ? "CUSTOMER" : safe(account.getRole()).toUpperCase();
        if (!"ADMIN".equals(roleDefault) && !"CUSTOMER".equals(roleDefault)) {
            roleDefault = "CUSTOMER";
        }
        cboRole.setValue(roleDefault);

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(12));
        grid.add(new Label("Họ tên:"), 0, 0);
        grid.add(txtFullName, 1, 0);
        grid.add(new Label("Số điện thoại:"), 0, 1);
        grid.add(txtPhone, 1, 1);
        grid.add(new Label("Email:"), 0, 2);
        grid.add(txtEmail, 1, 2);
        grid.add(new Label("Username:"), 0, 3);
        grid.add(txtUsername, 1, 3);
        grid.add(new Label(isCreate ? "Mật khẩu:" : "Mật khẩu mới:"), 0, 4);
        grid.add(txtPassword, 1, 4);
        grid.add(new Label("Vai trò:"), 0, 5);
        grid.add(cboRole, 1, 5);

        GridPane.setHgrow(txtFullName, Priority.ALWAYS);
        GridPane.setHgrow(txtPhone, Priority.ALWAYS);
        GridPane.setHgrow(txtEmail, Priority.ALWAYS);
        GridPane.setHgrow(txtUsername, Priority.ALWAYS);
        GridPane.setHgrow(txtPassword, Priority.ALWAYS);
        GridPane.setHgrow(cboRole, Priority.ALWAYS);

        dialog.getDialogPane().setContent(grid);
        Optional<ButtonType> result = dialog.showAndWait();
        if (result.isEmpty() || result.get() != ButtonType.OK) {
            return Optional.empty();
        }

        String fullName = safe(txtFullName.getText()).trim();
        String phone = safe(txtPhone.getText()).trim();
        String email = safe(txtEmail.getText()).trim();
        String username = safe(txtUsername.getText()).trim();
        String password = safe(txtPassword.getText()).trim();
        String role = cboRole.getValue();

        if (fullName.isEmpty() || phone.isEmpty() || email.isEmpty() || username.isEmpty()) {
            showWarning("Dữ liệu không hợp lệ", "Họ tên, SĐT, email, username không được để trống.");
            return Optional.empty();
        }
        if (isCreate && password.isEmpty()) {
            showWarning("Dữ liệu không hợp lệ", "Mật khẩu không được để trống khi tạo mới.");
            return Optional.empty();
        }
        if (role == null || role.isBlank()) {
            showWarning("Dữ liệu không hợp lệ", "Vui lòng chọn vai trò.");
            return Optional.empty();
        }

        return Optional.of(new AccountFormData(fullName, phone, email, username, password, role));
    }

    private boolean confirm(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        return alert.showAndWait().orElse(ButtonType.CANCEL) == ButtonType.OK;
    }

    private void showInfo(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void showWarning(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void showError(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(title);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private boolean requireAdmin() {
        if (SessionManager.isAdmin()) {
            return true;
        }
        showWarning("Không có quyền", "Chức năng này chỉ dành cho ADMIN.");
        return false;
    }

    private String safe(String value) {
        return value == null ? "" : value;
    }

    private String formatMoney(BigDecimal amount) {
        if (amount == null) {
            return "0 đ";
        }
        return String.format("%,.0f đ", amount.doubleValue());
    }

    private static class ProductFormData {
        private final String name;
        private final Category category;
        private final BigDecimal price;
        private final int stock;
        private final String description;
        private final String imagePath;

        private ProductFormData(String name, Category category, BigDecimal price, int stock,
                                String description, String imagePath) {
            this.name = name;
            this.category = category;
            this.price = price;
            this.stock = stock;
            this.description = description;
            this.imagePath = imagePath;
        }
    }

    private static class AccountFormData {
        private final String fullName;
        private final String phone;
        private final String email;
        private final String username;
        private final String password;
        private final String role;

        private AccountFormData(String fullName, String phone, String email,
                                String username, String password, String role) {
            this.fullName = fullName;
            this.phone = phone;
            this.email = email;
            this.username = username;
            this.password = password;
            this.role = role;
        }
    }

    // ── Logout ────────────────────────────────────────────────
    @FXML
    private void handleLogout() {
        SessionManager.logout();
        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/com/mycompany/quanlydienmay/login.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) lblWelcome.getScene().getWindow();
            Scene scene = new Scene(root, 520, 500);
            scene.getStylesheets().add(
                    getClass().getResource("/com/mycompany/quanlydienmay/app.css").toExternalForm());
            stage.setTitle("Đăng nhập - Quản Lý Điện Máy");
            stage.setScene(scene);
        } catch (Exception e) {
            showError("Lỗi đăng xuất", e.getMessage());
        }
    }
}