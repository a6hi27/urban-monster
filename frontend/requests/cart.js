function fetchCart () {
          const xhr = new XMLHttpRequest();
               xhr.open('GET', 'http://localhost:8080/user/cart');
               xhr.withCredentials = true;

               // Set the appropriate headers
//               xhr.setRequestHeader('app-id', 'f08356fc-9598-4d69-bf70-432e5ec5bc28');
//               xhr.setRequestHeader('app-secret', 'SGySIOv1kll81Um6Yx2AUQkv9DaoHYAg5ACSQlWtEZM');


               xhr.onload = () => {
                   if (xhr.status === 200) {
                       const data = xhr.response;
                       console.log(xhr.getAllResponseHeaders());
                       console.log('Cart fetched successfully!', data);
                   } else {
                        alert("Cart failed!");
                       console.error('Cart failed:', xhr.status, xhr.statusText);
                   }
               };
               // Send the form-encoded data
               xhr.send();

        }