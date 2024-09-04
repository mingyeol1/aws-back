package com.project.react_tft.Repository.search;

import com.project.react_tft.domain.*;
import com.project.react_tft.dto.*;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.Tuple;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.JPQLQuery;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.support.QuerydslRepositorySupport;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class MeetBoardSearchImpl extends QuerydslRepositorySupport implements MeetBoardSearch {

    public MeetBoardSearchImpl() {
        super(MeetBoard.class);
    }

    @Override
    public Page<MeetBoard> search1(Pageable pageable) {

        QMeetBoard meetBoard = QMeetBoard.meetBoard;    //Q도메인 객체

        JPQLQuery<MeetBoard> query = from(meetBoard);   //select.. from board

        // BooleanBuilder() 사용
        BooleanBuilder booleanBuilder = new BooleanBuilder(); // (

        booleanBuilder.or(meetBoard.meetTitle.contains("11"));   // title like ...
        booleanBuilder.or(meetBoard.meetContent.contains("11")); // content like ...

//        query.where(board.title.contains("1")); // where title like..

        query.where(booleanBuilder);                     // )
        query.where(meetBoard.meetId.gt(0L));              // bno > 0

        // paging
        this.getQuerydsl().applyPagination(pageable, query);

        List<MeetBoard> list = query.fetch();

        long count = query.fetchCount();

        return null;
    }

    @Override
    public Page<MeetBoard> searchAll(String[] types, String keyword, Pageable pageable) {

        // 1. Qdomain 객체 생성
        QMeetBoard meetBoard = QMeetBoard.meetBoard;

        // 2. QL 작성...
        JPQLQuery<MeetBoard> query = from(meetBoard);  // select ... from board

        if ((types != null && types.length > 0) && keyword != null) {
            // 검색 조건과 키워드가 있는 경우....

            BooleanBuilder booleanBuilder = new BooleanBuilder(); // (

            for (String type : types) {
                switch (type) {
                    case "t":
                        booleanBuilder.or(meetBoard.meetTitle.contains(keyword));  // title like concat('%',keyword,'%')
                        break;
                    case "c":
                        booleanBuilder.or(meetBoard.meetContent.contains(keyword));// content like concat('%',keyword,'%')
                        break;
                    case "w":
                        booleanBuilder.or(meetBoard.meetWriter.contains(keyword)); // writer like concat('%',keyword,'%')
                        break;
                }
            }  // for end

            query.where(booleanBuilder);  // )

        }// if end

        // bno > 0
        query.where(meetBoard.meetId.gt(0L));

        // paging...
        this.getQuerydsl().applyPagination(pageable, query);

        List<MeetBoard> list = query.fetch();

        long count = query.fetchCount();

        // Page<T> 형식으로 반환 : Page<Board>
        // PageImpl을 통해서 반환 : (list - 실제 목록 데이터, pageable, total -전체 개수)
        return new PageImpl<>(list, pageable, count);
    }

    @Override
    public Page<MeetBoardListReplyCountDTO> searchWithMeetReplyCount(String[] types,
                                                                     String keyword,
                                                                     Pageable pageable) {
        QMeetBoard meetBoard = QMeetBoard.meetBoard;
        QMeetReply meetReply = QMeetReply.meetReply;

        JPQLQuery<MeetBoard> query = from(meetBoard);  // select * from board
        query.leftJoin(meetReply).on(meetReply.meetBoard.eq(meetBoard));  // select * from board left join reply on reply.board_bno=board.bno

        query.groupBy(meetBoard);  // 게시물 당 처리...      // group by

        if ((types != null && types.length > 0) && keyword != null) {
            // 검색 조건과 키워드가 있는 경우....

            BooleanBuilder booleanBuilder = new BooleanBuilder(); // (

            for (String type : types) {
                switch (type) {
                    case "t":
                        booleanBuilder.or(meetBoard.meetTitle.contains(keyword));  // title like concat('%',keyword,'%')
                        break;
                    case "c":
                        booleanBuilder.or(meetBoard.meetContent.contains(keyword));// content like concat('%',keyword,'%')
                        break;
                    case "w":
                        booleanBuilder.or(meetBoard.meetWriter.contains(keyword)); // writer like concat('%',keyword,'%')
                        break;
                }
            }  // for end

            query.where(booleanBuilder);  // )

        }// if end

        // bno > 0
        query.where(meetBoard.meetId.gt(0L));

        // Projections.bean() -> JPQL의 결과를 바로 DTO로 처리하는 기능 제공.
        // Querydsl도 마찬가지로 이런 기능을 제공
        JPQLQuery<MeetBoardListReplyCountDTO> dtoQuery = query.select(Projections.
                bean(MeetBoardListReplyCountDTO.class,
                        meetBoard.meetId,
                        meetBoard.meetTitle,
                        meetBoard.meetWriter,
                        meetBoard.regDate,
                        meetReply.count().as("meetReplyCount")
                ));

        this.getQuerydsl().applyPagination(pageable, dtoQuery);

        List<MeetBoardListReplyCountDTO> dtoList = dtoQuery.fetch();

        Long count = dtoQuery.fetchCount();

        return new PageImpl<>(dtoList, pageable, count);
    }

    @Override
    public Page<MeetBoardListAllDTO> searchWithAll(String[] types, String keyword, Pageable pageable) {
        QMeetBoard meetBoard = QMeetBoard.meetBoard;
        QMeetReply meetReply = QMeetReply.meetReply;

        JPQLQuery<MeetBoard> query = from(meetBoard);
        query.leftJoin(meetReply).on(meetReply.meetBoard.eq(meetBoard));

       if ((types != null && types.length > 0) && keyword != null) {
           BooleanBuilder booleanBuilder = new BooleanBuilder();

           for (String type : types) {

               switch (type) {
                   case "t":
                       booleanBuilder.or(meetBoard.meetTitle.contains(keyword));
                       break;
                   case "c":
                       booleanBuilder.or(meetBoard.meetContent.contains(keyword));
                       break;
                   case "w":
                       booleanBuilder.or(meetBoard.meetWriter.contains(keyword));
                       break;
               }
           }
           query.where(booleanBuilder);
       }

       query.groupBy(meetBoard);



        getQuerydsl().applyPagination(pageable, query);

        JPQLQuery<Tuple> tupleJPQLQuery = query.select(meetBoard, meetReply.countDistinct());


        List<Tuple> tupleList = tupleJPQLQuery.fetch();

        List<MeetBoardListAllDTO> dtoList = tupleList.stream().map(tuple -> {

            MeetBoard meetBoard1 = (MeetBoard) tuple.get(meetBoard);
            long replyCount = tuple.get(1,Long.class);

            MeetBoardListAllDTO dto = MeetBoardListAllDTO.builder()
                    .meetId(meetBoard1.getMeetId())
                    .meetTitle(meetBoard1.getMeetTitle())
                    .meetWriter(meetBoard1.getMeetWriter())
                    .meetContent(meetBoard1.getMeetContent())
                    .regDate(meetBoard1.getRegDate())
                    .personnel(meetBoard1.getPersonnel())
                    .meetTime(meetBoard1.getMeetTime())
                    .replyCount(replyCount)
                    .build();

            List<MeetBoardImageDTO> imageDTOS = meetBoard1.getImageSet().stream().sorted()
                    .map(meetBoardImage -> MeetBoardImageDTO.builder()
                            .uuid(meetBoardImage.getUuid())
                            .fileName(meetBoardImage.getFileName())
                            .ord(meetBoardImage.getOrd())
                            .build()
                    ).collect(Collectors.toList());

            dto.setMeetBoardImages(imageDTOS);

            return dto;
        }).collect(Collectors.toList());

        long totalCount = query.fetchCount();

        return new PageImpl<>(dtoList, pageable, totalCount);
    }
}


