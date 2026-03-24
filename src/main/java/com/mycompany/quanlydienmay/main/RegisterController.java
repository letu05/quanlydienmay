package com.mycompany.quanlydienmay.main;

import com.mycompany.quanlydienmay.dao.AccountDAO;
import com.mycompany.quanlydienmay.model.Account;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

public class RegisterController {

    @FXML private TextField txtFullName;
    @FXML private TextField txtUsername;
    @FXML private TextField txtEmail;
    @FXML private TextField txtPhone;
    @FXML private PasswordField txtPassword;
    @FXML private PasswordField txtConfirmPassword;
    @FXML private Label lblError;
    @FXML private Button btnRegister;

    private final AccountDAO accountDAO = new AccountDAO();

    @FXML
    private void handleRegisterSubmit() {
        String fullName = txtFullName.getText().trim();
        String username = txtUsername.getText().trim();
        String email = txtEmail.getText().trim();
        String phone = txtPhone.getText().trim();
        String password = txtPassword.getText().trim();
        String confirmPassword = txtConfirmPassword.getText().trim();

        lblError.setTextFill(Color.web("#ef4444")); // red for errors
        lblError.setText("");

        if (fullName.isEmpty() || username.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
            lblError.setText("Vui lòng điền đầy đủ các trường bắt buộc.");
            return;
        }

        if (!password.equals(confirmPassword)) {
            lblError.setText("Mật khẩu và xác nhận mật khẩu không khớp.");
            return;
        }

        // Check user existence
        Account existing = accountDAO.findByUsername(username);
        if (existing != null) {
            lblError.setText("Tên đăng nhập đã tồn tại trong hệ thống.");
            return;
        }

        try {
            Account account = new Account();
            account.setFullName(fullName);
            account.setUsername(username);
            account.setEmail(email);
            account.setPhone(phone);
            account.setPassword(password); // In a real app we would hash this
            account.setRole("ROLE_USER"); // Default role

            accountDAO.save(account);

            lblError.setTextFill(Color.web("#10b981")); // green for success
            lblError.setText("Đăng ký thành công! Đang chuyển hướng...");
            
            // Switch to login after successful register
            handleBackToLogin();
        } catch (Exception ex) {
            ex.printStackTrace();
            lblError.setTextFill(Color.web("#ef4444"));
            lblError.setText("Có lỗi xảy ra trong quá trình đăng ký. Vui lòng thử lại.");
        }
    }

    @FXML
    private void handleBackToLogin() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/mycompany/quanlydienmay/login.fxml"));
            Parent root = loader.load();

            Stage stage = (Stage) btnRegister.getScene().getWindow();
            Scene scene = new Scene(root, 1200, 760);
            
            var cssResource = getClass().getResource("/com/mycompany/quanlydienmay/app.css");
            if (cssResource != null) {
                scene.getStylesheets().add(cssResource.toExternalForm());
            }

            stage.setScene(scene);
            stage.setTitle("Đăng nhập - Quản Lý Điện Máy");
        } catch (Exception e) {
            e.printStackTrace();
            lblError.setText("Lỗi mở màn hình đăng nhập.");
        }
    }
}
