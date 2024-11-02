const jwt = localStorage.getItem("token");
if (!jwt) location.href = "/views/signin";

// Initialize the map
const map = new naver.maps.Map('map', {
    zoom: 10,
    center: new naver.maps.LatLng(37.3614483, 127.1114883)
});

// Create markers for departure and arrival
const departureMarker = new naver.maps.Marker({
    map: map,
    position: map.getCenter(),
    title: 'Departure',
    visible: false

});

const arrivalMarker = new naver.maps.Marker({
    map: map,
    position: map.getCenter(),
    title: 'Arrival',
    visible: false

});

// Function to determine if address is specific or general
function isSpecificAddress(address) {
    const specificPlacePattern = /(\d{1,2}번출구|입구|학교|공원|지하철|역)$/;
    return !specificPlacePattern.test(address) && /\d+/.test(address);
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

// Function to update the map with fetched coordinates
function updateMap(addressId, marker) {
    const address = document.getElementById(addressId).value;
    fetchCoordinates(address, function(coords) {
        map.setCenter(coords);
        marker.setPosition(coords);
        marker.setVisible(true);
    });
}

// Event listeners for address changes
document.getElementById('departure-name').addEventListener('change', function() {
    updateMap('departure-name', departureMarker);
});
document.getElementById('arrival-name').addEventListener('change', function() {
    updateMap('arrival-name', arrivalMarker);
});

document.getElementById('create-plan-btn').addEventListener('click', function() {
    const title = document.getElementById('title').value;
    const departureName = document.getElementById('departure-name').value;
    const arrivalName = document.getElementById('arrival-name').value;
    const arrivalDate = document.getElementById('arrival-date').value;
    const arrivalTime = document.getElementById('arrival-time').value;
    const notificationMessage = document.getElementById('notification-message').value;

    // Đảm bảo tất cả các trường đều được điền
    if (!title || !departureName || !arrivalName || !arrivalDate || !arrivalTime) {
        alert('Please fill in all required fields.');
        return;
    }

    // Kết hợp ngày và giờ thành một đối tượng Date
    const [year, month, day] = arrivalDate.split('-').map(Number);
    const [hours, minutes] = arrivalTime.split(':').map(Number);
    const arrivalAtDate = new Date(year, month - 1, day, hours, minutes); // month - 1 vì tháng trong JavaScript bắt đầu từ 0

// Thay đổi: Định dạng time thành chuỗi theo định dạng HH:mm:ss
    const formattedArrivalAt = `${String(hours).padStart(2, '0')}:${String(minutes).padStart(2, '0')}:00`;

// Gửi dữ liệu kế hoạch tới backend
    const planData = {
        title,
        departureName,
        arrivalName,
        arrivalAt: `${arrivalDate}T${formattedArrivalAt}`, // Sử dụng ngày và thời gian đã định dạng
        notificationMessage
    };

    // Gửi dữ liệu kế hoạch tới backend
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
            // Chuyển hướng tới trang View Plan bằng ID của kế hoạch mới tạo
            const planId = data.id; // Đảm bảo backend gửi ID của kế hoạch đã tạo
            window.location.href = `/views/view-plan?id=${planId}`;
        })
        .catch(error => {
            alert(`An error occurred: ${error.message}`);
        });
});
