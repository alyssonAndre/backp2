package prova.appnativo.exception;

public class PlanoException {

    public static class NotFound extends NotFoundException {

        public NotFound() {
            super("Plano não encontrado");
        }
    }

    public static class DuplicateNome extends ConflictException {

        public DuplicateNome() {
            super("Já existe um plano com esse nome");
        }
    }

    public static class EmUso extends ConflictException {

        public EmUso(long quantidadeAlunos) {
            super("O plano não pode ser excluído: há " + quantidadeAlunos + " aluno(s) vinculado(s). Inative o plano ou troque o plano dos alunos.");
        }
    }

    public static class Inativo extends BusinessException {

        public Inativo() {
            super("O plano selecionado está inativo");
        }
    }

    public static class SemPlano extends BusinessException {

        public SemPlano() {
            super("O aluno não possui plano. Informe o valor da mensalidade.");
        }
    }
}
