// FOR GENERATING HASH FOR A PASSWORD.
// IT'S A TEMPORARY FILE, YOU CAN DELETE AFTER GENERATING A HASH FOR YOUR ADMIN USER.
// OTHERS ROLES CAN BE ADDED DIRECTLY FROM THE SIGN-UP PAGE BY SELECTING THE APPROPRIATE ROLE

package com.jobportal.main;

import org.mindrot.jbcrypt.BCrypt;

public class PasswordUtil {
    public static String hashPassword(String plainPassword) {
        return BCrypt.hashpw(plainPassword, BCrypt.gensalt());
    }

    public static void main(String[] args) {
        String plainPassword = "admin"; // Replace with your desired password
        String hashedPassword = hashPassword(plainPassword);
        System.out.println("Hashed Password: " + hashedPassword);
    }
}