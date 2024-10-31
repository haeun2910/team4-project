const jwt = localStorage.getItem("token");
const planId = new URLSearchParams(window.location.search).get('id');

if (!jwt) {
    location.href = "/views/signin";
}
if (!planId) {
    alert('No plan ID provided.');
    location.href = "/views/my-plan"; // Redirect if no ID
}

document.addEventListener('DOMContentLoaded', () => {
    // Initialize elements
    const outputElement = document.getElementById('output');
    const tasksContainer = document.getElementById('tasks-container');
    const mapElement = document.getElementById('map');

    fetchPlanDetails();
    fetchTimeRemaining();

    // Function to fetch plan details and display them
    function fetchPlanDetails() {
        fetch(`/plans/${planId}`, {
            method: 'GET',
            headers: { 'Content-Type': 'application/json', 'Authorization': `Bearer ${jwt}` }
        })
            .then(response => response.ok ? response.json() : Promise.reject(`Error fetching plan: ${response.statusText}`))
            .then(plan => displayPlanDetails(plan))
            .catch(error => alert(`An error occurred: ${error.message}`));
    }

    // Display Plan and Tasks
    function displayPlanDetails(plan) {
        document.getElementById('plan-title').innerText = plan.title;
        document.getElementById('departure-name').innerText = plan.departureName;
        document.getElementById('arrival-name').innerText = plan.arrivalName;
        document.getElementById('arrival-at').innerText = new Date(plan.arrivalAt).toLocaleString();
        document.getElementById('notification-message').innerText = plan.notificationMessage;

        displayTasks(plan.planTasks);

        setupMap(plan);
    }

    // Display each task with a checkbox for completion status
    function displayTasks(tasks) {
        tasksContainer.innerHTML = '<h3>Task List:</h3>';
        tasks.forEach(task => createTaskElement(task));
        addCreateTaskButton();
    }

    function createTaskElement(task) {
        const taskElement = document.createElement('div');
        taskElement.classList.add('task-item');

        const checkbox = document.createElement('input');
        checkbox.type = 'checkbox';
        checkbox.checked = task.completed;
        checkbox.addEventListener('change', () => handleTaskCompletion(task, checkbox));

        taskElement.innerHTML = `
            <div><strong>Task Title:</strong> ${task.title}</div>
            <div><strong>Time:</strong> ${task.time} minutes</div>
            <div><strong>Completed:</strong></div>
        `;
        taskElement.appendChild(checkbox);
        tasksContainer.appendChild(taskElement);
    }

    function addCreateTaskButton() {
        const createTaskButton = document.createElement('button');
        createTaskButton.innerText = 'Create Task';
        createTaskButton.onclick = () => window.location.href = `/views/create-plan-task?planId=${planId}`;
        tasksContainer.appendChild(createTaskButton);
    }

    // Mark task as completed with API call
    function handleTaskCompletion(task, checkbox) {
        if (checkbox.checked) {
            fetch(`/tasks/complete/${task.id}`, {
                method: 'PUT',
                headers: { 'Content-Type': 'application/json', 'Authorization': `Bearer ${jwt}` }
            })
                .then(response => response.ok ? response.text() : Promise.reject(`Error completing task: ${response.statusText}`))
                .then(message => alert(message))
                .catch(error => {
                    alert(`An error occurred: ${error.message}`);
                    checkbox.checked = false;
                });
        } else {
            alert('Task cannot be marked as incomplete.');
            checkbox.checked = true;
        }
    }

    // Initialize map and place markers for departure and arrival
    function setupMap(plan) {
        const map = new naver.maps.Map(mapElement, { zoom: 10, center: new naver.maps.LatLng(37.3614483, 127.1114883) });
        const departureMarker = createMarker(map, 'Departure');
        const arrivalMarker = createMarker(map, 'Arrival');

        fetchCoordinates(plan.departureName, coords => {
            map.setCenter(coords);
            departureMarker.setPosition(coords);
        });
        fetchCoordinates(plan.arrivalName, coords => arrivalMarker.setPosition(coords));
    }

    function createMarker(map, title) {
        return new naver.maps.Marker({ map: map, title: title });
    }

    // Fetch time remaining information
    function fetchTimeRemaining() {
        outputElement.innerHTML = '';
        const transportTypes = ['publicTransport', 'carOrTaxi'];

        Promise.all(transportTypes.map(type => fetchTimeByType(type)))
            .then(([publicTransportInfo, carOrTaxiInfo]) => displayTimeRemaining(publicTransportInfo, carOrTaxiInfo))
            .catch(error => outputElement.innerHTML = `<p>An error occurred while fetching data: ${error.message}</p>`);
    }

    function fetchTimeByType(transportType) {
        return fetch(`/plans/time-remaining/${planId}?transportType=${transportType}`, {
            method: 'GET',
            headers: { 'Content-Type': 'application/json', 'Authorization': `Bearer ${jwt}` }
        })
            .then(response => response.ok ? response.json() : Promise.reject(`Error fetching time remaining: ${response.statusText}`));
    }

    function displayTimeRemaining(publicTransportInfo, carOrTaxiInfo) {
        outputElement.innerHTML = `
            <div class="transport-info">
                <p><strong>Time Remaining:</strong> ${carOrTaxiInfo.remainingTime.hours} hours and ${carOrTaxiInfo.remainingTime.minutes} minutes</p>
                <p><strong>Total Ready Time:</strong> ${carOrTaxiInfo.totalReadyTimeAsMins} minutes</p>
            </div>
            <div class="average-info">
                <h3>Transport Information</h3>
                <div>
                    <h4>Public Transport</h4>
                    <p><strong>Average Time:</strong> ${publicTransportInfo.routeAverageTimeAsMins} minutes</p>
                    <p><strong>Recommended Departure Time:</strong> ${new Date(publicTransportInfo.recommendedDepartureTime).toLocaleString()}</p>
                    <button id="view-route-public">View Route for Public Transport</button>
                </div>
                <div>
                    <h4>Car / Taxi</h4>
                    <p><strong>Average Time:</strong> ${carOrTaxiInfo.routeAverageTimeAsMins} minutes</p>
                    <p><strong>Recommended Departure Time:</strong> ${new Date(carOrTaxiInfo.recommendedDepartureTime).toLocaleString()}</p>
                    <button id="view-route-car">View Route for Car / Taxi</button>
                </div>
            </div>
        `;
        document.getElementById('view-route-public').addEventListener('click', () => viewRoute('public'));
        document.getElementById('view-route-car').addEventListener('click', () => viewRoute('car'));
    }

    function viewRoute(transportType) {
        const routePath = transportType === 'public' ? 'pub-trans-route' : 'car-taxi-route';
        window.location.href = `/views/${routePath}?planId=${planId}`;
    }

    // Determine if an address is specific
    function isSpecificAddress(address) {
        return !/(\d{1,2}번출구|입구|학교|공원|지하철|역)$/.test(address) && /\d+/.test(address);
    }

    // Fetch coordinates based on address
    function fetchCoordinates(address, callback) {
        const endpoint = isSpecificAddress(address) ? 'search-location' : 'search-place';

        fetch(`/api/${endpoint}?address=${encodeURIComponent(address)}`, {
            method: 'GET',
            headers: { 'Content-Type': 'application/json', 'Authorization': `Bearer ${jwt}` }
        })
            .then(response => response.ok ? response.json() : Promise.reject(`Server returned ${response.status}: ${response.statusText}`))
            .then(data => {
                if (data?.places?.length) {
                    const coords = new naver.maps.LatLng(data.places[0].latitude, data.places[0].longitude);
                    callback(coords);
                } else {
                    alert('Address not found.');
                }
            })
            .catch(error => alert('An error occurred while finding the address.'));
    }
});
