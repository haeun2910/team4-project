document.addEventListener("DOMContentLoaded", async () => {
    const token = localStorage.getItem("token");
    if (!token) {
        window.location.href = "login.html";
        return;
    }

    const scheduleId = new URLSearchParams(window.location.search).get("id");
    if (!scheduleId) {
        document.getElementById("message").innerText = "Invalid schedule ID!";
        return;
    }

    const getScheduleUrl = `http://localhost:8080/schedule/${scheduleId}`;
    try {
        const response = await fetch(getScheduleUrl, {
            method: "GET",
            headers: {
                "Authorization": `Bearer ${token}`
            }
        });

        if (response.ok) {
            const schedule = await response.json();
            document.getElementById("scheduleId").value = schedule.id;
            document.getElementById("title").value = schedule.title;
            document.getElementById("date").value = schedule.date;
            document.getElementById("time").value = schedule.time;
            document.getElementById("location").value = schedule.location;
        } else {
            document.getElementById("message").innerText = "Failed to load schedule data!";
        }
    } catch (error) {
        document.getElementById("message").innerText = "An error occurred while fetching schedule data!";
    }
});

async function handleEditSchedule() {
    const scheduleId = document.getElementById("scheduleId").value;
    const title = document.getElementById("title").value;
    const date = document.getElementById("date").value;
    const time = document.getElementById("time").value;
    const location = document.getElementById("location").value;

    const token = localStorage.getItem("token");
    const updateScheduleUrl = `http://localhost:8080/schedule/update/${scheduleId}`;

    try {
        const response = await fetch(updateScheduleUrl, {
            method: "PUT",
            headers: {
                "Content-Type": "application/json",
                "Authorization": `Bearer ${token}`
            },
            body: JSON.stringify({ title, date, time, location })
        });

        if (response.ok) {
            alert("Schedule updated successfully!");
            window.location.href = "schedule.html";
        } else {
            document.getElementById("message").innerText = "Failed to update schedule!";
        }
    } catch (error) {
        document.getElementById("message").innerText = "An error occurred while updating schedule!";
    }
}