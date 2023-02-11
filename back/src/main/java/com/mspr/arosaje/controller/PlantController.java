package com.mspr.arosaje.controller;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import com.mspr.arosaje.entity.Client;
import com.mspr.arosaje.entity.Plant;
import com.mspr.arosaje.entity.Specie;
import com.mspr.arosaje.repository.ClientRepository;
import com.mspr.arosaje.repository.PlantRepository;
import com.mspr.arosaje.repository.SpecieRepository;

@CrossOrigin(origins = "http://localhost:3000/", allowCredentials = "true")
@RestController
public class PlantController {

    @Autowired
    ClientRepository clientRepository;

    @Autowired
    PlantRepository plantRepository;

    @Autowired
    SpecieRepository specieRepository;

    @GetMapping("plants")
    public ResponseEntity<List<Plant>> getPlants() {
        List<Plant> plants = plantRepository.findAll();
        return new ResponseEntity<>(plants, HttpStatus.OK);
    }

    @GetMapping("plants/{id}")
    public ResponseEntity<Plant> getPlantById(@PathVariable("id") int id) {
        Optional<Plant> plantData = plantRepository.findById(id);

        if (!plantData.isPresent()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(plantData.get(), HttpStatus.OK);
    }

    @PostMapping("plants")
    public ResponseEntity<HttpStatus> createPlant(@RequestBody Plant plant) {
        Optional<Client> clientData = clientRepository.findById(plant.getClient().getId());
        Optional<Specie> specieData = specieRepository.findById(plant.getSpecie().getId());
        if (!clientData.isPresent() && !specieData.isPresent()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        } else {
            plantRepository.save(plant);
            return new ResponseEntity<>(HttpStatus.CREATED);
        }
    }

    @PutMapping("plants/{id}")
    public ResponseEntity<Plant> updatePlant(@PathVariable("id") int id, @RequestBody Plant plant) {
        Optional<Plant> plantData = plantRepository.findById(id);
        Optional<Specie> specieData = specieRepository.findById(plant.getSpecie().getId());
        if (!plantData.isPresent() && !specieData.isPresent()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }
        Plant _plant = plantData.get();
        _plant.setClient(plant.getClient());
        _plant.setAddress(plant.getAddress());
        _plant.setProfil_photo(plant.getProfil_photo());
        _plant.setSpecie(plant.getSpecie());
        _plant.setLatitude(plant.getLatitude());
        _plant.setLongitude(plant.getLongitude());
        return new ResponseEntity<>(plantRepository.save(_plant), HttpStatus.OK);
    }

    @DeleteMapping("plants/{id}")
    public ResponseEntity<?> deletePlant(@PathVariable("id") int id) {
        try {
            plantRepository.deleteById(id);
            return new ResponseEntity<>("Plant deleted successfully", HttpStatus.OK);
        } catch (Exception err) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping("plants/client/{id}")
    public ResponseEntity<?> getClientPlants(@PathVariable("id") int id) {
        Optional<Client> clientData = clientRepository.findById(id);
        List<Plant> plantData = plantRepository.findByClientId(clientData.get().getId());

        if(!clientData.isPresent()) {
            return new ResponseEntity<>(plantData, HttpStatus.NOT_FOUND);
        }

        return new ResponseEntity<>(plantData, HttpStatus.OK);
    }
}
