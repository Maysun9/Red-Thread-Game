package com.example.redthreadgame.Model;

import com.example.redthreadgame.Enums.ProposalVoteType;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(
        name = "proposal_votes",
        uniqueConstraints = @UniqueConstraint(columnNames = {"solution_proposal_id", "player_id"})
)
public class ProposalVote {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ProposalVoteType vote;

    @Column(columnDefinition = "datetime", nullable = false)
    private LocalDateTime votedAt;

    @ManyToOne
    @JoinColumn(name = "solution_proposal_id", referencedColumnName = "id", nullable = false)
    @JsonIgnore
    private SolutionProposal solutionProposal;

    @ManyToOne
    @JoinColumn(name = "player_id", referencedColumnName = "id", nullable = false)
    private Player player;

}
