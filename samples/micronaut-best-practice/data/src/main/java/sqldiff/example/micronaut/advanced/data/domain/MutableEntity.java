package sqldiff.example.micronaut.advanced.data.domain;

import edu.umd.cs.findbugs.annotations.Nullable;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;
import javax.persistence.Transient;
import javax.persistence.Version;
import java.time.LocalDateTime;

@MappedSuperclass
public abstract class MutableEntity {

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


    /**
     * We use {@link GenerationType#IDENTITY} to have separate sequence per entity.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @CreationTimestamp
    @Column(columnDefinition = "timestamp without time zone NOT NULL DEFAULT now()")
    private LocalDateTime createdDate;

    @Nullable
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
     */
    @Transient
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

        // TODO support for hibernate proxy
        if (!getClass().equals(obj.getClass())) {
            return false;
        }

        MutableEntity that = (MutableEntity) obj;

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
