package prova.appnativo.dto;

import jakarta.validation.constraints.*;

import java.time.LocalDate;

public record ProfessorRequest(

        @NotBlank(message = "Nome é obrigatório")
        @Size(max = 150, message = "Nome deve ter no máximo 150 caracteres")
        String nome,

        @NotBlank(message = "CPF é obrigatório")
        @Pattern(regexp = "^\\d{3}\\.?\\d{3}\\.?\\d{3}-?\\d{2}$", message = "CPF inválido")
        String cpf,

        @NotBlank(message = "E-mail é obrigatório")
        @Email(message = "E-mail inválido")
        String email,

        String telefone,

        @NotBlank(message = "CREF é obrigatório")
        String cref,

        String especialidade,

        @NotNull(message = "Data de admissão é obrigatória")
        @PastOrPresent(message = "Data de admissão não pode estar no futuro")
        LocalDate dataAdmissao,

        Boolean ativo
) {
}
