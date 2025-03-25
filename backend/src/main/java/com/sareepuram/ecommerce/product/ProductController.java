package com.sareepuram.ecommerce.product;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;



@RestController
public class ProductController {
    @Autowired
    private ProductService productService;

    @GetMapping("product")
    public ResponseEntity<?> getProducts(@RequestParam(required = false) String searchQuery, @RequestParam(required = false) Integer id) {

        // Gets products using the searchQuery
        if (searchQuery != null) {
            try {
                Optional<Product> product = productService.findByName(searchQuery.toLowerCase());
                if (product.isPresent())
                    return ResponseEntity.status(HttpStatus.OK).body(product.get());
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            } catch (IllegalArgumentException e) {
                e.printStackTrace();
                return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
            }
        }

        // Gets products using the product id
        if (id != null) {
            try {
                Optional<Product> product = productService.findById(id);
                if (product.isPresent())
                    return ResponseEntity.status(HttpStatus.OK).body(product);
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            } catch (IllegalArgumentException e) {
                e.printStackTrace();
                return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
            }
        }

        // If no query parameters are found in URL, returns all the products
        List<Product> products = productService.findAll();
        return new ResponseEntity<>(products, HttpStatus.OK);
    }

    //add a product to product table
    @PostMapping("product")
    public ResponseEntity<Product> addProduct(@RequestBody Product product) {
        return new ResponseEntity<>(productService.addProduct(product), HttpStatus.CREATED);
    }

    @DeleteMapping("product/{id}")
    public ResponseEntity<Product> deleteProductById(@PathVariable int id) {
        try {
            Integer isDeleted = productService.deleteProductById(id);
            if (isDeleted > 0)
                return new ResponseEntity<>(HttpStatus.OK);
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

}