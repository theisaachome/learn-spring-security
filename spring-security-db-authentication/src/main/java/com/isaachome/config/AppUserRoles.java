package com.isaachome.config;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.security.core.authority.SimpleGrantedAuthority;

import com.google.common.collect.Sets;

public enum AppUserRoles {
	STUDENT(Sets.newHashSet()),
	ADMIN(Sets.newHashSet(AppUserPermission.COURSE_READ,
			AppUserPermission.COURSE_WRITE,
			AppUserPermission.STUDENT_READ,
			AppUserPermission.STUDENT_WRITE)),
	ADMINTRAINEE(Sets.newHashSet(AppUserPermission.COURSE_READ,
			AppUserPermission.STUDENT_READ));
	
	private final Set<AppUserPermission> permissions;

	private AppUserRoles(Set<AppUserPermission> permissions) {
		this.permissions = permissions;
	}

	public Set<AppUserPermission> getPermissions() {
		return permissions;
	}
	
	public Set<SimpleGrantedAuthority> getGrantedAuthorities(){
		var permissions= getPermissions().stream()
		.map((permission)-> new SimpleGrantedAuthority(permission.getPermission()))
		.collect(Collectors.toSet());
		permissions.add(new SimpleGrantedAuthority("ROLE_" + this.name()));
		
		return permissions;
	}
	
}
