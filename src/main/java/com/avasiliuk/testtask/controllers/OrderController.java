package com.avasiliuk.testtask.controllers;

import com.avasiliuk.testtask.model.rest.NewOrder;
import com.avasiliuk.testtask.model.rest.Order;
import com.avasiliuk.testtask.services.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@RestController
public class OrderController {

    private OrderService orderService;

    @Autowired
    public OrderController(final OrderService orderService) {
        this.orderService = orderService;
    }

    @RequestMapping(path = "/order", method = RequestMethod.POST)
    public ResponseEntity<Order> create(@RequestBody NewOrder newOrder) {
        return new ResponseEntity<>(orderService.create(newOrder), HttpStatus.CREATED);
    }

    @RequestMapping(path = "/order/{id}", method = RequestMethod.GET)
    public ResponseEntity<Order> get(@PathVariable("id") UUID id) {
        return new ResponseEntity<>(orderService.get(id), HttpStatus.OK);
    }

    @RequestMapping(path = "/order/{id}", method = RequestMethod.DELETE)
    public ResponseEntity<Void> delete(@PathVariable("id") UUID id) {
        orderService.delete(id);
        return new ResponseEntity<>(null, HttpStatus.NO_CONTENT);
    }

    @RequestMapping(path = "/orders", method = RequestMethod.GET)
    public ResponseEntity<List<Order>> list(
            @RequestParam(value = "from", required = false) Instant from,
            @RequestParam(value = "to", required = false) Instant to) {
        return new ResponseEntity<>(orderService.list(from, to), HttpStatus.OK);
    }

}
