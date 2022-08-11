package com.kosmo.zabara.api.dto;


import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserDTO{
    private int userno;
    private String email;
    private String phonenumber;
    private String nickname;
    private String password;
    private String fulladdress;
    private String simpleaddress;
    private String profile_img;
    private String platform;
    private float latitude;
    private float longitude;
}
