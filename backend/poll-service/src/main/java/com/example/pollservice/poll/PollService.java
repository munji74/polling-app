package com.example.pollservice.poll;

import com.example.pollservice.api.dto.CreatePollRequest;
import com.example.pollservice.api.dto.PollResponse;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;

@Service
public class PollService {

    private final PollRepository polls;
    private final PollOptionRepository options;
    private final VoteRepository votes;

    public PollService(PollRepository polls, PollOptionRepository options, VoteRepository votes) {
        this.polls = polls; this.options = options; this.votes = votes;
    }

    public List<PollResponse> listAll() {
        return polls.findAll().stream().map(this::toDtoWithCounts).toList();
    }

    public PollResponse getOne(Long id) {
        var p = polls.findById(id).orElseThrow();
        return toDtoWithCounts(p);
    }

    @Transactional
    public PollResponse create(CreatePollRequest req, String creatorEmail) {
        if (req.expiresAt().isBefore(Instant.now())) {
            throw new IllegalArgumentException("expiresAt must be in the future");
        }
        var p = new Poll();
        p.setQuestion(req.question());
        p.setExpiresAt(req.expiresAt());
        p.setCreatedBy(creatorEmail);
        req.options().forEach(text -> {
            var opt = new PollOption();
            opt.setPoll(p);
            opt.setText(text);
            p.getOptions().add(opt);
        });
        var saved = polls.save(p);
        return toDtoWithCounts(saved);
    }

    @Transactional
    public PollResponse vote(Long pollId, Long optionId, String voterEmail) {
        var poll = polls.findById(pollId).orElseThrow();
        if (poll.getExpiresAt().isBefore(Instant.now())) {
            throw new IllegalStateException("Poll expired");
        }
        if (votes.existsByPollIdAndVoter(pollId, voterEmail)) {
            throw new IllegalStateException("Already voted");
        }
        var opt = options.findById(optionId).orElseThrow();
        if (!opt.getPoll().getId().equals(pollId)) {
            throw new IllegalArgumentException("Option does not belong to poll");
        }
        var v = new Vote();
        v.setPoll(poll);
        v.setOption(opt);
        v.setVoter(voterEmail);
        votes.save(v);

        return toDtoWithCounts(poll);
    }

    // ---- helpers
    private PollResponse toDtoWithCounts(Poll p) {
        var optionDtos = p.getOptions().stream()
                .map(o -> new PollResponse.OptionDto(o.getId(), o.getText(), votes.countByOptionId(o.getId())))
                .toList();
        long total = votes.countByPollId(p.getId());
        String status = p.getExpiresAt().isAfter(Instant.now()) ? "ACTIVE" : "EXPIRED";
        return new PollResponse(p.getId(), p.getQuestion(), p.getExpiresAt(), status, total, optionDtos);
    }
}
