// Khai báo biến toàn cục để lưu các polylines và markers
let polylines = [];
let markers = []; // Mảng để lưu trữ tất cả các markers

// Lấy `planId` từ URL
const urlParams = new URLSearchParams(window.location.search);
const planId = urlParams.get('planId');

// Khởi tạo bản đồ Naver
let map = new naver.maps.Map("map", {
    center: new naver.maps.LatLng(37.5665, 126.9780), // Tọa độ mặc định (Seoul)
    zoom: 12
});

// Lấy JWT từ local storage hoặc cookie
const jwt = localStorage.getItem('token');

// Gọi API để lấy thông tin tuyến đường
fetch(`/api/route/${planId}`, {
    method: 'GET',
    headers: {
        'Content-Type': 'application/json',
        'Authorization': `Bearer ${jwt}`
    }
})
    .then(response => {
        if (!response.ok) {
            throw new Error("Unable to fetch route details. Please try again later.");
        }
        return response.json();
    })
    .then(data => {
        if (data.result && data.result.path && data.result.path.length > 0) {
            displayRouteDetails(data.result.path);
            displayInitialRouteOnMap(data.result.path);
        } else {
            document.getElementById('route-details').innerHTML = "<p>No routes found.</p>";
        }
    })
    .catch(error => {
        document.getElementById('route-details').innerHTML = `<p>Error: ${error.message}</p>`;
        console.error("Error fetching route details:", error);
    });

// Hàm hiển thị thông tin tuyến đường
function displayRouteDetails(paths) {
    const routeDetails = document.getElementById('route-details');
    routeDetails.innerHTML = "";

    const uniquePaths = [];
    const pathSet = new Set();

    // Create unique path identifiers
    paths.forEach(path => {
        const identifier = `${path.info.firstStartStation}-${path.info.lastEndStation}-${path.info.totalDistance}-${path.info.totalTime}`;
        if (!pathSet.has(identifier)) {
            pathSet.add(identifier);
            uniquePaths.push(path);
        }
    });

    const maxDisplayCount = 5;
    const pathsToDisplay = uniquePaths.slice(0, maxDisplayCount);

    pathsToDisplay.forEach((path, index) => {
        const pathContainer = document.createElement('div');
        pathContainer.classList.add('path');

        pathContainer.innerHTML = `
            <h2>Path ${index + 1}</h2>
            <p><strong>Starting Station:</strong> ${path.info.firstStartStation}</p>
            <p><strong>Ending Station:</strong> ${path.info.lastEndStation}</p>
            <p><strong>Total Time:</strong> ${path.info.totalTime} minutes</p>
            <p><strong>Total Cost:</strong> ${path.info.payment} won</p>
            <p><strong>Number of Bus Transfers:</strong> ${path.info.busTransitCount}</p>
            <p><strong>Number of Subway Transfers:</strong> ${path.info.subwayTransitCount}</p>
            <p><strong>Distance:</strong> ${path.info.totalDistance ? (path.info.totalDistance / 1000).toFixed(2) + " km" : "N/A"}</p>
            <p><strong>Station Count:</strong> ${path.info.totalStationCount || "N/A"}</p>
        `;

        // Hiển thị thông tin Lane nếu có
        if (path.subPath && path.subPath.length > 0) {
            const laneList = document.createElement('ul');
            let hasValidLaneInfo = false; // Track if we have any valid lane information
            laneList.innerHTML = `<strong>Lane Information:</strong>`;

            path.subPath.forEach(subPath => {
                if (subPath.lane && subPath.lane.length > 0) {
                    subPath.lane.forEach(lane => {
                        const laneName = lane.name || "N/A"; // Default to "N/A" if no name
                        const busNumber = lane.busNo ? " | Bus Number: " + lane.busNo : "N/A"; // Default to "N/A" if no bus number

                        // Only display if either lane name or bus number is valid
                        if (laneName !== "N/A" || busNumber !== "N/A") {
                            const laneItem = document.createElement('li');
                            if (laneName !== "N/A" && busNumber !== "N/A") {
                                // If both are available, show both
                                laneItem.innerHTML = `Lane Name: ${laneName} | Bus Number: ${lane.busNo}`;
                            } else if (laneName !== "N/A") {
                                // If only lane name is valid, show it
                                laneItem.innerHTML = `Lane Name: ${laneName}`;
                            } else {
                                // If only bus number is valid, show it
                                laneItem.innerHTML = `Bus Number: ${lane.busNo}`;
                            }
                            laneList.appendChild(laneItem);
                            hasValidLaneInfo = true; // We have valid lane info
                        }
                    });
                }
            });

            // Append laneList only if it has valid lane information
            if (hasValidLaneInfo) {
                pathContainer.appendChild(laneList);
            }
        }



        // Hiển thị thông tin PassStopList nếu có
        if (path.subPath && path.subPath.length > 0) {
            const stopList = document.createElement('div');
            stopList.innerHTML = `<strong>Pass Stop List:</strong>`;
            const stationList = document.createElement('ul');

            path.subPath.forEach(subPath => {
                if (subPath.passStopList && subPath.passStopList.stations && subPath.passStopList.stations.length > 0) {
                    subPath.passStopList.stations.forEach(station => {
                        const stationItem = document.createElement('li');
                        const stationType = subPath.trafficType === 1 ? "Subway Stop" : (subPath.trafficType === 2 ? "Bus Stop" : "N/A");
                        stationItem.innerHTML = `Name: ${station.stationName || "N/A"} (${stationType})`;
                        stationList.appendChild(stationItem);
                    });
                }
            });

            if (stationList.childNodes.length > 0) {
                stopList.appendChild(stationList);
            } else {
                const noStopsMessage = document.createElement('p');
                noStopsMessage.textContent = 'No stops available.';
                stopList.appendChild(noStopsMessage);
            }

            pathContainer.appendChild(stopList);
        } else {
            const noSubPathsMessage = document.createElement('p');
            noSubPathsMessage.textContent = 'No routes available.';
            pathContainer.appendChild(noSubPathsMessage);
        }

        pathContainer.innerHTML += `<button onclick="showPathOnMap(${index})">See on Map</button>`;
        routeDetails.appendChild(pathContainer);
    });
}


