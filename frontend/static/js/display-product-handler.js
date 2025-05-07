function displayProductHandler() {

    let allProducts = document.querySelectorAll('.product');
    if (allProducts.length === 0) return;

    allProducts.forEach((product) => {
        product.addEventListener('click', function () {
            let productId = product.querySelector('.product-id').textContent;
            let productName = product.querySelector('.product-title').textContent;
            let productImage = product.querySelector('img').getAttribute('src');
            let productPrice = product.querySelector('.box-p').innerText.replace('Price: ', '');
            localStorage.setItem('itemProductId', productId)
            localStorage.setItem('itemName', productName);
            localStorage.setItem('itemImage', productImage);
            localStorage.setItem('itemPrice', productPrice);
        })
    })
}