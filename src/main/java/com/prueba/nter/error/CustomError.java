package com.prueba.nter.error;

import java.time.LocalDateTime;

public record CustomError(
        String message,
        LocalDateTime timestamp
) {
}
