const getLoginData = () => {
    event.preventDefault();

    const username = document.getElementById("login-email").value;
    const password = document.getElementById("login-password").value;
    const role = "USER";
    console.log("inside script")

    // Create the form-encoded string
    const formData = `username=${encodeURIComponent(username)}&password=${encodeURIComponent(password)}`;

    const xhr = new XMLHttpRequest();
    xhr.open('POST', 'http://localhost:8080/login');
    xhr.withCredentials = true;

    // Set the appropriate headers
    xhr.setRequestHeader('Content-Type', 'application/x-www-form-urlencoded');
    xhr.setRequestHeader('app-id', 'f08356fc-9598-4d69-bf70-432e5ec5bc28');
    xhr.setRequestHeader('app-secret', 'SGySIOv1kll81Um6Yx2AUQkv9DaoHYAg5ACSQlWtEZM');

    xhr.onload = () => {
        if (xhr.status === 200) {
            const data = xhr.response;
            console.log(xhr.getAllResponseHeaders());
            console.log('Login successful:', data);
            window.location.href = '/index.html';
        } else {
            console.error('Login failed:', xhr.status, xhr.statusText);
        }
    };

    // Send the form-encoded data
    xhr.send(formData);
};
