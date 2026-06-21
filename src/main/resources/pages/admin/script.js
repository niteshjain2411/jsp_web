document.getElementById('loginForm').addEventListener('submit', function (e) {
    e.preventDefault();

    const user = document.getElementById('username').value;
    const pass = document.getElementById('password').value;
    const message = document.getElementById('message');

    if (user === 'jsp' && pass === 'jsp') {
        message.style.color = 'green';
        message.textContent = 'Login Successful! Redirecting...';
        sessionStorage.setItem("isLoggedIn", "true");
        // Redirect to your admin dashboard
         window.location.href = 'admin-console.html';
    } else {
        message.style.color = 'red';
        message.textContent = 'Invalid username or password.';
    }
});