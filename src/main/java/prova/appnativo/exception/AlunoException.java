package prova.appnativo.exception;

public class AlunoException {

    public static class NotFound extends NotFoundException {

        public NotFound() {
            super("Aluno não encontrado");
        }
    }

    public static class DuplicateCpf extends ConflictException {

        public DuplicateCpf() {
            super("Já existe um aluno com esse CPF");
        }
    }

    public static class DuplicateEmail extends ConflictException {

        public DuplicateEmail() {
            super("Já existe um aluno com esse e-mail");
        }
    }

    public static class Inativo extends BusinessException {

        public Inativo() {
            super("O aluno está inativo");
        }
    }
}
