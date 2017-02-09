package com.daqula.carmore.repository;

import com.daqula.carmore.model.order.Comment;
import com.daqula.carmore.model.shop.Shop;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.UUID;

public interface CommentRepository extends PagingAndSortingRepository<Comment, Long> {
    Page<Comment> findByShop(Shop shop, Pageable pageable);

    Comment findBySettleOrderUid(UUID uid);
}
