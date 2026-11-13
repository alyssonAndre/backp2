package prova.appnativo.exception;

public class MensalidadeException {

    public static class NotFound extends NotFoundException {

        public NotFound() {
            super("Mensalidade não encontrada");
        }
    }

    public static class JaPaga extends ConflictException {

        public JaPaga() {
            super("Esta mensalidade já está paga");
        }
    }

    public static class Cancelada extends BusinessException {

        public Cancelada() {
            super("Esta mensalidade está cancelada");
        }
    }

    public static class NaoPaga extends BusinessException {

        public NaoPaga() {
            super("Só é possível estornar mensalidades pagas");
        }
    }

    public static class VencimentoDuplicado extends ConflictException {

        public VencimentoDuplicado() {
            super("O aluno já possui uma mensalidade com esse vencimento");
        }
    }
}
