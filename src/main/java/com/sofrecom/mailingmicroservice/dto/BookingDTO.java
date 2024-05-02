package com.sofrecom.mailingmicroservice.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class BookingDTO {
    private Long idReservation;
    private String location;
    private Date dateBooking;
    private Integer userId;
    private Integer driverId;
    private Long announcementId;
}
