package com.mycompany.quanlydienmay.main;

import com.mycompany.quanlydienmay.dao.AccountDAO;
import com.mycompany.quanlydienmay.model.Account;
import com.mycompany.quanlydienmay.utils.SessionManager;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class LoginController {

    @FXML
    private TextField txtUsername;

    @FXML
    private PasswordField txtPassword;

    @FXML
    private Button btnLogin;

    @FXML
    private Label lblError;

    private final AccountDAO accountDAO = new AccountDAO();

    @FXML
    private void handleLogin() {
        String username = txtUsername.getText().trim();
        String password = txtPassword.getText().trim();

        lblError.setText("");

        if (username.isEmpty() || password.isEmpty()) {
            lblError.setText("Vui lòng nhập tên đăng nhập và mật khẩu.");
            return;
        }

        System.out.println("[LOGIN] Attempting login with username: " + username);

        Account account = accountDAO.findByUsernameAndPassword(username, password);

        if (account == null) {
            System.out.println("[LOGIN] Login failed - account not found or password incorrect");
            lblError.setText("Tên đăng nhập hoặc mật khẩu không đúng.");
            txtPassword.clear();
            return;
        }

        System.out.println("[LOGIN] Login success - Account ID: " + account.getId() + ", Role: " + account.getRole());
        SessionManager.login(account);
        openDashboard();
    }

    private void openDashboard() {
        try {
            System.out.println("[DASHBOARD] Loading dashboard.fxml...");
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/com/mycompany/quanlydienmay/dashboard.fxml"));
            
            if (loader.getLocation() == null) {
                System.err.println("[DASHBOARD] ERROR: Cannot find dashboard.fxml");
                lblError.setText("Lỗi: Không tìm thấy file dashboard.fxml");
                return;
            }
            
            Parent root = loader.load();
            System.out.println("[DASHBOARD] dashboard.fxml loaded successfully");

            Stage stage = (Stage) btnLogin.getScene().getWindow();
            Scene scene = new Scene(root, 1200, 760);
            
            var cssResource = getClass().getResource("/com/mycompany/quanlydienmay/app.css");
            if (cssResource != null) {
                scene.getStylesheets().add(cssResource.toExternalForm());
                System.out.println("[DASHBOARD] CSS loaded successfully");
            }

            String title = "Quản Lý Điện Máy - " + SessionManager.getCurrentUser().getFullName()
                    + " [" + SessionManager.getCurrentUser().getRole() + "]";
            stage.setTitle(title);
            stage.setScene(scene);
            stage.setMinWidth(960);
            stage.setMinHeight(640);
            
            System.out.println("[DASHBOARD] Dashboard opened successfully - " + title);
        } catch (Exception e) {
            String errorMsg = "Lỗi mở dashboard: " + e.getClass().getSimpleName() + " - " + e.getMessage();
            System.err.println("[DASHBOARD] ERROR: " + errorMsg);
            e.printStackTrace();
            lblError.setText(errorMsg);
        }
    }

    @FXML
    private void handleRegister() {
        try {
            System.out.println("[LOGIN] Loading register.fxml...");
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/com/mycompany/quanlydienmay/register.fxml"));
            
            Parent root = loader.load();

            Stage stage = (Stage) btnLogin.getScene().getWindow();
            Scene scene = new Scene(root, 1200, 760);
            
            var cssResource = getClass().getResource("/com/mycompany/quanlydienmay/app.css");
            if (cssResource != null) {
                scene.getStylesheets().add(cssResource.toExternalForm());
            }

            stage.setTitle("Đăng ký - Quản Lý Điện Máy");
            stage.setScene(scene);
        } catch (Exception e) {
            System.err.println("[LOGIN] ERROR opening register view:");
            e.printStackTrace();
            lblError.setText("Lỗi mở màn hình đăng ký.");
        }
    }
}
