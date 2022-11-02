package com.underground.invoiceservice.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.underground.invoiceservice.dto.ProductDTO;
import com.underground.invoiceservice.exceptions.ResourceAlreadyExistsException;
import com.underground.invoiceservice.exceptions.ResourceNotFoundException;
import com.underground.invoiceservice.model.ProductModel;
import com.underground.invoiceservice.repository.ProductRepository;
import com.underground.invoiceservice.validator.ProductValidator;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * @Author Edu Terror Gimeno.
 */

@Service
@Slf4j
public class ProductService {

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private ProductValidator productValidator;

    @Autowired
    private ObjectMapper mapper;

    public ProductDTO findById(Integer id) throws ResourceNotFoundException {
        if (id <= 0){
            throw new IllegalArgumentException("El id brindado no es valido. Verificar");
        }

        ProductModel product = this.productRepository.findById(id)
                            .orElseThrow(() -> new ResourceNotFoundException("El producto con el id " + id + " no existe. Verificar"));

        return mapper.convertValue(product, ProductDTO.class);
    }

    public List<ProductDTO> findAll(){
        return this.productRepository.findAll().stream()
                .map(product -> ProductDTO.builder()
                                .id(product.getId())
                                .code(product.getCode())
                                .description(product.getDescription())
                                .priceBuy(product.getPriceBuy())
                                .priceSell(product.getPriceSell())
                                .stock(product.getStock())
                        .build())
                .collect(Collectors.toList());
    }

    public ProductDTO create(ProductDTO newProduct) throws ResourceAlreadyExistsException {
        this.productValidator.validate(newProduct);

        Optional<ProductModel> productBD = this.productRepository.findByCode(newProduct.getCode());
        if (productBD.isPresent()){
            throw new ResourceAlreadyExistsException("Ya existe un producto con el codigo " + newProduct.getCode() +" . Verificar");
        }

        ProductModel productModel = mapper.convertValue(newProduct,ProductModel.class);
        productModel = this.productRepository.save(productModel);
        return mapper.convertValue(productModel, ProductDTO.class);
    }

    public ProductDTO updateProduct(Integer id, ProductDTO updateProduct) throws ResourceNotFoundException {
        if (id <= 0){
            throw new IllegalArgumentException("El id brindado no es valido. Verificar");
        }

        this.productValidator.validate(updateProduct);

        Optional<ProductModel> productOp = this.productRepository.findById(id);
        if (productOp.isPresent()) {
            throw  new ResourceNotFoundException("No existe ningun producto con el id brindado. Verificar");
        }

        ProductModel productBD = mapper.convertValue(updateProduct, ProductModel.class);
        productBD.setId(id);
        return mapper.convertValue(this.productRepository.save(productBD), ProductDTO.class);
    }

    public ProductDTO deleteById(Integer id) throws ResourceNotFoundException {
        if (id <= 0){
            throw new IllegalArgumentException("El id brindado no es valido. Verificar");
        }

        ProductModel product = this.productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("No existe ningun producto con el id brindado. Verificar"));

        product.setStatus(false);
        this.productRepository.save(product); // se aplica baja logica.
        return mapper.convertValue(product, ProductDTO.class);
    }

    public ProductDTO discountStock(Integer id, int stockToDiscount) throws ResourceNotFoundException {
        if (id <= 0){
            throw new IllegalArgumentException("El id brindado no es valido. Verificar");
        }

        ProductModel productModel = this.productRepository.findById(id)
                                .orElseThrow(() -> new ResourceNotFoundException("No existe ningun producto con el id brindado. Verificar"));

        if (stockToDiscount <= 0){
            throw new IllegalArgumentException("El stock indicado a descontar no puede ser negativo o 0. Verificar");
        }

        if (productModel.getStock() < stockToDiscount){
            throw new IllegalArgumentException("No hay stock suficiente del producto indicado. Verificar");
        }

        productModel.setStock(productModel.getStock() - stockToDiscount);
        return mapper.convertValue(this.productRepository.save(productModel), ProductDTO.class);
    }

}
