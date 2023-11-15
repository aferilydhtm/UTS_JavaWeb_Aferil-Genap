package com.uts.jwp;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.uts.jwp.domain.Teacher;

@RestController
public class TeacherController {

    // Aferil Yudhatama
	// 2112500786
    // UTS Java Web Programming (Genap)

    /* Codingan 1 ===================================================================*/
	// @GetMapping("/teachers")
	// public List<Teacher> getTeachers(){

	// 	List<Teacher> teacherList = new ArrayList<>();

	// 	Teacher teacher1 = new Teacher();
	// 	teacher1.setNip("12345");
	// 	teacher1.setFullName("Soekarno");
	// 	teacher1.setEmail(" soekarno@gmail.com");
	// 	teacher1.setPhoneNumber("0811111111");


	// 	Teacher teacher2 = new Teacher();
	// 	teacher2.setNip("67890");
	// 	teacher2.setFullName("Mohammad Hatta");
	// 	teacher2.setEmail(" hatta.muhammad@gmail.com");
	// 	teacher2.setPhoneNumber(" 0812222222");
		

	// 	Teacher teacher3 = new Teacher();
	// 	teacher3.setNip("212121");
	// 	teacher3.setFullName("Adam Malik");
	// 	teacher3.setEmail("malik.adam@gmail.com");
	// 	teacher3.setPhoneNumber("0855555555");

    // 	Teacher teacher4 = new Teacher();
	// 	teacher4.setNip("2112500786");
	// 	teacher4.setFullName("AferilYudhatama");
	// 	teacher4.setEmail("aferilyudhatama04@gmail.com");
	// 	teacher4.setPhoneNumber("08974835297");

	// 	teacherList.addAll(List.of(teacher1, teacher2, teacher3, teacher4));
	// 	return teacherList;
    // }

    /* Codingan 2 ======================================================================== */
    public static Map<String, Teacher> teacherMap = new HashMap<>();

    @GetMapping("/teachers")
	public List<Teacher> getTeachers(){
		return teacherMap.values().stream().toList();
	}

    @PostMapping("/teachers")
	public ResponseEntity<String> addTeacher(@RequestBody Teacher teacher){
		teacherMap.put(teacher.getNip(), teacher);
		Teacher savedTeacher = teacherMap.get(teacher.getNip());
		return new ResponseEntity<>("Teacher wit NIP: " + savedTeacher.getNip() + " has been created", HttpStatus.OK);
	}

    @GetMapping(value = "/teachers/{nip}")
	public ResponseEntity<Teacher> findTeacher(@PathVariable("nip") String nip){
		final Teacher teacher = teacherMap.get(nip);
		return new ResponseEntity<>(teacher, HttpStatus.OK);
	}

    @PutMapping(value = "/teachers/{nip}")
	public ResponseEntity<String> updateTeacher(@PathVariable("nip") String nip, @RequestBody Teacher teacher){
		final Teacher teacherToBeUpdated = teacherMap.get(teacher.getNip());
		teacherToBeUpdated.setFullName(teacher.getFullName());
		teacherToBeUpdated.setEmail(teacher.getEmail());
		teacherToBeUpdated.setPhoneNumber(teacher.getPhoneNumber()); 

		teacherMap.put(teacher.getNip(), teacherToBeUpdated);
		return new ResponseEntity<String>("Teacher with NIP: " + teacherToBeUpdated.getNip() + " has been updated", HttpStatus.OK);
	}

    @DeleteMapping(value = "/teachers/{nip}")
	public ResponseEntity<Void> deleteTeacher(@PathVariable("nip") String nip){
		teacherMap.remove(nip);
		return new ResponseEntity<Void>(HttpStatus.OK);
	}
}
