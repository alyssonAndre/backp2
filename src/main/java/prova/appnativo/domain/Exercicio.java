package prova.appnativo.domain;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

@Entity
@Table(name = "exercicios")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Exercicio {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "treino_id", nullable = false)
    private Treino treino;

    @Column(nullable = false)
    private String nome;

    private String grupoMuscular;

    @Column(nullable = false)
    private Integer series;

    @Column(nullable = false)
    private String repeticoes;

    @Column(precision = 6, scale = 2)
    private BigDecimal carga;

    private Integer descansoSegundos;

    @Column(nullable = false)
    private Integer ordem;

    private String observacao;
}
