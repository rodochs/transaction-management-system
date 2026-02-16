package com.transaction.beneficio.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.transaction.beneficio.api.dto.TransferRequest;
import com.transaction.beneficio.domain.Beneficio;
import com.transaction.beneficio.domain.Cliente;
import com.transaction.beneficio.domain.ContaBeneficio;
import com.transaction.beneficio.infra.repository.BeneficioRepository;
import com.transaction.beneficio.infra.repository.ClienteRepository;
import com.transaction.beneficio.infra.repository.ContaBeneficioRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
@Import(TestTransferCorePortConfig.class)
class TransferFlowIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private ClienteRepository clienteRepository;

    @Autowired
    private BeneficioRepository beneficioRepository;

    @Autowired
    private ContaBeneficioRepository contaBeneficioRepository;

    @Test
    void shouldTransferAmountBetweenAccountsViaApi() throws Exception {
        Cliente cliente = new Cliente("Maria", "maria@example.com");
        cliente = clienteRepository.save(cliente);

        Beneficio beneficioOrigem = new Beneficio("Vale Origem", "Descricao", new BigDecimal("1000.00"), true);
        beneficioOrigem = beneficioRepository.save(beneficioOrigem);

        Beneficio beneficioDestino = new Beneficio("Vale Destino", "Descricao", new BigDecimal("1000.00"), true);
        beneficioDestino = beneficioRepository.save(beneficioDestino);

        ContaBeneficio origem = new ContaBeneficio(cliente, beneficioOrigem, new BigDecimal("500.00"));
        origem = contaBeneficioRepository.save(origem);

        ContaBeneficio destino = new ContaBeneficio(cliente, beneficioDestino, new BigDecimal("100.00"));
        destino = contaBeneficioRepository.save(destino);

        TransferRequest request = new TransferRequest();
        request.setFromAccountId(origem.getId());
        request.setToAccountId(destino.getId());
        request.setAmount(new BigDecimal("150.00"));

        mockMvc.perform(post("/api/v1/transfers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());

        ContaBeneficio origemAfter = contaBeneficioRepository.findById(origem.getId()).orElseThrow();
        ContaBeneficio destinoAfter = contaBeneficioRepository.findById(destino.getId()).orElseThrow();

        assertThat(origemAfter.getSaldo()).isEqualByComparingTo("350.00");
        assertThat(destinoAfter.getSaldo()).isEqualByComparingTo("250.00");
    }
}
