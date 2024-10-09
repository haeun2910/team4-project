async function handleCreateSchedule() {
    const title = document.getElementById("title").value;
    const date = document.getElementById("date").value;
    const time = document.getElementById("time").value;
    const location = document.getElementById("location").value;

    const token = localStorage.getItem("token");
    if (!token) {
        window.location.href = "login.html";
        return;
    }

    const createScheduleUrl = "http://localhost:8080/schedule/create";

    try {
        const response = await fetch(createScheduleUrl, {
            method: "POST",
            headers: {
                "Content-Type": "application/json",
                "Authorization": `Bearer ${token}`
            },
            body: JSON.stringify({ title, date, time, location })
        });

        if (response.ok) {
            alert("Schedule created successfully!");
            window.location.href = "schedule.html";
        } else {
            const errorData = await response.json();
            document.getElementById("message").innerText = errorData.message || "Failed to create schedule!";
        }
    } catch (error) {
        document.getElementById("message").innerText = "An error occurred while creating schedule!";
    }
}