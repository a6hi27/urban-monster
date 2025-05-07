document.addEventListener("DOMContentLoaded", () => {
    var urlParams = new URLSearchParams(window.location.search);
    var orderId = Number(urlParams.get('orderId'));
    if (orderId) {
        getOrderDetails(orderId);
    }
});

function addItemToItemsHTML(item, itemsHTML) {
    itemsHTML += `
    <tr>
        <td>
            <div class="d-flex">
                <div class="flex-shrink-0 avatar-md bg-light rounded p-1">
                    <img src="/static/images/products/${item.product.name}.jpg" alt="${item.product.name}"
                        class="img-fluid d-block">
                </div>
                <div class="flex-grow-1 ms-3">
                    <h5 class="fs-14"><a href="apps-ecommerce-product-details.html"
                            class="text-body">${item.product.name}</a></h5>
                    <p class="text-muted mb-0">Color: <span
                            class="fw-medium">Pink</span></p>
                    <p class="text-muted mb-0">Size: <span
                            class="fw-medium">${item.product.size}</span></p>
                </div>
            </div>
        </td>
        <td>$${item.product.price}</td>
        <td>${item.quantity}</td>
        <td>
            <div class="text-warning fs-15">
                <i class="ri-star-fill"></i><i class="ri-star-fill"></i><i
                    class="ri-star-fill"></i><i class="ri-star-fill"></i><i
                    class="ri-star-half-fill"></i>
            </div>
        </td>
        <td class="fw-medium text-end">
            $${Number(item.product.price) * Number(item.quantity)}
        </td>
    </tr>`;
    return itemsHTML;
}

function addOrderDetailsToDocument(itemsHTML, order) {
    let orderDetailsCard = document.getElementById('order-details-card');
    orderDetailsCard.innerHTML += `
                    <div class="card-header">
                        <div class="d-flex align-items-center">
                            <h5 class="card-title flex-grow-1 mb-0">Order #${order.orderId}</h5>
                            <div class="flex-shrink-0">
                                <a href="/" class="btn btn-success btn-sm"><i
                                        class="ri-download-2-fill align-middle me-1"></i> Invoice</a>
                            </div>
                        </div>
                    </div>
                    <div class="card-body">
                        <div class="table-responsive table-card">
                            <table class="table table-nowrap align-middle table-borderless mb-0">
                                <thead class="table-light text-muted">
                                    <tr>
                                        <th scope="col">Product Details</th>
                                        <th scope="col">Item Price</th>
                                        <th scope="col">Quantity</th>
                                        <th scope="col">Rating</th>
                                        <th scope="col" class="text-end">Total Amount</th>
                                    </tr>
                                </thead>
                                <tbody>
                                    ${itemsHTML}
                                    <tr class="border-top border-top-dashed">
                                        <td colspan="3"></td>
                                        <td colspan="2" class="fw-medium p-0">
                                            <table class="table table-borderless mb-0">
                                                <tbody>
                                                    <tr>
                                                        <td>Sub Total :</td>
                                                        <td class="text-end">$${order.subTotal}</td>
                                                    </tr>
                                                    <tr>
                                                        <td>Shipping Charge :</td>
                                                        <td class="text-end">$${order.shippingFee}</td>
                                                    </tr>
                                                    <tr>
                                                        <td>Tax (GST) :</td>
                                                        <td class="text-end">$${order.tax}</td>
                                                    </tr>
                                                    <tr class="border-top border-top-dashed">
                                                        <th scope="row">Total (USD) :</th>
                                                        <th class="text-end">$${order.totalAmount}</th>
                                                    </tr>
                                                </tbody>
                                            </table>
                                        </td>
                                    </tr>
                                </tbody>
                            </table>
                        </div>
                    </div>
                `;
}

function addBillingAddressToDocument(order) {
    let billingAddressBody = document.getElementById('billing-address-card-body');
    billingAddressBody.innerHTML += `<ul class="list-unstyled vstack gap-2 fs-13 mb-0">
                                <li class="fw-medium fs-14">${order.user.name}</li>
                                <li>${order.user.phone}</li>
                                <li>${order.shippingAddress.addressLine1}</li>
                                ${order.shippingAddress.addressLine2 ? `<li>${order.shippingAddress.addressLine2}</li>` : ''}
                                <li>${order.shippingAddress.city}</li>
                                <li>${order.shippingAddress.zipCode}</li>
                                <li>${order.shippingAddress.state}</li>
                                <li>${order.shippingAddress.countryCode}</li>
                            </ul>`;
}

