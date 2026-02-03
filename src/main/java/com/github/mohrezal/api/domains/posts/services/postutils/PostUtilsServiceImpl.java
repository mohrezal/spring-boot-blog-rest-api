package com.github.mohrezal.api.domains.posts.services.postutils;

import com.github.mohrezal.api.domains.posts.models.Post;
import com.github.mohrezal.api.domains.users.models.User;
import java.util.Objects;
import org.springframework.stereotype.Service;

@Service
public class PostUtilsServiceImpl implements PostUtilsService {

    @Override
    public boolean isOwner(Post post, User user) {
        if (post == null || user == null || post.getUser() == null) {
            return false;
        }
        return Objects.equals(post.getUser().getId(), user.getId());
    }
}
