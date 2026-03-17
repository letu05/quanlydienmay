package com.mycompany.quanlydienmay.main;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import com.mycompany.quanlydienmay.dao.CategoryDAO;
import com.mycompany.quanlydienmay.dao.OrderDAO;
import com.mycompany.quanlydienmay.dao.ProductDAO;
import com.mycompany.quanlydienmay.model.Category;
import com.mycompany.quanlydienmay.model.Order;
import com.mycompany.quanlydienmay.model.Product;
import com.mycompany.quanlydienmay.utils.SessionManager;

import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.Tab;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class DashboardController {

    @FXML private Label lblTotalProducts;
    @FXML private Label lblTotalCategories;
    @FXML private Label lblTotalOrders;
    @FXML private Label lblWelcome;

    @FXML private Tab tabCategory;
    @FXML private Tab tabProduct;
    @FXML private Tab tabOrder;
    @FXML private Tab tabAccount;

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

    private final ObservableList<Category> categories = FXCollections.observableArrayList();
    private final ObservableList<Product>  products   = FXCollections.observableArrayList();
    private final ObservableList<Order>    orders     = FXCollections.observableArrayList();

    private final FilteredList<Category> filteredCategories = new FilteredList<>(categories, item -> true);
    private final FilteredList<Product> filteredProducts = new FilteredList<>(products, item -> true);
    private final FilteredList<Order> filteredOrders = new FilteredList<>(orders, item -> true);

    private final CategoryDAO categoryDAO = new CategoryDAO();
    private final ProductDAO  productDAO  = new ProductDAO();
    private final OrderDAO    orderDAO    = new OrderDAO();

    @FXML
    public void initialize() {
        setupColumns();
        setupSearch();
        applyRolePermissions();
        loadData();
        updateSummary();
    }

    // ── Phân quyền ───────────────────────────────────────────
    private void applyRolePermissions() {
        String role = SessionManager.getCurrentUser() == null ? "GUEST"
                : SessionManager.getCurrentUser().getRole();
        lblWelcome.setText("Xin chào, " + SessionManager.getCurrentUser().getFullName()
                + "  (" + role + ")");
        boolean isAdmin = "ADMIN".equalsIgnoreCase(role);
        tabCategory.setDisable(!isAdmin);
        tabAccount.setDisable(!isAdmin);
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
            
            System.out.println("[DASHBOARD] Binding tables...");
            categoryTable.setItems(filteredCategories);
            productTable.setItems(filteredProducts);
            orderTable.setItems(filteredOrders);
            System.out.println("[DASHBOARD] Tables bound successfully");
            
        } catch (Exception e) {
            System.err.println("[DASHBOARD] ERROR loading data: " + e.getMessage());
            e.printStackTrace();
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Lỗi");
            alert.setHeaderText("Không thể tải dữ liệu từ cơ sở dữ liệu");
            alert.setContentText("Chi tiết: " + e.getMessage() + "\n\nVui lòng kiểm tra MySQL server có chạy không.");
            alert.showAndWait();
        }
    }

    private void setupSearch() {
        txtCategorySearch.textProperty().addListener((obs, oldValue, keyword) -> {
            String text = normalize(keyword);
            filteredCategories.setPredicate(c -> text.isEmpty()
                    || String.valueOf(c.getId()).contains(text)
                    || normalize(c.getName()).contains(text));
        });

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
        });

        txtOrderSearch.textProperty().addListener((obs, oldValue, keyword) -> {
            String text = normalize(keyword);
            filteredOrders.setPredicate(o -> text.isEmpty()
                    || String.valueOf(o.getId()).contains(text)
                    || normalize(o.getCustomerName()).contains(text)
                    || normalize(o.getCustomerPhone()).contains(text)
                    || normalize(o.getStatus()).contains(text));
        });
    }

    private String normalize(String value) {
        return value == null ? "" : value.trim().toLowerCase();
    }

    @FXML
    private void handleRefresh() {
        loadData();
        updateSummary();
    }

    private void setupColumns() {
        categoryIdCol.setCellValueFactory(data -> new javafx.beans.property.SimpleObjectProperty<>(data.getValue().getId()));
        categoryNameCol.setCellValueFactory(data -> new ReadOnlyStringWrapper(data.getValue().getName()));

        productIdCol.setCellValueFactory(data -> new javafx.beans.property.SimpleObjectProperty<>(data.getValue().getId()));
        productNameCol.setCellValueFactory(data -> new ReadOnlyStringWrapper(data.getValue().getName()));
        productCategoryCol.setCellValueFactory(data -> {
            Category category = data.getValue().getCategory();
            return new ReadOnlyStringWrapper(category == null ? "" : category.getName());
        });
        productPriceCol.setCellValueFactory(data -> new javafx.beans.property.SimpleObjectProperty<>(data.getValue().getPrice()));
        productStockCol.setCellValueFactory(data -> new javafx.beans.property.SimpleObjectProperty<>(data.getValue().getStock()));

        orderIdCol.setCellValueFactory(data -> new javafx.beans.property.SimpleObjectProperty<>(data.getValue().getId()));
        orderCustomerCol.setCellValueFactory(data -> new ReadOnlyStringWrapper(data.getValue().getCustomerName()));
        orderPhoneCol.setCellValueFactory(data -> new ReadOnlyStringWrapper(data.getValue().getCustomerPhone()));
        orderDateCol.setCellValueFactory(data -> new javafx.beans.property.SimpleObjectProperty<>(data.getValue().getOrderDate()));
        orderStatusCol.setCellValueFactory(data -> new ReadOnlyStringWrapper(data.getValue().getStatus()));
        orderTotalCol.setCellValueFactory(data -> new javafx.beans.property.SimpleObjectProperty<>(data.getValue().getTotalAmount()));

        productPriceCol.setCellFactory(col -> moneyCell());
        orderTotalCol.setCellFactory(col -> moneyCell());
        orderDateCol.setCellFactory(col -> new TableCell<Order, LocalDateTime>() {
            private final DateTimeFormatter format = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
            @Override
            protected void updateItem(LocalDateTime item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? "" : item.format(format));
            }
        });
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
        lblTotalCategories.setText(String.valueOf(categories.size()));
        lblTotalProducts.setText(String.valueOf(products.size()));
        lblTotalOrders.setText(String.valueOf(orders.size()));
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
            e.printStackTrace();
        }
    }
}