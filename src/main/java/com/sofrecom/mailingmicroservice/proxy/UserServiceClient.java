package com.sofrecom.mailingmicroservice.proxy;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.cloud.openfeign.FeignClient;

@Service
@FeignClient(name = "AuthentificationMS", url = "http://localhost:8888")
public interface UserServiceClient {

    @GetMapping("/collaborators/{id}/email")
    String getCollaboratorEmailById(@PathVariable Integer id);
}
