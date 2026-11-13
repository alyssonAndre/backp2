package prova.appnativo.exception;

public class AvaliacaoFisicaException {

    public static class NotFound extends NotFoundException {

        public NotFound() {
            super("Avaliação física não encontrada");
        }
    }
}
