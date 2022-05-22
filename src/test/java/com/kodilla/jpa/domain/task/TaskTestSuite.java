package com.kodilla.jpa.domain.task;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.persistence.*;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@SpringBootTest
class TaskTestSuite {

    @PersistenceUnit
    private EntityManagerFactory emf;

    private List<Long> insertExampleData() {

        Person p1 = new Person("Jan", "Rubach");
        Person p2 = new Person("Jacek", "Rubach");
        Person p3 = new Person("Adam", "Rubach");
        Person p4 = new Person("Maria", "Rubach");
        Task t1 = new Task("Stworzenie widoku głównego", "Implement");
        Task t2 = new Task("Stworzenie widoku zakładki pomocniczej", "Implement");
        Task t3 = new Task("Wykonanie makiety", "Ready for test");
        SubTask s1 = new SubTask("[B] Przygotowanie EP dla widoku głównego", "Implement");
        SubTask s2 = new SubTask("[F] Przygotowanie widoku głównego", "Implement");
        SubTask s3 = new SubTask("[B] Przygotowanie EP dla zakładki pomocniczej", "Implement");
        SubTask s4 = new SubTask("[F] Przygotowanie widoku zakładki pomocniczej", "Implement");

        s1.setPerformersOfSubTasks(Set.of(p1));
        p1.setSubTasks(List.of(s1));
        s2.setPerformersOfSubTasks(Set.of(p2));
        p2.setSubTasks(List.of(s2));
        s3.setPerformersOfSubTasks(Set.of(p3, p4));
        s4.setPerformersOfSubTasks(Set.of(p3, p4));
        p3.setSubTasks(List.of(s3, s4));
        p4.setSubTasks(List.of(s3, s4));
        t1.setSubTasks(List.of(s1, s2));
        s1.setTask(t1);
        s2.setTask(t1);
        t2.setSubTasks(List.of(s3, s4));
        s3.setTask(t2);
        s4.setTask(t2);
        t1.setPerformersOfTasks(Set.of(p1, p2));
        p1.setTasks(List.of(t1));
        p2.setTasks(List.of(t1));
        t2.setPerformersOfTasks(Set.of(p3, p4));
        p3.setTasks(List.of(t2));
        t3.setPerformersOfTasks(Set.of(p4));
        p4.setTasks(List.of(t2, t3));

        EntityManager em = emf.createEntityManager();
        em.getTransaction().begin();
        em.persist(p1);
        em.persist(p2);
        em.persist(p3);
        em.persist(p4);
        em.persist(s1);
        em.persist(s2);
        em.persist(s3);
        em.persist(s4);
        em.persist(t1);
        em.persist(t2);
        em.persist(t3);
        em.flush();
        em.getTransaction().commit();
        em.close();

        return List.of(t1.getId(), t2.getId(), t3.getId());
    }

    @Test
    void shouldNPlusOneProblemOccure() {
        //Given
        List<Long> savedTasks = insertExampleData();
        EntityManager em = emf.createEntityManager();

        //When
        System.out.println("****************** BEGIN OF FETCHING *******************");
        System.out.println("*** STEP 1 – query for task ***");

        TypedQuery<Task> query = em.createQuery(
                "from Task "
                        + " where id in (" + taskIds(savedTasks) + ")",
                Task.class);
        EntityGraph<Task> eg = em.createEntityGraph(Task.class);
        eg.addAttributeNodes("performersOfTasks");
        eg.addSubgraph("subTasks").addAttributeNodes("performersOfSubTasks");

        query.setHint("javax.persistence.fetchgraph", eg);
        List<Task> tasks = query.getResultList();
        for (Task task : tasks) {
            System.out.println("*** STEP 2 – read basic data about task ***");
            System.out.println(task);

            System.out.println("*** STEP 3 – read the task performers task ***");
            for (Person person : task.getPerformersOfTasks()) {
                System.out.println(person);
            }

            for (SubTask subTask : task.getSubTasks()) {
                System.out.println("*** STEP 4 – read the basic data about subtask ***");
                System.out.println(subTask);
                System.out.println("*** STEP 5 – read the performers subtask ***");
                for (Person person : subTask.getPerformersOfSubTasks()) {
                    System.out.println(person);
                }
            }
        }

        System.out.println("****************** END OF FETCHING *******************");
    }

    private String taskIds(List<Long> invoiceIds) {
        return invoiceIds.stream()
                .map(n -> "" + n)
                .collect(Collectors.joining(","));
    }
}
