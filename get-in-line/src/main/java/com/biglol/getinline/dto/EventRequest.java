package com.biglol.getinline.dto;

import java.time.LocalDateTime;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;

import com.biglol.getinline.constant.EventStatus;
import org.springframework.format.annotation.DateTimeFormat;

public record EventRequest(
        Long id,
        @NotBlank String eventName,
        @NotNull EventStatus eventStatus,
        @NotNull @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime eventStartDatetime,
        @NotNull @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime eventEndDatetime,
        @NotNull @PositiveOrZero Integer currentNumberOfPeople,
        @NotNull @Positive Integer capacity,
        String memo
) {

    public static EventRequest of(
            Long id,
            String eventName,
            EventStatus eventStatus,
            LocalDateTime eventStartDatetime,
            LocalDateTime eventEndDatetime,
            Integer currentNumberOfPeople,
            Integer capacity,
            String memo
    ) {
        return new EventRequest(
                id,
                eventName,
                eventStatus,
                eventStartDatetime,
                eventEndDatetime,
                currentNumberOfPeople,
                capacity,
                memo
        );
    }

    public EventDto toDto(PlaceDto placeDto) {
        return EventDto.of(
                this.id(),
                placeDto,
                this.eventName(),
                this.eventStatus(),
                this.eventStartDatetime(),
                this.eventEndDatetime(),
                this.currentNumberOfPeople(),
                this.capacity(),
                this.memo(),
                null,
                null
        );
    }

}