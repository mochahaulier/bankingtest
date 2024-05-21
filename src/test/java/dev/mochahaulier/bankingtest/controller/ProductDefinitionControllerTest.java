package dev.mochahaulier.bankingtest.controller;

import dev.mochahaulier.bankingtest.service.ProductDefinitionService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class ProductDefinitionControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ProductDefinitionService productDefinitionService;

    @Test
    public void testAddNewProductDefinition() throws Exception {
        String requestBody = "{ \"definitions\": [ { \"operation\": \"N\", \"productKey\": \"CL48S5\", \"description\": \"consumer loan\", \"type\": \"LOAN\", \"rate\": 0.12, \"payRate\": { \"unit\": \"MONTH\", \"value\": \"3\" } } ] }";

        mockMvc.perform(post("/api/product-definitions")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody))
                .andExpect(status().isOk());
    }

    @Test
    public void testUpdateProductDefinition() throws Exception {
        String requestBody = "{ \"definitions\": [ { \"operation\": \"U\", \"productKey\": \"CL48S5\", \"rate\": 0.10, \"payRate\": { \"unit\": \"MONTH\", \"value\": \"2\" } } ] }";

        mockMvc.perform(post("/api/product-definitions")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody))
                .andExpect(status().isOk());
    }
}