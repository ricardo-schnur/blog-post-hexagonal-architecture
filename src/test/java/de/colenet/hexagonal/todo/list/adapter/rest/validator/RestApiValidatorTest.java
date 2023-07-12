package de.colenet.hexagonal.todo.list.adapter.rest.validator;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.UUID;
import java.util.stream.Stream;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;

class RestApiValidatorTest {

    private final RestApiValidator restApiValidator = new RestApiValidator();

    @ParameterizedTest
    @MethodSource("provideBlankStrings")
    void validateId_IdIsBlank_ReturnsMandatoryError(String id) {
        var result = restApiValidator.validateId(id);

        assertThat(result.isInvalid()).isTrue();
        assertThat(result.getError()).isEqualTo("ID is mandatory");
    }

    @ParameterizedTest
    @ValueSource(strings = { "1", "SomeId", "123e4567-e89b-42d3-a456-5566424400000" })
    void validateId_IdIsNoValidUuid_ReturnsInvalidError(String id) {
        var result = restApiValidator.validateId(id);

        assertThat(result.isInvalid()).isTrue();
        assertThat(result.getError()).isEqualTo("Not a valid UUID: " + id);
    }

    @Test
    void validateId_InputIsValid_ReturnsParsedUuid() {
        String id = "123e4567-e89b-42d3-a456-556642440000";

        var result = restApiValidator.validateId(id);

        assertThat(result.isValid()).isTrue();
        assertThat(result.get()).isEqualTo(UUID.fromString(id));
    }

    @ParameterizedTest
    @MethodSource("provideBlankStrings")
    void validateDescription_DescriptionIsBlank_ReturnsMandatoryError(String description) {
        var result = restApiValidator.validateDescription(description);

        assertThat(result.isInvalid()).isTrue();
        assertThat(result.getError()).isEqualTo("Description is mandatory");
    }

    @Test
    void validateDescription_InputIsValid_ReturnsValidatedInput() {
        String description = "Some description";

        var result = restApiValidator.validateDescription(description);

        assertThat(result.isValid()).isTrue();
        assertThat(result.get()).isEqualTo(description);
    }

    private static Stream<String> provideBlankStrings() {
        return Stream.of(null, "", "   ", "\n");
    }
}
