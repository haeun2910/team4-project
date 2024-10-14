async function handleCreateSchedule() {
    const title = document.getElementById("title").value;
    const date = document.getElementById("date").value;
    const time = document.getElementById("time").value;
    const location = document.getElementById("location").value;
    const createScheduleUrl = "http://localhost:8080/schedules/create";

    const token = localStorage.getItem("token");
    if (!token) {
        alert("You need to login to create a schedule.");
        window.location.href = "login.html";
        return;
    }



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
            console.error('Error:', errorData);
            document.getElementById("message").innerText = errorData.message || "Failed to create schedule!";
        }
    } catch (error) {
        console.error('Fetch Error:', error);
        document.getElementById("message").innerText = "An error occurred while creating schedule!";
    }
}
