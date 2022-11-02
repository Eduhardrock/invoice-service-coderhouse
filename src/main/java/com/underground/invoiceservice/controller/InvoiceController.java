package com.underground.invoiceservice.controller;

import com.underground.invoiceservice.dto.InvoiceDTO;
import com.underground.invoiceservice.exceptions.ResourceNotFoundException;
import com.underground.invoiceservice.service.InvoiceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(path = "/api/invoice")
public class InvoiceController {

    @Autowired
    private InvoiceService service;


    @GetMapping(path = "/{id}")
    public ResponseEntity<InvoiceDTO> findById(@PathVariable Integer id) throws ResourceNotFoundException {
        return new ResponseEntity<>(this.service.findById(id), HttpStatus.OK);
    }

    @PostMapping(path = "/")
    public ResponseEntity<InvoiceDTO> create(@RequestBody InvoiceDTO order) throws ResourceNotFoundException {
        return new ResponseEntity<>(this.service.create(order), HttpStatus.OK);
    }
}
