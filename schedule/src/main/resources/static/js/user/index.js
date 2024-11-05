
document.addEventListener('DOMContentLoaded', function() {
    var calendarEl = document.getElementById('calendar');
    var calendar = new FullCalendar.Calendar(calendarEl, {
        initialView: 'dayGridMonth',
        headerToolbar: {
            left: 'prev,next today',
            center: 'title',
            right: ''
        },
        events: [], // Start with an empty events array
        timeZone: 'UTC',
        selectable: true,
        eventClick: function(info) {
            alert('You have ' + info.event.title + ". " + 'Click "View my plan" to see details');
        }
    });

    // Fetch plans and populate the calendar
    fetch('plans/my-plans', {
        method: 'GET',
        headers: {
            'Content-Type': 'application/json',
            'Authorization': 'Bearer ' + localStorage.getItem('token') // Assuming you're using JWT for auth
        }
    })
        .then(response => {
            if (!response.ok) {
                throw new Error('Network response was not ok');
            }
            return response.json();
        })
        .then(data => {
            const events = {};

            // Process the received data to count plans per arrival date
            data.content.forEach(plan => {
                const arrivalDate = new Date(plan.arrivalAt); // Extract arrival date
                const dateString = arrivalDate.toISOString().split('T')[0]; // Get the date in YYYY-MM-DD format

                // Initialize event count for the date if it doesn't exist
                if (!events[dateString]) {
                    events[dateString] = 0;
                }
                events[dateString]++; // Increment the count for this date
            });

            // Convert the counts to FullCalendar events
            Object.keys(events).forEach(date => {
                calendar.addEvent({
                    title: events[date] + ' plan ', // Display number of plans
                    start: date,
                    allDay: true
                });
            });

            // Render the calendar
            calendar.render();
        })
        .catch(error => {
            console.error('There was a problem with the fetch operation:', error);
        });

    // Render the calendar
    calendar.render();
});

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
    const isSuspended = userInfo.roles.includes("ROLE_SUSPEND");
    const summary = document.getElementById("summary");

    if (isAdmin) {
        summary.innerText = "You are: ADMIN";
        document.getElementById("admin-menu").classList.remove("d-none");
    } else if (isActive) {
        summary.innerText = "You are: USER";
        document.getElementById("user-menu").classList.remove("d-none");
    } else if (isSuspended) {
        summary.innerText = "You are: INACTIVE (Suspended)";
        summary.innerText = "Click comeback to use";
        document.getElementById("comeback-button").classList.remove("d-none");
    } else {
        summary.innerText = "You are: INACTIVE";
        summary.innerText = "Update profile to use";
    }
}

const setBaseView = () => {
    const anonymousSection = document.getElementById("anonymous");
    const authenticatedSection = document.getElementById("authenticated");
    const welcomeText = document.querySelector(".welcome-text");
    const leftImage = document.querySelector(".left-image");
    const calender = document.getElementById("calendar")

    if (loggedIn) {
        authenticatedSection.classList.remove("d-none");
        anonymousSection.classList.add("d-none");
        document.body.classList.remove("background-anonymous");
        welcomeText.classList.add("d-none");
        leftImage.classList.add("d-none"); // Remove background for authenticated users
        calender.classList.remove("d-none");
    } else {
        anonymousSection.classList.remove("d-none");
        authenticatedSection.classList.add("d-none");
        document.body.classList.add("background-anonymous");
        welcomeText.classList.remove("d-none");
        leftImage.classList.remove("d-none"); // Show background for anonymous users
        calender.classList.add("d-none");
    }
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

// Handle suspension request submission
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
            if (!response.ok) throw new Error("Failed to submit suspension request.");
            alert("Suspension request submitted successfully. Wait for confirmation.");
            location.reload(); // Refresh the page to update the status
        })
        .catch(error => {
            alert("Error: " + error.message);
        });
});
