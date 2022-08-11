package com.kosmo.zabara.api.dto;

import java.sql.Date;
import java.sql.Timestamp;

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
public class ChatRoomDTO {
    String ProfileImg;
    String userNickname;
    String lastContent;
    Timestamp sendTime;
    int isRead;
    int roomNo;
    int auction_no;
    String email;
    int senduserno;
    int writeuserno;
}
