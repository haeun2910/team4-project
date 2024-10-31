// Select the task list element
const taskList = document.getElementById("task-list");
const jwt = localStorage.getItem("token");


// Fetch and display tasks
function fetchTasks() {
//    const jwt = localStorage.getItem("token");  // Get JWT from local storage

    // Make a GET request to retrieve the user's tasks
    fetch("/tasks/my-tasks", {
        method: "GET",
        headers: {
            "Authorization": `Bearer ${jwt}`
        }
    })
    .then(response => {
        if (!response.ok) throw new Error("Failed to fetch tasks");  // Handle unsuccessful response
        return response.json();
    })
    .then(data => {
       taskList.innerHTML = "";
               data.content.forEach((task, index) => { // Correctly use index here
                   const taskItem = document.createElement("li");
                   taskItem.className = "list-group-item";
                   // Display task number and title
                   taskItem.textContent = `${index + 1}. ${task.title}`;
                   taskList.appendChild(taskItem);
        });
    })
    .catch(error => {
        alert("Error: " + error.message);  // Display error message if request fails
    });
}

// Call fetchTasks when the page loads to display all tasks
fetchTasks();

// Event listener for task creation form submission
document.getElementById("create-task-form").addEventListener("submit", (e) => {
    e.preventDefault();  // Prevent the default form submission

    // Get the task title from the input field
    const title = document.getElementById("title").value;

    // Make a POST request to create a new task
    fetch("/tasks/create", {
        method: "POST",
        headers: {
            "Authorization": `Bearer ${jwt}`,
            "Content-Type": "application/json"
        },
        body: JSON.stringify({ title })
    })
    .then(response => {
        if (!response.ok) throw new Error("Failed to create task");
        return response.json();
    })
    .then(data => {
        fetchTasks();  // Refresh task list after creating a new task
        document.getElementById("create-task-form").reset();
    })
    .catch(error => {
        alert("Error: " + error.message);
    });
});
document.getElementById("back-button").addEventListener("click", () => {
    location.href = "/views/my-tasks"; // Update with the correct path to your tasks view
});
