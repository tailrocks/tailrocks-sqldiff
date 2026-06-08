package sqldiff.spring.boot.advanced.sample.data.domain;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.springframework.data.domain.Persistable;
import org.springframework.data.util.ProxyUtils;
import org.springframework.lang.Nullable;

import jakarta.persistence.Column;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.MappedSuperclass;
import jakarta.persistence.Transient;
import jakarta.persistence.Version;
import java.time.LocalDateTime;

@MappedSuperclass
public abstract class MutableEntity implements Persistable<Long> {

    /**
     * We use {@link GenerationType#IDENTITY} to have separate sequence per entity.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @CreationTimestamp
    @Column(columnDefinition = "timestamp without time zone NOT NULL DEFAULT now()")
    private LocalDateTime createdDate;

    /**
     * @see Persistable#getId()
     */
    @Nullable
    @Override
    public Long getId() {
        return id;
    }

    /**
     * Sets the id of the entity. Normally we do not need to override id, it will be automatically retrieved from
     * database during saving this entity.
     *
     * @param id the id to set
     */
    public void setId(@Nullable Long id) {
        this.id = id;
    }

    /**
     * Returns the date when this entity was created.
     *
     * @return the date. Can be {@literal null}.
     */
    @Nullable
    public LocalDateTime getCreatedDate() {
        return createdDate;
    }

    /**
     * Sets the created date of the entity. Normally we do not need to override this date, it will use now date time
     * and the moment when this entity was created.
     *
     * @param createdDate the new date
     */
    public void setCreatedDate(@Nullable LocalDateTime createdDate) {
        this.createdDate = createdDate;
    }

    /**
     * Must be {@link Transient} in order to ensure that no JPA provider complains because of a missing setter.
     *
     * @see Persistable#isNew()
     */
    @Transient
    @Override
    public boolean isNew() {
        return null == getId();
    }

    /**
     * By default we only print entity's simple name and id, to avoid 1+1 queries during processing toString value.
     *
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return getClass().getSimpleName() + "(id=" + this.getId() + ")";
    }

    @UpdateTimestamp
    @Column(columnDefinition = "timestamp without time zone NOT NULL DEFAULT now()")
    private LocalDateTime lastModifiedDate;

    @Version
    @Column(columnDefinition = "BIGINT NOT NULL DEFAULT 0")
    private Long version;

    @Nullable
    public LocalDateTime getLastModifiedDate() {
        return lastModifiedDate;
    }

    public void setLastModifiedDate(@Nullable LocalDateTime lastModifiedDate) {
        this.lastModifiedDate = lastModifiedDate;
    }

    @Nullable
    public Long getVersion() {
        return version;
    }

    public void setVersion(@Nullable Long version) {
        this.version = version;
    }

    /*
     * (non-Javadoc)
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public boolean equals(Object obj) {
        if (null == obj) {
            return false;
        }

        if (this == obj) {
            return true;
        }

        if (!getClass().equals(ProxyUtils.getUserClass(obj))) {
            return false;
        }

        Persistable<?> that = (Persistable<?>) obj;

        return null != this.getId() && this.getId().equals(that.getId());
    }

    /*
     * (non-Javadoc)
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {
        int hashCode = 17;

        hashCode += null == getId() ? 0 : getId().hashCode() * 31;

        return hashCode;
    }

}
