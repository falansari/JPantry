package com.ga.jpantry.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "categories")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString(exclude = {"item"})
public class Category {
    @Id
    @Column
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String name;

    /**
     * Default expiry period of the category.
     * User can set custom expiry period for an item, or use the item's category's default period.
     */
    @Column(nullable = false)
    private Integer defaultExpiryPeriodDays;

    @JsonIgnore
    @OneToOne(mappedBy = "category")
    private Item item;

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(nullable = false)
    private LocalDateTime updatedAt;

    @PrePersist
    public void prePersist() {
        if (defaultExpiryPeriodDays == null) defaultExpiryPeriodDays = 7; // Default days if not set for category by the user
    }
}
