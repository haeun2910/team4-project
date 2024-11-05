// Lấy JWT từ local storage để kiểm tra
const jwt = localStorage.getItem("token") ?? null;
if (jwt) {
    // Gửi yêu cầu để xác minh token hiện có và lấy thông tin người dùng
    fetch("/users/get-user-info", {
        headers: {
            "Authorization": `Bearer ${jwt}`,
        },
    })
    .then(response => {
        if (response.ok) location.href = "/views"; // Chuyển hướng nếu token hợp lệ
    })
    .catch(error => console.error("Error fetching user info:", error));
}

// Lấy token từ URL
const urlParams = new URLSearchParams(window.location.search);
const token = urlParams.get("token");

if (token) {
    validateTokenAndRedirect(token);
} else {
    console.error("Không tìm thấy token trong URL");
}

// Kiểm tra token từ Naver và gửi yêu cầu validate
//const token = naverLogin?.accessToken?.accessToken;
//if (token) {
//    fetch(`/users/validate?token=${token}`, {
//        method: 'GET',
//        headers: {
//            'Content-Type': 'application/json',
//        },
//    })
//    .then(response => {
//        if (!response.ok) {
//            throw new Error('Invalid token'); // Xử lý trường hợp token không hợp lệ
//        }
//        return response.json();
//    })
//    .then(json => {
//        localStorage.setItem("token", json.token); // Lưu token vào local storage
//        location.href = "/views"; // Chuyển hướng đến trang views
//    })
//    .catch(error => {
//        console.error('There was a problem with the token validation operation:', error);
//    });
//}

// Xử lý đăng nhập form
const loginForm = document.getElementById("signin-form");
const usernameInput = document.getElementById("username-input");
const passwordInput = document.getElementById("password-input");

loginForm.addEventListener("submit", e => {
    e.preventDefault();
    const username = usernameInput.value;
    const password = passwordInput.value;

    fetch("/users/signin", {
        method: "POST",
        headers: {
            "Content-Type": "application/json",
        },
        body: JSON.stringify({ username, password }),
    })
    .then(response => {
        if (response.ok) return response.json();
        else throw new Error("Failed to login");
    })
    .then(json => {
        localStorage.setItem("token", json.token); // Lưu token vào local storage
        location.href = "/views"; // Chuyển hướng đến trang views
    })
    .catch(error => alert(error.message));
});
