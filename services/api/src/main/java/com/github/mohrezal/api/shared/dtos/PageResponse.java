package com.github.mohrezal.api.shared.dtos;

import java.util.List;
import java.util.function.Function;
import org.jspecify.annotations.NonNull;
import org.springframework.data.domain.Page;

public record PageResponse<O>(
        @NonNull List<O> items,
        int pageNumber,
        int pageSize,
        long totalElements,
        int totalPages,
        boolean isFirst,
        boolean isLast,
        boolean isEmpty,
        boolean hasNext,
        boolean hasPrevious) {
    public static <I, O> PageResponse<O> from(
            Page<@NonNull I> page, @NonNull Function<I, O> mapper) {
        return new PageResponse<>(
                page.getContent().stream().map(mapper).toList(),
                page.getNumber(),
                page.getSize(),
                page.getTotalElements(),
                page.getTotalPages(),
                page.isFirst(),
                page.isLast(),
                page.isEmpty(),
                page.hasNext(),
                page.hasPrevious());
    }
}
