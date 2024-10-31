const jwt = localStorage.getItem("token");
if (!jwt) {
  location.href = "/views/signin";
} else {
  fetch("/users/get-user-info", {
    headers: {
      "Authorization": `Bearer ${jwt}`,
    },
  })
  .then(response => {
    if (!response.ok) {
      localStorage.removeItem("token");
      location.href = "/views/signin";
    }
    return response.json();
  })
  .catch(error => {
    console.error("Error fetching user info:", error);
    location.href = "/views/signin";
  });
}

document.getElementById("suspend-form").addEventListener("submit", e => {
  e.preventDefault();
  
  fetch("/users/suspend-req", {
    method: "PUT",
    headers: {
      "Authorization": `Bearer ${localStorage.getItem("token")}`,
      "Content-Type": "application/json",
    },
    body: JSON.stringify({ suspendReason: document.getElementById("suspend-reason").value })
  })
  .then(response => {
    if (response.ok) {
      alert("Request Submitted");
      location.href = "/views";
    } else {
      alert("Failed to submit request. Please try again.");
    }
  })
  .catch(error => {
    console.error("Error submitting suspension request:", error);
    alert("An error occurred. Please try again later.");
  });
});
