package com.transaction.beneficio.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.transaction.beneficio.api.controller.BeneficioController;
import com.transaction.beneficio.api.dto.BeneficioRequest;
import com.transaction.beneficio.domain.Beneficio;
import com.transaction.beneficio.infra.repository.BeneficioRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(BeneficioController.class)
class BeneficioControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private BeneficioRepository beneficioRepository;

    @Test
    void shouldListBeneficios() throws Exception {
        Beneficio b = new Beneficio("Vale Alimentacao", "", new BigDecimal("500.00"), true);
        given(beneficioRepository.findAll()).willReturn(List.of(b));

        mockMvc.perform(get("/api/v1/beneficios"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].nome").value("Vale Alimentacao"));
    }

    @Test
    void shouldCreateBeneficio() throws Exception {
        BeneficioRequest request = new BeneficioRequest();
        request.setNome("Vale Refeicao");
        request.setDescricao("Cartao refeicao");
        request.setValor(new BigDecimal("600.00"));
        request.setAtivo(true);

        Beneficio saved = new Beneficio("Vale Refeicao", "Cartao refeicao", new BigDecimal("600.00"), true);
        given(beneficioRepository.save(any(Beneficio.class))).willReturn(saved);

        mockMvc.perform(post("/api/v1/beneficios")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", org.hamcrest.Matchers.containsString("/api/v1/beneficios/")))
                .andExpect(jsonPath("$.nome").value("Vale Refeicao"));
    }

    @Test
    void shouldReturnValidationErrorWhenNomeMissing() throws Exception {
        BeneficioRequest request = new BeneficioRequest();
        request.setDescricao("Sem nome");
        request.setValor(new BigDecimal("100.00"));
        request.setAtivo(true);

        mockMvc.perform(post("/api/v1/beneficios")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("VALIDATION_ERROR"));
    }

    @Test
    void shouldGetBeneficioByIdOr404() throws Exception {
        Beneficio b = new Beneficio("Vale", null, new BigDecimal("100.00"), true);
        given(beneficioRepository.findById(1L)).willReturn(Optional.of(b));
        given(beneficioRepository.findById(2L)).willReturn(Optional.empty());

        mockMvc.perform(get("/api/v1/beneficios/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nome").value("Vale"));

        mockMvc.perform(get("/api/v1/beneficios/2"))
                .andExpect(status().isNotFound());
    }
}
