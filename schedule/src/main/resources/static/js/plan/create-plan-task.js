const jwt = localStorage.getItem("token");
if (!jwt) location.href = "/views/signin";

// Retrieve planId from URL parameters
const urlParams = new URLSearchParams(window.location.search);
const planId = urlParams.get('planId');
document.getElementById('planId').value = planId; // Set the planId in the hidden field

// Function to handle form submission
document.getElementById('create-task-form').addEventListener('submit', function (e) {
    e.preventDefault();

    // Get values from form
    const title = document.getElementById('title').value;
    const time = document.getElementById('time').value;

    // Create the request payload
    const taskData = {
        title: title,
        time: time,
        planId: planId
    };

    // Fetch the JWT token from localStorage
    const jwt = localStorage.getItem("token");

    // Send a POST request to create the task
    fetch('/plans/create-plan-task', {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json',
            'Authorization': `Bearer ${jwt}`
        },
        body: JSON.stringify(taskData)
    })
        .then(response => {
            if (!response.ok) {
                throw new Error('Failed to create task');
            }
            return response.json();
        })
        .then(data => {
            window.location.href = `/views/view-plan?id=${planId}`;
        })
        .catch(error => {
            document.getElementById('message').textContent = `Error: ${error.message}`;
        });
});