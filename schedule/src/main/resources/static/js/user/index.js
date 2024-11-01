let loggedIn = false;

const setUserInfo = userInfo => {
  const imageElem = document.createElement("img");
  imageElem.className = "img-thumbnail rounded";
  imageElem.src = userInfo.profileImg ?? "/static/img/user.png";

  const imgContainer = document.getElementById("img-container");
  imgContainer.innerHTML = "";
  imgContainer.appendChild(imageElem);

  document.getElementById("greeting").innerText = `Welcome, ${userInfo.name ?? userInfo.username}!!`;
  const isAdmin = userInfo.roles.includes("ROLE_ADMIN");
  const isActive = userInfo.roles.includes("ROLE_ACTIVE");
    const isSuspended = userInfo.roles.includes("ROLE_SUSPEND")
  const summary = document.getElementById("summary");

  if (isAdmin) {
    summary.innerText = "You are: ADMIN";
    document.getElementById("admin-menu").classList.remove("d-none");
  } else if (isActive) {
    summary.innerText = "You are: USER";
    document.getElementById("user-menu").classList.remove("d-none");
    } else if (isSuspended) {
          summary.innerText = "You are: INACTIVE (Suspended)";
          document.getElementById("comeback-button").classList.remove("d-none");
  }  else summary.innerText = "You are: INACTIVE";
}
//await fetch("/users/comeback", {
//      method: "PUT",
//      headers: {
//        "Authorization": `Bearer ${jwt}`,
//      }
//    })
//    .then(response => {
//      if (!response.ok) throw new Error("Failed to restore role");
//      return response.json();
//    })
//    .then(updatedUser => {
//      setUserInfo(updatedUser); // Cập nhật lại vai trò và hiển thị
//      alert("Your account has been reactivated.");
//    })
//    .catch(error => {
//      console.error("Error:", error);
//      alert("Error restoring role: " + error.message);
//    });
//  } else {
//    summary.innerText = "You are: INACTIVE";
//  }
//}

const setBaseView = () => {
  if (loggedIn) document.getElementById("authenticated").classList.remove("d-none");
  else document.getElementById("anonymous").classList.remove("d-none");
}


const jwt = localStorage.getItem("token");

if (jwt) {
  fetch("/users/get-user-info", {
    headers: {
      "Authorization": `Bearer ${jwt}`,
    },
  })
  .then(response => {
    loggedIn = response.ok;
    if (!loggedIn) localStorage.removeItem("token");
    setBaseView();
    return response.json();
  })
  .then(setUserInfo);
} else {
  setBaseView();
}


document.getElementById("comeback-button").addEventListener("click", () => {
  fetch("/users/comeback", {
    method: "PUT",
    headers: {
      "Authorization": `Bearer ${jwt}`,
      "Content-Type": "application/json"
    }
  })
  .then(response => {
    if (!response.ok) {
      throw new Error("Failed to come back to active status");
    }
    alert("Your account is now active.");
    location.reload(); // Refresh the page to update the status
  })
  .catch(error => {
    alert("Error: " + error.message);
  });
});

const logoutButton = document.getElementById("logout-button");
logoutButton.addEventListener("click", e => {
  if (loggedIn) {
    localStorage.removeItem("token");
  }
  location.href = "/views";
});
document.getElementById("show-suspend-form").addEventListener("click", () => {
  document.getElementById("suspend-form").classList.toggle("d-none");
});

// Xử lý yêu cầu khi nhấn Submit Suspension Request
document.getElementById("submit-suspend").addEventListener("click", () => {
  const suspendReason = document.getElementById("suspend-reason").value;

  if (!suspendReason) {
    alert("Please enter a reason for suspension.");
    return;
  }

  fetch("/users/suspend-req", {
    method: "PUT",
    headers: {
      "Authorization": `Bearer ${jwt}`,
      "Content-Type": "application/json"
    },
    body: JSON.stringify({ suspendReason })
  })
  .then(response => {
    if (!response.ok) throw new Error("Failed to submit suspension request");
    alert("Suspension request submitted successfully.");
    location.reload(); // Làm mới trang để cập nhật trạng thái
  })
  .catch(error => {
    alert("Error: " + error.message);
  });
});