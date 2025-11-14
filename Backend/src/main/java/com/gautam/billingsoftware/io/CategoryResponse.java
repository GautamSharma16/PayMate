package com.gautam.billingsoftware.io;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class CategoryResponse {
    private String categoryId;
    private String name;
    private String description;
    private String bgColor;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss") // optional formatting
    private LocalDateTime createdAt;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss") // optional formatting
    private LocalDateTime updatedAt;

    private String imgUrl;
    private Integer items;
}
