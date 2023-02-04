package vanek;

import java.time.LocalDateTime;

public class ErrorResponse {
    public String message;
    public LocalDateTime timeStamp;

    public ErrorResponse(String message, LocalDateTime now) {
        this.message = message;
        this.timeStamp = now;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public LocalDateTime getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(LocalDateTime timeStamp) {
        this.timeStamp = timeStamp;
    }
}
