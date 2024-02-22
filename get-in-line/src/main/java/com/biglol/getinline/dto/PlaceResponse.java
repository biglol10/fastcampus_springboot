package com.biglol.getinline.dto;

import com.biglol.getinline.constant.PlaceType;

public record PlaceResponse(
        PlaceType placeType,
        String placeName,
        String address,
        String phoneNumber,
        Integer capacity,
        String memo) {
    public static PlaceResponse of( // canonical constructor
            PlaceType placeType,
            String placeName,
            String address,
            String phoneNumber,
            Integer capacity,
            String memo) {
        return new PlaceResponse(placeType, placeName, address, phoneNumber, capacity, memo);
    }
}
