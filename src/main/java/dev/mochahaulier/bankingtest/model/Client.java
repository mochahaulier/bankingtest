package dev.mochahaulier.bankingtest.model;

import lombok.Data;
import jakarta.persistence.*;
import java.util.Set;

@Entity
@Data
public class Client {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private String email;

    @OneToMany(mappedBy = "client")
    private Set<ClientProduct> clientProducts;
}
