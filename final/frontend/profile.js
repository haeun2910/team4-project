async function handleProfileUpdate(event) {
    event.preventDefault();

    const username = document.getElementById("username").value;
    const email = document.getElementById("email").value;
    const phone = document.getElementById("phone").value;

    const token = localStorage.getItem("token");
    if (!token) {
        document.getElementById("message").innerText = "You need to login first!";
        return;
    }

    const updateProfileUrl = "http://localhost:8080/profile/update";
    document.getElementById("loading").style.display = "block";
    document.getElementById("message").innerText = "";

    try {
        const response = await fetch(updateProfileUrl, {
            method: "PUT",
            headers: {
                "Content-Type": "application/json",
                "Authorization": `Bearer ${token}`
            },
            body: JSON.stringify({ username, email, phone })
        });

        document.getElementById("loading").style.display = "none";

        if (response.ok) {
            document.getElementById("message").style.color = "green";
            document.getElementById("message").innerText = "Profile updated successfully!";
        } else {
            const errorData = await response.json();
            document.getElementById("message").innerText = errorData.message || "Failed to update profile!";
        }
    } catch (error) {
        document.getElementById("loading").style.display = "none";
        document.getElementById("message").innerText = "An error occurred while updating profile!";
    }
}
