package com.project.react_tft.domain;

import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ToString
@Table(name="Movie",
       uniqueConstraints = {
            @UniqueConstraint(
                name="movie_id_unique",
                columnNames="movie_id"
        )})
public class Movie extends BaseEntity {

    @OneToMany(mappedBy = "movie" , cascade = {CascadeType.ALL}, fetch = FetchType.LAZY, orphanRemoval = true)
    @ToString.Exclude
    private List<Review> reviews;

    //영화 id
    @Id
    @Column(length = 200, nullable = false)
    private Long movie_id;

    @Column(length = 200, nullable = false)
    private String movie_title;

}
