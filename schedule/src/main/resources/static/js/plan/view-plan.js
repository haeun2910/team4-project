const jwt = localStorage.getItem("token");
const planId = new URLSearchParams(window.location.search).get('id');

if (!jwt) location.href = "/views/signin";
if (!planId) {
    alert('No plan ID provided.');
    location.href = "/views/plans"; // Redirect if no ID
}

// Fetch plan details by ID
fetch(`/plans/${planId}`, {
    method: 'GET',
    headers: {
        'Content-Type': 'application/json',
        'Authorization': `Bearer ${jwt}`
    }
})
    .then(response => {
        if (!response.ok) {
            throw new Error(`Error fetching plan: ${response.statusText}`);
        }
        return response.json();
    })
    .then(plan => {
        // Display plan details
        document.getElementById('plan-title').innerText = plan.title;
        document.getElementById('departure-name').innerText = plan.departureName;
        document.getElementById('arrival-name').innerText = plan.arrivalName;
        document.getElementById('arrival-at').innerText = new Date(plan.arrivalAt).toLocaleString();
        document.getElementById('notification-message').innerText = plan.notificationMessage;

        // Initialize the map
        const map = new naver.maps.Map('map', {
            zoom: 10,
            center: new naver.maps.LatLng(37.3614483, 127.1114883)
        });

        // Create markers for departure and arrival
        const departureMarker = new naver.maps.Marker({
            map: map,
            position: new naver.maps.LatLng(37.3614483, 127.1114883),
            title: 'Departure'
        });

        const arrivalMarker = new naver.maps.Marker({
            map: map,
            position: new naver.maps.LatLng(37.3614483, 127.1114883),
            title: 'Arrival'
        });

        // Fetch coordinates for departure and arrival addresses
        fetchCoordinates(plan.departureName, (coords) => {
            map.setCenter(coords);
            departureMarker.setPosition(coords);
        });

        fetchCoordinates(plan.arrivalName, (coords) => {
            arrivalMarker.setPosition(coords);
        });
    })
    .catch(error => {
        alert(`An error occurred: ${error.message}`);
    });

function isSpecificAddress(address) {
    const specificPlacePattern = /(\d{1,2}번출구|입구|학교|공원|지하철|역)$/;
    if (specificPlacePattern.test(address)) {
        return false;
    }
    const generalAddressPattern = /\d+/;

    return generalAddressPattern.test(address);
}

// Function to fetch coordinates based on address type
function fetchCoordinates(address, callback) {
    const endpoint = isSpecificAddress(address) ? 'search-location' : 'search-place';

    fetch(`/api/${endpoint}?address=${encodeURIComponent(address)}`, {
        method: 'GET',
        headers: {
            'Content-Type': 'application/json',
            'Authorization': `Bearer ${jwt}`
        }
    })
        .then(response => {
            if (!response.ok) {
                throw new Error(`Server returned ${response.status}: ${response.statusText}`);
            }
            return response.json();
        })
        .then(data => {
            if (data && data.places && data.places.length > 0) {
                const coords = new naver.maps.LatLng(data.places[0].latitude, data.places[0].longitude);
                callback(coords);
            } else {
                alert('Address not found.');
            }
        })
        .catch(error => {
            console.error('Error fetching coordinates:', error);
            alert('An error occurred while finding the address.');
        });
}
