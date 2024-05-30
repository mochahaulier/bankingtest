package dev.mochahaulier.bankingtest.request;

import org.junit.jupiter.api.Test;

import dev.mochahaulier.bankingtest.dto.ProductDefinitionRequest;
import dev.mochahaulier.bankingtest.dto.ProductDefinitionRequest.DefinitionRequest.PayRateDto;
import jakarta.validation.ConstraintViolation;

import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;

import org.hibernate.validator.HibernateValidator;
import org.junit.jupiter.api.BeforeEach;

import java.math.BigDecimal;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

public class ProductDefinitionRequestValidationTest {

    private Validator validator;

    @BeforeEach
    public void setUp() {
        ValidatorFactory factory = Validation.byProvider(HibernateValidator.class)
                .configure()
                .buildValidatorFactory();
        validator = factory.getValidator();
    }

    // @NotNull(message = "Operation is required.")
    // @Pattern(regexp = "^[NU]$", message = "Operation must be 'N' or 'U'.")
    @Test
    public void whenOperationIsInvalid_thenValidationFails() {
        ProductDefinitionRequest.DefinitionRequest request = createValidDefinitionRequest();
        request.setOperation("X"); // Invalid operation

        Set<ConstraintViolation<ProductDefinitionRequest.DefinitionRequest>> violations = validator.validate(request);

        assertThat(violations).hasSize(1);
        assertThat(violations.iterator().next().getMessage()).isEqualTo("Operation must be 'N' or 'U'.");
    }

    @Test
    public void whenOperationIsNull_thenValidationFails() {
        ProductDefinitionRequest.DefinitionRequest request = createValidDefinitionRequest();
        request.setOperation(null); // Null operation

        Set<ConstraintViolation<ProductDefinitionRequest.DefinitionRequest>> violations = validator.validate(request);

        assertThat(violations).hasSize(1);
        assertThat(violations.iterator().next().getMessage()).isEqualTo("Operation is required.");
    }

    @Test
    public void whenOperationIsValid_thenValidationOK() {
        ProductDefinitionRequest.DefinitionRequest request = createValidDefinitionRequest();
        request.setOperation("N"); // Valid operation

        Set<ConstraintViolation<ProductDefinitionRequest.DefinitionRequest>> violations = validator.validate(request);

        assertThat(violations).hasSize(0);

    }

    // @NotNull(message = "Product key is required.")
    // @Size(min = 6, max = 6, message = "Product key must have 6 characters.")
    // private String productKey;
    @Test
    public void whenProductKeyIsInvalid_thenValidationFails() {
        ProductDefinitionRequest.DefinitionRequest request = createValidDefinitionRequest();
        request.setProductKey("123"); // Invalid product key

        Set<ConstraintViolation<ProductDefinitionRequest.DefinitionRequest>> violations = validator.validate(request);

        assertThat(violations).hasSize(1);
        assertThat(violations.iterator().next().getMessage()).isEqualTo("Product key must have 6 characters.");
    }

    @Test
    public void whenProductKeyIsNull_thenValidationFails() {
        ProductDefinitionRequest.DefinitionRequest request = createValidDefinitionRequest();
        request.setProductKey(null); // Null product key

        Set<ConstraintViolation<ProductDefinitionRequest.DefinitionRequest>> violations = validator.validate(request);

        assertThat(violations).hasSize(1);
        assertThat(violations.iterator().next().getMessage()).isEqualTo("Product key is required.");
    }

    @Test
    public void whenProductKeyIsValid_thenValidationOK() {
        ProductDefinitionRequest.DefinitionRequest request = createValidDefinitionRequest();
        request.setProductKey("123456"); // Invalid product key

        Set<ConstraintViolation<ProductDefinitionRequest.DefinitionRequest>> violations = validator.validate(request);

        assertThat(violations).hasSize(0);
    }

    // @NotNull(message = "Rate is required.")
    // @DecimalMin(value = "0.0", inclusive = false, message = "Rate must be greater
    // than 0.")
    // private BigDecimal rate;
    @Test
    public void whenRateIsInvalid_thenValidationFails() {
        ProductDefinitionRequest.DefinitionRequest request = createValidDefinitionRequest();
        request.setRate(BigDecimal.ZERO); // Invalid rate

        Set<ConstraintViolation<ProductDefinitionRequest.DefinitionRequest>> violations = validator.validate(request);

        assertThat(violations).hasSize(1);
        assertThat(violations.iterator().next().getMessage()).isEqualTo("Rate must be greater than 0.");
    }

    @Test
    public void whenRateIsNull_thenValidationFails() {
        ProductDefinitionRequest.DefinitionRequest request = createValidDefinitionRequest();
        request.setRate(null); // Invalid rate

        Set<ConstraintViolation<ProductDefinitionRequest.DefinitionRequest>> violations = validator.validate(request);

        assertThat(violations).hasSize(1);
        assertThat(violations.iterator().next().getMessage()).isEqualTo("Rate is required.");
    }

    @Test
    public void whenRateIsValid_thenValidationOK() {
        ProductDefinitionRequest.DefinitionRequest request = createValidDefinitionRequest();
        request.setRate(BigDecimal.ONE); // Invalid rate

        Set<ConstraintViolation<ProductDefinitionRequest.DefinitionRequest>> violations = validator.validate(request);

        assertThat(violations).hasSize(0);
    }

