package com.galasso.rest.controllers;

import com.galasso.rest.models.Task;
import com.galasso.rest.services.TaskService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.stream.Collectors;

@RestController
public class ToDoController {


    @Autowired
    private TaskService taskService;

    @GetMapping(value = "/tasks")
    public List<Task> getTasks() {
        Comparator<Task> taskComparator = Comparator.comparing(Task::getCreatedAt);
        ArrayList<Task> tasks = this.taskService.findAll();
        tasks.sort(taskComparator);
        return tasks;
    }

    @GetMapping(path = "/tasks/{id}")
    public ResponseEntity<?> getTaskById(@PathVariable String id) {
        Map<String, Object> response = new HashMap<>();
        Optional<Task> task = this.taskService.findById(id);

        if (task.isEmpty()) {
            response.put("message", "Task not found");
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        }
        response.put("message", "Task founded");
        response.put("task", task);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PostMapping(path = "/tasks")
    public ResponseEntity<?> createTask(@RequestBody Task task, BindingResult result) {
        Map<String, Object> response = new HashMap<>();
        if (result.hasErrors()) {
            List<String> errors = result.getFieldErrors()
                    .stream()
                    .map(err -> "Field " + err.getField() + " not founded")
                    .collect(Collectors.toList());

            response.put("error", errors);
            response.put("message", "Too many errors in the request");
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        }

        Task newTask = null;

        try {
            newTask = this.taskService.save(task);
        } catch (DataAccessException error) {
            response.put("message", "An exception has occurred creating the task");
            response.put("error", error.getMessage().concat(": ").concat(error.getMostSpecificCause().getMessage()));
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }

        response.put("message", "Task created successfully");
        response.put("task", newTask);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PutMapping(value = "/tasks/{id}")
    public ResponseEntity<?> modifyTask(@PathVariable("id") String id, @RequestBody Task newTask) {
        Map<String, Object> response = new HashMap<>();
        Optional<Task> taskOptional = this.taskService.findById(id);

        if (taskOptional.isEmpty()) {
            response.put("message", "Task not found");
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        }
        Task task = taskOptional.orElse(null);

        task.setTitle(newTask.getTitle());
        task.setDescription(newTask.getDescription());

        response.put("message", "Task edited successfully");
        response.put("task", this.taskService.save(task));
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @DeleteMapping(value = "/tasks/{id}")
    public ResponseEntity<?> deleteTask(@PathVariable("id") String id) {
        Map<String, Object> response = new HashMap<>();
        boolean isDeleted = this.taskService.delete(id);
        response.put("message", isDeleted ? "Task deleted" : "Task not found");
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}
