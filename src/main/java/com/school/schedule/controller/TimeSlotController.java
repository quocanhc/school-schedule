package com.school.schedule.controller;

import com.school.schedule.model.TimeSlot;
import com.school.schedule.service.TimeSlotService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.DayOfWeek;
import java.util.LinkedHashMap;
import java.util.Map;

@Controller
@RequestMapping("/timeslots")
@RequiredArgsConstructor
public class TimeSlotController {

    private final TimeSlotService service;

    private Map<DayOfWeek, String> days() {
        Map<DayOfWeek, String> map = new LinkedHashMap<>();
        for (DayOfWeek d : TimeSlotService.SCHOOL_DAYS) {
            map.put(d, TimeSlot.label(d));
        }
        return map;
    }

    @GetMapping
    public String list(Model model) {
        model.addAttribute("slots", service.findAll());
        return "timeslots/list";
    }

    @GetMapping("/new")
    public String createForm(Model model) {
        model.addAttribute("timeSlot", new TimeSlot());
        model.addAttribute("days", days());
        return "timeslots/form";
    }

    @GetMapping("/{id}/edit")
    public String editForm(@PathVariable Long id, Model model) {
        model.addAttribute("timeSlot", service.findById(id));
        model.addAttribute("days", days());
        return "timeslots/form";
    }

    @PostMapping("/save")
    public String save(@ModelAttribute TimeSlot timeSlot, Model model) {
        if (service.exists(timeSlot)) {
            model.addAttribute("error", "This day and period already exists");
            model.addAttribute("days", days());
            return "timeslots/form";
        }
        service.save(timeSlot);
        return "redirect:/timeslots";
    }

    @PostMapping("/{id}/delete")
    public String delete(@PathVariable Long id) {
        service.delete(id);
        return "redirect:/timeslots";
    }

    @PostMapping("/generate")
    public String generate() {
        service.generateDefaults();
        return "redirect:/timeslots";
    }
}
