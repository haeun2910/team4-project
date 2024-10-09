async function handleRegister() {
    const username = document.getElementById("username").value;
    const email = document.getElementById("email").value;
    const password = document.getElementById("password").value;

    const registerUrl = "http://localhost:8080/register";

    try {
        const response = await fetch(registerUrl, {
            method: "POST",
            headers: {
                "Content-Type": "application/json"
            },
            body: JSON.stringify({ username: username, email: email, password: password })
        });

        if (response.ok) {
            document.getElementById("message").innerText = "Registration successful! Please login.";
            setTimeout(() => {
                window.location.href = "login.html";
            }, 2000);
        } else {
            const errorData = await response.json();
            document.getElementById("message").innerText = errorData.message || "Registration failed!";
        }
    } catch (error) {
        document.getElementById("message").innerText = "An error occurred during registration!";
    }
}

function handleSocialLogin(platform) {
    alert(`Social login with ${platform} is not implemented yet.`);
}
async function handleDeleteSchedule(scheduleId) {
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
