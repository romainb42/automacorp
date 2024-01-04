package com.emse.spring.automacorp.api;

import com.emse.spring.automacorp.dao.RoomDao;
import com.emse.spring.automacorp.dao.SensorDao;
import com.emse.spring.automacorp.dao.WindowDao;
import com.emse.spring.automacorp.dto.Window;
import com.emse.spring.automacorp.dto.WindowMapper;
import com.emse.spring.automacorp.model.RoomEntity;
import com.emse.spring.automacorp.model.SensorEntity;
import com.emse.spring.automacorp.model.SensorType;
import com.emse.spring.automacorp.model.WindowEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@CrossOrigin
@RestController
@RequestMapping("/api/windows")
@Transactional
public class WindowController {
    private final WindowDao windowDao;

    public WindowController(WindowDao windowDao) {
        this.windowDao = windowDao;
    }

    @GetMapping
    public List<Window> findAll() {
        return windowDao.findAll()
                .stream()
                .map(WindowMapper::of)
                .sorted(Comparator.comparing(Window::name))
                .collect(Collectors.toList());
    }

    @GetMapping(path = "/{id}")
    public Window findById(@PathVariable Long id) {
        return windowDao.findById(id).map(WindowMapper::of).orElse(null);
    }

    @PostMapping
    public ResponseEntity<Window> create(@RequestBody WindowCommand window) {
        WindowEntity entity = new WindowEntity(window.name(), window.windowStatus(), window.room());
        WindowEntity saved = windowDao.save(entity);
        return ResponseEntity.ok(WindowMapper.of(saved));
    }

    @PutMapping(path = "/{id}")
    public ResponseEntity<Window> update(@PathVariable Long id, @RequestBody WindowCommand window) {
        WindowEntity entity = windowDao.findById(id).orElse(null);
        if (entity == null) {
            return ResponseEntity.badRequest().build();
        }
        entity.setName(window.name());
        entity.setWindowStatus(window.windowStatus());
        entity.setRoom(window.room());
        return ResponseEntity.ok(WindowMapper.of(entity));
    }

    @DeleteMapping(path = "/{id}")
    public void delete(@PathVariable Long id) {
        windowDao.deleteById(id);
    }
}