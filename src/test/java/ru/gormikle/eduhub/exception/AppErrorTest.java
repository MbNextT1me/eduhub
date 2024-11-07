package ru.gormikle.eduhub.exception;

import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.assertThat;

import java.util.Date;

class AppErrorTest {
    @Test
    void testAppErrorConstructor() {
        int status = 404;
        String message = "Resource not found";

        AppError appError = new AppError(status, message);

        assertThat(appError.getStatus()).isEqualTo(status);
        assertThat(appError.getMessage()).isEqualTo(message);

        assertThat(appError.getTimestamp()).isNotNull();

        Date now = new Date();
        long difference = now.getTime() - appError.getTimestamp().getTime();
        assertThat(difference).isLessThanOrEqualTo(1000);
    }

    @Test
    void testAppErrorSettersAndGetters() {
        AppError appError = new AppError(200, "OK");

        appError.setStatus(500);
        appError.setMessage("Internal Server Error");

        assertThat(appError.getStatus()).isEqualTo(500);
        assertThat(appError.getMessage()).isEqualTo("Internal Server Error");
    }
}
