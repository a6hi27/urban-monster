document.addEventListener("DOMContentLoaded", () => {
    getAllOrders();
});

function getAllOrders() {
    const xhr = new XMLHttpRequest();
    xhr.open('GET', 'http://localhost:8080/user/orders');
    xhr.withCredentials = true;

    // Set the appropriate headers
    // xhr.setRequestHeader('app-id', 'f08356fc-9598-4d69-bf70-432e5ec5bc28');
    // xhr.setRequestHeader('app-secret', 'SGySIOv1kll81Um6Yx2AUQkv9DaoHYAg5ACSQlWtEZM');

    xhr.onload = () => {
        if (xhr.status === 200) {
            let orders = JSON.parse(xhr.response);
            if (orders && orders.length != 0) {
                let orderContainer = document.getElementById('order-container');
                orderContainer.innerText = "";
                orders.forEach(order => {
                    orderContainer.innerHTML += `<div class="row flex-wrap mb-4">
            <div class="col-sm-4 mb-2">
                <div class="bg-secondary p-4 text-dark text-center">Order #<a href="/order-details.html?orderId=${order.orderId}" class="font-weight-semibold mr-2"
                        id="order-number">
                    </a>${order.orderId}</div>
            </div>
            <div class="col-sm-4 mb-2">
                <div class="bg-secondary p-4 text-dark text-center"><span
                        class="font-weight-semibold mr-2">Status:</span id="order-creation-status">${order.orderCreationStatus}</div>
            </div>
            <div class="col-sm-4 mb-2">
                <div class="bg-secondary p-4 text-dark text-center"><span class="font-weight-semibold mr-2"
                        id="order-paypal-payment-id">${order.paypalPaymentId}</span></div>
            </div>
            <div class="col-sm-4 mb-2">
                <a class="btn btn-primary btn-sm mt-2" href="/order-details.html?orderId=${order.orderId}" >View Order
                    Details</a>
            </div>
        </div>`;
                });
                console.log('Orders fetched successfully!', orders);
            }
        } else {
            alert("Orders fetch failed!");
            console.error('Orders fetch failed:', xhr.status, xhr.statusText);
        }
    };
    // Send the request
    xhr.send();
}