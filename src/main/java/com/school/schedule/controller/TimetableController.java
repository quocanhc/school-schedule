package com.school.schedule.controller;

import com.school.schedule.model.Room;
import com.school.schedule.model.SchoolClass;
import com.school.schedule.model.Teacher;
import com.school.schedule.repository.RoomRepository;
import com.school.schedule.repository.SchoolClassRepository;
import com.school.schedule.repository.TeacherRepository;
import com.school.schedule.service.TimetableExcelService;
import com.school.schedule.service.TimetableService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.io.IOException;
import java.util.List;

@Controller
@RequestMapping("/timetable")
@RequiredArgsConstructor
public class TimetableController {

    private final TimetableService service;
    private final TimetableExcelService excel;
    private final SchoolClassRepository classRepo;
    private final TeacherRepository teacherRepo;
    private final RoomRepository roomRepo;

    @GetMapping
    public String view(@RequestParam(defaultValue = "overview") String mode,
                       @RequestParam(required = false) Long id, Model model) {
        List<SchoolClass> classes = classRepo.findAllByOrderByNameAsc();
        List<Teacher> teachers = teacherRepo.findAll(Sort.by("fullName"));
        List<Room> rooms = roomRepo.findAll(Sort.by("name"));

        if (id == null) {
            if (mode.equals("class") && !classes.isEmpty()) id = classes.get(0).getId();
            if (mode.equals("teacher") && !teachers.isEmpty()) id = teachers.get(0).getId();
            if (mode.equals("room") && !rooms.isEmpty()) id = rooms.get(0).getId();
        }

        model.addAttribute("matrix", service.build(mode, id));
        model.addAttribute("mode", mode);
        model.addAttribute("selectedId", id);
        model.addAttribute("classes", classes);
        model.addAttribute("teachers", teachers);
        model.addAttribute("rooms", rooms);
        return "timetable/view";
    }

    @GetMapping("/export")
    public ResponseEntity<byte[]> export(@RequestParam(defaultValue = "overview") String mode,
                                         @RequestParam(required = false) Long id) throws IOException {
        byte[] data = excel.export(service.build(mode, id));
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"timetable.xlsx\"")
                .contentType(MediaType.parseMediaType(
                        "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"))
                .body(data);
    }
}
