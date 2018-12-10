package com.avasiliuk.testtask.controllers;

import com.avasiliuk.testtask.model.rest.NewProduct;
import com.avasiliuk.testtask.model.rest.Product;
import com.avasiliuk.testtask.services.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.List;
import java.util.UUID;

@RestController
public class ProductController {

    private ProductService productService;

    @Autowired
    public ProductController(final ProductService productService) {
        this.productService = productService;
    }

    @RequestMapping(path = "/product", method = RequestMethod.POST)
    public ResponseEntity<Product> create(@Valid @RequestBody NewProduct newProduct) {
        return new ResponseEntity<>(productService.create(newProduct), HttpStatus.CREATED);
    }

    @RequestMapping(path = "/product/{id}", method = RequestMethod.PUT)
    public ResponseEntity<Product> update(
            @PathVariable("id") UUID id, @Valid @RequestBody NewProduct newProduct) {
        return new ResponseEntity<>(productService.update(id, newProduct), HttpStatus.OK);
    }

    @RequestMapping(path = "/product/{id}", method = RequestMethod.DELETE)
    public ResponseEntity<Void> delete(@PathVariable("id") UUID id) {
        productService.delete(id);
        return new ResponseEntity<>(null, HttpStatus.NO_CONTENT);
    }

    @RequestMapping(path = "/product/{id}", method = RequestMethod.GET)
    public ResponseEntity<Product> get(@PathVariable("id") UUID id) {
        return new ResponseEntity<>(productService.get(id), HttpStatus.OK);
    }

    @RequestMapping(path = "/products", method = RequestMethod.GET)
    public ResponseEntity<List<Product>> list() {
        return new ResponseEntity<>(productService.list(), HttpStatus.OK);
    }
}
