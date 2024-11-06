let polylines = [];
let markers = [];
const urlParams = new URLSearchParams(window.location.search);
const planId = urlParams.get('planId');

let map = new naver.maps.Map("map", {
    center: new naver.maps.LatLng(37.5665, 126.9780),
    zoom: 12
});

const jwt = localStorage.getItem('token');
let routeData;

fetch(`/api/route-car-taxi/${planId}`, {
    method: 'GET',
    headers: {
        'Content-Type': 'application/json',
        'Authorization': `Bearer ${jwt}`
    }
})
    .then(response => {
        if (!response.ok) throw new Error(`HTTP error! status: ${response.status}`);
        return response.json();
    })
    .then(data => {
        routeData = data;
        displayRouteInfo(routeData);
    })
    .catch(error => console.error("Fetch error:", error));

function displayRouteInfo(data) {
    const routeInfoDiv = document.getElementById('routeInfo');
    routeInfoDiv.innerHTML = '';

    if (data.code === 0 && data.route && data.route.traoptimal) {
        data.route.traoptimal.forEach((optimalRoute, index) => {
            const summary = optimalRoute.summary;
            const routeDetail = document.createElement('div');
            routeDetail.className = 'routeDetail';

            routeDetail.innerHTML = `
                    <h3>Route ${index + 1}</h3>
                    <p><strong>Distance:</strong> ${(summary.distance / 1000).toFixed(2)} km</p>
                    <p><strong>Duration:</strong> ${Math.round(summary.duration / 1000 / 60)} minutes</p>
                    <p><strong>Toll Fare:</strong> ${summary.tollFare} won</p>
                    <p><strong>Taxi Fare:</strong> ${summary.taxiFare} won</p>
                    <p><strong>Fuel Price:</strong> ${summary.fuelPrice} won</p>
                    <button onclick="showPathOnMap(${index})">Show on Map</button>
                `;

            routeInfoDiv.appendChild(routeDetail);
        });
    } else {
        routeInfoDiv.innerHTML = `<p>Error: ${data.message || "No route data available."}</p>`;
    }
}

function showPathOnMap(routeIndex) {
    removePolylines();
    removeMarkers();

    const selectedRoute = routeData.route.traoptimal[routeIndex];
    const pathData = selectedRoute.path;
    const path = pathData.map(coords => new naver.maps.LatLng(coords[1], coords[0]));

    const polyline = new naver.maps.Polyline({
        map: map,
        path: path,
        strokeColor: '#0000FF',
        strokeWeight: 5,
    });
    polylines.push(polyline);

    if (selectedRoute.summary) {
        const startLocation = selectedRoute.summary.start?.location;
        const goalLocation = selectedRoute.summary.goal?.location;

        if (startLocation && goalLocation) {
            addMarker(startLocation, 'Start Point', 'start', map);
            addMarker(goalLocation, 'Goal Point', 'goal', map);
            fitMapToPath(path, startLocation, goalLocation);
            displayGuideInfo(selectedRoute.guide);
        } else {
            console.error("Start or goal location is undefined");
        }
    } else {
        console.error("Route summary is undefined");
    }
}

function displayGuideInfo(guide) {
    const routeInfoDiv = document.getElementById('routeInfo');
    const guideDetailDiv = document.createElement('div');
    guideDetailDiv.className = 'guideDetail';
    guideDetailDiv.innerHTML = `<h4>Guide Instructions:</h4>`;

    guide.forEach((step, index) => {
        if (step.location) addMarker(step.location, step.instructions, 'blue', map);

        guideDetailDiv.innerHTML += `
                <p><strong>Instruction ${index + 1}:</strong> ${step.instructions}</p>
                <p><strong>Distance to Next Step:</strong> ${step.distance} m</p>
                <p><strong>Duration to Next Step:</strong> ${(step.duration / 1000 / 60).toFixed(1)} minutes</p>
                <hr />
            `;
    });

    routeInfoDiv.appendChild(guideDetailDiv);
}

function addMarker(location, title, type, map) {
    let iconUrl;
    if (type === 'start') {
        iconUrl = 'https://t1.daumcdn.net/localimg/localimages/07/mapapidoc/red_b.png'; // Start marker
    } else if (type === 'goal') {
        iconUrl = 'https://t1.daumcdn.net/localimg/localimages/07/mapapidoc/blue_b.png'; // Goal marker
    } else {
        iconUrl = '/static/icon/map.png'; // Default or other marker if needed
    }

    const marker = new naver.maps.Marker({
        position: new naver.maps.LatLng(location[1], location[0]),
        map: map,
        title: title,
        icon: {
            url: iconUrl,
            size: new naver.maps.Size(50, 45), // Adjust size if necessary
            origin: new naver.maps.Point(0, 0),
            anchor: new naver.maps.Point(20, 40)
        }
    });
    markers.push(marker);
}


function removePolylines() {
    polylines.forEach(polyline => polyline.setMap(null));
    polylines = [];
}

function removeMarkers() {
    markers.forEach(marker => marker.setMap(null));
    markers = [];
}

function fitMapToPath(path, startLocation, goalLocation) {
    const bounds = new naver.maps.LatLngBounds();
    path.forEach(point => bounds.extend(point));
    bounds.extend(new naver.maps.LatLng(startLocation[1], startLocation[0]));
    bounds.extend(new naver.maps.LatLng(goalLocation[1], goalLocation[0]));
    map.fitBounds(bounds);
}