package com.school.schedule.controller;

import com.school.schedule.model.Room;
import com.school.schedule.service.RoomService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/rooms")
@RequiredArgsConstructor
public class RoomController {

    private final RoomService roomService;

    @GetMapping
    public String list(Model model) {
        model.addAttribute("rooms", roomService.findAll());
        return "rooms/list";
    }

    @GetMapping("/new")
    public String createForm(Model model) {
        model.addAttribute("room", new Room());
        return "rooms/form";
    }

    @GetMapping("/{id}/edit")
    public String editForm(@PathVariable Long id, Model model) {
        model.addAttribute("room", roomService.findById(id));
        return "rooms/form";
    }

    @PostMapping("/save")
    public String save(@ModelAttribute Room room, Model model) {
        if (roomService.nameTaken(room)) {
            model.addAttribute("error", "Room name already exists");
            return "rooms/form";
        }
        roomService.save(room);
        return "redirect:/rooms";
    }

    @PostMapping("/{id}/delete")
    public String delete(@PathVariable Long id) {
        roomService.delete(id);
        return "redirect:/rooms";
    }
}
