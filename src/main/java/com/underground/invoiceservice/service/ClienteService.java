package com.underground.invoiceservice.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.underground.invoiceservice.commons.Util;
import com.underground.invoiceservice.dto.ClientDTO;
import com.underground.invoiceservice.exceptions.ResourceAlreadyExistsException;
import com.underground.invoiceservice.exceptions.ResourceNotFoundException;
import com.underground.invoiceservice.model.ClientModel;
import com.underground.invoiceservice.repository.ClientRepository;
import com.underground.invoiceservice.validator.ClientValidator;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Slf4j
@Service
public class ClienteService {

    @Autowired
    private ClientRepository clienteRepository;

    @Autowired
    private ClientValidator clientValidator;

    @Autowired
    private ObjectMapper mapper;

    public ClientDTO findById(Integer id) throws ResourceNotFoundException {
        log.info("ID CON EL QUE INGRESAMOS A CLIENTE ID " + id);
        if (id <= 0) {
            throw new IllegalArgumentException("El id brindado no es valido. Verificar");
        }

        ClientModel clientModel = this.clienteRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("El Cliente con el ID " + id + " no existe . Verificar"));

        return this.mapper.convertValue(clientModel, ClientDTO.class);
    }

    public List<ClientModel> findAll() {
        return this.clienteRepository.findAll();
    }

    public ClientDTO create(ClientDTO newClient) throws ResourceAlreadyExistsException {
        this.clientValidator.validate(newClient);

        Optional<ClientModel> clientOp = this.clienteRepository.findByDocNumber(newClient.getDocNumber());

        if (clientOp.isPresent()) {
            throw new ResourceAlreadyExistsException("Ya existe un cliente registrado con el dni brindado. Verificar");
        }

        ClientModel clientModel = mapper.convertValue(newClient, ClientModel.class);
        clientModel = this.clienteRepository.save(clientModel);
        log.info("NUEVO CLIENTE : " + clientModel);
        newClient.setId(clientModel.getId());

        return newClient;
    }

    public ClientDTO update(Integer id, ClientDTO clientUpdated) throws ResourceNotFoundException {
        if (id <= 0) {
            throw new IllegalArgumentException("El id brindado no es valido. Verificar");
        }

        this.clientValidator.validate(clientUpdated);

        log.info("ID BUSCAMOS PARA ACTUALIZAR : " + id);
        ClientModel clientOp = this.clienteRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("El Cliente con el ID " + id + " no existe . Verificar"));

        log.info("RESULTADO DEL MAPPER " + mapper.convertValue(clientUpdated, ClientModel.class));

        clientUpdated.setId(clientOp.getId());
        ClientModel clientBD = this.clienteRepository.save(mapper.convertValue(clientUpdated, ClientModel.class));

        return mapper.convertValue(clientBD, ClientDTO.class);

    }

    public ClientDTO deleteById(Integer id) throws ResourceNotFoundException {
        if (id <= 0) {
            throw new IllegalArgumentException("El id brindado no es valido. Verificar");
        }

        ClientModel client = this.clienteRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("El Cliente con el ID " + id + " no existe . Verificar"));

        client.setStatus(false);
        this.clienteRepository.save(client);
        return mapper.convertValue(client, ClientDTO.class);
    }

}