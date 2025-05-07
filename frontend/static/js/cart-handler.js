function checkCart() {
    var cookieValue = document.cookie
        .split('; ')
        .find(row => row.startsWith('listCart='));
    if (cookieValue) {
        let storedCart = JSON.parse(cookieValue.split('=')[1]);
        listCart = new Map(storedCart);
        if (Object.keys(listCart).length === 0)
            return false;
        return true;
    }
    return false;
}

function fetchCart() {
    const xhr = new XMLHttpRequest();
    xhr.open('GET', 'http://localhost:8080/user/cart');
    xhr.withCredentials = true;

    // Set the appropriate headers
    // xhr.setRequestHeader('app-id', 'f08356fc-9598-4d69-bf70-432e5ec5bc28');
    // xhr.setRequestHeader('app-secret', 'SGySIOv1kll81Um6Yx2AUQkv9DaoHYAg5ACSQlWtEZM');

    xhr.onload = () => {
        if (xhr.status === 200) {
            let productsInCart = JSON.parse(xhr.response);
            productsInCart.forEach(cartDTO => {
                listCart.set(cartDTO.product.productId, cartDTO);
                // console.log(cartDTO.product.productId + "This is listCart")
            });
            document.cookie = "listCart=" + JSON.stringify(Array.from(listCart)) + "; path=/;";
            addCartToHTML();
            // console.log('Cart fetched successfully!', productsInCart);
        } else {
            alert("Cart failed!");
            console.error('Cart failed:', xhr.status, xhr.statusText);
        }
    };
    // Send the form-encoded data
    xhr.send();
}

function cookieExists(name) {
    var cks = document.cookie.split(';');
    for (i = 0; i < cks.length; i++)
        if (cks[i].split('=')[0].trim() == name) return true;
    return false;
}

function addCart(productId, quantity) {
    const xhr = new XMLHttpRequest();
    xhr.open('POST', 'http://localhost:8080/user/cart');
    xhr.withCredentials = true;
    let cartDTO = { "product": { "productId": productId }, "quantity": quantity }
    xhr.setRequestHeader("Content-Type", "application/json");
    // Set the appropriate headers
    //               xhr.setRequestHeader('app-id', 'f08356fc-9598-4d69-bf70-432e5ec5bc28');
    //               xhr.setRequestHeader('app-secret', 'SGySIOv1kll81Um6Yx2AUQkv9DaoHYAg5ACSQlWtEZM');

    xhr.onload = () => {
        if (xhr.status === 200) {

            let productAddedToCart = JSON.parse(xhr.response);
            console.log('Product added to Cart successfully!', productAddedToCart);

            if (!listCart.productId) {
                listCart.productId = productAddedToCart;
                listCart.productId.quantity = 1;
            } else {
                //If this product is already in the cart.
                //I just increased the quantity
                listCart.productId.quantity++;
            }
            document.cookie = "listCart=" + JSON.stringify(Array.from(listCart)) + ";path=/;";

            addCartToHTML();
        } else {
            alert("addToCart(productId) failed!");
            console.error('addToCart failed:', xhr.status, xhr.statusText);
        }
    };
    // Send the form-encoded data
    xhr.send(JSON.stringify(cartDTO));
}

function addCartToHTML() {
    // clear data default
    let listCartHTML = document.querySelector('.listCart');
    listCartHTML.innerHTML = '';

    let totalHTML = document.querySelector('.totalQuantity');
    let totalQuantity = 0;
    // if has product in Cart
    if (listCart.size > 0) {
        listCart.forEach((cartDTO, productId) => {
            if (cartDTO) {
                let newCart = document.createElement('div');
                newCart.classList.add('item');
                newCart.innerHTML =
                    `<img src="/static/images/products/${cartDTO.product.name}.jpg">
                    <div class="content">
                        <div class="name">${cartDTO.product.name}</div>
                        <div class="price">$${cartDTO.product.price} / 1 product</div>
                    </div>
                    <div class="quantity">
                        <button class="minus-btn" data-product-id="${productId}">-</button>
                        <span id="product-quantity-${productId}" class="value">${cartDTO.quantity}</span>
                        <button class="plus-btn" data-product-id="${productId}">+</button>
                    </div>`;
                listCartHTML.appendChild(newCart);
                totalQuantity += cartDTO.quantity;
                totalHTML.innerText = totalQuantity;
            }
        });

        // Attach event listeners AFTER elements are created
        document.querySelectorAll(".minus-btn").forEach(button => {
            button.addEventListener("click", () => {
                let productId = button.dataset.productId; // Get productId from the button
                changeQuantity(productId, '-');
            });
        });

        document.querySelectorAll(".plus-btn").forEach(button => {
            button.addEventListener("click", () => {
                let productId = button.dataset.productId; // Get productId from the button
                changeQuantity(productId, '+');
            });
        });
    }
}

function changeQuantity(productId, type) {
    productId = Number(productId);
    let product = listCart.get(productId);
    switch (type) {
        case '+':
            product.quantity = product.quantity + 1;
            break;
        case '-':
            if (product.quantity - 1 <= 0) {
                listCart.delete(productId);
            } else {
                product.quantity = product.quantity - 1;
            }
            break;

        default:
            break;
    }
    // save new data in cookie
    document.cookie = "listCart=" + JSON.stringify(Array.from(listCart)) + ";path=/;";
    // reload html view cart
    addCartToHTML();
}

let iconCart = document.querySelector('.iconCart');
let cart = document.querySelector('.cart');
let close = document.querySelector('.close');
let listCart = new Map();

document.addEventListener("DOMContentLoaded", () => {
    let valCheckCart = checkCart();
    // console.log(checkCart());
    if (!valCheckCart) {
        fetchCart();
    } else {
        addCartToHTML();
    }
});

iconCart.addEventListener('click', function () {
    if (cart.style.right == '-100%') {
        cart.style.right = '0';
    } else {
        cart.style.right = '-100%';
    }
})

close.addEventListener('click', function () {
    cart.style.right = '-100%';
})

export { addCart, checkCart, fetchCart, addCartToHTML };
