package sqldiff.spring.boot.advanced.sample.data.domain;

import com.scentbird.hurma.hibernate.annotation.Comment;
import com.vladmihalcea.hibernate.type.array.EnumArrayType;
import com.vladmihalcea.hibernate.type.array.internal.AbstractArrayType;
import org.hibernate.annotations.Parameter;
import org.hibernate.annotations.Type;
import org.hibernate.annotations.TypeDef;
import org.hibernate.annotations.TypeDefs;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import sqldiff.spring.boot.advanced.sample.data.model.Authority;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Index;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import javax.persistence.Version;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

import static sqldiff.spring.boot.advanced.sample.data.model.Authority.ROLE_USER;

@Entity
@Table(
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_account_username",
                        columnNames = {"username"}
                )
        },
        indexes = {
                @Index(
                        name = "ix_account_email",
                        columnList = "email"
                )
        }
)
@TypeDefs({
        @TypeDef(
                name = "authorities",
                typeClass = EnumArrayType.class,
                defaultForType = Authority[].class,
                parameters = {
                        @Parameter(
                                name = AbstractArrayType.SQL_ARRAY_TYPE,
                                value = Authority.COLUMN_DEFINITION
                        )
                }
        )
})
@EntityListeners({AuditingEntityListener.class})
public class Account {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @CreatedDate
    @Column(columnDefinition = "timestamp without time zone NOT NULL DEFAULT now()")
    private LocalDateTime createdDate;

    @LastModifiedDate
    @Column(columnDefinition = "timestamp without time zone NOT NULL DEFAULT now()")
    private LocalDateTime lastModifiedDate;

    @Version
    @Column(columnDefinition = "BIGINT NOT NULL DEFAULT 0")
    private Long version;

    @Comment("Customer's username")
    @Column(nullable = false)
    private String username;

    @Comment(deprecated = true, value = "Use username field instead")
    @Column
    private String login;

    @Comment("Customer's password")
    @Column(nullable = false)
    private String password;

    private String email;

    @Type(type = "authorities")
    @Column(columnDefinition = Authority.COLUMN_DEFINITION + "[]")
    @NotNull
    private Authority[] roles = new Authority[]{ROLE_USER};

    @Column(name = "registration_date", columnDefinition = "timestamp without time zone NOT NULL DEFAULT now()")
    @NotNull
    private LocalDateTime registrationDate;

    public Long getId() {
        return id;
    }

    protected void setId(Long id) {
        this.id = id;
    }

    public LocalDateTime getCreatedDate() {
        return createdDate;
    }

    public LocalDateTime getLastModifiedDate() {
        return lastModifiedDate;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Authority[] getRoles() {
        return roles;
    }

    public void setRoles(Authority[] roles) {
        this.roles = roles;
    }

    public Long getVersion() {
        return version;
    }

}
