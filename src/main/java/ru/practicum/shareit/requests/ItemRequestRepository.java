package ru.practicum.shareit.requests;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.shareit.requests.model.ItemRequest;

import java.util.List;
import java.util.Optional;

public interface ItemRequestRepository extends JpaRepository<ItemRequest, Long> {

    List<ItemRequest> findAllByRequestor_IdOrderByCreatedDesc(Long userId);

    Optional<ItemRequest> findItemRequestById(Long id);

    List<ItemRequest> findAllByRequestor_IdIsNot(Long userId, PageRequest pageRequest, Sort sort);

}
