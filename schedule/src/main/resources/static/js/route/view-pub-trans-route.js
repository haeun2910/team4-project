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

        // Hiển thị thông tin Lane
        if (path.subPath && path.subPath.length > 0) {
            const laneList = document.createElement('ul');
            laneList.innerHTML = `<strong>Lane Information:</strong>`;
            let hasValidLaneInfo = false;

            path.subPath.forEach(subPath => {
                if (subPath.lane && subPath.lane.length > 0) {
                    subPath.lane.forEach(lane => {
                        const laneItem = document.createElement('li');
                        laneItem.innerHTML = `
                            Lane Name: ${lane.name || "N/A"}
                            ${lane.busNo ? " | Bus Number: " + lane.busNo : ""}
                        `;
                        laneList.appendChild(laneItem);
                        hasValidLaneInfo = true;
                    });
                }
            });

            if (hasValidLaneInfo) {
                pathContainer.appendChild(laneList);
            }
        }

        // Hiển thị thông tin PassStopList
        if (path.subPath && path.subPath.length > 0) {
            const stopList = document.createElement('div');
            stopList.innerHTML = `<strong>Pass Stop List:</strong>`;
            const stationList = document.createElement('ul');

            path.subPath.forEach(subPath => {
                if (subPath.passStopList && subPath.passStopList.stations && subPath.passStopList.stations.length > 0) {
                    subPath.passStopList.stations.forEach(station => {
                        const stationItem = document.createElement('li');
                        stationItem.innerHTML = `Name: ${station.stationName || "N/A"}`;
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

// Hàm hiển thị cung đường cụ thể trên bản đồ
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
                    if (subPath.trafficType === 2) {
                        // Process bus route with passStopList for coordinates
                        if (subPath.passStopList && Array.isArray(subPath.passStopList.stations)) {
                            const busCoords = subPath.passStopList.stations.map(station =>
                                new naver.maps.LatLng(station.y, station.x)
                            );

                            if (busCoords.length > 0) {
                                createPolyline(busCoords, '#FF0000'); // Red for bus route
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
                    else if (subPath.trafficType === 1) {
                        if (subPath.passStopList && Array.isArray(subPath.passStopList.stations)) {
                            const subwayCoords = subPath.passStopList.stations.map(station =>
                                new naver.maps.LatLng(station.y, station.x)
                            );

                            if (subwayCoords.length > 0) {
                                createPolyline(subwayCoords, '#0000FF'); // Blue for subway route
                            }

                            // Add markers for each subway station
                            subPath.passStopList.stations.forEach(station => {
                                if (station.y && station.x && station.stationName) {
                                    createMarker(station.y, station.x, station.stationName);
                                }
                            });
                        }
                    }
                    // Process walk section, only add start and end markers
                    else if (subPath.trafficType === 3) {
                        if (subPath.startY && subPath.startX && subPath.startName && subPath.endY && subPath.endX && subPath.endName) {
                            createMarker(subPath.startY, subPath.startX, subPath.startName);
                            createMarker(subPath.endY, subPath.endX, subPath.endName);
                        }
                    } else {
                        console.warn("Unsupported trafficType:", subPath.trafficType);
                    }
                });
            } else {
                console.warn("No subPath available for the selected route");
            }
        })
        .catch(error => {
            console.error('Error fetching route:', error);
        });
}

// Function to create polylines with specified color
function createPolyline(coords, color) {
    const polyline = new naver.maps.Polyline({
        path: coords,
        strokeColor: color,
        strokeOpacity: 0.7,
        strokeWeight: 6,
        map: map
    });
    polylines.push(polyline);
}

// Function to create markers
function createMarker(lat, lng, title) {
    const marker = new naver.maps.Marker({
        position: new naver.maps.LatLng(lat, lng),
        map: map,
        title: title
    });
    markers.push(marker);
}



