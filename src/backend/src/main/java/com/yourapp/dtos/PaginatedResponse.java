package com.yourapp.dtos;

import java.util.List;

public record PaginatedResponse<T>(
        List<T> data,
        String nextCursor,
        boolean hasMore
) {
}
