document.addEventListener("DOMContentLoaded", async () => {
    const token = localStorage.getItem("token");

    if (!token) {
        window.location.href = "login.html";
        return;
    }

    const scheduleUrl = "http://localhost:8080/schedule";
    try {
        const response = await fetch(scheduleUrl, {
            method: "GET",
            headers: {
                "Authorization": `Bearer ${token}`
            }
        });

        if (response.ok) {
            const schedules = await response.json();
            const scheduleList = document.getElementById("scheduleList");
            scheduleList.innerHTML = "";

            schedules.forEach(schedule => {
                const div = document.createElement("div");
                div.classList.add("schedule-item");
                div.innerHTML = `
                    <p>Title: ${schedule.title}</p>
                    <p>Date: ${schedule.date}</p>
                    <button onclick="editSchedule(${schedule.id})">Edit</button>
                    <button onclick="deleteSchedule(${schedule.id})">Delete</button>
                `;
                scheduleList.appendChild(div);
            });
        } else {
            console.error("Failed to fetch schedules.");
        }
    } catch (error) {
        console.error("An error occurred while fetching schedules.", error);
    }
});

function createNewSchedule() {
    window.location.href = "create_schedule.html";
}

function editSchedule(scheduleId) {
    window.location.href = `edit_schedule.html?id=${scheduleId}`;
}

async function deleteSchedule(scheduleId) {
    const token = localStorage.getItem("token");
    const deleteUrl = `http://localhost:8080/schedule/delete/${scheduleId}`;

    try {
        const response = await fetch(deleteUrl, {
            method: "DELETE",
            headers: {
                "Authorization": `Bearer ${token}`
            }
        });

        if (response.ok) {
            alert("Schedule deleted successfully!");
            window.location.reload();
        } else {
            alert("Failed to delete schedule.");
        }
    } catch (error) {
        console.error("Error deleting schedule:", error);
    }
}
