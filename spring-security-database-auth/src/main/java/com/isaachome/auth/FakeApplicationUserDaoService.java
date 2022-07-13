package com.isaachome.auth;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Repository;

import com.google.common.collect.Lists;
import com.isaachome.config.AppUserRoles;

@Repository("fake")
public class FakeApplicationUserDaoService implements ApplicationUserDao{

	 private final PasswordEncoder passwordEncoder;

	    @Autowired
	    public FakeApplicationUserDaoService(PasswordEncoder passwordEncoder) {
	        this.passwordEncoder = passwordEncoder;
	    }

	    @Override
	    public Optional<ApplicationUser> selectApplicationUserByUsername(String username) {
	        return getApplicationUsers()
	                .stream()
	                .filter(applicationUser -> username.equals(applicationUser.getUsername()))
	                .findFirst();
	    }

	    private List<ApplicationUser> getApplicationUsers() {
	        List<ApplicationUser> applicationUsers = Lists.newArrayList(
	                new ApplicationUser(
	                        "annasmith",
	                        passwordEncoder.encode("password"),
	                        AppUserRoles.STUDENT.getGrantedAuthorities(),
	                        true,
	                        true,
	                        true,
	                        true
	                ),
	                new ApplicationUser(
	                        "linda",
	                        passwordEncoder.encode("password"),
	                        AppUserRoles.ADMIN.getGrantedAuthorities(),
	                        true,
	                        true,
	                        true,
	                        true
	                ),
	                new ApplicationUser(
	                        "tom",
	                        passwordEncoder.encode("password"),
	                        AppUserRoles.ADMINTRAINEE.getGrantedAuthorities(),
	                        true,
	                        true,
	                        true,
	                        true
	                )
	        );

	        return applicationUsers;
	    }

}
