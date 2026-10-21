package com.school.schedule.config;

import com.school.schedule.model.*;
import com.school.schedule.repository.*;
import com.school.schedule.service.TimeSlotService;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;

import java.time.DayOfWeek;
import java.util.*;

@Component
@Profile("local")
@RequiredArgsConstructor
public class DataSeeder implements CommandLineRunner {

    private final TeacherRepository teacherRepo;
    private final SubjectRepository subjectRepo;
    private final SchoolClassRepository classRepo;
    private final RoomRepository roomRepo;
    private final TimeSlotRepository slotRepo;
    private final TimeSlotService slotService;
    private final ScheduleEntryRepository entryRepo;

    @Override
    public void run(String... args) {
        if (teacherRepo.count() > 0 || subjectRepo.count() > 0
                || classRepo.count() > 0 || entryRepo.count() > 0) {
            return;
        }

        slotService.generateDefaults();
        Map<String, TimeSlot> slots = new HashMap<>();
        for (TimeSlot t : slotRepo.findAll()) {
            slots.put(t.getDayOfWeek() + "-" + t.getPeriod(), t);
        }

        String[] subjectNames = {"Toán", "Ngữ văn", "Tiếng Anh", "Vật lí", "Hóa học", "Thể dục"};
        String[] subjectCodes = {"TOAN", "VAN", "ANH", "LY", "HOA", "TD"};
        String[] teacherNames = {"Nguyễn Văn An", "Trần Thị Bình", "Lê Văn Cường",
                "Phạm Thị Dung", "Hoàng Văn Em", "Vũ Thị Phương"};

        List<Subject> subjects = new ArrayList<>();
        List<Teacher> teachers = new ArrayList<>();
        for (int i = 0; i < 6; i++) {
            Subject s = new Subject();
            s.setName(subjectNames[i]);
            s.setCode(subjectCodes[i]);
            subjects.add(subjectRepo.save(s));

            Teacher t = new Teacher();
            t.setFullName(teacherNames[i]);
            t.setCode(String.format("GV%02d", i + 1));
            teachers.add(teacherRepo.save(t));
        }
        Subject meeting = new Subject();
        meeting.setName("Sinh hoạt lớp");
        meeting.setCode("SHL");
        meeting.setClassMeeting(true);
        meeting = subjectRepo.save(meeting);

        // Need at least 3 rooms
        List<Room> rooms = new ArrayList<>(roomRepo.findAll(Sort.by("id")));
        for (String name : new String[]{"P101", "P102", "P103"}) {
            if (rooms.size() >= 3) break;
            if (!roomRepo.existsByName(name)) {
                Room r = new Room();
                r.setName(name);
                r.setCapacity(45);
                rooms.add(roomRepo.save(r));
            }
        }

        String[] classNames = {"10A1", "10A2", "10A3"};
        List<SchoolClass> classes = new ArrayList<>();
        for (int c = 0; c < 3; c++) {
            SchoolClass sc = new SchoolClass();
            sc.setName(classNames[c]);
            sc.setHomeroomTeacher(teachers.get(c));
            classes.add(classRepo.save(sc));
        }

        DayOfWeek[] weekdays = {DayOfWeek.MONDAY, DayOfWeek.TUESDAY, DayOfWeek.WEDNESDAY,
                DayOfWeek.THURSDAY, DayOfWeek.FRIDAY};
        List<ScheduleEntry> entries = new ArrayList<>();

        // Mon-Fri, periods 1-4: rotating subjects, one teacher per subject, no clashes
        for (int d = 0; d < 5; d++) {
            for (int p = 1; p <= 4; p++) {
                int base = d * 4 + (p - 1);
                for (int c = 0; c < classes.size(); c++) {
                    int s = (base + c) % 6;
                    entries.add(entry(classes.get(c), subjects.get(s), teachers.get(s),
                            rooms.get(c), slots.get(weekdays[d] + "-" + p)));
                }
            }
        }
        // Friday period 5: class meeting taught by each homeroom teacher
        for (int c = 0; c < classes.size(); c++) {
            entries.add(entry(classes.get(c), meeting, teachers.get(c),
                    rooms.get(c), slots.get(DayOfWeek.FRIDAY + "-5")));
        }
        entryRepo.saveAll(entries);
    }

    private ScheduleEntry entry(SchoolClass c, Subject s, Teacher t, Room r, TimeSlot slot) {
        ScheduleEntry e = new ScheduleEntry();
        e.setSchoolClass(c);
        e.setSubject(s);
        e.setTeacher(t);
        e.setRoom(r);
        e.setTimeSlot(slot);
        return e;
    }
}