package prova.appnativo.domain;

import jakarta.persistence.*;
import lombok.*;
import prova.appnativo.domain.enums.FormaPagamento;
import prova.appnativo.domain.enums.StatusMensalidade;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "mensalidades")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Mensalidade {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "aluno_id", nullable = false)
    private Aluno aluno;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal valor;

    @Column(nullable = false)
    private LocalDate dataVencimento;

    private LocalDate dataPagamento;

    @Enumerated(EnumType.STRING)
    private FormaPagamento formaPagamento;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private StatusMensalidade status;

    public StatusMensalidade getSituacao() {
        if (status == StatusMensalidade.PENDENTE && dataVencimento.isBefore(LocalDate.now())) {
            return StatusMensalidade.ATRASADA;
        }
        return status;
    }
}
