package com.github.valhio.storeapi.model;

import com.github.valhio.storeapi.enumeration.OrderStatus;
import com.github.valhio.storeapi.enumeration.PaymentMethod;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "orders")
public class Order extends Auditable<String> {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    private String firstName;

    private String lastName;

    private String email;

    private String phone;

    private String address;

    private String notes;

    private String city;

    private String zipCode;

    private String country;

    private LocalDateTime orderDate;

    private OrderStatus orderStatus;

    private PaymentMethod paymentMethod;

    private Double productsTotal;

    private Double deliveryFee;

    private Double discount;

    private Double totalAmount;

    private LocalDateTime deliveredDate;

//    @ManyToOne
//    @JoinColumn(name = "guest_user_id")
//    private GuestUser guestUser;

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL)
    private List<OrderItem> orderItems;

}
