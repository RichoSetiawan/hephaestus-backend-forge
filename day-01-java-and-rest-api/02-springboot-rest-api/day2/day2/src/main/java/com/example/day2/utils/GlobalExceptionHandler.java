package com.example.day2.utils;

import com.example.day2.dto.WebResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import java.time.LocalDateTime;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<WebResponse<String>> handleIllegalArgumentException(IllegalArgumentException ex) {
        
        WebResponse<String> errorResponse = WebResponse.<String>builder()
                .code(HttpStatus.BAD_REQUEST.value())
                .status("BAD REQUEST")
                .message(ex.getMessage()) // Mengambil pesan dari throw baru Anda ("Nama Harus Diisi", dll)
                .data(null)
                .timestamp(LocalDateTime.now())
                .build();
                
        return ResponseEntity.badRequest().body(errorResponse);
    }
}