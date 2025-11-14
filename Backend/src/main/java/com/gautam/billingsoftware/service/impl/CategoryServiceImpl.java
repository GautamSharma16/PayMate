package com.gautam.billingsoftware.service.impl;

import com.gautam.billingsoftware.entity.CategoryEntity;
import com.gautam.billingsoftware.io.CategoryRequest;
import com.gautam.billingsoftware.io.CategoryResponse;
import com.gautam.billingsoftware.repository.CategoryRepository;
import com.gautam.billingsoftware.repository.ItemRepository;
import com.gautam.billingsoftware.service.CategoryService;
import com.gautam.billingsoftware.service.FileUploadService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collector;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CategoryServiceImpl implements CategoryService {

    private final CategoryRepository categoryRepository;

    private final FileUploadService fileUploadService;

    private final ItemRepository itemRepository;

    @Override
    public CategoryResponse add(CategoryRequest request, MultipartFile file) {
        if(categoryRepository.existsByName(request.getName())) {
            throw new RuntimeException("Category with name '" + request.getName() + "' already exists");
        }

        String imgUrl = fileUploadService.uploadFile(file);
        CategoryEntity newCategory = convertToEntity(request);
        newCategory.setImgUrl(imgUrl);
        newCategory = categoryRepository.save(newCategory);
        return convertToResponse(newCategory);
    }


    @Override
    public List<CategoryResponse> read() {
      return   categoryRepository.findAll().stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());

    }

    @Override
    public void delete(String categoryId) {
      CategoryEntity existingCategory = categoryRepository.findByCategoryId(categoryId)
                .orElseThrow(()-> new RuntimeException("Category not found"+categoryId));
                 fileUploadService.deleteFile(existingCategory.getImgUrl());
                 categoryRepository.delete(existingCategory);

    }

    private CategoryResponse convertToResponse(CategoryEntity newCategory) {
      Integer itemsCount = itemRepository.countByCategoryId(newCategory.getId());
        return CategoryResponse.builder()
                .categoryId(newCategory.getCategoryId())
                .name(newCategory.getName())
                .description(newCategory.getDescription())
                .bgColor(newCategory.getBgColor())
                .createdAt(newCategory.getCreatedAt())
                .updatedAt(newCategory.getUpdatedAt())
                .imgUrl(newCategory.getImgUrl())
                .items(itemsCount)
                .build();
    }

    private CategoryEntity convertToEntity(CategoryRequest request) {
        return CategoryEntity.builder()
                .categoryId(UUID.randomUUID().toString())
                .name(request.getName())
                .description(request.getDescription())
                .bgColor(request.getBgColor())
                .build();
    }
}
