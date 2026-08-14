package com.github.mohrezal.api.domains.privilege.constant;

import java.util.List;

public final class PermissionCatalog {

    private static final String BLOG_SERVICE = "blog";

    private PermissionCatalog() {}

    private static final Definition BLOG_POSTS_CREATE =
            new Definition(Permissions.BLOG_POSTS_CREATE, "Create posts", BLOG_SERVICE);
    private static final Definition BLOG_POSTS_UPDATE =
            new Definition(Permissions.BLOG_POSTS_UPDATE, "Update posts", BLOG_SERVICE);
    private static final Definition BLOG_POSTS_PUBLISH =
            new Definition(Permissions.BLOG_POSTS_PUBLISH, "Publish posts", BLOG_SERVICE);
    private static final Definition BLOG_POSTS_ARCHIVE =
            new Definition(Permissions.BLOG_POSTS_ARCHIVE, "Archive posts", BLOG_SERVICE);
    private static final Definition BLOG_POSTS_UNARCHIVE =
            new Definition(Permissions.BLOG_POSTS_UNARCHIVE, "Unarchive posts", BLOG_SERVICE);
    private static final Definition BLOG_POSTS_DELETE =
            new Definition(Permissions.BLOG_POSTS_DELETE, "Delete posts", BLOG_SERVICE);
    private static final Definition BLOG_POSTS_MODERATE =
            new Definition(Permissions.BLOG_POSTS_MODERATE, "Moderate posts", BLOG_SERVICE);
    private static final Definition BLOG_STORAGE_UPLOAD =
            new Definition(Permissions.BLOG_STORAGE_UPLOAD, "Upload storage", BLOG_SERVICE);
    private static final Definition BLOG_STORAGE_DELETE =
            new Definition(Permissions.BLOG_STORAGE_DELETE, "Delete storage", BLOG_SERVICE);
    private static final Definition BLOG_STORAGE_LIST =
            new Definition(Permissions.BLOG_STORAGE_LIST, "List storage", BLOG_SERVICE);
    private static final Definition BLOG_STORAGE_PROFILE =
            new Definition(Permissions.BLOG_STORAGE_PROFILE, "Profile storage", BLOG_SERVICE);
    private static final Definition BLOG_STORAGE_MODERATE =
            new Definition(Permissions.BLOG_STORAGE_MODERATE, "Moderate storage", BLOG_SERVICE);
    private static final Definition BLOG_CATEGORIES_CREATE =
            new Definition(Permissions.BLOG_CATEGORIES_CREATE, "Create categories", BLOG_SERVICE);
    private static final Definition BLOG_PRIVILEGE_PERMISSIONS_READ =
            new Definition(
                    Permissions.BLOG_PRIVILEGE_PERMISSIONS_READ,
                    "Read privilege permissions",
                    BLOG_SERVICE);
    private static final Definition BLOG_PRIVILEGE_PERMISSIONS_UPDATE =
            new Definition(
                    Permissions.BLOG_PRIVILEGE_PERMISSIONS_UPDATE,
                    "Update privilege permissions",
                    BLOG_SERVICE);
    private static final Definition BLOG_PRIVILEGE_ROLES_READ =
            new Definition(
                    Permissions.BLOG_PRIVILEGE_ROLES_READ, "Read privilege roles", BLOG_SERVICE);
    private static final Definition BLOG_PRIVILEGE_ROLES_CREATE =
            new Definition(
                    Permissions.BLOG_PRIVILEGE_ROLES_CREATE,
                    "Create privilege roles",
                    BLOG_SERVICE);
    private static final Definition BLOG_PRIVILEGE_ROLES_UPDATE =
            new Definition(
                    Permissions.BLOG_PRIVILEGE_ROLES_UPDATE,
                    "Update privilege roles",
                    BLOG_SERVICE);
    private static final Definition BLOG_PRIVILEGE_ROLES_DELETE =
            new Definition(
                    Permissions.BLOG_PRIVILEGE_ROLES_DELETE,
                    "Delete privilege roles",
                    BLOG_SERVICE);
    private static final Definition BLOG_PRIVILEGE_USERS_ASSIGN_ROLES =
            new Definition(
                    Permissions.BLOG_PRIVILEGE_USERS_ASSIGN_ROLES,
                    "Assign privilege roles to users",
                    BLOG_SERVICE);

    public static final List<Definition> USER =
            List.of(
                    BLOG_POSTS_CREATE,
                    BLOG_POSTS_UPDATE,
                    BLOG_POSTS_PUBLISH,
                    BLOG_POSTS_ARCHIVE,
                    BLOG_POSTS_UNARCHIVE,
                    BLOG_POSTS_DELETE,
                    BLOG_STORAGE_UPLOAD,
                    BLOG_STORAGE_DELETE,
                    BLOG_STORAGE_LIST,
                    BLOG_STORAGE_PROFILE);

    public static final List<Definition> ALL =
            List.of(
                    BLOG_POSTS_CREATE,
                    BLOG_POSTS_UPDATE,
                    BLOG_POSTS_PUBLISH,
                    BLOG_POSTS_ARCHIVE,
                    BLOG_POSTS_UNARCHIVE,
                    BLOG_POSTS_DELETE,
                    BLOG_STORAGE_UPLOAD,
                    BLOG_STORAGE_DELETE,
                    BLOG_STORAGE_LIST,
                    BLOG_STORAGE_PROFILE,
                    BLOG_POSTS_MODERATE,
                    BLOG_STORAGE_MODERATE,
                    BLOG_CATEGORIES_CREATE,
                    BLOG_PRIVILEGE_PERMISSIONS_READ,
                    BLOG_PRIVILEGE_PERMISSIONS_UPDATE,
                    BLOG_PRIVILEGE_ROLES_READ,
                    BLOG_PRIVILEGE_ROLES_CREATE,
                    BLOG_PRIVILEGE_ROLES_UPDATE,
                    BLOG_PRIVILEGE_ROLES_DELETE,
                    BLOG_PRIVILEGE_USERS_ASSIGN_ROLES);

    public record Definition(String key, String name, String service) {}
}
