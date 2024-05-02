package com.sofrecom.mailingmicroservice.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CancellingDTO {
    private Long idCancelling;
    private String cause ;
    private Date dateCancelling;

}
