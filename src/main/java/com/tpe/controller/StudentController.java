package com.tpe.controller;

import com.tpe.domain.Student;
import com.tpe.service.StudentService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

/*
clienttan 3 şekilde veri alınır:
1-requestin BODY(JSON)
2-requestin URL query param
3-requestin URL path param
 */

@RestController//requestler bu classtaki methodlarla eslestirilecek ve responselar hazirlanacak

//@ResponseBody :metodun dönüş değerini JSON formatında cevap olarak hazırlar
//@RequestBody  :requestin içindeki(body) JSON formatında olan datayı metodalrın parametresinde kullanabilmemizi sağlar
// :obje<->JSON donusumu : Jackson kutuphanesi

@RequestMapping("/students") //https://localhost:8080/students...
@RequiredArgsConstructor //sadece final olan feildlari set eder
// @Autowired (Zorunlu degil)
// public StudentController(StudentService service) {
//     this.service = service;
// }
// Eger class seviyesinde RequiredArgsConstructor  kullanirsak usteki cons. yazmaya gerek kalmaz

/*
@NoArgsConstructor -- default parametresiz const olusturur
@AllArgsConstructor-- tum fieldlari parametre olarak alan const. olusturur
@RequiredArgsConstructor -- zorunlu (final) olan field lari parametre olarak alan const. olusturur
*/

public class StudentController {

    private final StudentService service;


    //SpringBOOT'u selamlama:)
    //https://localhost:8080/students/greet + GET
    //@ResponseBody
    @GetMapping("/greet")
    public String greet() {
        return "Hello Spring Boot :)";
    }

    //1-tüm öğrencileri listeleyelim : READ
    //Request : http://localhost:8080/students + GET
    //Response: tüm öğrencilerin listesini + 200 : OK(HttpStatus Code)
    @GetMapping
    //@ResponseBody:@Restcontroller içinde var, burada gerek kalmadı.
    public ResponseEntity<List<Student>> getAllStudents() {
        //tablodan ogrencileri getirelim
        List<Student> allStudents = service.findAllStudents();
        return new ResponseEntity<>(allStudents, HttpStatus.OK);//200
    }


    //ResponseEntity : cevabın body + status kodu


    //3-öğrenci ekleme : CREATE
    //Request : http://localhost:8080/students + POST + body(JSON)

        /*
    {
    "name":"Jack",
    "lastname":"Sparrow",
    "email":"jack@mail.com",
    "grade":98
    }
       */

    //Response: öğrenci tabloya eklenir , başarılı mesaj+ 201(Created)
    @PostMapping
    public ResponseEntity<String> createStudent(@Valid @RequestBody Student student) {

        service.saveStudent(student);

        return new ResponseEntity<>("Student is created successfully...", HttpStatus.CREATED);//201
    }


    //6- query param ile id si verilen öğrenciyi getirme
    //request: http://localhost:8080/students/query?id=1 + GET
    //response: student + 200
    @GetMapping("/query")
    public ResponseEntity<Student> getStudent(@RequestParam("id") Long id) {

        Student foundStudent = service.getStudentById(id);
        return new ResponseEntity<>(foundStudent,HttpStatus.OK);

    }


    //ÖDEV:(Alternatif)6-path param ile id si verilen öğrenciyi getirme
    //request: http://localhost:8080/students/1 + GET
    //response : student + 200
    @GetMapping
    public ResponseEntity<Student> pathStudent(@RequestPart("id") Long id) {

        Student foundStudent = service.getStudentById(id);
        return new ResponseEntity<>(foundStudent, HttpStatus.OK);

    }










}
