function getAllProducts() {
    const xhr = new XMLHttpRequest();
    xhr.open('GET', 'http://localhost:8080/product');
    xhr.withCredentials = true;

    // Set the appropriate headers
    //               xhr.setRequestHeader('app-id', 'f08356fc-9598-4d69-bf70-432e5ec5bc28');
    //               xhr.setRequestHeader('app-secret', 'SGySIOv1kll81Um6Yx2AUQkv9DaoHYAg5ACSQlWtEZM');
    xhr.onload = () => {
        if (xhr.status === 200) {
            let allProducts = JSON.parse(xhr.response);
            console.log(allProducts);
            let firstBoxIndex = 0;
            const boxesDisplayedPerPage = 8;
            let lastBoxIndex = firstBoxIndex + boxesDisplayedPerPage;
            let pageNumber;

            //updates page number
            function updatePage(allProductsDisplayed) {
                for (let i = 0; i < allProductsDisplayed.length; i++) {
                    if (i >= firstBoxIndex && i < lastBoxIndex) {
                        allProductsDisplayed[i].style.display = "flex"
                    }
                    else {
                        allProductsDisplayed[i].style.display = "none"
                    }
                }
                pageNumber = (firstBoxIndex / boxesDisplayedPerPage) + 1;
                document.querySelector('#page-number').innerHTML = pageNumber;
            }

            // updates page when "next" button is clicked
            function nextPage(allProductsDisplayed) {
                if (lastBoxIndex < allProductsDisplayed.length) {
                    firstBoxIndex = firstBoxIndex + boxesDisplayedPerPage;
                    lastBoxIndex = firstBoxIndex + boxesDisplayedPerPage;
                }

                lastBoxIndex = Math.min(lastBoxIndex, allProductsDisplayed.length)
                updatePage(allProductsDisplayed)
            }

            // updates page when "prev" button is clicked
            function prevPage(allProductsDisplayed) {
                if (firstBoxIndex >= boxesDisplayedPerPage) {
                    firstBoxIndex = firstBoxIndex - boxesDisplayedPerPage;
                    lastBoxIndex = firstBoxIndex + boxesDisplayedPerPage;
                }
                updatePage(allProductsDisplayed)
            }

            // creates & organizes all_products info as html, for display
            function displayProducts(productsArray) {
                console.log("This is display products " + productsArray.length)
                let productContentHtml = '';
                let length = productsArray.length;
                for (let i = 0; i < length; i++) {
                    productContentHtml += `
                                       <a href="product.html" style="text-decoration: none">
                                           <div class="product" style="display: none">
                                           <div class="product-id" style="display:none">${productsArray[i].productId}
                                           </div>
                                               <div class="product-img">
                                                   <img src="static/images/products/${productsArray[i].name}.jpg"
                                                   alt="${productsArray[i].name}">
                                               </div>
                                               <div class="product-section-1">
                                                   <div class="product-title">${productsArray[i].name}</div>
                                               </div>
                                               <div class="product-section-2">
                                                   <div class="box-p">Price: $${productsArray[i].price}.00</div>
                                                   <div>
                                                       <button class="cart-btn">View Product</button>
                                                   </div>
                                               </div>
                                           </div>
                                       </a>`
                }

                productContent.innerHTML = productContentHtml;
                displayProductHandler();
            }

            // deals with page button clicks (All products, Sofas, Chairs, Beds)
            function button_clicked(btn, buttons, allProducts) {
                // first sets all buttons to the "unclicked" theme colors (light bg, dark text)
                for (let i = 0; i < buttons.length; i++) {
                    buttons[i].style.backgroundColor = 'rgb(229, 229, 229)';
                    buttons[i].style.color = 'rgb(50, 50, 50)';
                }

                // highlights current page button clicked
                btn.style.backgroundColor = 'rgb(205, 172, 72)';
                btn.style.color = 'white';

                let contentType = btn.innerText.toLowerCase(); // the current page button, in lowercase
                let currentProductType = allProducts.filter(obj => obj.type === contentType);
                // currentProductType returns part of the "all_products" array/dict where key=type, value=contentType (e.g: sofas) (check productStorage.js)
                console.log("current: ", currentProductType)

                if (contentType === "all products") {
                    displayProducts(allProducts);
                }
                else {
                    displayProducts(currentProductType);
                }

                firstBoxIndex = 0;
                lastBoxIndex = firstBoxIndex + boxesDisplayedPerPage;

                let allProductsDisplayed = document.querySelectorAll('.product');
                updatePage(allProductsDisplayed);
            }

            // all below deals with button clicks (start, end, next, prev)
            let buttons = document.querySelectorAll('.product-type-btn');
            let productContent = document.querySelector('#product-content');

            // sets "All Products" to be coloured by default when page loads
            // and displays all products
            buttons.forEach(function (btn) {
                // when function first loads, "All Products" button should be highlighted
                if (btn.innerText === "All Products") {
                    btn.style.backgroundColor = 'rgb(205, 172, 72)';
                    btn.style.color = 'white';
                    displayProducts(allProducts);

                    let allProductsDisplayed = document.getElementsByClassName('product');
                    updatePage(allProductsDisplayed) // updates page number
                    buttonHandler(allProductsDisplayed) // event listeners for next, prev, first & last buttons
                }

                // page button click (All Products, Sofas, Beds, Chairs)
                btn.addEventListener('click', function () {
                    button_clicked(btn, buttons, allProducts);
                })
            })

            // start, end, next, previous page button click
            function buttonHandler(allProductsDisplayed) {
                let clickNext = document.getElementById('next-page-btn');
                let clickPrev = document.getElementById('prev-page-btn');
                let firstPage = document.querySelector('#first-page-btn');
                let lastPage = document.querySelector('#last-page-btn');

                clickNext.addEventListener('click', function () {
                    nextPage(allProductsDisplayed)
                })

                clickPrev.addEventListener('click', function () {
                    prevPage(allProductsDisplayed)
                })

                firstPage.addEventListener('click', function () {
                    firstBoxIndex = 0;
                    lastBoxIndex = firstBoxIndex + boxesDisplayedPerPage;
                    updatePage(allProductsDisplayed)
                })

                lastPage.addEventListener('click', function () {
                    lastBoxIndex = allProductsDisplayed.length;
                    if ((allProductsDisplayed.length % boxesDisplayedPerPage) == 0) {
                        firstBoxIndex = lastBoxIndex - boxesDisplayedPerPage;
                    }
                    else {
                        firstBoxIndex = parseInt(allProductsDisplayed.length / boxesDisplayedPerPage) * boxesDisplayedPerPage;
                    }
                    updatePage(allProductsDisplayed)
                })
            }
            //                           })
        } else {
            alert("Product fetch failed!");
            console.error('Product fetch failed:', xhr.status, xhr.statusText);
        }
    };
    xhr.send();
}
