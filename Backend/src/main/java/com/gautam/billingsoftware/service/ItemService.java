package com.gautam.billingsoftware.service;

import com.gautam.billingsoftware.io.ItemRequest;
import com.gautam.billingsoftware.io.ItemResponse;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface ItemService {
   ItemResponse add(ItemRequest request, MultipartFile file);
   List<ItemResponse> fetchItems();

    void delete(String itemId);
}
