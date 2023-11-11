package com.Heart2Hub.Heart2Hub_Backend.util;

import com.Heart2Hub.Heart2Hub_Backend.entity.Staff;
import com.Heart2Hub.Heart2Hub_Backend.enumeration.StaffRoleEnum;
import com.Heart2Hub.Heart2Hub_Backend.service.StaffService;
import jakarta.servlet.http.HttpSession;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
public class TestAuthenticationUtil {

    private final AuthenticationManager authenticationManager;
    private final StaffService staffService;

    public TestAuthenticationUtil(AuthenticationManager authenticationManager, StaffService staffService) {
        this.authenticationManager = authenticationManager;
        this.staffService = staffService;
    }

    public Authentication authenticateUser(String username, String password) {
        // Create staff data
        Staff admin = new Staff(username, password, "Elgin", "Chan", 97882145L,
                StaffRoleEnum.ADMIN, true);
        Staff superAdmin = staffService.createSuperAdmin(admin);
        System.out.println("ASDASD" + superAdmin);

        // Set auth context using the provided username and password
        Authentication auth = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(username, password));
        SecurityContext sc = SecurityContextHolder.getContext();
        sc.setAuthentication(auth);

        System.out.println("ASDASD" + auth);

        return auth;
    }
}

