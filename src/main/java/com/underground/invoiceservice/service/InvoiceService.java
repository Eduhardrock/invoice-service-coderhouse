package com.underground.invoiceservice.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.underground.invoiceservice.dto.*;
import com.underground.invoiceservice.exceptions.ResourceNotFoundException;
import com.underground.invoiceservice.model.ClientModel;
import com.underground.invoiceservice.model.InvoiceModel;
import com.underground.invoiceservice.model.ItemModel;
import com.underground.invoiceservice.model.ProductModel;
import com.underground.invoiceservice.repository.InvoiceRepository;
import com.underground.invoiceservice.repository.ItemRepository;
import com.underground.invoiceservice.validator.InvoiceValidator;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
public class InvoiceService {

    @Autowired
    private InvoiceRepository invoiceRepository;

    @Autowired
    private ItemRepository itemRepository;

    @Autowired
    private ProductService productService;

    @Autowired
    private ClienteService clienteService;

    @Autowired
    private InvoiceValidator invoiceValidator;

    @Autowired
    private ObjectMapper mapper;


    public InvoiceDTO create(InvoiceDTO order) throws ResourceNotFoundException {
        this.invoiceValidator.validate(order);

        InvoiceModel facturaCabecera = new InvoiceModel();
        ClientModel client = mapper.convertValue(this.clienteService.findById(order.getClient().getId()), ClientModel.class);
        facturaCabecera.setClient(client);

        log.info("FACTURA CABECERA : " + facturaCabecera);

        /**
         * Validando la existencia de los productos que se compro
         */
        List<ItemModel> detalleFactura = new ArrayList<>();
        double total = 0.0;
        for (ItemDTO item: order.getItems()) {
            ItemModel i = new ItemModel();

            i.setInvoice(facturaCabecera); //le asigno la factura a la que corresponde este detalle

            i.setProduct(mapper.convertValue(this.productService.findById(item.getProduct().getId()), ProductModel.class));
            this.productService.discountStock(item.getProduct().getId(), item.getQuantity());
            i.setQuantity(item.getQuantity());
            i.setUnitPrice(i.getProduct().getPriceSell());
            i.setSubtotal(item.getQuantity() * i.getProduct().getPriceSell());

            total += i.getSubtotal();//calculamos el monto total
            detalleFactura.add(i); //agregamos el item al detalle de la factura
        }

        log.info("FACTURA DETALLE : " + detalleFactura);

        facturaCabecera.setTotal(total);
        facturaCabecera = this.invoiceRepository.save(facturaCabecera);

        log.info("FACTURA DETALLE : " + detalleFactura);

       facturaCabecera.setItems(this.itemRepository.saveAll(detalleFactura));

        return this.findById(facturaCabecera.getId());
    }

    public InvoiceDTO findById(Integer id) throws ResourceNotFoundException {
        if (id <= 0){
            throw new IllegalArgumentException("El id de la factura es incorrecto. Verificar");
        }

        return mapper.convertValue(this.invoiceRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("La factura solicitada no existe. Verificar")),InvoiceDTO.class);
    }
}
