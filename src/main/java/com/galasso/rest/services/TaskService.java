package com.galasso.rest.services;

import com.galasso.rest.models.Task;
import com.galasso.rest.repositories.ITaskRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Optional;
import java.util.UUID;

@Service
public class TaskService {

    @Autowired
    private ITaskRepository taskRepository;

    public ArrayList<Task> findAll() {
        return (ArrayList<Task>) this.taskRepository.findAll();
    }

    public Optional<Task> findById(String id) {
        UUID uuid = UUID.fromString(id);
        return this.taskRepository.findById(uuid);
    }

    public Optional<Task> findById(UUID uuid) {
        return this.taskRepository.findById(uuid);
    }

    public Task save(Task task) {
        return this.taskRepository.save(task);
    }

    public boolean delete(String id) {
        try {
            UUID uuid = UUID.fromString(id);
            this.taskRepository.deleteById(uuid);
            return true;
        } catch (Exception ignored) { }
        return false;
    }
    public boolean delete(UUID uuid) {
        this.taskRepository.deleteById(uuid);
        return true;
    }
}
