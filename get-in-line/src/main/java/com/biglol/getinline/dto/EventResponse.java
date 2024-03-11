package com.biglol.getinline.dto;

import java.time.LocalDateTime;

import com.biglol.getinline.constant.EventStatus;

public record EventResponse(
        Long id,
        PlaceDto place,
        String eventName,
        EventStatus eventStatus,
        LocalDateTime eventStartDatetime,
        LocalDateTime eventEndDatetime,
        Integer currentNumberOfPeople,
        Integer capacity,
        String memo) {

    public static EventResponse of(
            Long id,
            PlaceDto place,
            String eventName,
            EventStatus eventStatus,
            LocalDateTime eventStartDatetime,
            LocalDateTime eventEndDatetime,
            Integer currentNumberOfPeople,
            Integer capacity,
            String memo) {
        return new EventResponse(
                id,
                place,
                eventName,
                eventStatus,
                eventStartDatetime,
                eventEndDatetime,
                currentNumberOfPeople,
                capacity,
                memo);
    }

    public static EventResponse from(EventDto eventDTO) {
        if (eventDTO == null) {
            return null;
        }
        return EventResponse.of(
                eventDTO.id(),
                eventDTO.placeDto(),
                eventDTO.eventName(),
                eventDTO.eventStatus(),
                eventDTO.eventStartDatetime(),
                eventDTO.eventEndDatetime(),
                eventDTO.currentNumberOfPeople(),
                eventDTO.capacity(),
                eventDTO.memo());
    }

    // event/index.th.xml 의 event.placeName 때문에 추가
    public String getPlaceName() {
        return this.place.placeName();
    }
}
