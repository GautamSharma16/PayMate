package com.gautam.billingsoftware.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import java.time.LocalDateTime;

@Entity
@Table(name = "tbl_category")
@Data
@NoArgsConstructor                      // ✅ Needed by JPA
@AllArgsConstructor                     // ✅ Needed for @Builder
@Builder                                 // ✅ Works with the above constructor
public class CategoryEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String name;

    private String description;

    @Column(unique = true, nullable = false)
    private String categoryId;

    private String bgColor;
    private String imgUrl;

    @CreationTimestamp
    @Column(updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;
}
