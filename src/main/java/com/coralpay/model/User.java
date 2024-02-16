package com.coralpay.model;

import com.coralpay.enums.UserCategory;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;
import org.hibernate.proxy.HibernateProxy;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.Objects;


@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
@Entity
@JsonInclude(JsonInclude.Include.NON_NULL)
@Table(name = "user_dt")
public class User extends CoreEntity{


//    private Long loginAttempts;
    private int loginAttempts;
    private LocalDateTime failedLoginDate;
    private LocalDateTime lastLogin;
    private String password;
    private String passwordExpiration;
    private Date lockedDate;
    private String firstName;
    private String lastName;
    private String middleName;
    private String username;
    private LocalDateTime passwordChangedOn;
    @Transient
    private boolean loginStatus;
    private String email;
    private String phone;
    @Enumerated(value = EnumType.STRING)
    private UserCategory userCategory;
    private String resetToken;
    private String resetTokenExpirationDate;

    private String address;


    @Transient
    private boolean accountNonLocked;
    @Transient
    private String registrationToken;
    @Transient
    private String registrationTokenExpiration;
    @Transient
    private Boolean isEmailVerified ;

    @Override
    public final boolean equals(Object o) {
        if (this == o) return true;
        if (o == null) return false;
        Class<?> oEffectiveClass = o instanceof HibernateProxy ? ((HibernateProxy) o).getHibernateLazyInitializer().getPersistentClass() : o.getClass();
        Class<?> thisEffectiveClass = this instanceof HibernateProxy ? ((HibernateProxy) this).getHibernateLazyInitializer().getPersistentClass() : this.getClass();
        if (thisEffectiveClass != oEffectiveClass) return false;
        User user = (User) o;
        return getId() != null && Objects.equals(getId(), user.getId());
    }

    @Override
    public final int hashCode() {
        return this instanceof HibernateProxy ? ((HibernateProxy) this).getHibernateLazyInitializer().getPersistentClass().hashCode() : getClass().hashCode();
    }
}
