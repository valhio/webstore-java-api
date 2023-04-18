package com.github.valhio.storeapi.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.github.valhio.storeapi.enumeration.OrderStatus;
import com.github.valhio.storeapi.enumeration.PaymentMethod;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

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

    private String orderId;

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

    @OneToOne(mappedBy = "order", cascade = CascadeType.ALL)
    @JsonIgnoreProperties("order")
    private Invoice invoice;

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL)
    private List<OrderItem> orderItems;

    @PostPersist // This method will be executed after the entity is persisted
    public void generateOrderId() {
        String prefix = "KB";
        String date = LocalDate.now().format(DateTimeFormatter.ofPattern("yyMMdd"));
//        String dateTimeString = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyMMddss"));
        int randomNum = ThreadLocalRandom.current().nextInt(100, 999);
        this.orderId = prefix + date + randomNum + this.id;
    }
}
