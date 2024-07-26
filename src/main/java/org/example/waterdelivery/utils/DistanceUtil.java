package org.example.waterdelivery.utils;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.waterdelivery.dto.Location;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class DistanceUtil {

    private static final String API_KEY = "AIzaSyCgZ-vdmLDVDKGocVYBPuYM_IK7IjDbIh4";

    public String getOptimizedRoute(List<Location> locations, Location from) {
        RestTemplate restTemplate = new RestTemplate();
        String url = buildDirectionsApiUrl(locations, from);
        return restTemplate.getForObject(url, String.class);
    }

    public String buildDirectionsApiUrl(List<Location> locations, Location locationFrom) {
        StringBuilder url = new StringBuilder("https://maps.googleapis.com/maps/api/directions/json?");
        url.append("origin=").append(locationFrom.toString());
        url.append("&destination=").append(locationFrom.toString()); // locationTo?
        url.append("&waypoints=optimize:true");

        for (Location location : locations) {
            url.append("|").append(location.toString());
        }

        url.append("&key=").append(API_KEY);
        return url.toString();
    }

    public long getTotalTime(String jsonResponse) {
        long totalTime = 0;
        try {
            String[] legs = jsonResponse.split("\"legs\"");
            for (int i = 1; i < legs.length; i++) {
                String leg = legs[i];
                String[] durations = leg.split("\"duration\"");
                for (int j = 1; j < durations.length; j++) {
                    String duration = durations[j];
                    String[] values = duration.split("\"value\"");
                    if (values.length > 1) {
                        String value = values[1].split(",")[0].replace("[^0-9]", "").trim();
                        totalTime += Long.parseLong(value);
                    }
                }
            }
        } catch (NumberFormatException e) {
            log.error("number format exception while parsing duration values", e);
        } catch (Exception e) {
            log.error("Exception while parsing JSON response", e);
        }
        return totalTime;
    }

    public int[] getOptimizedOrder(String jsonResponse) {
        try {
            String[] waypointOrderPart = jsonResponse.split("\"waypoint_order\"\\s*\\[")[1].split("]")[0].split(",");
            int[] waypointOrder = new int[waypointOrderPart.length];
            for (int i = 0; i < waypointOrderPart.length; i++) {
                waypointOrder[i] = Integer.parseInt(waypointOrderPart[i].trim());
            }
            return waypointOrder;
        } catch (Exception e) {
            log.error("Exception while parsing waypoint order", e);
        }
        return new int[0];
    }
}
