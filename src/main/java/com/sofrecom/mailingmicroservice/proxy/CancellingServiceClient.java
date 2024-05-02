package com.sofrecom.mailingmicroservice.proxy;

import com.sofrecom.mailingmicroservice.dto.BookingDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

@Service
@FeignClient(name = "BookingMS", url = "http://localhost:4242")
public interface CancellingServiceClient {

    @GetMapping("/cancelling/{idCancelling}/booking")
    BookingDTO getBookingDTOByIdCancelling(@PathVariable Long idCancelling);

    @GetMapping("/booking/userIds/{announcementId}")
    List<Integer> getUserIdsByAnnouncementId(@PathVariable Long announcementId);

}
