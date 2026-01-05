package com.github.mohrezal.springbootblogrestapi.shared.dtos;

import java.util.List;
import java.util.function.Function;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.domain.Page;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PageResponse<O> {
    private List<O> items;
    private int pageNumber;
    private int pageSize;
    private long totalElements;
    private int totalPages;
    private boolean isFirst;
    private boolean isLast;
    private boolean isEmpty;
    private boolean hasNext;
    private boolean hasPrevious;

    public static <I, O> PageResponse<O> from(Page<I> page, Function<I, O> mapper) {
        return PageResponse.<O>builder()
                .items(page.getContent().stream().map(mapper).toList())
                .pageNumber(page.getNumber())
                .pageSize(page.getSize())
                .totalElements(page.getTotalElements())
                .totalPages(page.getTotalPages())
                .isFirst(page.isFirst())
                .isLast(page.isLast())
                .isEmpty(page.isEmpty())
                .hasNext(page.hasNext())
                .hasPrevious(page.hasPrevious())
                .build();
    }
}
