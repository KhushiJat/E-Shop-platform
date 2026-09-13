package com.ecommerce.project.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;
import org.hibernate.annotations.ColumnDefault;
import org.jspecify.annotations.Nullable;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;


@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name="users",
        uniqueConstraints = {
                @UniqueConstraint(columnNames = "username"),
                @UniqueConstraint(columnNames = "email")
        })
@EqualsAndHashCode(onlyExplicitlyIncluded = true) // Prevents deep recursive auto-generation
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    @EqualsAndHashCode.Include // Only use the ID for equals/hashCode safely
    private Long userId;

    @NotBlank
    @Size(max = 20)
    @Column(name="username")
    private String userName;

    @NotBlank
    @Size(max = 50)
    @Email
    @Column(name="email")
    private String email;

    @NotBlank
    @Size(max = 120)
    @Column(name="password")
    private String password;

    @Column(name="enabled", nullable = false)
    private boolean enabled = true;

    public User(String userName, String email, String password){
        this.userName = userName;
        this.email = email;
        this.password = password;
        this.enabled = true;
    }

    @ManyToMany(cascade = {CascadeType.PERSIST, CascadeType.MERGE},
            fetch = FetchType.EAGER)
    @JoinTable(name = "user_role",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "role_id"))
    private Set<Role> roles = new HashSet<>();

    @OneToMany(mappedBy = "user", cascade = {CascadeType.PERSIST, CascadeType.MERGE}, orphanRemoval = true)
    private List<Address> addresses = new ArrayList<>();

    @ToString.Exclude
    @OneToOne(mappedBy = "user", cascade = {CascadeType.PERSIST, CascadeType.MERGE}, orphanRemoval = true)
    private Cart cart;

    @ToString.Exclude
    @OneToMany(mappedBy = "user", cascade = {CascadeType.PERSIST, CascadeType.MERGE},
            orphanRemoval = true)
    private Set<Product> products = new HashSet<>();
}


//@Data
//@NoArgsConstructor
//@Table(name="users",
//        uniqueConstraints = {
//        @UniqueConstraint(columnNames = "username"),
//                @UniqueConstraint(columnNames = "email")
//        })
//@Entity
//public class User {
//    @Id
//    @GeneratedValue(strategy = GenerationType.IDENTITY)
//    @Column(name = "user_id")
//    private Long userId;
//
//    @NotBlank
//    @Size(max = 20)
//    @Column(name="username")
//    private String userName;
//
//    @NotBlank
//    @Size(max = 50)
//    @Email
//    @Column(name="email")
//    private String email;
//
//    @NotBlank
//    @Size(max = 120)
//    @Column(name="password")
//    private String password;
//
//    @Column(name="enabled", nullable = false)
//    private boolean enabled = true;
//
//    public User(String userName, String email, String password){
//        this.userName = userName;
//        this.email = email;
//        this.password = password;
//        this.enabled = true;
//    }
//
//    @Setter
//    @Getter
//    @ManyToMany(cascade = {CascadeType.PERSIST, CascadeType.MERGE},
//            fetch = FetchType.EAGER)
//    @JoinTable(name = "user_role",
//            joinColumns = @JoinColumn(name = "user_id"),
//            inverseJoinColumns = @JoinColumn(name = "role_id"))
//    private Set<Role> roles = new HashSet<>();
//
//    @Getter
//    @Setter
//    @OneToMany(mappedBy = "user", cascade = {CascadeType.PERSIST, CascadeType.MERGE}, orphanRemoval = true)
////    @JoinTable(name="user_address",
////               joinColumns = @JoinColumn(name="user_id"),
////               inverseJoinColumns = @JoinColumn(name = "address_id"))
//
//    private List<Address> addresses = new ArrayList<>();
//
//    @ToString.Exclude
//    @OneToOne(mappedBy = "user", cascade = {CascadeType.PERSIST, CascadeType.MERGE}, orphanRemoval = true)
//    private Cart cart;
//
//    @ToString.Exclude
//    @OneToMany(mappedBy = "user", cascade = {CascadeType.PERSIST, CascadeType.MERGE},
//              orphanRemoval = true)
//    private Set<Product> products;
//}
