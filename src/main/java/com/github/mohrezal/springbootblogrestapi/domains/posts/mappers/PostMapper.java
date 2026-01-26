package com.github.mohrezal.springbootblogrestapi.domains.posts.mappers;

import com.github.mohrezal.springbootblogrestapi.domains.categories.mappers.CategoryMapper;
import com.github.mohrezal.springbootblogrestapi.domains.posts.dtos.CreatePostRequest;
import com.github.mohrezal.springbootblogrestapi.domains.posts.dtos.PostDetail;
import com.github.mohrezal.springbootblogrestapi.domains.posts.dtos.PostSummary;
import com.github.mohrezal.springbootblogrestapi.domains.posts.dtos.UpdatePostRequest;
import com.github.mohrezal.springbootblogrestapi.domains.posts.models.Post;
import com.github.mohrezal.springbootblogrestapi.domains.storage.mappers.StorageMapper;
import com.github.mohrezal.springbootblogrestapi.domains.users.mappers.UserMapper;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(
        componentModel = "spring",
        uses = {UserMapper.class, CategoryMapper.class, StorageMapper.class})
public interface PostMapper {

    @Mapping(source = "user", target = "author")
    PostSummary toPostSummary(Post post);

    @Mapping(source = "user", target = "author")
    PostDetail toPostDetail(Post post);

    @Mapping(target = "user", ignore = true)
    @Mapping(target = "categories", ignore = true)
    @Mapping(target = "slug", ignore = true)
    @Mapping(target = "status", ignore = true)
    @Mapping(target = "publishedAt", ignore = true)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "version", ignore = true)
    Post toPost(CreatePostRequest createPostRequest);

    @Mapping(target = "user", ignore = true)
    @Mapping(target = "categories", ignore = true)
    @Mapping(target = "status", ignore = true)
    @Mapping(target = "publishedAt", ignore = true)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "version", ignore = true)
    void toTargetPost(UpdatePostRequest request, @MappingTarget Post post);
}
