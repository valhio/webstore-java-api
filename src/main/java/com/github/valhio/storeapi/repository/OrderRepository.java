package com.github.valhio.storeapi.repository;

import com.github.valhio.storeapi.model.Order;
import com.github.valhio.storeapi.model.Product;
import com.github.valhio.storeapi.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OrderRepository extends PagingAndSortingRepository<Order, Long> {

    @Query("select o from Order o order by o.orderDate desc")
    Iterable<Order> findAllOrderByOrderDateDesc();

    @Query("select o from Order o where o.user.userId like %?1%")
    Iterable<Order> findAllByUserId(String userId);

    Iterable<Order> findAllByEmail(String email);

    Iterable<Order> findAllByUserOrderByOrderDateDesc(User user);

//    @Query("select o from Order o where concat(" +
//            "o.id" +
//            ",o.user" +
//            ",o.guestUser" +
//            ",o.orderDate" +
//            ",o.orderStatus" +
//            ",o.orderItems" +
//            ") like %?1%")
//    Page<Order> findAllContaining(String keyword, Pageable paging);

//    Page<Order> findByUserContaining(String user, Pageable paging);
//
//    Page<Order> findByGuestUserContaining(String guestUser, Pageable paging);
//
//    Page<Order> findByOrderDateContaining(String orderDate, Pageable paging);
//
//    Page<Order> findByOrderStatusContaining(String orderStatus, Pageable paging);
//
//    Page<Order> findByOrderItemsContaining(String orderItems, Pageable paging);


}
