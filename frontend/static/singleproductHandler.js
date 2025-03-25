import { addCart } from './cartHandler.js';

let addToCartBtn = document.getElementById("add-to-cart-btn");
let plusBtn = document.getElementById("plus-btn");
let minusBtn = document.getElementById("minus-btn");

addToCartBtn.addEventListener("click", () => {
    let productId = document.getElementById("single-product-id").textContent.trim();
    let quantity = document.getElementById("single-product-quantity").textContent;
    addCart(productId, quantity);
});

plusBtn.addEventListener("click", () => {
    let quantityElement = document.getElementById("single-product-quantity");
    let quantity = parseInt(quantityElement.textContent, 10) + 1;
    quantityElement.textContent = quantity;
})

minusBtn.addEventListener("click", () => {
    let quantityElement = document.getElementById("single-product-quantity");
    let quantity = parseInt(quantityElement.textContent, 10);
    if (quantity > 1) {
        quantity--;
        quantityElement.textContent = quantity;
    }
})