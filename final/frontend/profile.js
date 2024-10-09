async function handleProfileUpdate() {
    const username = document.getElementById("username").value;
    const email = document.getElementById("email").value;
    const phone = document.getElementById("phone").value;

    const token = localStorage.getItem("token");
    const updateUrl = "http://localhost:8080/user/update";

    try {
        const response = await fetch(updateUrl, {
            method: "PUT",
            headers: {
                "Content-Type": "application/json",
                "Authorization": `Bearer ${token}`
            },
            body: JSON.stringify({ username, email, phone })
        });

        if (response.ok) {
            alert("Profile updated successfully!");
        } else {
            const errorData = await response.json();
            document.getElementById("message").innerText = errorData.message || "Failed to update profile!";
        }
    } catch (error) {
        document.getElementById("message").innerText = "An error occurred while updating profile!";
    }
}
