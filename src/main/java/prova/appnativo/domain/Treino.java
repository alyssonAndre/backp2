package prova.appnativo.domain;

import jakarta.persistence.*;
import lombok.*;
import prova.appnativo.domain.enums.ObjetivoTreino;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "treinos")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Treino {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String nome;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "aluno_id", nullable = false)
    private Aluno aluno;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "professor_id", nullable = false)
    private Professor professor;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ObjetivoTreino objetivo;

    @Column(nullable = false)
    private LocalDate dataInicio;

    private LocalDate dataFim;

    @Column(length = 1000)
    private String observacoes;

    @Column(nullable = false)
    private Boolean ativo;

    @OneToMany(mappedBy = "treino", cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("ordem ASC")
    private List<Exercicio> exercicios = new ArrayList<>();

    public void substituirExercicios(List<Exercicio> novos) {
        exercicios.clear();
        novos.forEach(exercicio -> {
            exercicio.setTreino(this);
            exercicios.add(exercicio);
        });
    }
}
