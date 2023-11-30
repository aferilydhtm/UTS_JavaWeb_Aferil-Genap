package com.uts.jwp;

// import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.hibernate.validator.internal.constraintvalidators.bv.EmailValidator;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.uts.jwp.domain.Teacher;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import lombok.extern.slf4j.Slf4j;

@Controller
@Slf4j
public class TeacherController {

    // Aferil Yudhatama
	// 2112500786
    // UTS Java Web Programming (Genap)
    public static Map<String, Teacher> teacherMap = new HashMap<>();

    @GetMapping("/teachers")
    public String getTeachers(Model model) {
        model.addAttribute("teachers", fetchTeachers());
        return "index";
    }

	@GetMapping("/signup")
    public String showSignUpForm(Teacher teacher) {
        return "addTeachers";
    }

    @PostMapping("/teachers")
    public String addTeacher(@Valid Teacher teacher, BindingResult bindingResult, Model model) {

        // Validate NIP
        String errorNIP = validateNIP(teacher.getNip());
        if (errorNIP != null) {
            ObjectError error = new ObjectError("globalError", errorNIP);
            bindingResult.addError(error);
        }

        // Validate Email
        String errorEmail = validateEmail(teacher.getEmail());
        if (errorEmail != null) {
            ObjectError error = new ObjectError("globalError", errorEmail);
            bindingResult.addError(error);
        }

        // Validate Phone Number
        String errorPhoneNumber = validatePhoneNumber(teacher.getPhoneNumber());
        if (errorPhoneNumber != null) {
            ObjectError error = new ObjectError("globalError", errorPhoneNumber);
            bindingResult.addError(error);
        }

        // Prevent duplicate data
        String duplicateDataError = checkDuplicateData(teacher);
        if (duplicateDataError != null) {
            ObjectError error = new ObjectError("globalError", duplicateDataError);
            bindingResult.addError(error);
        }

        log.info("bindingResult {}", bindingResult);

        if (bindingResult.hasErrors()) {
            return "addTeachers";
        }

        String nip = teacher.getNip();
        boolean exists = teacherMap.values().stream()
                .anyMatch(data -> nip.equals(data.getNip()));

        if (exists) {
            throw new IllegalArgumentException("Teacher with ID:" + nip + " is already exist");
        }

        teacherMap.put(nip, teacher);
        model.addAttribute("teachers", fetchTeachers());
        return "index";
    }

    private String validateNIP(String nip) {
        // Check if NIP starts with "LCT" and ends with 10 digits
        if (!nip.startsWith("LCT") || !nip.substring(3).matches("\\d{10}")) {
            return "NIP must start with 'LCT' and be followed by 10 digits";
        }
        return null;
    }

    private String validateEmail(String email) {
        // Add your email validation logic here
        // Return an error message if validation fails, otherwise return null
        return null; // Placeholder, implement your validation logic
    }

    private String validatePhoneNumber(String phoneNumber) {
        // Add your phone number validation logic here
        // Return an error message if validation fails, otherwise return null
        return null; // Placeholder, implement your validation logic
    }

    private String checkDuplicateData(Teacher teacher) {
        // Check if the teacher already exists in the teacherMap
        boolean exists = teacherMap.values().stream()
                .anyMatch(data ->
                        teacher.getEmail().equals(data.getEmail()) ||
                        teacher.getNip().equals(data.getNip()) ||
                        teacher.getPhoneNumber().equals(data.getPhoneNumber())
                );

        if (exists) {
            return "Teacher with the same NIP, Email, or Phone Number already exists";
        }

        return null;
    }

    // Show Teacher Data===========================================================
	@GetMapping(value = "/teachers/{nip}")
    public ResponseEntity<Teacher> findTeacher(@PathVariable("nip") String nip) {
        final Teacher teacher = teacherMap.get(nip);
        return new ResponseEntity<>(teacher, HttpStatus.OK);
    }

	private static List<Teacher> fetchTeachers() {
        return teacherMap.values().stream().toList();
    }
    // =============================================================================

	// @PostMapping(value = "/teachers/{nip}")
    // public String updateTeacher(@PathVariable("nip") String nip,
    //                             Teacher teacher,
    //                             BindingResult result, Model model) {
    //     final Teacher teacherToBeUpdated = teacherMap.get(teacher.getNip());
    //     teacherToBeUpdated.setFullName(teacher.getFullName());
    //     teacherToBeUpdated.setEmail(teacher.getEmail());
    //     teacherToBeUpdated.setPhoneNumber(teacher.getPhoneNumber());
    //     teacherMap.put(teacher.getNip(), teacherToBeUpdated);

