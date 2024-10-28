const jwt = localStorage.getItem("token");
if (!jwt) location.href = "/views/signin";

// Function to fetch all plans
function fetchPlans(page = 0, size = 10) { // Add pagination parameters
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
            displayPlans(data.content); // Access the plans array
            setupPagination(data); // Setup pagination controls
        })
        .catch(error => {
            alert(`An error occurred: ${error.message}`);
        });
}

// Function to display plans in the container
function displayPlans(plans) {
    const container = document.getElementById('plans-container');
    container.innerHTML = '';

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
                <button onclick="viewPlan(${plan.id})">View Details</button>
            `;
        container.appendChild(planDiv);
    });
}
function viewPlan(planId) {
    location.href = `/views/view-plan?id=${planId}`; // Redirect to view-plan.html with planId in the URL
}

// Function to set up pagination
function setupPagination(data) {
    const paginationContainer = document.createElement('div');
    paginationContainer.className = 'pagination';

    // Add previous page button
    if (data.number > 0) {
        const prevButton = document.createElement('button');
        prevButton.innerText = 'Previous';
        prevButton.onclick = () => fetchPlans(data.number - 1);
        paginationContainer.appendChild(prevButton);
    }

    // Add next page button
    if (data.number < data.totalPages - 1) {
        const nextButton = document.createElement('button');
        nextButton.innerText = 'Next';
        nextButton.onclick = () => fetchPlans(data.number + 1);
        paginationContainer.appendChild(nextButton);
    }

    document.body.appendChild(paginationContainer); // Append to body or a specific container
}

// Fetch plans on page load
fetchPlans();