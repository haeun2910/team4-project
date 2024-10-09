async function handleLogin() {
    const username = document.getElementById("username").value;
    const password = document.getElementById("password").value;

    const loginUrl = "http://localhost:8080/login";

    try {
        const response = await fetch(loginUrl, {
            method: "POST",
            headers: {
                "Content-Type": "application/json"
            },
            body: JSON.stringify({ username: username, password: password })
        });

        if (response.ok) {
            const data = await response.json();
            localStorage.setItem("token", data.token);
            window.location.href = "schedule.html";
        } else {
            const errorData = await response.json();
            document.getElementById("message").innerText = errorData.message || "Login failed!";
        }
    } catch (error) {
        document.getElementById("message").innerText = "An error occurred while logging in!";
    }
}

function handleSocialLogin(platform) {
    alert(`Social login with ${platform} is not implemented yet.`);
}

function handleForgotPassword() {
    const email = document.getElementById("forgotPasswordEmail").value;
    const forgotPasswordUrl = "http://localhost:8080/forgot-password";

    try {
        fetch(forgotPasswordUrl, {
            method: "POST",
            headers: {
                "Content-Type": "application/json"
            },
            body: JSON.stringify({ email: email })
        }).then(response => {
            if (response.ok) {
                alert("A reset password link has been sent to your email.");
            } else {
                alert("Failed to send reset password link. Please try again.");
            }
        }).catch(error => {
            alert("An error occurred while sending reset password link.");
        });
    } catch (error) {
        alert("An error occurred while sending reset password link.");
    }
}

function showForgotPasswordForm() {
    document.getElementById("forgotPasswordForm").style.display = "block";
}