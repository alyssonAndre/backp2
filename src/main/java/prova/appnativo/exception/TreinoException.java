package prova.appnativo.exception;

public class TreinoException {

    public static class NotFound extends NotFoundException {

        public NotFound() {
            super("Ficha de treino não encontrada");
        }
    }

    public static class PeriodoInvalido extends BusinessException {

        public PeriodoInvalido() {
            super("A data de término deve ser igual ou posterior à data de início");
        }
    }
}
