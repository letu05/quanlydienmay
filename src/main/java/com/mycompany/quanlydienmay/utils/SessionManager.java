package com.mycompany.quanlydienmay.utils;

import com.mycompany.quanlydienmay.model.Account;

/**
 * Lưu thông tin tài khoản đang đăng nhập trong phiên làm việc.
 */
public class SessionManager {

    private static Account currentUser;

    public static void login(Account account) {
        currentUser = account;
    }

    public static void logout() {
        currentUser = null;
    }

    public static Account getCurrentUser() {
        return currentUser;
    }

    public static boolean isLoggedIn() {
        return currentUser != null;
    }

    public static boolean isAdmin() {
        return currentUser != null && "ADMIN".equalsIgnoreCase(currentUser.getRole());
    }

    public static boolean isCustomer() {
        return currentUser != null && "CUSTOMER".equalsIgnoreCase(currentUser.getRole());
    }
}
