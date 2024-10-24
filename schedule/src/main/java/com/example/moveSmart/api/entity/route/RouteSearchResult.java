package com.example.moveSmart.api.entity.route;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Getter
public class RouteSearchResult {
    private int searchType;
    private List<OdsayRouteSearchResponse.Result.Path> paths;
}
