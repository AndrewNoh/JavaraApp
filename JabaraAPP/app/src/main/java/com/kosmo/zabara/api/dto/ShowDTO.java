package com.kosmo.zabara.api.dto;

import java.sql.Date;

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
public class ShowDTO {
    private String auction_no;
    private String title;
    private String imagename;
    private String simpleaddress;
    private Date postdate;
    private String upper_price;
    private int likes;
    private String status;
}
