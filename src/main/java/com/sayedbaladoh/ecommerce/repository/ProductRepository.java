package com.sayedbaladoh.ecommerce.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.sayedbaladoh.ecommerce.model.Product;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {

}
