package com.github.mohrezal.api.domains.posts.services.postutils;

import com.github.mohrezal.api.domains.posts.models.Post;
import com.github.mohrezal.api.domains.users.models.User;

public interface PostUtilsService {

    boolean isOwner(Post post, User user);
}
