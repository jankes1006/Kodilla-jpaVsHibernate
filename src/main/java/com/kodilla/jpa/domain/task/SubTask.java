package com.kodilla.jpa.domain.task;

import javax.persistence.*;
import java.util.Set;

@Entity
public class SubTask {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    private String status;
    @ManyToOne
    @JoinColumn(name = "task_id")
    private Task task;
    @ManyToMany(cascade = {CascadeType.ALL})
    @JoinTable(
            name = "Person_SubTask",
            joinColumns = {@JoinColumn(name = "subtask_id")},
            inverseJoinColumns = {@JoinColumn(name = "person_id")}
    )
    private Set<Person> performersOfSubTasks;

    public SubTask(String name, String status) {
        this.name = name;
        this.status = status;
    }

    public SubTask() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Task getTask() {
        return task;
    }

    public void setTask(Task task) {
        this.task = task;
    }

    public Set<Person> getPerformersOfSubTasks() {
        return performersOfSubTasks;
    }

    public void setPerformersOfSubTasks(Set<Person> performersOfSubTasks) {
        this.performersOfSubTasks = performersOfSubTasks;
    }

    @Override
    public String toString() {
        return "SubTask{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", status='" + status + '\'' +
                '}';
    }
}
