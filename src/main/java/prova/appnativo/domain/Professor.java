package prova.appnativo.domain;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Entity
@Table(name = "professores")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Professor {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String nome;

    @Column(nullable = false, unique = true, length = 11)
    private String cpf;

    @Column(nullable = false, unique = true)
    private String email;

    private String telefone;

    @Column(nullable = false, unique = true)
    private String cref;

    private String especialidade;

    @Column(nullable = false)
    private LocalDate dataAdmissao;

    @Column(nullable = false)
    private Boolean ativo;
}
