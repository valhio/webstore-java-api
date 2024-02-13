package com.github.valhio.storeapi.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Objects;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "favorites")
public class Favorite {

    @Id
    private String id;

    @DBRef
    private User user;

    @DBRef
    private Product product;

    public Favorite(User user, Product product) {
        this.user = user;
        this.product = product;
    }

    // Equals and HashCode based on user and product
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Favorite favorite = (Favorite) o;
        return Objects.equals(user, favorite.user) && Objects.equals(product, favorite.product);
    }

    @Override
    public int hashCode() {
        return Objects.hash(user, product);
    }
}
