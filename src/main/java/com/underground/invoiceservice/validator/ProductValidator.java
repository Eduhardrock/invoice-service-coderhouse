package com.underground.invoiceservice.validator;

import com.underground.invoiceservice.dto.ProductDTO;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

@Component
public class ProductValidator {

    public void validate(ProductDTO newProduct){
        if (newProduct == null){
            throw new IllegalArgumentException("El producto brindado es nulo. Verificar");
        }

        if (StringUtils.isBlank(newProduct.getCode())){
            throw new IllegalArgumentException("El codigo del producto no es valido. Verificar");
        }

        if (newProduct.getStock() <= 0){
            throw new IllegalArgumentException("El stock del producto no puede ser menor o igual a 0");
        }

        if (StringUtils.isBlank(newProduct.getDescription())){
            throw new IllegalArgumentException("El producto no contiene una descripcion valida. Verificar");
        }

        if (newProduct.getPriceBuy() <= 0.0){
            throw new IllegalArgumentException("El precio de compra del producto no puede ser menor o igual a cero. Verificar");
        }

        if (newProduct.getPriceSell() <= 0.0){
            throw new IllegalArgumentException("El precio de venta del producto no puede ser menor o igual a cero. Verificar");
        }
    }
}
