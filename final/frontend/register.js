async function handleRegister() {
    const username = document.getElementById("username").value;
    const email = document.getElementById("email").value;
    const password = document.getElementById("password").value;

    const registerUrl = "http://localhost:8080/register";
    document.getElementById("loading").style.display = "block";
    document.getElementById("message").innerText = "";

    try {
        const response = await fetch(registerUrl, {
            method: "POST",
            headers: {
                "Content-Type": "application/json"
            },
            body: JSON.stringify({ username: username, email: email, password: password })
        });

        document.getElementById("loading").style.display = "none";

        if (response.ok) {
            document.getElementById("message").style.color = "green";
            document.getElementById("message").innerText = "Registration successful! Please login.";
            setTimeout(() => {
                window.location.href = "login.html";
            }, 2000);
        } else {
            const errorData = await response.json();
            document.getElementById("message").style.color = "red";
            document.getElementById("message").innerText = errorData.message || "Registration failed!";
        }
    } catch (error) {
        document.getElementById("loading").style.display = "none"; /
        document.getElementById("message").style.color = "red";
        document.getElementById("message").innerText = "An error occurred during registration!";
        console.error("Error during registration:", error);
    }
}

function handleSocialLogin(platform) {
    alert(`Social login with ${platform} is not implemented yet.`);
}

async function handleDeleteSchedule(scheduleId) {
    const token = localStorage.getItem("token");
    if (!token) {
        alert("You need to login to perform this action.");
        window.location.href = "login.html";
        return;
    }

    const deleteUrl = `http://localhost:8080/schedule/delete/${scheduleId}`;
    document.getElementById("loading").style.display = "block";

    try {
        const response = await fetch(deleteUrl, {
            method: "DELETE",
            headers: {
                "Authorization": `Bearer ${token}`
            }
        });

        document.getElementById("loading").style.display = "none";

        if (response.ok) {
            alert("Schedule deleted successfully!");
            window.location.reload();
        } else {
            alert("Failed to delete schedule.");
        }
    } catch (error) {
        document.getElementById("loading").style.display = "none";
        alert("An error occurred while deleting the schedule. Please try again later.");
        console.error("Error deleting schedule:", error);
    }
}
