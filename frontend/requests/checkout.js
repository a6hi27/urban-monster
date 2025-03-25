 document.getElementById('place_order_btn').addEventListener('click', function () {
          const xhr = new XMLHttpRequest();
               xhr.open('GET', 'http://localhost:8080/user/checkout');
               xhr.withCredentials = true;

               // Set the appropriate headers
//               xhr.setRequestHeader('app-id', 'f08356fc-9598-4d69-bf70-432e5ec5bc28');
//               xhr.setRequestHeader('app-secret', 'SGySIOv1kll81Um6Yx2AUQkv9DaoHYAg5ACSQlWtEZM');


    // Set up a callback to handle the response
    xhr.onreadystatechange = function () {
        if (xhr.readyState === XMLHttpRequest.DONE) {
            if (xhr.status === 200) {
                try {
                    // Parse the JSON response
                    const payment = JSON.parse(xhr.responseText);

                    // Extract the approval_url from the links array
                    const approvalLink = payment.links.find(link => link.rel === "approval_url");

                    if (approvalLink) {
                        // Redirect the user to the PayPal payment page
                        window.location.href = approvalLink.href;
                    } else {
                        console.error("Approval URL not found in the response.");
                    }
                } catch (error) {
                    console.error("Error parsing response JSON:", error);
                }
            } else {
                console.error("Failed to initiate checkout. Status:", xhr.status, xhr.statusText);
            }
        }
    };

    // Handle network errors
    xhr.onerror = function () {
        console.error("Network error occurred while initiating checkout.");
    };

    xhr.send();

 });
