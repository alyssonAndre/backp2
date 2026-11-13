package prova.appnativo.dto;

import java.math.BigDecimal;

public record ExercicioResponse(
        Long id,
        Integer ordem,
        String nome,
        String grupoMuscular,
        Integer series,
        String repeticoes,
        BigDecimal carga,
        Integer descansoSegundos,
        String observacao
) {
}
