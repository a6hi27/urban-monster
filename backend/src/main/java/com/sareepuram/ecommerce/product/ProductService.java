package com.sareepuram.ecommerce.product;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@Transactional
public class ProductService {
    @Autowired
    private ProductRepository productRepository;

    // Get all products
    public List<Product> findAll() {
        List<Product> products = new ArrayList<>(productRepository.findAll());
        return products;
    }

    // Get products by searchQuery
    public Optional<Product> findByName(String searchString) throws IllegalArgumentException {
        return productRepository.findByName(searchString);
    }

    // Get a product using productId
    public Optional<Product> findById(Integer productId) throws IllegalArgumentException {
        return productRepository.findById(productId);
    }

    //Add a product to product table
    public Product addProduct(Product product) {
        return productRepository.save(product);
    }

    public Integer deleteProductById(Integer id) throws IllegalArgumentException {
        return productRepository.deleteByProductId(id);
    }
}
