package prova.appnativo.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import prova.appnativo.domain.*;
import prova.appnativo.domain.enums.*;
import prova.appnativo.repository.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Component
@ConditionalOnProperty(name = "app.seed.enabled", havingValue = "true", matchIfMissing = true)
public class DataSeeder implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(DataSeeder.class);

    private final PlanoRepository planoRepository;
    private final ProfessorRepository professorRepository;
    private final AlunoRepository alunoRepository;
    private final MensalidadeRepository mensalidadeRepository;
    private final TreinoRepository treinoRepository;
    private final AvaliacaoFisicaRepository avaliacaoFisicaRepository;

    public DataSeeder(
            PlanoRepository planoRepository,
            ProfessorRepository professorRepository,
            AlunoRepository alunoRepository,
            MensalidadeRepository mensalidadeRepository,
            TreinoRepository treinoRepository,
            AvaliacaoFisicaRepository avaliacaoFisicaRepository
    ) {
        this.planoRepository = planoRepository;
        this.professorRepository = professorRepository;
        this.alunoRepository = alunoRepository;
        this.mensalidadeRepository = mensalidadeRepository;
        this.treinoRepository = treinoRepository;
        this.avaliacaoFisicaRepository = avaliacaoFisicaRepository;
    }

    @Override
    @Transactional
    public void run(String... args) {
        if (planoRepository.count() > 0 || alunoRepository.count() > 0) {
            return;
        }

        LocalDate hoje = LocalDate.now();

        Plano mensal = plano("Mensal", "Acesso livre à musculação e aulas coletivas, sem fidelidade.", "119.90", 1, true);
        Plano trimestral = plano("Trimestral", "Fidelidade de 3 meses com desconto na mensalidade.", "109.90", 3, true);
        Plano semestral = plano("Semestral", "Fidelidade de 6 meses + 1 avaliação física por trimestre.", "99.90", 6, true);
        Plano anual = plano("Anual", "Melhor custo-benefício: 12 meses, avaliações mensais e acesso a todas as unidades.", "89.90", 12, true);
        plano("Promoção de Verão", "Campanha encerrada. Mantido apenas para histórico.", "79.90", 3, false);

        Professor carlos = professor("Carlos Mendes", "52998224725", "carlos.mendes@academia.com", "(11) 98888-1001", "012345-G/SP", "Musculação e Hipertrofia", hoje.minusYears(4));
        Professor fernanda = professor("Fernanda Lima", "11144477735", "fernanda.lima@academia.com", "(11) 98888-1002", "023456-G/SP", "Treinamento Funcional e Emagrecimento", hoje.minusYears(2));
        Professor rafael = professor("Rafael Souza", "39053344705", "rafael.souza@academia.com", "(11) 98888-1003", "034567-G/SP", "Avaliação Física e Reabilitação", hoje.minusMonths(8));

        Aluno ana = aluno("Ana Beatriz Rocha", "28625587887", "ana.rocha@email.com", "(11) 97777-2001", LocalDate.of(1996, 3, 14), hoje.minusMonths(7), anual, StatusAluno.ATIVO);
        Aluno bruno = aluno("Bruno Carvalho", "71428793860", "bruno.carvalho@email.com", "(11) 97777-2002", LocalDate.of(1990, 8, 2), hoje.minusMonths(5), mensal, StatusAluno.ATIVO);
        Aluno camila = aluno("Camila Ferreira", "84434895894", "camila.ferreira@email.com", "(11) 97777-2003", LocalDate.of(2001, 11, 25), hoje.minusMonths(4), semestral, StatusAluno.ATIVO);
        Aluno diego = aluno("Diego Martins", "05631758001", "diego.martins@email.com", "(11) 97777-2004", LocalDate.of(1988, 1, 9), hoje.minusMonths(6), trimestral, StatusAluno.ATIVO);
        Aluno eduarda = aluno("Eduarda Alves", "93541134780", "eduarda.alves@email.com", "(11) 97777-2005", LocalDate.of(1999, 6, 30), hoje.minusMonths(3), anual, StatusAluno.ATIVO);
        Aluno felipe = aluno("Felipe Nogueira", "67876577048", "felipe.nogueira@email.com", "(11) 97777-2006", LocalDate.of(1994, 12, 17), hoje.minusMonths(4), mensal, StatusAluno.ATIVO);
        Aluno gabriela = aluno("Gabriela Santos", "34608514300", "gabriela.santos@email.com", "(11) 97777-2007", LocalDate.of(2003, 4, 5), hoje.minusMonths(2), trimestral, StatusAluno.ATIVO);
        aluno("Henrique Oliveira", "16899535009", "henrique.oliveira@email.com", "(11) 97777-2008", LocalDate.of(1985, 9, 21), hoje.minusMonths(10), semestral, StatusAluno.INATIVO);

        Set<Aluno> inadimplentes = Set.of(bruno, felipe);
        List<Aluno> ativos = List.of(ana, bruno, camila, diego, eduarda, felipe, gabriela);
        int[] diasVencimento = {5, 10, 15, 20, 25, 8, 12};
        FormaPagamento[] formas = FormaPagamento.values();

        List<Mensalidade> mensalidades = new ArrayList<>();
        for (int i = 0; i < ativos.size(); i++) {
            Aluno aluno = ativos.get(i);
            LocalDate base = hoje.withDayOfMonth(diasVencimento[i]);

            List<LocalDate> vencimentos = new ArrayList<>();
            for (int mes = -5; mes <= 1; mes++) {
                LocalDate vencimento = base.plusMonths(mes);
                if (!vencimento.isBefore(aluno.getDataMatricula().withDayOfMonth(1))) {
                    vencimentos.add(vencimento);
                }
            }

            LocalDate ultimoVencido = vencimentos.stream()
                    .filter(data -> data.isBefore(hoje))
                    .max(LocalDate::compareTo)
                    .orElse(null);

            for (LocalDate vencimento : vencimentos) {
                Mensalidade mensalidade = new Mensalidade();
                mensalidade.setAluno(aluno);
                mensalidade.setValor(aluno.getPlano().getValorMensal());
                mensalidade.setDataVencimento(vencimento);

                boolean deixarEmAtraso = inadimplentes.contains(aluno) && vencimento.equals(ultimoVencido);

                if (vencimento.isBefore(hoje) && !deixarEmAtraso) {
                    mensalidade.setStatus(StatusMensalidade.PAGA);
                    mensalidade.setDataPagamento(vencimento.minusDays(i % 3));
                    mensalidade.setFormaPagamento(formas[(i + vencimento.getMonthValue()) % formas.length]);
                } else {
                    mensalidade.setStatus(StatusMensalidade.PENDENTE);
                }
                mensalidades.add(mensalidade);
            }
        }
        mensalidadeRepository.saveAll(mensalidades);

        treino("Treino A - Peito, Ombro e Tríceps", ana, carlos, ObjetivoTreino.HIPERTROFIA, hoje.minusDays(20), hoje.plusDays(70),
                "Aumentar a carga progressivamente a cada 2 semanas.",
                List.of(
                        exercicio("Supino reto com barra", "Peito", 4, "8-10", "30", 90),
                        exercicio("Supino inclinado com halteres", "Peito", 3, "10-12", "14", 75),
                        exercicio("Desenvolvimento com halteres", "Ombro", 3, "10", "12", 75),
                        exercicio("Elevação lateral", "Ombro", 3, "12-15", "6", 60),
                        exercicio("Tríceps na polia (corda)", "Tríceps", 4, "12", "20", 60)
                ));
        treino("Treino B - Costas e Bíceps", ana, carlos, ObjetivoTreino.HIPERTROFIA, hoje.minusDays(20), hoje.plusDays(70), null,
                List.of(
                        exercicio("Puxada frontal", "Costas", 4, "10", "40", 90),
                        exercicio("Remada curvada", "Costas", 4, "8-10", "30", 90),
                        exercicio("Rosca direta com barra", "Bíceps", 3, "10-12", "15", 60),
                        exercicio("Rosca martelo", "Bíceps", 3, "12", "10", 60)
                ));
        treino("Circuito Funcional - Emagrecimento", diego, fernanda, ObjetivoTreino.EMAGRECIMENTO, hoje.minusDays(35), hoje.plusDays(55),
                "Executar em circuito: 3 voltas, 1 min de descanso entre voltas.",
                List.of(
                        exercicio("Burpee", "Corpo inteiro", 3, "15", null, 30),
                        exercicio("Agachamento com salto", "Pernas", 3, "20", null, 30),
                        exercicio("Kettlebell swing", "Posterior", 3, "20", "16", 30),
                        exercicio("Prancha abdominal", "Core", 3, "45s", null, 30)
                ));
        treino("Fortalecimento de Joelho", camila, rafael, ObjetivoTreino.REABILITACAO, hoje.minusDays(10), null,
                "Evitar impacto. Dor acima de 3/10 interromper o exercício.",
                List.of(
                        exercicio("Cadeira extensora (isometria)", "Quadríceps", 3, "30s", "10", 60),
                        exercicio("Ponte de glúteo", "Glúteo", 3, "15", null, 45),
                        exercicio("Agachamento na parede", "Pernas", 3, "40s", null, 60)
                ));

        avaliacao(ana, rafael, hoje.minusMonths(6), "68.40", "1.65", "29.5", "24.10", "88.0", "78.5", "101.0", "29.0", "57.0", 74);
        avaliacao(ana, rafael, hoje.minusMonths(3), "66.10", "1.65", "27.0", "24.80", "87.0", "75.0", "99.0", "29.5", "56.5", 70);
        avaliacao(ana, rafael, hoje.minusDays(5), "64.30", "1.65", "24.2", "25.60", "86.5", "71.5", "97.5", "30.0", "56.0", 66);
        avaliacao(diego, fernanda, hoje.minusMonths(5), "98.70", "1.80", "31.0", "33.00", "112.0", "104.0", "110.0", "37.0", "63.0", 82);
        avaliacao(diego, fernanda, hoje.minusMonths(2), "94.20", "1.80", "28.4", "33.40", "109.0", "99.0", "107.0", "37.5", "62.0", 78);
        avaliacao(camila, rafael, hoje.minusDays(12), "58.00", "1.62", "22.0", "21.50", "84.0", "66.0", "94.0", "26.0", "53.0", 68);

        log.info("Banco populado com dados de exemplo da academia.");
    }

    private Plano plano(String nome, String descricao, String valor, int duracao, boolean ativo) {
        return planoRepository.save(new Plano(null, nome, descricao, new BigDecimal(valor), duracao, ativo));
    }

    private Professor professor(String nome, String cpf, String email, String telefone, String cref, String especialidade, LocalDate admissao) {
        return professorRepository.save(new Professor(null, nome, cpf, email, telefone, cref, especialidade, admissao, true));
    }

    private Aluno aluno(String nome, String cpf, String email, String telefone, LocalDate nascimento, LocalDate matricula, Plano plano, StatusAluno status) {
        return alunoRepository.save(new Aluno(null, nome, cpf, email, telefone, nascimento, matricula, status, plano));
    }

    private Exercicio exercicio(String nome, String grupo, int series, String repeticoes, String carga, int descanso) {
        Exercicio exercicio = new Exercicio();
        exercicio.setNome(nome);
        exercicio.setGrupoMuscular(grupo);
        exercicio.setSeries(series);
        exercicio.setRepeticoes(repeticoes);
        exercicio.setCarga(carga == null ? null : new BigDecimal(carga));
        exercicio.setDescansoSegundos(descanso);
        return exercicio;
    }

    private void treino(String nome, Aluno aluno, Professor professor, ObjetivoTreino objetivo, LocalDate inicio, LocalDate fim, String observacoes, List<Exercicio> exercicios) {
        Treino treino = new Treino();
        treino.setNome(nome);
        treino.setAluno(aluno);
        treino.setProfessor(professor);
        treino.setObjetivo(objetivo);
        treino.setDataInicio(inicio);
        treino.setDataFim(fim);
        treino.setObservacoes(observacoes);
        treino.setAtivo(true);

        for (int i = 0; i < exercicios.size(); i++) {
            exercicios.get(i).setOrdem(i + 1);
        }
        treino.substituirExercicios(exercicios);

        treinoRepository.save(treino);
    }

    private void avaliacao(Aluno aluno, Professor professor, LocalDate data, String peso, String altura, String gordura, String musculo,
                           String torax, String cintura, String quadril, String braco, String coxa, int fc) {
        AvaliacaoFisica avaliacao = new AvaliacaoFisica();
        avaliacao.setAluno(aluno);
        avaliacao.setProfessor(professor);
        avaliacao.setDataAvaliacao(data);
        avaliacao.setPeso(new BigDecimal(peso));
        avaliacao.setAltura(new BigDecimal(altura));
        avaliacao.setPercentualGordura(new BigDecimal(gordura));
        avaliacao.setMassaMuscular(new BigDecimal(musculo));
        avaliacao.setTorax(new BigDecimal(torax));
        avaliacao.setCintura(new BigDecimal(cintura));
        avaliacao.setQuadril(new BigDecimal(quadril));
        avaliacao.setBraco(new BigDecimal(braco));
        avaliacao.setCoxa(new BigDecimal(coxa));
        avaliacao.setFrequenciaCardiacaRepouso(fc);
        avaliacaoFisicaRepository.save(avaliacao);
    }
}
