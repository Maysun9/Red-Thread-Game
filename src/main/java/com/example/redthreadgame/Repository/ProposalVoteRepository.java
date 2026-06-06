package com.example.redthreadgame.Repository;

import com.example.redthreadgame.Model.ProposalVote;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProposalVoteRepository extends JpaRepository<ProposalVote, Integer> {

    ProposalVote findProposalVoteBySolutionProposalIdAndPlayerId(Integer proposalId, Integer playerId);
    List<ProposalVote> findAllBySolutionProposalId(Integer proposalId);
}
