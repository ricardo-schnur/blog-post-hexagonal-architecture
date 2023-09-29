package de.colenet.hexagonal.todo.list.adapter.rest.validator;

import static org.assertj.core.api.Assertions.assertThat;

import io.vavr.Tuple;
import java.time.LocalDate;
import java.util.Optional;
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

    @Test
    void validateCreateTaskParameters_BothParametersInvalid_ReturnsTwoErrorMessages() {
        String description = " ";
        String dueDate = "2023-07-111";

        var result = restApiValidator.validateCreateTaskParameters(description, dueDate);

        assertThat(result.isInvalid()).isTrue();
        assertThat(result.getError())
            .containsExactlyInAnyOrder(
                "Description is mandatory",
                "Due date has to be in format yyyy-MM-dd: " + dueDate
            );
    }

    @ParameterizedTest
    @MethodSource("provideBlankStrings")
    void validateCreateTaskParameters_DescriptionInvalidAndDueDateValid_ReturnsOneError(String description) {
        String dueDate = "2023-07-11";

        var result = restApiValidator.validateCreateTaskParameters(description, dueDate);

        assertThat(result.isInvalid()).isTrue();
        assertThat(result.getError()).containsExactlyInAnyOrder("Description is mandatory");
    }

    @ParameterizedTest
    @ValueSource(strings = { "Not a date in format yyyy-MM-dd", "11.07.2023", "11/07/2023", "2023-7-11" })
    void validateCreateTaskParameters_DescriptionValidAndDueDateInvalid_ReturnsOneError(String dueDate) {
        String description = "Some description";

        var result = restApiValidator.validateCreateTaskParameters(description, dueDate);

        assertThat(result.isInvalid()).isTrue();
        assertThat(result.getError()).containsExactlyInAnyOrder("Due date has to be in format yyyy-MM-dd: " + dueDate);
    }

    @ParameterizedTest
    @MethodSource("provideBlankStrings")
    void validateCreateTaskParameters_ValidParametersDueDateBlank_ReturnsResultWithEmptyDate(String dueDate) {
        String description = "Some description";

        var result = restApiValidator.validateCreateTaskParameters(description, dueDate);

        assertThat(result.isValid()).isTrue();
        assertThat(result.get()).isEqualTo(Tuple.of(description, Optional.empty()));
    }

    @Test
    void validateCreateTaskParameters_ValidParametersDueDateNotBlank_ReturnsResultWithParsedDate() {
        String description = "Some description";
        String dueDate = "2023-07-11";

        var result = restApiValidator.validateCreateTaskParameters(description, dueDate);

        assertThat(result.isValid()).isTrue();
        assertThat(result.get()).isEqualTo(Tuple.of(description, Optional.of(LocalDate.of(2023, 7, 11))));
    }

    private static Stream<String> provideBlankStrings() {
        return Stream.of(null, "", "   ", "\n");
    }
}
