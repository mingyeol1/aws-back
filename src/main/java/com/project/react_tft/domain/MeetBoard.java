package com.project.react_tft.domain;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.BatchSize;

import java.sql.Time;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Entity
@Getter
@Setter
@Builder
@ToString(exclude = "imageSet")
@AllArgsConstructor
@NoArgsConstructor
public class MeetBoard extends BaseEntity{


   @Id
   @GeneratedValue(strategy = GenerationType.IDENTITY)
   private Long meetId;

   private String meetTitle;

   private String meetWriter;

   private String meetContent;

   private int personnel;

   private LocalDateTime meetTime;


   @OneToMany(mappedBy = "meetBoard",
           cascade = {CascadeType.ALL}
           , fetch = FetchType.LAZY
           , orphanRemoval = true)
   @Builder.Default
   @BatchSize(size = 20)
   private Set<MeetBoardImage> imageSet = new HashSet<>();

   public void addImage(String uuid, String fileName){
      MeetBoardImage meetBoardImage = MeetBoardImage.builder()
              .uuid(uuid)
              .fileName(fileName)
              .meetBoard(this)
              .ord(imageSet.size())
              .build();
      imageSet.add(meetBoardImage);
   }

   public void clearImages(){
      imageSet.forEach(meetBoardImage -> meetBoardImage.changeBoard(null));

      this.imageSet.clear();
   }




}
