package com.deepprotech.deepproject.common.security;

import java.util.Set;

public enum AppRole {
    SYSTEM_ADMIN(Set.of(
            Permission.SYSTEM_ADMIN,
            Permission.ORG_CREATE, Permission.ORG_READ, Permission.ORG_UPDATE, Permission.ORG_DELETE, Permission.ORG_MEMBER_MANAGE,
            Permission.WORKSPACE_CREATE, Permission.WORKSPACE_READ, Permission.WORKSPACE_UPDATE, Permission.WORKSPACE_DELETE, Permission.WORKSPACE_MEMBER_MANAGE,
            Permission.PROJECT_CREATE, Permission.PROJECT_READ, Permission.PROJECT_UPDATE, Permission.PROJECT_DELETE, Permission.PROJECT_MEMBER_MANAGE,
            Permission.TASK_CREATE, Permission.TASK_READ, Permission.TASK_UPDATE, Permission.TASK_DELETE, Permission.TASK_ASSIGN, Permission.TASK_STATUS_CHANGE,
            Permission.COMMENT_CREATE, Permission.COMMENT_READ, Permission.COMMENT_UPDATE_OWN, Permission.COMMENT_DELETE_OWN, Permission.COMMENT_DELETE_ANY,
            Permission.NOTIFICATION_READ, Permission.NOTIFICATION_MANAGE
    )),

    ORGANIZATION_ADMIN(Set.of(
            Permission.ORG_READ, Permission.ORG_UPDATE, Permission.ORG_MEMBER_MANAGE,
            Permission.WORKSPACE_CREATE, Permission.WORKSPACE_READ, Permission.WORKSPACE_UPDATE, Permission.WORKSPACE_DELETE, Permission.WORKSPACE_MEMBER_MANAGE,
            Permission.PROJECT_CREATE, Permission.PROJECT_READ, Permission.PROJECT_UPDATE, Permission.PROJECT_DELETE, Permission.PROJECT_MEMBER_MANAGE,
            Permission.TASK_CREATE, Permission.TASK_READ, Permission.TASK_UPDATE, Permission.TASK_DELETE, Permission.TASK_ASSIGN, Permission.TASK_STATUS_CHANGE,
            Permission.COMMENT_CREATE, Permission.COMMENT_READ, Permission.COMMENT_UPDATE_OWN, Permission.COMMENT_DELETE_OWN, Permission.COMMENT_DELETE_ANY,
            Permission.NOTIFICATION_READ, Permission.NOTIFICATION_MANAGE
    )),

    ORGANIZATION_MEMBER(Set.of(
            Permission.ORG_READ,
            Permission.WORKSPACE_READ,
            Permission.PROJECT_READ,
            Permission.TASK_READ,
            Permission.COMMENT_READ,
            Permission.NOTIFICATION_READ
    )),

    WORKSPACE_ADMIN(Set.of(
            Permission.WORKSPACE_READ, Permission.WORKSPACE_UPDATE, Permission.WORKSPACE_MEMBER_MANAGE,
            Permission.PROJECT_CREATE, Permission.PROJECT_READ, Permission.PROJECT_UPDATE, Permission.PROJECT_DELETE, Permission.PROJECT_MEMBER_MANAGE,
            Permission.TASK_CREATE, Permission.TASK_READ, Permission.TASK_UPDATE, Permission.TASK_DELETE, Permission.TASK_ASSIGN, Permission.TASK_STATUS_CHANGE,
            Permission.COMMENT_CREATE, Permission.COMMENT_READ, Permission.COMMENT_UPDATE_OWN, Permission.COMMENT_DELETE_OWN, Permission.COMMENT_DELETE_ANY,
            Permission.NOTIFICATION_READ, Permission.NOTIFICATION_MANAGE
    )),

    PROJECT_MANAGER(Set.of(
            Permission.PROJECT_READ, Permission.PROJECT_UPDATE, Permission.PROJECT_MEMBER_MANAGE,
            Permission.TASK_CREATE, Permission.TASK_READ, Permission.TASK_UPDATE, Permission.TASK_DELETE, Permission.TASK_ASSIGN, Permission.TASK_STATUS_CHANGE,
            Permission.COMMENT_CREATE, Permission.COMMENT_READ, Permission.COMMENT_UPDATE_OWN, Permission.COMMENT_DELETE_OWN, Permission.COMMENT_DELETE_ANY,
            Permission.NOTIFICATION_READ
    )),

    CONTRIBUTOR(Set.of(
            Permission.PROJECT_READ,
            Permission.TASK_READ, Permission.TASK_CREATE, Permission.TASK_UPDATE, Permission.TASK_STATUS_CHANGE,
            Permission.COMMENT_CREATE, Permission.COMMENT_READ, Permission.COMMENT_UPDATE_OWN,
            Permission.NOTIFICATION_READ
    )),

    VIEWER(Set.of(
            Permission.WORKSPACE_READ,
            Permission.PROJECT_READ,
            Permission.TASK_READ,
            Permission.COMMENT_READ,
            Permission.NOTIFICATION_READ
    ));

    private final Set<Permission> permissions;

    AppRole(Set<Permission> permissions) {
        this.permissions = permissions;
    }

    public Set<Permission> getPermissions() {
        return permissions;
    }

    public static AppRole fromName(String roleName) {
        try {
            return AppRole.valueOf(roleName.toUpperCase());
        } catch (IllegalArgumentException e) {
            return null;
        }
    }
}