package com.MNS.global_shipping_platform.exception;

import java.time.LocalDateTime;

public record CustomerErrorResponse(LocalDateTime timestamp,
                                    int status,
                                    String error,
                                    String message,
                                    String path){
}
