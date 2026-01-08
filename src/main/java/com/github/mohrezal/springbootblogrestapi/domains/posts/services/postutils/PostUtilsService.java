package com.github.mohrezal.springbootblogrestapi.domains.posts.services.postutils;

import com.github.mohrezal.springbootblogrestapi.domains.posts.models.Post;
import com.github.mohrezal.springbootblogrestapi.domains.users.models.User;

public interface PostUtilsService {

    boolean isOwner(Post post, User user);
}
