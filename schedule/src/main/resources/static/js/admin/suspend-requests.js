const jwt = localStorage.getItem("token");
if (!jwt) location.href = "/views/login";

const requestContainer = document.getElementById("request-container");
const pageForm = document.getElementById("page-form");
const pageInput = pageForm.querySelector("input");

pageForm.addEventListener("submit", e => {
  e.preventDefault();
  const page = parseInt(pageInput.value) - 1;
  location.href = `/views/admin/suspend-request?page=${page}`;
});

const setData = (requestPage) => {
  const { content, totalPages } = requestPage;
  if (content.length === 0) {
    pageInput.disabled = true;
  } else {
    pageInput.max = totalPages;
  }

  if (content.length !== 0) {
    const table = document.createElement("table");
    table.className = "table table-hover";

    // Tạo phần tiêu đề của bảng
    const head = document.createElement("thead");
    const headTr = document.createElement("tr");

    const headTdId = document.createElement("td");
    headTdId.innerText = "ID";

    const headTdUsername = document.createElement("td");
    headTdUsername.innerText = "Username";

    const headTdSuspendReason = document.createElement("td");
    headTdSuspendReason.innerText = "Suspend Reason";

    const headTdSuspendStartDate = document.createElement("td");
    headTdSuspendStartDate.innerText = "Suspend Start Date";

    const headTdStatus = document.createElement("td");
    headTdStatus.innerText = "Status";

    const headTdActions = document.createElement("td");
    headTdActions.innerText = "Actions";

    headTr.append(headTdId, headTdUsername, headTdSuspendReason, headTdSuspendStartDate, headTdStatus, headTdActions);
    head.append(headTr);
    table.append(head);

    // Tạo phần nội dung của bảng
    const body = document.createElement("tbody");
    content.forEach(request => {
      const row = document.createElement("tr");
      row.style.verticalAlign = "middle";

      const rowTdId = document.createElement("td");
      rowTdId.innerText = request.id;

      const rowTdUsername = document.createElement("td");
      rowTdUsername.innerText = request.username;

      const rowTdSuspendReason = document.createElement("td");
      rowTdSuspendReason.innerText = request.suspendReason;

      const rowTdSuspendStartDate = document.createElement("td");
      rowTdSuspendStartDate.innerText = request.suspendStartDate;

      const rowTdStatus = document.createElement("td");
      rowTdStatus.innerText = request.suspended ? "Suspended" : "Pending";

      const rowTdActions = document.createElement("td");

      if (!request.suspended) {
        const approveButton = document.createElement("button");
        approveButton.className = "btn btn-primary me-2";
        approveButton.innerText = "Confirm";
        approveButton.addEventListener("click", () => {
          fetch(`/admin/approveSuspend/${request.id}`, {
            method: "PUT",
            headers: {
              "Authorization": `Bearer ${jwt}`,
            },
          })
            .then(response => {
              if (response.ok) location.reload();
              else alert(`Error: ${response.status}`);
            });
        });
        rowTdActions.append(approveButton);
      }

      row.append(rowTdId, rowTdUsername, rowTdSuspendReason, rowTdSuspendStartDate, rowTdStatus, rowTdActions);
      body.append(row);
    });
    table.append(body);

    requestContainer.append(table);
  } else {
    const messageHead = document.createElement("h3");
    messageHead.innerText = "No Suspend Requests";
    requestContainer.append(messageHead);
  }
}

const pageNum = new URLSearchParams(location.search).get("page") ?? 0;

fetch(`/admin/suspend-request?page=${pageNum}`, {
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
  .then(setData)
  .catch(error => console.error("Error fetching data:", error));
