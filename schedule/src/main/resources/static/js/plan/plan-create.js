const jwt = localStorage.getItem("token");
if (!jwt) location.href = "/views/signin";

// Initialize the map
const map = new naver.maps.Map('map', {
    zoom: 10,
    center: new naver.maps.LatLng(37.3614483, 127.1114883)
});

// Create markers for departure and arrival
const departureMarkers = [];
const arrivalMarkers = [];
let selectedDepartureAddress = ""; // Store selected departure address
let selectedArrivalAddress = ""; // Store selected arrival address

function placeMarkers(locations, markersArray, isDeparture) {
    clearMarkers(markersArray);
    const currentMarkers = [];

    locations.forEach(location => {
        const coords = new naver.maps.LatLng(location.latitude, location.longitude);
        const marker = new naver.maps.Marker({
            map: map,
            position: coords,
            title: location.name || 'Location',
            visible: true
        });

        marker.addListener('click', () => {
            const selectedMarker = markersArray[0];

            if (selectedMarker && selectedMarker !== marker) {
                selectedMarker.setMap(null);
                markersArray.length = 0;
            }

            markersArray.push(marker);

            const selectedAddress = location.roadAddress || location.name;

            // Update the global address variable based on isDeparture
            if (isDeparture) {
                selectedDepartureAddress = selectedAddress;
            } else {
                selectedArrivalAddress = selectedAddress;
            }

            const inputFieldId = isDeparture ? 'departure-name' : 'arrival-name';
            const inputField = document.getElementById(inputFieldId);
            if (inputField) {
                inputField.value = selectedAddress;
            }
        });

        currentMarkers.push(marker);
    });

    markersArray.push(...currentMarkers);

    if (markersArray.length > 1) {
        const bounds = new naver.maps.LatLngBounds();
        markersArray.forEach(marker => bounds.extend(marker.getPosition()));
        map.fitBounds(bounds);
    } else if (markersArray.length === 1) {
        map.setCenter(markersArray[0].getPosition());
    }
}

function clearMarkers(markersArray) {
    markersArray.forEach(marker => marker.setMap(null));
    markersArray.length = 0;
}

function fetchCoordinates(address, markersArray, isDeparture, callback) {
    fetch(`/api/search-location?address=${encodeURIComponent(address)}`, {
        method: 'GET',
        headers: {
            'Content-Type': 'application/json',
            'Authorization': `Bearer ${jwt}`
        }
    })
        .then(response => {
            if (!response.ok) {
                throw new Error(`Error fetching from search-location: ${response.status} ${response.statusText}`);
            }
            return response.json();
        })
        .then(data => {
            if (data && data.places && data.places.length > 0) {
                const locations = data.places.map(place => ({
                    latitude: place.latitude,
                    longitude: place.longitude,
                    name: place.name,
                    roadAddress: place.roadAddress
                }));
                placeMarkers(locations, markersArray, isDeparture);

                if (typeof callback === 'function') {
                    callback(locations);
                }
            } else {
                console.warn(`No places found in search-location for address: ${address}`);
                return fetch(`/api/search-place?address=${encodeURIComponent(address)}`, {
                    method: 'GET',
                    headers: {
                        'Content-Type': 'application/json',
                        'Authorization': `Bearer ${jwt}`
                    }
                });
            }
        })
        .then(response => {
            if (response) {
                if (!response.ok) {
                    throw new Error(`Error fetching from search-place: ${response.status} ${response.statusText}`);
                }
                return response.json();
            }
        })
        .then(data => {
            if (data && data.places && data.places.length > 0) {
                const locations = data.places.map(place => ({
                    latitude: place.latitude,
                    longitude: place.longitude,
                    name: place.name,
                    roadAddress: place.roadAddress
                }));
                placeMarkers(locations, markersArray, isDeparture);

                if (typeof callback === 'function') {
                    callback(locations);
                }
            } else {
                console.warn(`No places found in search-place for address: ${address}`);
                console.log('Address not found in both search-location and search-place');
            }
        })
        .catch(error => {
            console.error('Error fetching coordinates:', error);
            alert('An error occurred while finding the address: ' + error.message);
        });
}

function updateMap(addressId, markersArray, isDeparture) {
    const address = document.getElementById(addressId).value;
    clearMarkers(markersArray);
    fetchCoordinates(address, markersArray, isDeparture, function(locations) {});
}

document.getElementById('departure-name').addEventListener('change', function() {
    updateMap('departure-name', departureMarkers, true);
});
document.getElementById('arrival-name').addEventListener('change', function() {
    updateMap('arrival-name', arrivalMarkers, false);
});

document.getElementById('create-plan-btn').addEventListener('click', function() {
    const title = document.getElementById('title').value;
    const arrivalDate = document.getElementById('arrival-date').value;
    const arrivalTime = document.getElementById('arrival-time').value;
    const notificationMessage = document.getElementById('notification-message').value;

    if (!title || !selectedDepartureAddress || !selectedArrivalAddress || !arrivalDate || !arrivalTime) {
        alert('Please fill in all required fields.');
        return;
    }

    const [year, month, day] = arrivalDate.split('-').map(Number);
    const [hours, minutes] = arrivalTime.split(':').map(Number);
    const arrivalAt = `${arrivalDate}T${String(hours).padStart(2, '0')}:${String(minutes).padStart(2, '0')}:00`;

    const planData = {
        title,
        departureName: selectedDepartureAddress,
        arrivalName: selectedArrivalAddress,
        arrivalAt,
        notificationMessage
    };

    fetch('/plans/create', {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json',
            'Authorization': `Bearer ${jwt}`
        },
        body: JSON.stringify(planData)
    })
        .then(response => {
            if (!response.ok) {
                return response.json().then(errorData => {
                    throw new Error(errorData.message || 'Unable to create the plan.');
                });
            }
            return response.json();
        })
        .then(data => {
            const planId = data.id;
            window.location.href = `/views/view-plan?id=${planId}`;
        })
        .catch(error => {
            alert(`An error occurred: ${error.message}`);
        });
});
