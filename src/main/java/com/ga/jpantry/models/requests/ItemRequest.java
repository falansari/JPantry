package com.ga.jpantry.models.requests;

import com.ga.jpantry.models.Item;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ItemRequest {
    private Item item;
    private Long categoryId;
    private Long locationId;
    private Long sourceId;
}
