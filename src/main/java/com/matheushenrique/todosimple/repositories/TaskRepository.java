package com.matheushenrique.todosimple.repositories;

import com.matheushenrique.todosimple.models.Task;
import com.matheushenrique.todosimple.models.projection.TaskProjection;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TaskRepository extends JpaRepository<Task, Long> {

    Page<TaskProjection> findByUser_Id(Long id, Pageable pageable);

    //@Query(value = "SELECT t FROM Task t WHERE t.user.id = :id")
    //List<Task> findByUserId(@Param("id" Long id));

    //@Query(value = "SELECT * FROM task t WHERE t.user_id = :id", nativeQuery = true)
    //List<Task> findByUserId(@Param("id" Long id));

}