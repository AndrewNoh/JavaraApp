package com.kosmo.zabara.api.dto;

import android.os.Parcel;
import android.os.Parcelable;

import java.io.Serializable;

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
public class ImageDTO{

    // 글 번호
    private String imageNo;

    private String product_no;
    private String auction_no;
    private String townlist_no;

    private String imageName;

}
