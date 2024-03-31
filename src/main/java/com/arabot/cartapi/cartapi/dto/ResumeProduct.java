package com.arabot.cartapi.cartapi.dto;


import lombok.Builder;
import lombok.Data;

import java.util.UUID;

@Data
@Builder
public class ResumeProduct {

    private UUID productId;
    private int cant;

    public void increaseCant() {
        this.cant ++;
    }

}
