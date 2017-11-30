package com.centit.framework.cas;


import org.springframework.http.HttpHeaders;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

@RestController("/authuser")
public class CentitAuthUserHanderController {

    @PostMapping("/auth")
    public Object authUser(@RequestHeader HttpHeaders httpHeaders) {
        return new String("OK!");
    }
}
