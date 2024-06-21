package dev.mochahaulier.bankingtest.controller;

import dev.mochahaulier.bankingtest.dto.ProductDefinitionRequest;
import dev.mochahaulier.bankingtest.dto.ProductDefinitionResponse;
import dev.mochahaulier.bankingtest.model.Operation;
import dev.mochahaulier.bankingtest.service.ProductDefinitionService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.mockito.ArgumentMatchers.anyMap;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

@WebMvcTest(controllers = ProductDefinitionController.class)
public class ProductDefinitionControllerTest {

        @Autowired
        private MockMvc mockMvc;

        @MockBean
        private ProductDefinitionService productDefinitionService;

        @Test
        public void whenPostValidUpdateDefinitionWithoutRate_thenReturns200() throws Exception {
                String requestBody = "{ \"definitions\": [ { \"operation\": \"U\", \"productKey\": \"CL48S5\", \"rate\": \"\", \"payRate\": { \"unit\": \"MONTH\", \"value\": \"2\" } } ] }";

                // Mock the service to return the expected response
                ProductDefinitionResponse mockResponse = new ProductDefinitionResponse(
                                List.of("[0]: [VALIDATION ERROR]: Rate is required."),
                                Collections.emptyList());
                Mockito.when(productDefinitionService.processProductDefinitions(Mockito.anyMap()))
                                .thenReturn(mockResponse);

                mockMvc.perform(post("/api/v1/product-definitions")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(requestBody))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.errors").isArray())
                                .andExpect(jsonPath("$.errors", hasSize(1)))
                                .andExpect(jsonPath("$.errors[0]")
                                                .value(containsString("[0]: [VALIDATION ERROR]: Rate is required.")))
                                .andExpect(jsonPath("$.successes").isArray())
                                .andExpect(jsonPath("$.successes", hasSize(0)));
        }

