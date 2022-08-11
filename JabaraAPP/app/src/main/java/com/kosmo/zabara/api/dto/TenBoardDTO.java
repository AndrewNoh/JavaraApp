package com.kosmo.zabara.api.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TenBoardDTO {
    private String auction_no;
    private String title;
    private String upper_price;
    private String imagename;
}