package com.underground.invoiceservice.validator;

import com.underground.invoiceservice.dto.ClientDTO;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.Period;
import java.util.regex.Pattern;

@Component
public class ClientValidator {

    private String REGEX_ONLY_LETTERS = "[^a-zA-Z]";
    private String REGEX_ONLY_NUMBERS = "\\D";

    public void validate(ClientDTO newClient){
        if (newClient == null){
            throw new IllegalArgumentException("El cliente es nulo o vacio. Verificar");
        }

        if (StringUtils.isBlank(newClient.getName()) || Pattern.matches(REGEX_ONLY_LETTERS,newClient.getName())){
            throw new IllegalArgumentException("El nombre del cliente no es valido. Verificar");
        }

        if (StringUtils.isBlank(newClient.getLastname()) || Pattern.matches(REGEX_ONLY_LETTERS,newClient.getLastname())){
            throw new IllegalArgumentException("El apellido del cliente no es valido. Verificar");
        }

        if (StringUtils.isBlank(newClient.getDocNumber()) || Pattern.matches(REGEX_ONLY_NUMBERS,newClient.getDocNumber())){
            throw new IllegalArgumentException("El dni del cliente no es valido. Verificar");
        }

        if (newClient.getDateOfBirth() == null){
            throw new IllegalArgumentException("La fecha de nacimiento del cliente no es valido. Verificar");
        }else {
            boolean edadPermitida = Period.between(newClient.getDateOfBirth(), LocalDate.now()).getYears() >= 18;
            if (!edadPermitida){
                throw new IllegalArgumentException("El cliente no tiene la edad minima permitida para comprar");
            }
        }
    }
}
