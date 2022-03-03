package com.quickwork.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import javax.persistence.*;
import java.util.Collection;
import java.util.List;

@Entity
@Table(name = "user", schema = "qw_dta")
@Getter
@Setter
@NoArgsConstructor
public class User implements UserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String username;
    private String email;
    private String password;
    private String role;
    private String phoneNumber;

    @OneToMany(mappedBy = "user")
    List<Ad> ads;


    @OneToMany(mappedBy = "user")
    List<Review> reviews;

    //TODO change this to one to one
    @OneToMany(mappedBy = "user")
    List<ProfilePic> profilePic;

    public User(String username, String email, String encodedPassword, String roleCode, String phoneNumber) {
        this.username = username;
        this.email = email;
        this.password = encodedPassword;
        this.role = roleCode;
        this.phoneNumber = phoneNumber;

    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return null;
    }

    @Override
    public boolean isAccountNonExpired() {
        return false;
    }

    @Override
    public boolean isAccountNonLocked() {
        return false;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return false;
    }

    @Override
    public boolean isEnabled() {
        return false;
    }
}
