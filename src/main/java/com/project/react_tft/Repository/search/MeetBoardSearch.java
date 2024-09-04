package com.project.react_tft.Repository.search;

import com.project.react_tft.domain.Board;
import com.project.react_tft.domain.MeetBoard;
import com.project.react_tft.dto.BoardListReplyCountDTO;
import com.project.react_tft.dto.MeetBoardListAllDTO;
import com.project.react_tft.dto.MeetBoardListReplyCountDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface MeetBoardSearch {

    Page<MeetBoard> search1(Pageable pageable);

    Page<MeetBoard> searchAll(String[] types, String keyword, Pageable pageable);

    Page<MeetBoardListAllDTO> searchWithAll(String[] types,
                                            String keyword,
                                            Pageable pageable);



    Page<MeetBoardListReplyCountDTO> searchWithMeetReplyCount(String[] types,
                                                          String keyword,
                                                          Pageable pageable);
}