    //     model.addAttribute("teachers", fetchTeachers());
    //     return "redirect:/teachers";
    // }

    // Edit Teachers Data==============================================
    @PostMapping(value = "/teachers/{nip}")
    public String updateTeacher(@PathVariable("nip") String nip,
                                @Valid Teacher updatedTeacher,
                                BindingResult result, Model model) {
        if (result.hasErrors()) {
            // If there are validation errors, return to the edit form
            return "editTeachers";
        }

        final Teacher teacherToBeUpdated = teacherMap.get(nip);
        if (teacherToBeUpdated == null) {
            throw new IllegalArgumentException("Teacher with NIP:" + nip + " is not found");
        }

        // Check for duplicate data
        if (!isUpdateValid(updatedTeacher, teacherToBeUpdated)) {
            ObjectError error = new ObjectError("globalError", "Edited data is the same as existing data");
            result.addError(error);
            return "editTeachers";
        }

        // Update teacher information
        teacherToBeUpdated.setFullName(updatedTeacher.getFullName());
        teacherToBeUpdated.setEmail(updatedTeacher.getEmail());
        teacherToBeUpdated.setPhoneNumber(updatedTeacher.getPhoneNumber());
        teacherMap.put(nip, teacherToBeUpdated);

        model.addAttribute("teachers", fetchTeachers());
        // Redirect to the teachers list page
        return "redirect:/teachers";
    }

    private boolean isUpdateValid(Teacher updatedTeacher, Teacher existingTeacher) {
        // Check if edited data is the same as existing data
        return !updatedTeacher.equals(existingTeacher) &&
            !isDuplicateData(updatedTeacher);
    }

    private boolean isDuplicateData(Teacher teacher) {
        // Check if the teacher already exists in the teacherMap
        return teacherMap.values().stream()
                .anyMatch(data ->
                        teacher.getEmail().equals(data.getEmail()) ||
                        teacher.getFullName().equals(data.getFullName()) ||
                        teacher.getPhoneNumber().equals(data.getPhoneNumber())
                );
    }

    // @PostMapping(value = "/teachers/{nip}")
    // public String updateTeacher(@PathVariable("nip") String nip,
    //                         @Valid Teacher updatedTeacher,
    //                         BindingResult result, Model model) {
    //     if (result.hasErrors()) {
    //         // If there are validation errors, return to the edit form
    //         return "editTeachers";
    //     }

    //     final Teacher teacherToBeUpdated = teacherMap.get(nip);
    //     if (teacherToBeUpdated == null) {
    //         throw new IllegalArgumentException("Teacher with NIP:" + nip + " is not found");
    //     }

    //     // Check for duplicate data
    //     if (!isUpdateValid(updatedTeacher, teacherToBeUpdated)) {
    //         ObjectError error = new ObjectError("globalError", "Updated data is the same as existing data");
    //         result.addError(error);
    //         return "editTeachers";
    //     }

    //     // Update teacher information
    //     teacherToBeUpdated.setFullName(updatedTeacher.getFullName());
    //     teacherToBeUpdated.setEmail(updatedTeacher.getEmail());
    //     teacherToBeUpdated.setPhoneNumber(updatedTeacher.getPhoneNumber());
    //     teacherMap.put(nip, teacherToBeUpdated);

    //     model.addAttribute("teachers", fetchTeachers());
    //     // Redirect to the teachers list page
    //     return "redirect:/teachers";
    // }

    // private boolean isUpdateValid(Teacher updatedTeacher, Teacher existingTeacher) {
    //     return !updatedTeacher.equals(existingTeacher);
    // }
	
	@GetMapping("/edit/{nip}")
    public String showUpdateForm(@PathVariable("nip") String nip, Model model) {
        final Teacher teacherToBeUpdated = teacherMap.get(nip);
        if (teacherToBeUpdated == null) {
            throw new IllegalArgumentException("Teacher with NIP:" + nip + " is not found");
        }
        model.addAttribute("teacher", teacherToBeUpdated);
        return "editTeachers";
    }
    // ========================================================================================

    // Delete Teacher Data===================================================================
	@GetMapping(value = "/teachers/{nip}/delete")
    public String deleteTeacher(@PathVariable("nip") String nip) {
        teacherMap.remove(nip);
        return "redirect:/teachers";
    }

}
