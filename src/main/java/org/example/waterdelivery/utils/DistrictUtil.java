package org.example.waterdelivery.utils;

import lombok.RequiredArgsConstructor;
import org.example.waterdelivery.dto.Location;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
@RequiredArgsConstructor
public class DistrictUtil {

    private final String apiKey = "AIzaSyCgZ-vdmLDVDKGocVYBPuYM_IK7IjDbIh4";
    private final RestTemplate restTemplate;

    // delivers a huge response to find the district according to the lat and long
    public String getDistrictName(Location location) {
        String url = String.format(
                "https://maps.googleapis.com/maps/api/geocode/json?latlng=%s,%s&key=%s",
                location.getLatitude(), location.getLongitude(), apiKey
        );

        String response = restTemplate.getForObject(url, String.class);
        return parseDistrictFromResponse(response).split(" ")[0]; // updates
    }

    private String parseDistrictFromResponse(String response) {
        String target = "sublocality_level_1";
        int index1 = response.indexOf(target);
        response = response.substring(index1 + target.length()); // added new
        int index = response.indexOf(target); // added new
        if (index != -1) {
            int start = response.lastIndexOf("long_name", index) + "long_name".length() + 5;
            int end = response.indexOf("\"", start);
            return response.substring(start, end);

        }
        return "District not found";
    }
}
