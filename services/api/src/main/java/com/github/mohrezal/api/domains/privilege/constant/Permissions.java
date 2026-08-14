package com.github.mohrezal.api.domains.privilege.constant;

public final class Permissions {

    private Permissions() {}

    public static final String BLOG_POSTS_CREATE = "blog.posts.create";
    public static final String BLOG_POSTS_UPDATE = "blog.posts.update";
    public static final String BLOG_POSTS_PUBLISH = "blog.posts.publish";
    public static final String BLOG_POSTS_ARCHIVE = "blog.posts.archive";
    public static final String BLOG_POSTS_UNARCHIVE = "blog.posts.unarchive";
    public static final String BLOG_POSTS_DELETE = "blog.posts.delete";
    public static final String BLOG_POSTS_MODERATE = "blog.posts.moderate";
    public static final String BLOG_STORAGE_UPLOAD = "blog.storage.upload";
    public static final String BLOG_STORAGE_DELETE = "blog.storage.delete";
    public static final String BLOG_STORAGE_LIST = "blog.storage.list";
    public static final String BLOG_STORAGE_PROFILE = "blog.storage.profile";
    public static final String BLOG_STORAGE_MODERATE = "blog.storage.moderate";
    public static final String BLOG_CATEGORIES_CREATE = "blog.categories.create";
    public static final String BLOG_PRIVILEGE_PERMISSIONS_READ = "blog.privilege.permissions.read";
    public static final String BLOG_PRIVILEGE_PERMISSIONS_UPDATE =
            "blog.privilege.permissions.update";
    public static final String BLOG_PRIVILEGE_ROLES_READ = "blog.privilege.roles.read";
    public static final String BLOG_PRIVILEGE_ROLES_CREATE = "blog.privilege.roles.create";
    public static final String BLOG_PRIVILEGE_ROLES_UPDATE = "blog.privilege.roles.update";
    public static final String BLOG_PRIVILEGE_ROLES_DELETE = "blog.privilege.roles.delete";
    public static final String BLOG_PRIVILEGE_USERS_ASSIGN_ROLES =
            "blog.privilege.users.assign-roles";
}
