let loggedIn = false;
const setUserInfo = userInfo => {

  const imageElem = document.createElement("img");
  imageElem.className = "img-thumbnail rounded";
  imageElem.src = userInfo.profileImg ?? "/static/img/user.png";

  const imgContainer = document.getElementById("img-container");
  imgContainer.innerHTML = "";
  imgContainer.appendChild(imageElem);

  document.getElementById("greeting").innerText = `Welcome, ${userInfo.nickname ?? userInfo.username}.`
  const isAdmin = userInfo.roles.includes("ROLE_ADMIN");
  const isActive = userInfo.roles.includes("ROLE_ACTIVE");
  const summary = document.getElementById("summary");
  if (isAdmin) {
    summary.innerText = "You are: ADMIN";
    document.getElementById("admin-menu").classList.remove("d-none");
  }
  else if (isActive) {
    summary.innerText = "You are: USER";
    document.getElementById("user-menu").classList.remove("d-none");
  }
  else summary.innerText = "You are: INACTIVE";
}

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
} else setBaseView();

const logoutButton = document.getElementById("logout-button");
logoutButton.addEventListener("click", e => {
  if (loggedIn) localStorage.removeItem("token");
  location.href = "/users";
});
