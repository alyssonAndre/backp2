package prova.appnativo.domain;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "avaliacoes_fisicas")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AvaliacaoFisica {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "aluno_id", nullable = false)
    private Aluno aluno;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "professor_id", nullable = false)
    private Professor professor;

    @Column(nullable = false)
    private LocalDate dataAvaliacao;

    @Column(nullable = false, precision = 5, scale = 2)
    private BigDecimal peso;

    @Column(nullable = false, precision = 3, scale = 2)
    private BigDecimal altura;

    @Column(precision = 4, scale = 1)
    private BigDecimal percentualGordura;

    @Column(precision = 5, scale = 2)
    private BigDecimal massaMuscular;

    @Column(precision = 5, scale = 1)
    private BigDecimal torax;

    @Column(precision = 5, scale = 1)
    private BigDecimal cintura;

    @Column(precision = 5, scale = 1)
    private BigDecimal quadril;

    @Column(precision = 5, scale = 1)
    private BigDecimal braco;

    @Column(precision = 5, scale = 1)
    private BigDecimal coxa;

    private Integer frequenciaCardiacaRepouso;

    @Column(length = 1000)
    private String observacoes;
}
