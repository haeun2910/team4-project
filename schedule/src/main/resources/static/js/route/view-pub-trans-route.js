// Khai báo biến toàn cục để lưu các polylines và markers
let polylines = [];
let markers = [];

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

    // Lọc và lấy các tuyến đường duy nhất
    const uniquePaths = [];
    const pathSet = new Set();
    paths.forEach(path => {
        const identifier = `${path.info.firstStartStation}-${path.info.lastEndStation}-${path.info.totalDistance}-${path.info.totalTime}`;
        if (!pathSet.has(identifier)) {
            pathSet.add(identifier);
            uniquePaths.push(path);
        }
    });

    // Hiển thị tối đa 5 tuyến đường
    const pathsToDisplay = uniquePaths.slice(0, 5);
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
            <p><strong>Distance:</strong> ${(path.info.totalDistance / 1000).toFixed(2)} km</p>
        `;

        pathContainer.innerHTML += `<button onclick="showPathOnMap(${index})">See on Map</button>`;
        // Hiển thị thông tin Lane và điểm dừng
        displayLaneInfo(path, pathContainer);
        displayStopList(path, pathContainer);

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

// Hàm hiển thị tuyến đường trên bản đồ dựa vào chỉ số
// Hàm hiển thị tuyến đường trên bản đồ dựa vào chỉ số
function showPathOnMap(index) {
    clearMapElements();

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
            const path = data.result.path[index];
            if (path && Array.isArray(path.subPath) && path.subPath.length > 0) {
                path.subPath.forEach(subPath => {
                    const color = subPath.trafficType === 2 ? '#FF0000' : '#0000FF'; // Red for bus, blue for subway
                    const stations = (subPath.passStopList && subPath.passStopList.stations) || [];
                    const routeCoords = (subPath.routeCoords || []).map(coord => new naver.maps.LatLng(coord.y, coord.x));

                    if (routeCoords.length > 0) {
                        createPolyline(routeCoords, color);
                    } else if (stations.length > 0) {
                        const coords = stations.map(station => new naver.maps.LatLng(station.y, station.x));
                        createPolyline(coords, color);
                    }

                    // Chỉ thêm markers cho điểm bắt đầu và điểm kết thúc
                    if (stations.length > 0) {
                        const startStation = stations[0];
                        const endStation = stations[stations.length - 1];

                        // Thêm marker cho điểm bắt đầu
                        createMarker(startStation.y, startStation.x, startStation.stationName);

                        // Thêm marker cho điểm kết thúc
                        createMarker(endStation.y, endStation.x, endStation.stationName);
                    }
                });

                adjustMapBounds();
            }
        })
        .catch(error => {
            console.error('Error fetching route data:', error);
        });
}


// Hàm xóa tất cả markers và polylines
function clearMapElements() {
    polylines.forEach(polyline => polyline.setMap(null));
    polylines = [];
    markers.forEach(marker => marker.setMap(null));
    markers = [];
}

// Hàm tạo Polyline với màu sắc chỉ định
function createPolyline(coords, color) {
    const polyline = new naver.maps.Polyline({
        path: coords,
        strokeColor: color,
        strokeWeight: 4,
        map: map
    });
    polylines.push(polyline);
}

// Hàm tạo marker tại tọa độ cụ thể
function createMarker(lat, lng, title) {
    const marker = new naver.maps.Marker({
        position: new naver.maps.LatLng(lat, lng),
        map: map,
        title: title
    });
    markers.push(marker);
}

// Điều chỉnh bản đồ để bao gồm tất cả các markers
function adjustMapBounds() {
    const bounds = new naver.maps.LatLngBounds();
    markers.forEach(marker => bounds.extend(marker.getPosition()));
    map.fitBounds(bounds);
}

// Hàm hiển thị thông tin Lane
function displayLaneInfo(path, container) {
    if (path.subPath && path.subPath.length > 0) {
        const laneList = document.createElement('ul');
        let hasValidLaneInfo = false;
        laneList.innerHTML = `<strong>Lane Information:</strong>`;

        path.subPath.forEach(subPath => {
            if (subPath.lane && subPath.lane.length > 0) {
                subPath.lane.forEach(lane => {
                    // Retrieve lane name and bus number
                    const laneName = lane.name;
                    const busNumber = lane.busNo;

                    // Create a display string based on available information
                    const laneDisplay = [];
                    if (laneName) {
                        laneDisplay.push(`Lane Name: ${laneName}`);
                    }
                    if (busNumber) {
                        laneDisplay.push(`Bus Number: ${busNumber}`);
                    }

                    // Only create an item if there is valid information to display
                    if (laneDisplay.length > 0) {
                        const laneItem = document.createElement('li');
                        laneItem.innerHTML = laneDisplay.join(' | ');
                        laneList.appendChild(laneItem);
                        hasValidLaneInfo = true;
                    }
                });
            }
        });

        // Append the list to the container only if there is valid lane info
        if (hasValidLaneInfo) {
            container.appendChild(laneList);
        }
    }
}




// Hàm hiển thị danh sách các điểm dừng
function displayStopList(path, container) {
    if (path.subPath && path.subPath.length > 0) {
        const stopList = document.createElement('div');
        stopList.innerHTML = `<strong>Pass Stop List:</strong>`;
        const stationList = document.createElement('ul');

        path.subPath.forEach(subPath => {
            if (subPath.passStopList && subPath.passStopList.stations) {
                subPath.passStopList.stations.forEach(station => {
                    const stationItem = document.createElement('li');
                    const stationType = subPath.trafficType === 1 ? "Subway Stop" : "Bus Stop";
                    stationItem.innerHTML = `Name: ${station.stationName || "N/A"} (${stationType})`;
                    stationList.appendChild(stationItem);
                });
            }
        });

        stopList.appendChild(stationList);
        container.appendChild(stopList);
    }
}
