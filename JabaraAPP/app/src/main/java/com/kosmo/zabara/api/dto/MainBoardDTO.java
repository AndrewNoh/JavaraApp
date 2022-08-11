package com.kosmo.zabara.api.dto;

import java.sql.Date;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class MainBoardDTO {
    public String like;
    private String auction_no;
    private  List<String> itemImageResId;
    private String title;
    private String money;
    private Date postDate;
    private String profileImageResId;
    private String username;
    private String content;
    private String mainCategory;


}