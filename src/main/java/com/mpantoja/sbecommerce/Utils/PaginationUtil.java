package com.mpantoja.sbecommerce.Utils;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

public class PaginationUtil {

    public static Pageable buildPageRequest(int page, int size, String sortBy, String sortOrder){
        Sort sort = sortOrder.equalsIgnoreCase("asc")
                ?Sort.by(sortBy).ascending()
                :Sort.by(sortBy).descending();
        return PageRequest.of(page,size,sort);
    }
}
