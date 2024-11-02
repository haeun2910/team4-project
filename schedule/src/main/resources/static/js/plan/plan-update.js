// Retrieve JWT token from local storage
const jwt = localStorage.getItem("token");
if (!jwt) location.href = "/views/signin"; // Redirect if not authenticated

// Get the plan ID from the URL parameters
const urlParams = new URLSearchParams(window.location.search);
const planId = urlParams.get('id');

// Initialize the map
const mapElement = document.getElementById('map');
const map = new naver.maps.Map(mapElement, {
    zoom: 10,
    center: new naver.maps.LatLng(37.3614483, 127.1114883) // Default center
});

// Create markers for departure and arrival
const departureMarker = new naver.maps.Marker({
    map: map,
    title: 'Departure'
});

const arrivalMarker = new naver.maps.Marker({
    map: map,
    title: 'Arrival'
});

// Function to fetch plan details and populate the form and map
function fetchPlanDetails() {
    fetch(`/plans/${planId}`, {
        method: 'GET',
        headers: {
            'Content-Type': 'application/json',
            'Authorization': `Bearer ${jwt}`
        }
    })
        .then(response => {
            if (!response.ok) {
                throw new Error('Failed to fetch plan details');
            }
            return response.json();
        })
        .then(plan => {
            // Populate the form fields with the plan details
            document.getElementById('title').value = plan.title;
            document.getElementById('departure-name').value = plan.departureName;
            document.getElementById('arrival-name').value = plan.arrivalName;
            document.getElementById('arrival-date').value = plan.arrivalAt.split('T')[0];
            document.getElementById('arrival-time').value = plan.arrivalAt.split('T')[1].substring(0, 5);
            document.getElementById('notification-message').value = plan.notificationMessage;

            // Set initial marker positions
            const departureCoords = new naver.maps.LatLng(plan.departureLatitude, plan.departureLongitude);
            const arrivalCoords = new naver.maps.LatLng(plan.arrivalLatitude, plan.arrivalLongitude);
            departureMarker.setPosition(departureCoords);
            arrivalMarker.setPosition(arrivalCoords);
            map.setCenter(departureCoords); // Center map on departure location

            // Update map markers
            fetchCoordinates(plan.departureName, coords => {
                departureMarker.setPosition(coords);
                map.setCenter(coords); // Center on departure
            });
            fetchCoordinates(plan.arrivalName, coords => {
                arrivalMarker.setPosition(coords);
            });
        })
        .catch(error => {
            alert(`An error occurred: ${error.message}`);
        });
}

// Function to fetch coordinates based on address type
function isSpecificAddress(address) {
    return !/(\d{1,2}번출구|입구|학교|공원|지하철|역)$/.test(address) && /\d+/.test(address);
}

// Fetch coordinates based on address
function fetchCoordinates(address, callback) {
    const endpoint = isSpecificAddress(address) ? 'search-location' : 'search-place';

    console.log(`Fetching coordinates for address: ${address} using endpoint: ${endpoint}`);

    fetch(`/api/${endpoint}?address=${encodeURIComponent(address)}`, {
        method: 'GET',
        headers: {
            'Content-Type': 'application/json',
            'Authorization': `Bearer ${jwt}`
        }
    })
        .then(response => {
            console.log(`Response status for ${address}:`, response.status);
            if (!response.ok) {
                throw new Error(`Server returned ${response.status}: ${response.statusText}`);
            }
            return response.json();
        })
        .then(data => {
            console.log('Data received:', data);
            if (data && data.places && data.places.length > 0) {
                const coords = new naver.maps.LatLng(data.places[0].latitude, data.places[0].longitude);
                callback(coords);
            } else {
                alert('Address not found.');
            }
        })
        .catch(error => {
            console.error('Error fetching coordinates:', error);
            alert(`An error occurred while finding the address: ${error.message}`);
        });
}


// Function to update the map with fetched coordinates
function updateMap(addressId, marker) {
    const address = document.getElementById(addressId).value;
    fetchCoordinates(address, function(coords) {
        map.setCenter(coords);
        marker.setPosition(coords);
    });
}

// Event listeners for address changes
document.getElementById('departure-name').addEventListener('change', function() {
    updateMap('departure-name', departureMarker);
});
document.getElementById('arrival-name').addEventListener('change', function() {
    updateMap('arrival-name', arrivalMarker);
});

// Handle the update plan button click
document.getElementById('update-plan-btn').addEventListener('click', function() {
    const title = document.getElementById('title').value;
    const departureName = document.getElementById('departure-name').value;
    const arrivalName = document.getElementById('arrival-name').value;
    const arrivalDate = document.getElementById('arrival-date').value;
    const arrivalTime = document.getElementById('arrival-time').value;
    const notificationMessage = document.getElementById('notification-message').value;

    // Ensure all fields are filled
    if (!title || !departureName || !arrivalName || !arrivalDate || !arrivalTime) {
        alert('Please fill in all required fields.');
        return;
    }
    // Combine date and time into a Date object without timezone
    const arrivalDateTime = new Date(`${arrivalDate}T${arrivalTime}:00`);

// Format the date string to LocalDateTime format without timezone information
    const formattedArrivalDateTime = arrivalDateTime.toISOString().slice(0, 19); // Excludes the "Z"

    const updatedPlan = {
        title,
        departureName,
        arrivalName,
        arrivalAt: formattedArrivalDateTime, // Use the formatted date string
        notificationMessage,
        departureLatitude: departureMarker.getPosition().lat(),
        departureLongitude: departureMarker.getPosition().lng(),
        arrivalLatitude: arrivalMarker.getPosition().lat(),
        arrivalLongitude: arrivalMarker.getPosition().lng()
    };


    console.log('Updating plan with:', updatedPlan); // Log updated plan data

    // Update the plan
    fetch(`/plans/update/${planId}`, {
        method: 'PUT',
        headers: {
            'Content-Type': 'application/json',
            'Authorization': `Bearer ${jwt}`
        },
        body: JSON.stringify(updatedPlan)
    })
        .then(response => {
            console.log('Response status:', response.status); // Log the status
            // Check if the response is successful (status in the range 200-299)
            if (!response.ok) {
                return response.json().then(data => {
                    throw new Error(data.message || 'Failed to update the plan');
                });
            }
            return response.json(); // Handle the successful response
        })
        .then(data => {
            alert('Plan updated successfully!');
            window.location.href = `/views/my-plan`; // Redirect after update
        })
        .catch(error => {
            alert(`An error occurred: ${error.message}`);
        });

});

// Fetch plan details on page load
fetchPlanDetails();
