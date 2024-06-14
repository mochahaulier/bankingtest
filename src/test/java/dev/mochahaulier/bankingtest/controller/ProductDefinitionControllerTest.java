package dev.mochahaulier.bankingtest.controller;

import dev.mochahaulier.bankingtest.dto.ProductDefinitionResponse;
import dev.mochahaulier.bankingtest.service.ProductDefinitionService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyMap;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.Collections;
import java.util.List;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

@WebMvcTest(controllers = ProductDefinitionController.class)
public class ProductDefinitionControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ProductDefinitionService productDefinitionService;

    // @Test
    // public void whenPostValidNewDefinition_thenReturns200() throws Exception {
    // String requestBody = "{ \"definitions\": [ { \"operation\": \"N\",
    // \"productKey\": \"CL48S5\", \"description\": \"consumer loan\", \"type\":
    // \"LOAN\", \"rate\": 0.12, \"payRate\": { \"unit\": \"MONTH\", \"value\":
    // \"3\" } } ] }";

    // mockMvc.perform(post("/api/product-definitions")
    // .contentType(MediaType.APPLICATION_JSON)
    // .content(requestBody))
    // .andExpect(status().isOk());
    // }

    // @Test
    // public void whenPostValidUpdateDefinition_thenReturns200() throws Exception {
    // String requestBody = "{ \"definitions\": [ { \"operation\": \"U\",
    // \"productKey\": \"CL48S5\", \"rate\": 0.10, \"payRate\": { \"unit\":
    // \"MONTH\", \"value\": \"2\" } } ] }";

    // mockMvc.perform(post("/api/product-definitions")
    // .contentType(MediaType.APPLICATION_JSON)
    // .content(requestBody))
    // .andExpect(status().isOk());
    // }

    // @Test
    // public void whenPostValidUpdateDefinitionWihtoutRate_thenReturns200() throws
    // Exception {
    // String requestBody = "{ \"definitions\": [ { \"operation\": \"U\",
    // \"productKey\": \"CL48S5\", \"rate\": \"\", \"payRate\": { \"unit\":
    // \"MONTH\", \"value\": \"2\" } } ] }";

    // mockMvc.perform(post("/api/product-definitions")
    // .contentType(MediaType.APPLICATION_JSON)
    // .content(requestBody))
    // .andExpect(status().isOk());
    // }

    @Test
    public void whenPostValidUpdateDefinitionWithoutPRUnit_thenReturns200() throws Exception {
        String requestBody = "{ \"definitions\": [ { \"operation\": \"U\", \"productKey\": \"CL48S5\", \"rate\": 0.10, \"payRate\": { \"unit\": \"\", \"value\": \"2\" } } ] }";

        mockMvc.perform(post("/api/product-definitions")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody))
                .andExpect(status().isOk());
    }

    // @Test
    // public void whenPostValidUpdateDefinitionWithoutPRValue_thenReturns200()
    // throws Exception {
    // String requestBody = "{ \"definitions\": [ { \"operation\": \"U\",
    // \"productKey\": \"CL48S5\", \"rate\": 0.10, \"payRate\": { \"unit\":
    // \"MONTH\", \"value\": \"\" } } ] }";

    // mockMvc.perform(post("/api/product-definitions")
    // .contentType(MediaType.APPLICATION_JSON)
    // .content(requestBody))
    // .andExpect(status().isOk());
    // }

    // @Test
    // public void whenPostValidUpdateDefinitionWithOnlyRate_thenReturns200() throws
    // Exception {
    // String requestBody = "{ \"definitions\": [ { \"operation\": \"U\",
    // \"productKey\": \"CL48S5\", \"rate\": 0.10, \"payRate\": { \"unit\": \"\",
    // \"value\": \"\" } } ] }";

    // mockMvc.perform(post("/api/product-definitions")
    // .contentType(MediaType.APPLICATION_JSON)
    // .content(requestBody))
    // .andExpect(status().isOk());
    // }

    // @Test
    // void whenPostInvalidOperation_thenReturns400() throws Exception {
    // String invalidRequest = "{\"definitions\": [{\"operation\": \"X\",
    // \"productKey\": \"12345\", \"description\": \"\", \"type\": \"\", \"rate\":
    // -1, \"payRate\": {\"unit\": \"WEEK\", \"value\": 0}}]}";

    // mockMvc.perform(post("/api/product-definitions")
    // .contentType(MediaType.APPLICATION_JSON)
    // .content(invalidRequest))
    // .andExpect(status().isBadRequest());

    // }

    // @Test
    // void whenPostInvalidDefintion_thenReturns400() throws Exception {
    // String invalidRequest = "{\"definitions\": [{\"operation\": \"\",
    // \"productKey\": \"12345\", \"description\": \"\", \"type\": \"\", \"rate\":
    // -1, \"payRate\": {\"unit\": \"WEEK\", \"value\": 0}}]}";

    // mockMvc.perform(post("/api/product-definitions")
    // .contentType(MediaType.APPLICATION_JSON)
    // .content(invalidRequest))
    // .andExpect(status().isBadRequest());

    // }

    // @Test
    // public void whenPostUpdateDefinitionWithoutProductKey_thenReturns400() throws
    // Exception {
    // String requestBody = "{ \"definitions\": [ { \"operation\": \"U\",
    // \"productKey\": \"\", \"rate\": 0.10, \"payRate\": { \"unit\": \"MONTH\",
    // \"value\": \"2\" } } ] }";

    // mockMvc.perform(post("/api/product-definitions")
    // .contentType(MediaType.APPLICATION_JSON)
    // .content(requestBody))
    // .andExpect(status().isBadRequest());
    // }

    // @Test
    // public void testProcessProductDefinitions_ValidationError() throws Exception
    // {
    // String requestBody =
    // "{\"definitions\":[{\"operation\":\"N\",\"productKey\":\"\",\"description\":\"Desc\",\"rate\":0.5,\"payRate\":{\"unit\":\"MONTH\",\"value\":1}}]}";

    // mockMvc.perform(MockMvcRequestBuilders.post("/api/product-definitions")
    // .contentType(MediaType.APPLICATION_JSON)
    // .content(requestBody))
    // .andExpect(status().isOk())
    // .andExpect(jsonPath("$.errors").isArray())
    // .andExpect(jsonPath("$.errors[0]").value("[0]: [VALIDATION ERROR]: Product
    // key is required"))
    // .andExpect(jsonPath("$.successfulIndexes").isEmpty());
    // }

    @Test
    public void testProcessProductDefinitions_ProcessingError() throws Exception {
        String requestBody = "{\"definitions\":[{\"operation\":\"U\",\"productKey\":\"123456\",\"rate\":0.5,\"payRate\":{\"unit\":\"MONTH\",\"value\":1}}]}";

        when(productDefinitionService.processProductDefinitions(anyMap())).thenReturn(
                new ProductDefinitionResponse(
                        List.of("[0]: [PROCESSING ERROR]: Error processing product 123456"),
                        Collections.emptyList()));

        mockMvc.perform(MockMvcRequestBuilders.post("/api/product-definitions")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.errors").isArray())
                .andExpect(jsonPath("$.errors[0]").value("[0]: [PROCESSING ERROR]: Error processing product 123"))
                .andExpect(jsonPath("$.successes").isEmpty());
    }

    @Test
    public void testProcessProductDefinitions_Success() throws Exception {
        String requestBody = "{\"definitions\":[{\"operation\":\"N\",\"productKey\":\"123\",\"rate\":0.5,\"payRate\":{\"unit\":\"MONTH\",\"value\":1}}]}";

        when(productDefinitionService.processProductDefinitions(anyMap())).thenReturn(
                new ProductDefinitionResponse(
                        Collections.emptyList(),
                        Collections.emptyList()));

        mockMvc.perform(MockMvcRequestBuilders.post("/api/product-definitions")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.errors").isArray())
                .andExpect(jsonPath("$.errors").isEmpty())
                .andExpect(jsonPath("$.successfulIndexes").isArray())
                .andExpect(jsonPath("$.successfulIndexes[0]").value(0));
    }
}