        @Test
        public void testProcessProductDefinitions_ProcessingError() throws Exception {
                String requestBody = "{\"definitions\":[{\"operation\":\"U\",\"productKey\":\"123456\",\"rate\":0.5,\"payRate\":{\"unit\":\"MONTH\",\"value\":1}}]}";

                when(productDefinitionService.processProductDefinitions(anyMap())).thenReturn(
                                new ProductDefinitionResponse(
                                                List.of("[0]: [PROCESSING ERROR]: Error processing product 123456"),
                                                Collections.emptyList()));

                mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/product-definitions")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(requestBody))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.errors").isArray())
                                .andExpect(jsonPath("$.errors[0]")
                                                .value("[0]: [PROCESSING ERROR]: Error processing product 123456"))
                                .andExpect(jsonPath("$.successes").isEmpty());
        }

        @Test
        public void testProcessProductDefinitionsWithValidAndInvalidRequests() throws Exception {
                ProductDefinitionRequest.DefinitionRequest validRequest = new ProductDefinitionRequest.DefinitionRequest();
                validRequest.setOperation(Operation.NEW);
                validRequest.setProductKey("123456");
                validRequest.setDescription("Valid Description");
                validRequest.setType("ACCOUNT");
                validRequest.setRate(BigDecimal.valueOf(5.0));
                validRequest.setPayRate(new ProductDefinitionRequest.DefinitionRequest.PayRateDto("DAY", 5));

                ProductDefinitionRequest.DefinitionRequest invalidRequest = new ProductDefinitionRequest.DefinitionRequest();
                invalidRequest.setOperation(Operation.NEW);
                invalidRequest.setProductKey("123456");
                invalidRequest.setDescription("Invalid Description");
                invalidRequest.setType("ACCOUNT");
                invalidRequest.setRate(BigDecimal.valueOf(-5.0)); // Invalid rate
                invalidRequest.setPayRate(new ProductDefinitionRequest.DefinitionRequest.PayRateDto("DAY", 5));

                List<ProductDefinitionRequest.DefinitionRequest> definitionRequests = Arrays.asList(validRequest,
                                invalidRequest);
                ProductDefinitionRequest request = new ProductDefinitionRequest();
                request.setDefinitions(definitionRequests);

                when(productDefinitionService.processProductDefinitions(anyMap()))
                                .thenReturn(new ProductDefinitionResponse(Collections.emptyList(),
                                                Arrays.asList("Processed valid request")));

                mockMvc.perform(post("/api/v1/product-definitions")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(asJsonString(request)))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.errors").isArray())
                                .andExpect(jsonPath("$.errors", hasSize(1)))
                                .andExpect(jsonPath("$.successes").isArray())
                                .andExpect(jsonPath("$.successes", hasSize(1)))
                                .andExpect(jsonPath("$.errors[0]").value(containsString("[1]: [VALIDATION ERROR]:")));
        }

        @Test
        public void testProcessProductDefinitionsWithAllValidRequests() throws Exception {
                ProductDefinitionRequest.DefinitionRequest validRequest1 = new ProductDefinitionRequest.DefinitionRequest();
                validRequest1.setOperation(Operation.NEW);
                validRequest1.setProductKey("123456");
                validRequest1.setDescription("Valid Description 1");
                validRequest1.setType("ACCOUNT");
                validRequest1.setRate(BigDecimal.valueOf(5.0));
                validRequest1.setPayRate(new ProductDefinitionRequest.DefinitionRequest.PayRateDto("DAY", 5));

                ProductDefinitionRequest.DefinitionRequest validRequest2 = new ProductDefinitionRequest.DefinitionRequest();
                validRequest2.setOperation(Operation.UPDATE);
                validRequest2.setProductKey("654321");
                validRequest2.setDescription("Valid Description 2");
                validRequest2.setType("ACCOUNT");
                validRequest2.setRate(BigDecimal.valueOf(10.0));
                validRequest2.setPayRate(new ProductDefinitionRequest.DefinitionRequest.PayRateDto("DAY", 10));

                List<ProductDefinitionRequest.DefinitionRequest> definitionRequests = Arrays.asList(validRequest1,
                                validRequest2);
                ProductDefinitionRequest request = new ProductDefinitionRequest();
                request.setDefinitions(definitionRequests);

                when(productDefinitionService.processProductDefinitions(anyMap()))
                                .thenReturn(new ProductDefinitionResponse(Collections.emptyList(), Arrays
                                                .asList("Processed valid request 1", "Processed valid request 2")));

                mockMvc.perform(post("/api/v1/product-definitions")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(asJsonString(request)))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.errors").isArray())
                                .andExpect(jsonPath("$.errors", hasSize(0)))
                                .andExpect(jsonPath("$.successes").isArray())
                                .andExpect(jsonPath("$.successes", hasSize(2)))
                                .andExpect(jsonPath("$.successes[0]").value("Processed valid request 1"))
                                .andExpect(jsonPath("$.successes[1]").value("Processed valid request 2"));
        }

        @Test
        public void testProcessProductDefinitionsWithAllInvalidRequests() throws Exception {
                ProductDefinitionRequest.DefinitionRequest invalidRequest1 = new ProductDefinitionRequest.DefinitionRequest();
                invalidRequest1.setOperation(Operation.NEW);
                invalidRequest1.setProductKey("123456");
                invalidRequest1.setDescription("Invalid Description 1");
                invalidRequest1.setType("ACCOUNT");
                invalidRequest1.setRate(BigDecimal.valueOf(-5.0)); // Invalid rate
                invalidRequest1.setPayRate(new ProductDefinitionRequest.DefinitionRequest.PayRateDto("DAY", 5));

                ProductDefinitionRequest.DefinitionRequest invalidRequest2 = new ProductDefinitionRequest.DefinitionRequest();
                invalidRequest2.setOperation(Operation.UPDATE);
                invalidRequest2.setProductKey("654321");
                invalidRequest2.setDescription("Invalid Description 2");
                invalidRequest2.setType("ACCOUNT");
                invalidRequest2.setRate(BigDecimal.valueOf(-10.0)); // Invalid rate
                invalidRequest2.setPayRate(new ProductDefinitionRequest.DefinitionRequest.PayRateDto("DAY", 10));

                List<ProductDefinitionRequest.DefinitionRequest> definitionRequests = Arrays.asList(invalidRequest1,
                                invalidRequest2);
                ProductDefinitionRequest request = new ProductDefinitionRequest();
                request.setDefinitions(definitionRequests);

                mockMvc.perform(post("/api/v1/product-definitions")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(asJsonString(request)))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.errors").isArray())
                                .andExpect(jsonPath("$.errors", hasSize(2)))
                                .andExpect(jsonPath("$.errors[0]").value(containsString("[0]: [VALIDATION ERROR]:")))
                                .andExpect(jsonPath("$.errors[1]").value(containsString("[1]: [VALIDATION ERROR]:")))
                                .andExpect(jsonPath("$.successes").isArray())
                                .andExpect(jsonPath("$.successes", hasSize(0)));
        }

        private static String asJsonString(final Object obj) {
                try {
                        return new ObjectMapper().writeValueAsString(obj);
                } catch (Exception e) {
                        throw new RuntimeException(e);
                }
        }
}