package com.underground.invoiceservice.validator;

import com.underground.invoiceservice.dto.InvoiceDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class InvoiceValidator {

    @Autowired
    private ClientValidator clientValidator;

    @Autowired
    private ProductValidator productValidator;

    public void validate(InvoiceDTO newInvoice){
        if (newInvoice == null){
            throw new IllegalArgumentException("La factura enviada es nula o vacia. Verificar");
        }

        this.clientValidator.validate(newInvoice.getClient());

        newInvoice.getItems().forEach(itemDTO -> this.productValidator.validate(itemDTO.getProduct()));
    }
}
