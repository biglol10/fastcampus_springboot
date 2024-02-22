package com.biglol.getinline.dto;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDateTime;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.biglol.getinline.constant.ErrorCode;
import com.biglol.getinline.constant.EventStatus;

// Spring의 무언가를 사용하는 것이 아니기에 @WebMvcTest안 써도 됨
class APIDataResponseTest {
    @DisplayName("표준 성공 응답 테스트")
    @Test
    void test() {
        String data = "test data";

        EventResponse eventResponse =
                EventResponse.of(
                        1L,
                        "오후 운동",
                        EventStatus.OPENED,
                        LocalDateTime.of(2021, 1, 1, 13, 0, 0),
                        LocalDateTime.of(2021, 1, 1, 16, 0, 0),
                        0,
                        24,
                        "마스크 꼭 착용하세요");

        APIDataResponse<String> response = APIDataResponse.of(data);

        assertThat(response)
                .hasFieldOrPropertyWithValue("success", true)
                .hasFieldOrPropertyWithValue("errorCode", ErrorCode.OK.getCode())
                .hasFieldOrPropertyWithValue("message", ErrorCode.OK.getMessage())
                .hasFieldOrPropertyWithValue("data", data);

        //        APIDataResponse<EventResponse> response = APIDataResponse.of(
        //                eventResponse);

        //        assertThat(response).isNotNull()
        //                .hasFieldOrPropertyWithValue("success", response.getSuccess())  // success
        // field가 true냐 false이냐만 검사하는게 아니라 null인지도 자연스럽게 자동으로 검사. 그래서 isNotNull()제거 가능
        //                .hasFieldOrPropertyWithValue("errorCode", response.getErrorCode())
        //                .hasFieldOrPropertyWithValue("message", response.getMessage())
        //                .hasFieldOrPropertyWithValue("data", eventResponse);
        ;
    }

    @DisplayName("데이터가 없을 때, 비어있는 표준 성공 응답을 생성한다.")
    @Test
    void givenNothing_whenCreatingResponse_thenReturnsEmptySuccessfulResponse() {
        // Given

        // When
        APIDataResponse<String> response = APIDataResponse.empty();

        // Then
        assertThat(response)
                .hasFieldOrPropertyWithValue("success", true)
                .hasFieldOrPropertyWithValue("errorCode", ErrorCode.OK.getCode())
                .hasFieldOrPropertyWithValue("message", ErrorCode.OK.getMessage())
                .hasFieldOrPropertyWithValue("data", null);
    }
}
