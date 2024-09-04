package com.project.react_tft.Repository;

import com.project.react_tft.domain.MeetReply;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;



public interface MeetReplyRepository extends JpaRepository<MeetReply, Long> {

    @Query("select mr from MeetReply mr where mr.meetBoard.meetId = :meetId")
    Page<MeetReply> listOfMeetBoard(Long meetId, Pageable pageable);
}
