package com.school.schedule.service;

import com.school.schedule.model.*;
import com.school.schedule.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.DayOfWeek;
import java.util.*;
import java.util.function.Function;
import java.util.stream.IntStream;

@Service
@RequiredArgsConstructor
public class TimetableService {

    public static final int PERIODS = 10;

    private final ScheduleEntryRepository entryRepo;
    private final SchoolClassRepository classRepo;
    private final TeacherRepository teacherRepo;
    private final RoomRepository roomRepo;

    public Matrix build(String mode, Long id) {
        if (id == null && !mode.equals("overview")) {
            return weekly("Thời khóa biểu", List.of(), e -> "");
        }
        return switch (mode) {
            case "class" -> {
                SchoolClass c = classRepo.findById(id).orElseThrow();
                yield weekly("Thời khóa biểu lớp " + c.getName(), entryRepo.findBySchoolClassId(id),
                        e -> e.getSubject().getName() + "\n" + e.getTeacher().getFullName()
                                + "\n" + e.getRoom().getName());
            }
            case "teacher" -> {
                Teacher t = teacherRepo.findById(id).orElseThrow();
                yield weekly("Thời khóa biểu giáo viên " + t.getFullName(), entryRepo.findByTeacherId(id),
                        e -> e.getSubject().getName() + "\n" + e.getSchoolClass().getName()
                                + "\n" + e.getRoom().getName());
            }
            case "room" -> {
                Room r = roomRepo.findById(id).orElseThrow();
                yield weekly("Thời khóa biểu phòng " + r.getName(), entryRepo.findByRoomId(id),
                        e -> e.getSchoolClass().getName() + "\n" + e.getSubject().getName()
                                + "\n" + e.getTeacher().getFullName());
            }
            default -> overview();
        };
    }

    /** Rows = Tiết 1..N, columns = days. */
    private Matrix weekly(String title, List<ScheduleEntry> list, Function<ScheduleEntry, String> text) {
        List<DayOfWeek> days = TimeSlotService.SCHOOL_DAYS;
        String[][] cells = new String[PERIODS][days.size()];
        for (String[] row : cells) Arrays.fill(row, "");

        for (ScheduleEntry e : list) {
            int d = days.indexOf(e.getTimeSlot().getDayOfWeek());
            int p = e.getTimeSlot().getPeriod() - 1;
            if (d >= 0 && p >= 0 && p < PERIODS) {
                cells[p][d] = text.apply(e);
            }
        }
        List<String> rows = IntStream.rangeClosed(1, PERIODS).mapToObj(i -> "Tiết " + i).toList();
        List<String> dayLabels = days.stream().map(TimeSlot::label).toList();
        return new Matrix(title, "Tiết", rows, dayLabels, 1, false, cells);
    }

    /** Rows = classes, columns = every day x period. */
    private Matrix overview() {
        List<SchoolClass> classes = classRepo.findAllByOrderByNameAsc();
        List<DayOfWeek> days = TimeSlotService.SCHOOL_DAYS;

        Map<Long, Integer> rowOf = new HashMap<>();
        for (int i = 0; i < classes.size(); i++) rowOf.put(classes.get(i).getId(), i);

        String[][] cells = new String[classes.size()][days.size() * PERIODS];
        for (String[] row : cells) Arrays.fill(row, "");

        for (ScheduleEntry e : entryRepo.findAll()) {
            Integer r = rowOf.get(e.getSchoolClass().getId());
            int d = days.indexOf(e.getTimeSlot().getDayOfWeek());
            int p = e.getTimeSlot().getPeriod() - 1;
            if (r != null && d >= 0 && p >= 0 && p < PERIODS) {
                cells[r][d * PERIODS + p] = e.getSubject().getName() + "\n(" + e.getTeacher().getCode() + ")";
            }
        }
        List<String> names = classes.stream().map(SchoolClass::getName).toList();
        List<String> dayLabels = days.stream().map(TimeSlot::label).toList();
        return new Matrix("Thời khóa biểu toàn trường", "Lớp", names, dayLabels, PERIODS, true, cells);
    }
}