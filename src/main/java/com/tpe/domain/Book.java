package com.tpe.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@Getter
@Setter
@Entity
public class Book {//MANY

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Setter(AccessLevel.NONE)
    private Long id;

    @JsonProperty("bookName")
    //sadece JSON formatinda bu fieldin isminin belirtilen sekilde gosterilmesini saglar
    private String name;

    @ManyToOne
    @JsonIgnore //bu fieldi JSON formatinda ignore et (gormezden gel)
    private Student student;
    //kitabı kaydederken bu kitap hangi öğrenciye ait
    //bu öğrenciyi bulup set etmeliyiz

}
