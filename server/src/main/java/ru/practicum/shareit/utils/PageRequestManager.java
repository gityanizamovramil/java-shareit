package ru.practicum.shareit.utils;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import ru.practicum.shareit.common.PaginationException;

public class PageRequestManager {

    public static PageRequest form(int from, int size, Sort.Direction direction, String properties
    ) throws PaginationException {
        if (from < 0) throw new PaginationException("paging invalid");
        if (size <= 0) throw new PaginationException("paging invalid");
        Sort sort = Sort.by(direction, properties);
        return PageRequest.of(from / size, size, sort);
    }

}
