package com.tpe.controller;

import com.tpe.domain.Student;

import com.tpe.dto.UpdateStudentDTO;
import com.tpe.exception.ResourceNotFoundException;
import com.tpe.service.StudentService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
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

@RestController//requestler bu classtaki metodlarla eşleştirilecek ve responselar hazırlanacak
//@ResponseBody :metodun dönüş değerini JSON formatında cevap olarak hazırlar
//@RequestBody  :requestin içindeki(body) JSON formatında olan datayı metodların parametresinde kullanabilmemizi sağlar
// obje<->JSON dönüşümü: Jackson kütüphanesi
@RequestMapping("/students")//https://localhost:8080/students...
@RequiredArgsConstructor//sadece final olan fieldları set eder
//    public StudentController(StudentService service) {
//        this.service = service;
//    }
public class StudentController {

    //@Autowired
    private final StudentService service;


    //SpringBOOT'u selamlama:)
    //http://localhost:8080/students/greet + GET
    //@ResponseBody
    @GetMapping("/greet")
    public String greet() {
        return "Hello Spring Boot:)";
    }

    //1-tüm öğrencileri listeleyelim : READ
    //Request : http://localhost:8080/students + GET
    //Response: tüm öğrencilerin listesini + 200 : OK(HttpStatus Code)
    @GetMapping
    //@ResponseBody:@Restcontroller içinde var, burada gerek kalmadı.
    public ResponseEntity<List<Student>> getAllStudents() {
        //tablodan öğrencileri getirelim
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

    //6-query param ile id si verilen öğrenciyi getirme
    //request : http://localhost:8080/students/query?id=1 + GET
    //response:student + 200
    @GetMapping("/query")
    public ResponseEntity<Student> getStudent(@RequestParam("id") Long id) {
        Student foundstudent = service.getStudentById(id);
        return new ResponseEntity<>(foundstudent, HttpStatus.OK);
    }


    //ÖDEV:(Alternatif)6-path param ile id si verilen öğrenciyi getirme
    //request: http://localhost:8080/students/1 + GET
    //response : student + 200
    @GetMapping("/{id}")
    public ResponseEntity<Student> findStudent(@PathVariable("id") Long id) {
        Student foundstudent = service.getStudentById(id);
        return new ResponseEntity<>(foundstudent, HttpStatus.OK);
    }

    //8-path param ile id si verilen öğrenciyi silme
    //request: http://localhost:8080/students/1 + DELETE
    //response: tablodan kayıt silinir,başarılı mesajı + 200
    @DeleteMapping("/{deletedId}")
    public ResponseEntity<String> deleteStudent(@PathVariable("deletedId") Long id) {

        service.deleteStudentById(id);

        //return new ResponseEntity<>("Student is deleted successfully...", HttpStatus.OK);
        return ResponseEntity.ok("Student is deleted successfully...");
    }


    //10- idsi verilen öğrencinin name,lastname ve emailini değiştirme(guncelleme)
    //request : http://localhost:8080/students/1 + PUT(yerine koyma)/PATCH(kismi) + BODY(JSON)
    //response : güncelleme, başarılı mesaj + 201
    @PatchMapping("/{id}")
    public ResponseEntity<String> updateStudent(@PathVariable("id") Long id, @Valid @RequestBody UpdateStudentDTO studentDTO) {

        service.updateStudent(id, studentDTO);


        return new ResponseEntity<>("Student is updated successfully...", HttpStatus.CREATED); //201
    }


    //12-tüm öğrencileri listeleme : READ
    //pagination(sayfalandırma) : hız/performans
    //tüm kayıtları page page(sayfa sayfa) gösterelim

    //request :
    //http://localhost:8080/students/page?
    //                               page=3&
    //                               size=20&
    //                               sort=name&
    //                               direction=DESC(ASC) + GET
    @GetMapping("/page")
    public ResponseEntity<Page<Student>> getAllStudents(@RequestParam("page") int pageNo, //kacinci sayfa
                                                        @RequestParam("size") int size,   //her sayfada kac tane kayit
                                                        @RequestParam("sort") String property, //hangi ozellige gore siralama
                                                        @RequestParam("direction") Sort.Direction direction){ //siralamanin yonu icin sabit degisken

        //findAll metodunun sayfa getirmesi için gerekli olan bilgileri
        //pageable tipinde verebiliriz.
        Pageable pageable = PageRequest.of(pageNo, size, Sort.by(direction, property));

        Page<Student> studentPage = service.getAllStudentsByPage(pageable);

        return new ResponseEntity<>(studentPage, HttpStatus.OK);//200






    }












    //Not:http://localhost:8080/students/update?name=Ali&lastname=Can&email=ali@mail.com


}