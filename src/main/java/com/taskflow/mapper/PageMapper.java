package com.taskflow.mapper;

import com.taskflow.dto.common.PageResponse;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.function.Function;

/**
 * Utility mapper for converting Spring Data Page to PageResponse.
 */
@Component
public class PageMapper {

    /**
     * Convert a Spring Data Page to a PageResponse.
     * @param page the source page
     * @param mapper function to convert page content elements
     * @param <S> source type
     * @param <T> target type
     * @return PageResponse containing converted content
     */
    public <S, T> PageResponse<T> toPageResponse(Page<S> page, Function<S, T> mapper) {
        List<T> content = page.getContent().stream()
                .map(mapper)
                .toList();

        return PageResponse.<T>builder()
                .content(content)
                .pageNumber(page.getNumber())
                .pageSize(page.getSize())
                .totalElements(page.getTotalElements())
                .totalPages(page.getTotalPages())
                .first(page.isFirst())
                .last(page.isLast())
                .empty(page.isEmpty())
                .build();
    }
}
