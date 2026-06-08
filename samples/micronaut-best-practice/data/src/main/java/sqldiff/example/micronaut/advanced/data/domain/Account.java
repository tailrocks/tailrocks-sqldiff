package sqldiff.example.micronaut.advanced.data.domain;

import com.scentbird.hurma.hibernate.annotation.Comment;
import io.hypersistence.utils.hibernate.type.array.EnumArrayType;
import sqldiff.example.micronaut.advanced.data.model.Authority;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.Parameter;
import org.hibernate.annotations.Type;
import org.hibernate.annotations.UpdateTimestamp;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import jakarta.persistence.Version;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;

import static sqldiff.example.micronaut.advanced.data.model.Authority.ROLE_USER;

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
public class Account {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @CreationTimestamp
    @Column(columnDefinition = "timestamp without time zone NOT NULL DEFAULT now()")
    private LocalDateTime createdDate;

    @UpdateTimestamp
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

    @Type(value = EnumArrayType.class, parameters = @Parameter(name = "sql_array_type", value = "authority"))
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
