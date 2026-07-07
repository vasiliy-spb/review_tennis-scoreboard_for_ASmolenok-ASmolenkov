package io.github.asmolenkov.tennismatchscoreboard.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "MATCHES")
@Builder
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class Match {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @ManyToOne()
    @JoinColumn(name = "PLAYER1", referencedColumnName = "ID", nullable = false)
    private Player playerOne;

    @ManyToOne
    @JoinColumn(name = "PLAYER2", referencedColumnName = "ID", nullable = false)
    private Player playerSecond;

    @ManyToOne
    @JoinColumn(name = "WINNER", referencedColumnName = "ID", nullable = false)
    private Player winner;
}
