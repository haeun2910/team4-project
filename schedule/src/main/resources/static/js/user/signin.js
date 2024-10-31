const jwt = localStorage.getItem("token") ?? null;
if (jwt) fetch("/users/get-user-info", {
  headers: {
    "Authorization": `Bearer ${jwt}`,
  },
}).then(response => {
  if (response.ok) location.href = "/views";
})

const loginForm = document.getElementById("signin-form");
const usernameInput = document.getElementById("username-input");
const passwordInput = document.getElementById("password-input");
loginForm.addEventListener("submit", e => {
  e.preventDefault();
  const username = usernameInput.value;
  const password = passwordInput.value;
  fetch("/users/signin", {
    method: "post",
    headers: {
      "Content-Type": "application/json",
    },
    body: JSON.stringify({ username, password }),
  })
      .then(response => {
        if (response.ok) return response.json();
        else throw Error("failed to login");
      })
      .then(json => {
        localStorage.setItem("token", json.token);
        location.href = "/views/update";
      })
      .catch(error => alert(error.message));
          console.error(error);

});
