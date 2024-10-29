

const jwt = localStorage.getItem("token");
if (!jwt) location.href = "/views/login";

const pageForm = document.getElementById("page-form");
const pageInput = pageForm.querySelector("input");

pageForm.addEventListener("submit", e => {
  e.preventDefault();
  const page = parseInt(pageInput.value) - 1;
  location.href = `/views/admin/users?page=${page}`;
});

const usersContainer = document.getElementById("users-container");
const setData = (requestPage) => {
  const { content, page } = requestPage;
  if (content.length === 0)
    pageInput.disabled = true;
//  else pageInput.max = page.totalPages;

  if (content.length !== 0) {
    const table = document.createElement("table");
    table.className = "table table-hover";
    const head = document.createElement("thead");
    const headTr = document.createElement("tr");
    const headTdId = document.createElement("td");
    headTdId.innerText = "ID";
    const headTdName = document.createElement("td");
    headTdName.innerText = "Name";
    const headTdEmail = document.createElement("td");
    headTdEmail.innerText = "Email";
    const headTdAge = document.createElement("td");
        headTdAge.innerText = "Age";
    const headTdPhoneNumber = document.createElement("td");
    headTdPhoneNumber.innerText = "Phone Number";
    const headTdRole = document.createElement("td");
    headTdRole.innerText = "Role";
    headTr.append(headTdId, headTdName, headTdAge, headTdEmail, headTdPhoneNumber, headTdRole);
    head.append(headTr);
    table.append(head);

    const body = document.createElement("tbody");
    content.forEach(user => {
      const row = document.createElement("tr");
      row.style.verticalAlign = "middle";
      const rowTdId = document.createElement("td");
      rowTdId.innerText = user.id;
      const rowTdName = document.createElement("td");
      rowTdName.innerText = user.name;
      const rowTdAge = document.createElement("td");
            rowTdAge.innerText = user.age;
      const rowTdEmail = document.createElement("td");
      rowTdEmail.innerText = user.email;
      const rowTdPhoneNumber = document.createElement("td");
      rowTdPhoneNumber.innerText = user.phone;
      const rowTdRole = document.createElement("td");
      rowTdRole.innerText = user.roles;
      row.append(rowTdId, rowTdName, rowTdAge, rowTdEmail, rowTdPhoneNumber, rowTdRole);
      body.append(row);
    });
    table.append(body);
    usersContainer.append(table);
  }
  else {
    const messageHead = document.createElement("h3");
    messageHead.innerText = "No Users";
    ordersContainer.append(messageHead);
  }
}


fetch("/admin/users", {
  headers: {
    "Authorization": `Bearer ${jwt}`,
  },
}).then(response => {
  if (!response.ok) {
    localStorage.removeItem("token");
    location.href = "/views/login";
  }
  return response.json();
}).then(setData);
