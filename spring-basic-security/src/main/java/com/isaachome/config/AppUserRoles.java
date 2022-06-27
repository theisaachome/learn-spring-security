package com.isaachome.config;
import java.util.Set;
import com.google.common.collect.Sets;

public enum AppUserRoles {
	STUDENT(Sets.newHashSet()),
	ADMIN(Sets.newHashSet(AppUserPermission.COURSE_READ,
			AppUserPermission.COURSE_WRITE,
			AppUserPermission.STUDENT_READ,
			AppUserPermission.STUDENT_WRITE));
	
	private final Set<AppUserPermission> permissions;

	private AppUserRoles(Set<AppUserPermission> permissions) {
		this.permissions = permissions;
	}

	public Set<AppUserPermission> getPermissions() {
		return permissions;
	}
	
	
}
