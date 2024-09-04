package com.project.react_tft.Repository.search;

import com.project.react_tft.domain.Board;
import com.project.react_tft.domain.QBoard;
import com.project.react_tft.domain.QReply;
import com.project.react_tft.dto.BoardListReplyCountDTO;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.JPQLQuery;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.support.QuerydslRepositorySupport;

import java.util.List;

public class BoardSearchImpl extends QuerydslRepositorySupport implements  BoardSearch {

    public BoardSearchImpl() {
        super(Board.class);
    }

    @Override
    public Page<Board> search1(Pageable pageable) {

        QBoard board = QBoard.board;    //Q도메인 객체

        JPQLQuery<Board> query = from(board);   //select.. from board

        // BooleanBuilder() 사용
        BooleanBuilder booleanBuilder = new BooleanBuilder(); // (

        booleanBuilder.or(board.title.contains("11"));   // title like ...
        booleanBuilder.or(board.content.contains("11")); // content like ...

//        query.where(board.title.contains("1")); // where title like..

        query.where(booleanBuilder);                     // )
        query.where(board.bno.gt(0L));              // bno > 0

        // paging
        this.getQuerydsl().applyPagination(pageable, query);

        List<Board> list = query.fetch();

        long count = query.fetchCount();

        return null;
    }

    @Override
    public Page<Board> searchAll(String[] types, String keyword, Pageable pageable) {

        // 1. Qdomain 객체 생성
        QBoard board = QBoard.board;

        // 2. QL 작성...
        JPQLQuery<Board> query = from(board);  // select ... from board

        if( ( types != null && types.length > 0) && keyword != null ) {
            // 검색 조건과 키워드가 있는 경우....

            BooleanBuilder booleanBuilder = new BooleanBuilder(); // (

            for(String type: types) {
                switch (type) {
                    case "t":
                        booleanBuilder.or(board.title.contains(keyword));  // title like concat('%',keyword,'%')
                        break;
                    case "c":
                        booleanBuilder.or(board.content.contains(keyword));// content like concat('%',keyword,'%')
                        break;
                    case "w":
                        booleanBuilder.or(board.writer.contains(keyword)); // writer like concat('%',keyword,'%')
                        break;
                }
            }  // for end

            query.where(booleanBuilder);  // )

        }// if end

        // bno > 0
        query.where(board.bno.gt(0L));

        // paging...
        this.getQuerydsl().applyPagination(pageable, query);

        List<Board> list = query.fetch();

        long count = query.fetchCount();

        // Page<T> 형식으로 반환 : Page<Board>
        // PageImpl을 통해서 반환 : (list - 실제 목록 데이터, pageable, total -전체 개수)
        return new PageImpl<>(list,pageable,count);
    }

    @Override
    public Page<BoardListReplyCountDTO> searchWithReplyCount(String[] types,
                                                             String keyword,
                                                             Pageable pageable){
        QBoard board = QBoard.board;
        QReply reply = QReply.reply;

        JPQLQuery<Board> query = from(board);  // select * from board
        query.leftJoin(reply).on(reply.board.eq(board));  // select * from board left join reply on reply.board_bno=board.bno

        query.groupBy(board);  // 게시물 당 처리...      // group by

        if( ( types != null && types.length > 0) && keyword != null ) {
        // 검색 조건과 키워드가 있는 경우....

        BooleanBuilder booleanBuilder = new BooleanBuilder(); // (

        for(String type: types) {
            switch (type) {
                case "t":
                    booleanBuilder.or(board.title.contains(keyword));  // title like concat('%',keyword,'%')
                    break;
                case "c":
                    booleanBuilder.or(board.content.contains(keyword));// content like concat('%',keyword,'%')
                    break;
                case "w":
                    booleanBuilder.or(board.writer.contains(keyword)); // writer like concat('%',keyword,'%')
                    break;
            }
        }  // for end

        query.where(booleanBuilder);  // )

        }// if end

        // bno > 0
        query.where(board.bno.gt(0L));

        // Projections.bean() -> JPQL의 결과를 바로 DTO로 처리하는 기능 제공.
        // Querydsl도 마찬가지로 이런 기능을 제공
        JPQLQuery<BoardListReplyCountDTO> dtoQuery = query.select(Projections.
                bean(BoardListReplyCountDTO.class,
                        board.bno,
                        board.title,
                        board.writer,
                        board.regDate,
                        reply.count().as("replyCount")
                ));

        this.getQuerydsl().applyPagination(pageable,dtoQuery);

        List<BoardListReplyCountDTO> dtoList = dtoQuery.fetch();

        Long count = dtoQuery.fetchCount();

        return new PageImpl<>(dtoList, pageable, count);
    }

}
