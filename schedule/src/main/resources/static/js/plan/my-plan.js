const jwt = localStorage.getItem("token");
if (!jwt) location.href = "/views/signin";

document.addEventListener("DOMContentLoaded", () => {
    fetchPlans(); // Fetch plans on page load
});

// Function to fetch all plans with pagination parameters
function fetchPlans(page = 0, size = 10) {
    fetch(`/plans/my-plans?page=${page}&size=${size}`, {
        method: 'GET',
        headers: {
            'Content-Type': 'application/json',
            'Authorization': `Bearer ${jwt}`
        }
    })
        .then(response => {
            if (!response.ok) {
                throw new Error('Failed to fetch plans');
            }
            return response.json();
        })
        .then(data => {
            displayPlans(data.content);
            setupPagination(data);
        })
        .catch(error => {
            alert(`An error occurred: ${error.message}`);
        });
}

// Function to display plans in the container
function displayPlans(plans) {
    const container = document.getElementById('plans-container');
    container.innerHTML = ''; // Clear previous plans

    plans.forEach(plan => {
        const planDiv = document.createElement('div');
        planDiv.className = 'plan';
        planDiv.innerHTML = `
            <h3 class="plan-title">${plan.title}</h3>
            <div class="plan-details">
                <span><strong>Departure:</strong> ${plan.departureName}</span>
                <span><strong>Arrival:</strong> ${plan.arrivalName}</span>
                <span><strong>Arrival At:</strong> ${new Date(plan.arrivalAt).toLocaleString()}</span>
                <span><strong>Notification Message:</strong> ${plan.notificationMessage}</span>
            </div>
            <div class="completion-checkbox">
                <input type="checkbox" id="completed-${plan.id}" ${plan.completed ? 'checked' : ''} onchange="toggleCompletion(${plan.id}, this.checked)">
<!--                <label for="completed-${plan.id}">${plan.completed ? 'Completed' : 'Not Completed'}</label>-->
            </div>
            <button onclick="viewPlan(${plan.id})">View Details</button>
            <button onclick="createTask(${plan.id})">Create Task</button>
        `;
        container.appendChild(planDiv);
    });
}

// Function to view a specific plan
function viewPlan(planId) {
    location.href = `/views/view-plan?id=${planId}`;
}

// Function to create a task for a specific plan
function createTask(planId) {
    location.href = `/views/create-plan-task?planId=${planId}`;
}

// Function to toggle completion status of a plan
function toggleCompletion(planId, isChecked) {
    fetch(`/plans/complete/${planId}`, {
        method: 'PUT',
        headers: {
            'Content-Type': 'application/json',
            'Authorization': `Bearer ${jwt}`
        }
    })
        .then(response => {
            if (!response.ok) {
                throw new Error('Failed to update completion status');
            }
            return response.json();
        })
        .then(data => {
            // Optionally, handle success feedback here
            console.log('Completion status updated:', data);
            // Refresh plans to reflect the new status
            fetchPlans();
        })
        .catch(error => {
            alert(`An error occurred: ${error.message}`);
        });
}


// Function to set up pagination controls
function setupPagination(data) {
    const paginationContainer = document.getElementById('pagination-container');
    paginationContainer.innerHTML = ''; // Clear previous pagination controls

    // Previous page button
    if (data.number > 0) {
        const prevButton = document.createElement('button');
        prevButton.innerText = 'Previous';
        prevButton.onclick = () => fetchPlans(data.number - 1);
        paginationContainer.appendChild(prevButton);
    }

    // Next page button
    if (data.number < data.totalPages - 1) {
        const nextButton = document.createElement('button');
        nextButton.innerText = 'Next';
        nextButton.onclick = () => fetchPlans(data.number + 1);
        paginationContainer.appendChild(nextButton);
    }
}
