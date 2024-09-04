package com.project.react_tft.Repository;


import com.project.react_tft.domain.Board;
import com.project.react_tft.Repository.search.BoardSearch;
import com.project.react_tft.domain.Reply;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface BoardRepository extends JpaRepository<Board, Long> , BoardSearch {

    @Query(value = "select now()", nativeQuery = true)
    String getTime();

//    @Query("select r from Board r where r.board.bno = :bno")
//    Page<Board> listOfBoard(Pageable pageable);
    @Query("select b from Board b order by b.bno desc")
    Page<Board> listOfBoard(Pageable pageable);
}
