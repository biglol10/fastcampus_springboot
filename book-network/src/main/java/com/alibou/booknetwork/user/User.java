package com.alibou.booknetwork.user;

import com.alibou.booknetwork.book.Book;
import com.alibou.booknetwork.history.BookTransactionHistory;
import com.alibou.booknetwork.role.Role;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.security.Principal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

// When you want to use this User class in the spring security, you need to implement UserDetails interface
// security context holder or authentication needs principal so you can implement Principal interface

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "_user") // user is a reserved keyword in SQL
@EntityListeners(AuditingEntityListener.class)
public class User implements UserDetails, Principal {
    @Id
    @GeneratedValue
    private Integer id;

    private String firstname;
    private String lastname;
    private LocalDate dateOfBirth;
    @Column(unique = true)
    private String email;
    private String password;
    private boolean accountLocked;
    private boolean enabled;

    @ManyToMany(fetch = FetchType.EAGER) // EAGER means that it will load all the roles when the user is loaded
    private List<Role> roles; // if you want to use roles

    @OneToMany(mappedBy = "owner") // mappedBy is the field name in the Book class. User can have many books. mappedby is necessary when using one to many
    private List<Book> books;

    @OneToMany(mappedBy = "user")
    private List<BookTransactionHistory> histories;

    @CreatedDate // to make automatically audited
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdDate;

    @LastModifiedDate
    @Column(insertable = false) // insertable = false means that this field will not be inserted into the database
    private LocalDateTime lastModifiedDate;

    @Override
    public String getName() {
        return email; // since it is unique identifier of user
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return this.roles
                .stream()
                .map(r -> new SimpleGrantedAuthority(r.getName())) // role name should be prefixed with ROLE_ to be recognized by spring security
                .collect(Collectors.toList());
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return email;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return !accountLocked;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return enabled;
    }

    public String fullName() {
        return firstname + " " + lastname;
    }
}
