package prova.appnativo.exception;

public class ProfessorException {

    public static class NotFound extends NotFoundException {

        public NotFound() {
            super("Professor não encontrado");
        }
    }

    public static class DuplicateCpf extends ConflictException {

        public DuplicateCpf() {
            super("Já existe um professor com esse CPF");
        }
    }

    public static class DuplicateEmail extends ConflictException {

        public DuplicateEmail() {
            super("Já existe um professor com esse e-mail");
        }
    }

    public static class DuplicateCref extends ConflictException {

        public DuplicateCref() {
            super("Já existe um professor com esse CREF");
        }
    }

    public static class EmUso extends ConflictException {

        public EmUso() {
            super("O professor possui fichas de treino ou avaliações vinculadas. Inative o professor em vez de excluí-lo.");
        }
    }

    public static class Inativo extends BusinessException {

        public Inativo() {
            super("O professor selecionado está inativo");
        }
    }
}
