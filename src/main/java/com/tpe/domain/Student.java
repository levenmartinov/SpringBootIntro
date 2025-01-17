package com.tpe.domain;

import lombok.*;

import javax.persistence.*;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter//tum filedlar icin getter metodunun tanimlamasini saglar
@Setter//tum filedlar icin setter metodunun tanimlamasini saglar
@AllArgsConstructor//tum filedlrin parametre de verildigini Constructor medotunu tanimlar
@NoArgsConstructor//defult Constructor metodunu tanimlar

//@RequiredArgsConstructor
//cons. => objeyi const ederken final olan zorunlu olan degerleri verecegiz
//public Student(String name, String lastname) {
//    this.name = name;
//    this.lastname = lastname;
//}

@Entity
public class Student {

    @Id
    @GeneratedValue(strategy =  GenerationType.IDENTITY)
    @Setter(AccessLevel.NONE)
    private Long id;


    @NotBlank(message = "name can not be blank!")
    @Size(min = 2, max = 50, message = "name must be between 2 and 50")
    @Column(nullable = false, length = 50)
    /*final*/ private String name;  //burda finallari eger @RequiredArgsConstructor'i kullanmis olsaydik kullancaktik


    @NotBlank(message = "lastname can not be blank!")
    @Size(min = 2, max = 50, message = "lastname must be between 2 and 50")
    @Column(nullable = false)
    /*final*/ private String lastname;


    @NotNull(message = "please provide grade!")
    @Column(nullable = false)
    private Integer grade;

    @Email(message = "please provide valid email!")//aaa@bb.cccemail formatinda olmasini dogrula
    //@Pattern((regexp = "^[a-zA-Z0-9+_.-]+@[a-zA-Z0-9.-]+$"))//bu da email formatında olmasını saglamak icin doğrulama
    @Column(nullable = false, unique = true)
    private String email;

    @Setter(AccessLevel.NONE)
    private LocalDateTime createDate = LocalDateTime.now();

    //getter & setter

    @OneToMany(mappedBy = "student")
    private List<Book> bookList=new ArrayList<>();


}

