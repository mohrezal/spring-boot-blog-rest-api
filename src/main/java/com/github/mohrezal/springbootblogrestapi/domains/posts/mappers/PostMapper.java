package com.github.mohrezal.springbootblogrestapi.domains.posts.mappers;

import com.github.mohrezal.springbootblogrestapi.domains.categories.mappers.CategoryMapper;
import com.github.mohrezal.springbootblogrestapi.domains.posts.dtos.PostDetail;
import com.github.mohrezal.springbootblogrestapi.domains.posts.dtos.PostSummary;
import com.github.mohrezal.springbootblogrestapi.domains.posts.models.Post;
import com.github.mohrezal.springbootblogrestapi.domains.users.mappers.UserMapper;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(
        componentModel = "spring",
        uses = {UserMapper.class, CategoryMapper.class})
public interface PostMapper {

    @Mapping(source = "user", target = "author")
    PostSummary toPostSummary(Post post);

    @Mapping(source = "user", target = "author")
    PostDetail toPostDetail(Post post);
}
