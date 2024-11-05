
        // Lấy JWT từ URL
        const urlParams = new URLSearchParams(window.location.search);
        const token = urlParams.get('token');

        if (token) {
            // Lưu token vào localStorage
            localStorage.setItem('jwt', token);

            // Chuyển hướng đến trang chính hoặc dashboard
            window.location.href = "/views";  // Thay đổi thành trang bạn muốn chuyển hướng đến
        } else {
            console.error("Không tìm thấy token trong URL.");
        }