// Hàm hiển thị điểm bắt đầu và kết thúc trên bản đồ
function displayInitialRouteOnMap(paths) {
    const initialPath = paths[0];
    if (initialPath && initialPath.subPath && initialPath.subPath.length > 0) {
        const startLocation = new naver.maps.LatLng(initialPath.subPath[0].startY, initialPath.subPath[0].startX);
        const endLocation = new naver.maps.LatLng(initialPath.subPath[0].endY, initialPath.subPath[0].endX);

        const startMarker = new naver.maps.Marker({
            position: startLocation,
            map: map,
            title: initialPath.subPath[0].startName
        });

        const endMarker = new naver.maps.Marker({
            position: endLocation,
            map: map,
            title: initialPath.subPath[0].endName
        });

        markers.push(startMarker, endMarker);
        map.setCenter(startLocation);
        map.setZoom(12);
    }
}

// Hàm xóa tất cả markers trên bản đồ
function clearMarkers() {
    markers.forEach(marker => {
        marker.setMap(null); // Xóa marker khỏi bản đồ
    });
    markers = []; // Đặt lại mảng markers
}

function showPathOnMap(index) {
    // Clear existing polylines and markers
    polylines.forEach(polyline => polyline.setMap(null));
    polylines = [];
    clearMarkers();

    // Fetch route data from the API
    fetch(`/api/route/${planId}`, {
        method: 'GET',
        headers: {
            'Content-Type': 'application/json',
            'Authorization': `Bearer ${jwt}`
        }
    })
        .then(response => {
            if (!response.ok) {
                throw new Error(`HTTP error! status: ${response.status}`);
            }
            return response.json();
        })
        .then(data => {
            console.log(data); // Check the returned data
            const path = data.result.path[index]; // Get path based on index

            // Check if the path exists
            if (path && Array.isArray(path.subPath) && path.subPath.length > 0) {
                path.subPath.forEach(subPath => {
                    // Process bus route
                    if (subPath.trafficType === 2) { // Bus
                        if (subPath.passStopList && Array.isArray(subPath.passStopList.stations)) {
                            const busCoords = subPath.passStopList.stations.map(station =>
                                new naver.maps.LatLng(station.y, station.x)
                            );

                            // Check if the API provides actual route coordinates
                            if (subPath.routeCoords && Array.isArray(subPath.routeCoords)) {
                                const routeCoords = subPath.routeCoords.map(coord =>
                                    new naver.maps.LatLng(coord.y, coord.x)
                                );

                                if (routeCoords.length > 0) {
                                    createPolyline(routeCoords, '#FF0000'); // Red for bus route
                                }
                            } else if (busCoords.length > 0) {
                                // Use the bus stops as polyline if route coordinates are not available
                                createPolyline(busCoords, '#FF0000'); // Fallback to connecting bus stops
                            }

                            // Add markers for each bus stop
                            subPath.passStopList.stations.forEach(station => {
                                if (station.y && station.x && station.stationName) {
                                    createMarker(station.y, station.x, station.stationName);
                                }
                            });
                        }
                    }
                    // Process subway route
                    else if (subPath.trafficType === 1) { // Subway
                        if (subPath.passStopList && Array.isArray(subPath.passStopList.stations)) {
                            const subwayCoords = subPath.passStopList.stations.map(station =>
                                new naver.maps.LatLng(station.y, station.x)
                            );

                            if (subPath.routeCoords && Array.isArray(subPath.routeCoords)) {
                                const routeCoords = subPath.routeCoords.map(coord =>
                                    new naver.maps.LatLng(coord.y, coord.x)
                                );

                                if (routeCoords.length > 0) {
                                    createPolyline(routeCoords, '#0000FF'); // Blue for subway route
                                }
                            } else if (subwayCoords.length > 0) {
                                createPolyline(subwayCoords, '#0000FF'); // Fallback to connecting subway stations
                            }

                            // Add markers for each subway station
                            subPath.passStopList.stations.forEach(station => {
                                if (station.y && station.x && station.stationName) {
                                    createMarker(station.y, station.x, station.stationName);
                                }
                            });
                        }
                    }
                });

                // Adjust map to show all markers
                const bounds = new naver.maps.LatLngBounds();
                markers.forEach(marker => bounds.extend(marker.getPosition()));
                map.fitBounds(bounds);
            }
        })
        .catch(error => {
            console.error('Error fetching route data:', error);
        });
}


// Function to create polylines with specified color
// Create Polyline with color for different transportation types
function createPolyline(coords, color) {
    const polyline = new naver.maps.Polyline({
        path: coords,
        strokeColor: color,
        strokeWeight: 4,
        map: map
    });
    polylines.push(polyline);
}

// Create marker at specific coordinates
function createMarker(lat, lng, title) {
    const marker = new naver.maps.Marker({
        position: new naver.maps.LatLng(lat, lng),
        map: map,
        title: title
    });
    markers.push(marker);
}