function addShippingAddressToDocument(order) {
    let shippingAddressBody = document.getElementById('shipping-address-card-body');
    shippingAddressBody.innerHTML += `<ul class="list-unstyled vstack gap-2 fs-13 mb-0">
    <li class="fw-medium fs-14">${order.user.name}</li>
    <li>${order.user.phone}</li>
    <li>${order.shippingAddress.addressLine1}</li>
    ${order.shippingAddress.addressLine2 ? `<li>${order.shippingAddress.addressLine2}</li>` : ''}
    <li>${order.shippingAddress.city}</li>
    <li>${order.shippingAddress.zipCode}</li>
    <li>${order.shippingAddress.state}</li>
    <li>${order.shippingAddress.countryCode}</li>
</ul>`;

}

function addCustomerDetailsToDocument(order) {
    let customerDetailsBody = document.getElementById('customer-details-card-body');
    customerDetailsBody.innerHTML += `<ul class="list-unstyled mb-0 vstack gap-3">
                                <li>
                                    <div class="d-flex align-items-center">
                                        <div class="flex-grow-1 ms-3">
                                            <h6 class="fs-14 mb-1">${order.user.name}</h6>
                                            <p class="text-muted mb-0">Customer</p>
                                        </div>
                                    </div>
                                </li>
                                <li><i
                                        class="ri-mail-line me-2 align-middle text-muted fs-16"></i>${order.user.email}
                                </li>
                                <li><i class="ri-phone-line me-2 align-middle text-muted fs-16"></i>${order.user.phone}
                                </li>
                            </ul>`;
}

function addPaymentDetailsToDocument(order) {
    let paymentDetailsBody = document.getElementById('payment-details-card-body');
    paymentDetailsBody.innerHTML += `<div class="d-flex align-items-center mb-2">
                                <div class="flex-shrink-0">
                                    <p class="text-muted mb-0">Transactions:</p>
                                </div>
                                <div class="flex-grow-1 ms-2">
                                    <h6 class="mb-0">${order.paypalPaymentId}</h6>
                                </div>
                            </div>
                            <div class="d-flex align-items-center mb-2">
                                <div class="flex-shrink-0">
                                    <p class="text-muted mb-0">Payment Method:</p>
                                </div>
                                <div class="flex-grow-1 ms-2">
                                    <h6 class="mb-0" id="transactions-card-payment-method">Debit Card</h6>
                                </div>
                            </div>
                            <div class="d-flex align-items-center mb-2">
                                <div class="flex-shrink-0">
                                    <p class="text-muted mb-0">Card Holder Name:</p>
                                </div>
                                <div class="flex-grow-1 ms-2">
                                    <h6 class="mb-0"></h6>
                                </div>
                            </div>
                            <div class="d-flex align-items-center mb-2">
                                <div class="flex-shrink-0">
                                    <p class="text-muted mb-0">Card Number:</p>
                                </div>
                                <div class="flex-grow-1 ms-2">
                                    <h6 class="mb-0" id="transactions-card-card-number">xxxx xxxx xxxx 2456</h6>
                                </div>
                            </div>
                            <div class="d-flex align-items-center">
                                <div class="flex-shrink-0">
                                    <p class="text-muted mb-0">Total Amount:</p>
                                </div>
                                <div class="flex-grow-1 ms-2">
                                    <h6 class="mb-0">${order.totalAmount}</h6>
                                </div>
                            </div>`;
}
function getOrderDetails(orderId) {
    const xhr = new XMLHttpRequest();
    xhr.open('GET', 'http://localhost:8080/user/order?orderId=' + Number(orderId));
    xhr.withCredentials = true;

    // Set the appropriate headers
    // xhr.setRequestHeader('app-id', 'f08356fc-9598-4d69-bf70-432e5ec5bc28');
    // xhr.setRequestHeader('app-secret', 'SGySIOv1kll81Um6Yx2AUQkv9DaoHYAg5ACSQlWtEZM');

    xhr.onload = () => {
        if (xhr.status === 200) {
            let order = JSON.parse(xhr.response);
            console.log(order);
            if (order) {
                //Render the item HTML first and move on to order details HTML element
                let itemsHTML = '';
                order.items.forEach(item => {
                    itemsHTML = addItemToItemsHTML(item, itemsHTML);
                });
                addOrderDetailsToDocument(itemsHTML, order);
                addBillingAddressToDocument(order);
                addShippingAddressToDocument(order);
                addCustomerDetailsToDocument(order);
                addPaymentDetailsToDocument(order);
                console.log('Orders fetched successfully!', order);
            }
        } else {
            alert("Orders fetch failed!");
            console.error('Orders fetch failed:', xhr.status, xhr.statusText);
        }
    };
    // Send the request
    xhr.send();

}