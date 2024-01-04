package com.emse.spring.automacorp.api;

import com.emse.spring.automacorp.dao.BuildingDao;
import com.emse.spring.automacorp.dto.Building;
import com.emse.spring.automacorp.dto.BuildingMapper;
import com.emse.spring.automacorp.model.BuildingEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@CrossOrigin
@RestController
@RequestMapping("/api/buildings")
@Transactional
public class BuildingController {

    private final BuildingDao buildingDao;

    public BuildingController(BuildingDao buildingDao) {
        this.buildingDao = buildingDao;
    }

    @GetMapping
    public List<Building> findAll() {
        return buildingDao.findAll()
                .stream()
                .map(BuildingMapper::of)
                .sorted(Comparator.comparing(Building::name))
                .collect(Collectors.toList());
    }

    @GetMapping(path = "/{id}")
    public Building findById(@PathVariable Long id) {
        return buildingDao.findById(id).map(BuildingMapper::of).orElse(null);
    }

    @PostMapping
    public ResponseEntity<Building> create(@RequestBody BuildingCommand building) {
        BuildingEntity entity = new BuildingEntity(building.name(), building.outsideTemperature());
        BuildingEntity saved = buildingDao.save(entity);
        return ResponseEntity.ok(BuildingMapper.of(saved));
    }

    @PutMapping(path = "/{id}")
    public ResponseEntity<Building> update(@PathVariable Long id, @RequestBody BuildingCommand building) {
        BuildingEntity entity = buildingDao.findById(id).orElse(null);
        if (entity == null) {
            return ResponseEntity.badRequest().build();
        }
        entity.setName(building.name());
        entity.setOutsideTemperature(building.outsideTemperature());
        return ResponseEntity.ok(BuildingMapper.of(entity));
    }

    @DeleteMapping(path = "/{id}")
    public void delete(@PathVariable Long id) {
        buildingDao.deleteById(id);
    }

}
