package com.biglol.getinline.dto;

import com.biglol.getinline.constant.ErrorCode;

import lombok.*;

@Getter
@ToString
@EqualsAndHashCode
@RequiredArgsConstructor(
        access =
                AccessLevel
                        .PROTECTED) // 상속에 열려 있어야 되기 때문에 protected. 바깥에는 쓸 수 없고 상속받은 데이터 클래스는 사용할 수
// 있게
public class ApiErrorResponse {
    private final Boolean success;
    private final Integer errorCode;
    private final String message;

    public static ApiErrorResponse of(Boolean success, Integer errorCode, String message) {
        return new ApiErrorResponse(success, errorCode, message);
    }

    public static ApiErrorResponse of(Boolean success, ErrorCode errorCode) {
        return new ApiErrorResponse(success, errorCode.getCode(), errorCode.getMessage());
    }

    public static ApiErrorResponse of(Boolean success, ErrorCode errorCode, Exception e) {
        return new ApiErrorResponse(success, errorCode.getCode(), errorCode.getMessage(e));
    }

    public static ApiErrorResponse of(Boolean success, ErrorCode errorCode, String message) {
        return new ApiErrorResponse(success, errorCode.getCode(), errorCode.getMessage(message));
    }
}
