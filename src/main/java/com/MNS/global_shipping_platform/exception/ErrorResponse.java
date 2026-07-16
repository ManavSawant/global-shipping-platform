package com.MNS.global_shipping_platform.exception;

import java.io.Serializable;
import java.time.LocalDateTime;

public record ErrorResponse(LocalDateTime timestamp,
                            int status,
                            String error,
                            String message,
                            String path){
}
