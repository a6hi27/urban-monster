let listCart = new Map();
var cookieValue = document.cookie
    .split('; ')
    .find(row => row.startsWith('listCart='));
let storedCart = JSON.parse(cookieValue.split('=')[1]);
let checkoutBtn = document.querySelector(".checkout-btn");
listCart = new Map(storedCart);

document.addEventListener("click", () => {
    initiateCheckout();
})

addCartToCheckoutHTML();


function addCartToCheckoutHTML() {
    // clear data default
    let listCartHTML = document.querySelector('.return-cart .list');
    listCartHTML.innerHTML = '';

    let totalQuantityHTML = document.querySelector('.total-quantity');
    let totalPriceHTML = document.querySelector('.total-price');
    let totalQuantity = 0;
    let totalPrice = 0;
    // if has product in Cart
    if (listCart.size > 0) {
        listCart.forEach((cartDTO, productId) => {
            if (cartDTO) {
                let newCart = document.createElement('div');
                newCart.classList.add('item');
                newCart.innerHTML =
                    `<img src="/static/images/products/${cartDTO.product.name}.jpg">
                    <div class="info">
                        <div class="product-id" style="display:none">${productId}</div>
                        <div class="product-name">${cartDTO.product.name}</div>
                        <div class="product-price">$${cartDTO.product.price}/1 product</div>
                    </div>
                    <div class="product-quantity">${cartDTO.quantity}</div>
                    <div class="product-total">$${cartDTO.product.price * cartDTO.quantity}</div>`;
                listCartHTML.appendChild(newCart);
                totalQuantity = totalQuantity + cartDTO.quantity;
                totalPrice = totalPrice + (cartDTO.product.price * cartDTO.quantity);
            }
        })
    }
    totalQuantityHTML.innerText = totalQuantity;
    totalPriceHTML.innerText = '$' + totalPrice;
}

function initiateCheckout() {
    const xhr = new XMLHttpRequest();
    xhr.open('GET', 'http://localhost:8080/user/checkout');
    xhr.withCredentials = true;

    // Set the appropriate headers
    // xhr.setRequestHeader('app-id', 'f08356fc-9598-4d69-bf70-432e5ec5bc28');
    // xhr.setRequestHeader('app-secret', 'SGySIOv1kll81Um6Yx2AUQkv9DaoHYAg5ACSQlWtEZM');

    xhr.onload = () => {
        if (xhr.status === 200) {
            let paypalCheckoutLink = xhr.response;
            console.log(paypalCheckoutLink);
            window.location.replace(paypalCheckoutLink);
        } else {
            alert("Checkout initiation failed!");
            console.error(xhr.status, xhr.statusText);
        }
    };
    // Send the form-encoded data
    xhr.send();
}

