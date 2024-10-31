const jwt = localStorage.getItem("token");

// Function to fetch tasks and render them in the table
function loadTasks() {
    fetch("/tasks/my-tasks", {
        headers: {
            "Authorization": `Bearer ${jwt}`
        }
    })
    .then(response => response.json())
    .then(tasks => {
        const taskTableBody = document.querySelector("#task-table tbody");
        taskTableBody.innerHTML = ""; // Clear existing tasks

        tasks.content.forEach((task, index) => {
            const row = document.createElement("tr");

            // Task index
            row.innerHTML = `
                <td>${index + 1}</td>
                <td>${task.title}</td>
                <td>${task.completed ? "Completed" : "Incomplete"}</td>
                <td>
                    <button class="btn btn-sm btn-primary" onclick="updateTask(${task.id})">Update</button>
                    <button class="btn btn-sm btn-success" onclick="completeTask(${task.id})">Complete</button>
                    <button class="btn btn-sm btn-danger" onclick="deleteTask(${task.id})">Delete</button>
                </td>
            `;

            taskTableBody.appendChild(row);
        });
    })
    .catch(error => console.error("Error fetching tasks:", error));
}

function updateTask(taskId) {
    const newTitle = prompt("Enter the new title for the task:");

    if (!newTitle) {
        alert("Task title cannot be empty.");
        return;
    }


const updatedTask = {
        id: taskId,        // Assuming the task ID is needed in the DTO
        title: newTitle    // Include the new title in the DTO
    };

    fetch(`/tasks/update/${taskId}`, {
        method: "PUT",
        headers: {
            "Authorization": `Bearer ${jwt}`,
            "Content-Type": "application/json"
        },
        body: JSON.stringify(updatedTask)
    })
    .then(response => {
        if (!response.ok) throw new Error("Failed to update task");
        return response.json();
    })
    .then(updatedTask => {
        alert("Task updated successfully.");
        loadTasks(); // Reload the task list to show the updated task
    })
    .catch(error => alert("Error updating task: " + error.message));
}

function completeTask(taskId) {
    fetch(`/tasks/complete/${taskId}`, {
        method: "PUT",
        headers: {
            "Authorization": `Bearer ${jwt}`,
            "Content-Type": "application/json"
        }
    })
    .then(response => {
        if (!response.ok) throw new Error("Failed to complete task");
        loadTasks(); // Refresh task list
    })
    .catch(error => alert("Error completing task: " + error.message));
}

function deleteTask(taskId) {
    fetch(`/tasks/delete/${taskId}`, {
        method: "DELETE",
        headers: {
            "Authorization": `Bearer ${jwt}`
        }
    })
    .then(response => {
        if (!response.ok) throw new Error("Failed to delete task");
        loadTasks(); // Refresh task list
    })
    .catch(error => alert("Error deleting task: " + error.message));
}

// Load tasks on page load
document.addEventListener("DOMContentLoaded", loadTasks);
