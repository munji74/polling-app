package com.example.pollservice.poll;

import com.example.pollservice.api.dto.CreatePollRequest;
import com.example.pollservice.api.dto.PollResponse;
import jakarta.transaction.Transactional;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

@Service
public class PollService {

    private final PollRepository polls;
    private final PollOptionRepository options;
    private final VoteRepository votes;

    public PollService(PollRepository polls, PollOptionRepository options, VoteRepository votes) {
        this.polls = polls;
        this.options = options;
        this.votes = votes;
    }

    /* -------------------- READ -------------------- */

    /** Public list; if email is present, include hasVoted/userOptionId per poll. */
    public List<PollResponse> listAllForUser(String emailOrNull) {
        return polls.findAll().stream()
                .map(p -> toDtoWithCounts(p, Optional.ofNullable(emailOrNull)))
                .toList();
    }

    /** Public get; if email is present, include hasVoted/userOptionId. */
    public PollResponse getOneForUser(Long id, String emailOrNull) {
        var p = polls.findById(id).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
        return toDtoWithCounts(p, Optional.ofNullable(emailOrNull));
    }

    /* Backward-compat (used by existing controller methods if needed) */
    public List<PollResponse> listAll() {
        return listAllForUser(null);
    }

    public PollResponse getOne(Long id) {
        return getOneForUser(id, null);
    }

    /* -------------------- WRITE -------------------- */

    @Transactional
    public PollResponse create(CreatePollRequest req, String creatorEmail) {
        if (req.expiresAt().isBefore(Instant.now())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "expiresAt must be in the future");
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
        return toDtoWithCounts(saved, Optional.of(creatorEmail)); // creator hasn't voted; flags will be false
    }

    /**
     * One vote per user. If user already voted in this poll, return 409 CONFLICT.
     */
    @Transactional
    public PollResponse vote(Long pollId, Long optionId, String voterEmail) {
        var poll = polls.findById(pollId).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

        if (poll.getExpiresAt().isBefore(Instant.now())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Poll expired");
        }

        if (votes.existsByPollIdAndVoter(pollId, voterEmail)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "You already voted in this poll.");
        }

        var opt = options.findById(optionId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Option not found"));

        if (!opt.getPoll().getId().equals(pollId)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Option does not belong to poll");
        }

        var v = new Vote();
        v.setPoll(poll);
        v.setOption(opt);
        v.setVoter(voterEmail);
        votes.save(v);

        return toDtoWithCounts(poll, Optional.of(voterEmail));
    }

    /* -------------------- helpers -------------------- */

    private PollResponse toDtoWithCounts(Poll p, Optional<String> email) {
        var optionDtos = p.getOptions().stream()
                .map(o -> new PollResponse.OptionDto(
                        o.getId(),
                        o.getText(),
                        votes.countByOptionId(o.getId())
                ))
                .toList();

        long total = votes.countByPollId(p.getId());
        String status = p.getExpiresAt().isAfter(Instant.now()) ? "ACTIVE" : "EXPIRED";

        boolean hasVoted = false;
        Long userOptionId = null;

        if (email.isPresent()) {
            // Prefer fetching the actual vote so UI can preselect the option.
            // Ensure VoteRepository has: Optional<Vote> findByPollIdAndVoter(Long pollId, String voter);
            var myVote = votes.findByPollIdAndVoter(p.getId(), email.get());
            if (myVote.isPresent()) {
                hasVoted = true;
                userOptionId = myVote.get().getOption().getId();
            }
        }

        return new PollResponse(
                p.getId(),
                p.getQuestion(),
                p.getExpiresAt(),
                status,
                total,
                optionDtos,
                hasVoted,
                userOptionId
        );
    }
}
