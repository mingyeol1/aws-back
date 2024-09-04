package com.project.react_tft.Repository;


import com.project.react_tft.Repository.search.BoardSearch;
import com.project.react_tft.Repository.search.MeetBoardSearch;
import com.project.react_tft.domain.MeetBoard;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface MeetBoardRepository extends JpaRepository<MeetBoard, Long> , MeetBoardSearch {

    @Query("select b from Board b order by b.bno desc")
    Page<MeetBoard> listOfBoard(Pageable pageable);

    @EntityGraph(attributePaths = {"imageSet"})
    @Query("select m from MeetBoard m where m.meetId =:meetId")
    Optional<MeetBoard> findByIdWithImages(Long meetId);

}