    // @NotNull(message = "Type is required.")
    // @Pattern(regexp = "^(ACCOUNT|LOAN)$", message = "Type must be 'ACCOUNT' or
    // 'LOAN'.")
    // private String type;
    @Test
    public void whenTypeIsInvalid_thenValidationFails() {
        ProductDefinitionRequest.DefinitionRequest request = createValidDefinitionRequest();
        request.setType("SAVINGS"); // Invalid type

        Set<ConstraintViolation<ProductDefinitionRequest.DefinitionRequest>> violations = validator.validate(request);

        assertThat(violations).hasSize(1);
        assertThat(violations.iterator().next().getMessage()).isEqualTo("Type must be 'ACCOUNT' or 'LOAN'.");
    }

    @Test
    public void whenTypeIsNull_thenValidationFails() {
        ProductDefinitionRequest.DefinitionRequest request = createValidDefinitionRequest();
        request.setType(null); // Null type

        Set<ConstraintViolation<ProductDefinitionRequest.DefinitionRequest>> violations = validator.validate(request);

        assertThat(violations).hasSize(1);
        assertThat(violations.iterator().next().getMessage()).isEqualTo("Type is required.");
    }

    @Test
    public void whenTypeIsValid_thenValidationOK() {
        ProductDefinitionRequest.DefinitionRequest request = createValidDefinitionRequest();
        request.setType("LOAN"); // Invalid type

        Set<ConstraintViolation<ProductDefinitionRequest.DefinitionRequest>> violations = validator.validate(request);

        assertThat(violations).hasSize(0);
    }

    // @NotNull(message = "Please provide a description.")
    // private String description;
    @Test
    public void whenDescriptionIsNull_thenValidationFails() {
        ProductDefinitionRequest.DefinitionRequest request = createValidDefinitionRequest();
        request.setDescription(null); // Null description

        Set<ConstraintViolation<ProductDefinitionRequest.DefinitionRequest>> violations = validator.validate(request);

        assertThat(violations).hasSize(1);
        assertThat(violations.iterator().next().getMessage()).isEqualTo("Please provide a description.");
    }

    @Test
    public void whenDescriptionIsNotNull_thenValidationOK() {
        ProductDefinitionRequest.DefinitionRequest request = createValidDefinitionRequest();
        request.setDescription("Description"); // Null description

        Set<ConstraintViolation<ProductDefinitionRequest.DefinitionRequest>> violations = validator.validate(request);

        assertThat(violations).hasSize(0);
    }

    // @Pattern(regexp = "^(DAY|MONTH)$", message = "Unit must be 'DATE' or
    // 'MONTH'.")
    // private String unit;
    @Test
    public void whenPayRateUnitIsInvalid_thenValidationFails() {
        ProductDefinitionRequest.DefinitionRequest request = createValidDefinitionRequest();
        request.setPayRate(new PayRateDto("WEEK", 1)); // Invalid PayRate unit

        Set<ConstraintViolation<ProductDefinitionRequest.DefinitionRequest>> violations = validator.validate(request);

        assertThat(violations).hasSize(1);
        assertThat(violations.iterator().next().getMessage()).isEqualTo("Unit must be 'DATE' or 'MONTH'.");
    }

    @Test
    public void whenPayRateUnitIsNull_thenValidationFails() {
        ProductDefinitionRequest.DefinitionRequest request = createValidDefinitionRequest();
        request.setPayRate(new PayRateDto(null, 1)); // Null PayRate unit

        Set<ConstraintViolation<ProductDefinitionRequest.DefinitionRequest>> violations = validator.validate(request);

        assertThat(violations).hasSize(1);
        assertThat(violations.iterator().next().getMessage()).isEqualTo("Pay rate unit is required.");
    }

    @Test
    public void whenPayRateUnitIsValid_thenValidationOK() {
        ProductDefinitionRequest.DefinitionRequest request = createValidDefinitionRequest();
        request.setPayRate(new PayRateDto("DAY", 1)); // Invalid PayRate unit

        Set<ConstraintViolation<ProductDefinitionRequest.DefinitionRequest>> violations = validator.validate(request);

        assertThat(violations).hasSize(0);
    }

    // @NotNull(message = "Pay rate value is required.")
    // @DecimalMin(value = "1", message = "Pay rate value must be at least 1.")
    // private int value;
    @Test
    public void whenPayRateValueIsInvalid_thenValidationFails() {
        ProductDefinitionRequest.DefinitionRequest request = createValidDefinitionRequest();
        request.setPayRate(new PayRateDto("DAY", 0)); // Invalid PayRate Value

        Set<ConstraintViolation<ProductDefinitionRequest.DefinitionRequest>> violations = validator.validate(request);

        assertThat(violations).hasSize(1);
        assertThat(violations.iterator().next().getMessage()).isEqualTo("Pay rate value must be at least 1.");
    }

    @Test
    public void whenPayRateValueIsValid_thenValidationOK() {
        ProductDefinitionRequest.DefinitionRequest request = createValidDefinitionRequest();
        request.setPayRate(new PayRateDto("DAY", 14)); // Invalid PayRate Value

        Set<ConstraintViolation<ProductDefinitionRequest.DefinitionRequest>> violations = validator.validate(request);

        assertThat(violations).hasSize(0);
    }

    private ProductDefinitionRequest.DefinitionRequest createValidDefinitionRequest() {
        ProductDefinitionRequest.DefinitionRequest request = new ProductDefinitionRequest.DefinitionRequest();
        request.setOperation("N");
        request.setProductKey("TEST01");
        request.setDescription("Test description");
        request.setType("ACCOUNT");
        request.setRate(BigDecimal.valueOf(1.0));
        PayRateDto payRateDto = new PayRateDto("DAY", 1);
        request.setPayRate(payRateDto);
        return request;
    }
}
