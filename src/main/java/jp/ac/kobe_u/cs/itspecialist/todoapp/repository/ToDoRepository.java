package jp.ac.kobe_u.cs.itspecialist.todoapp.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import jp.ac.kobe_u.cs.itspecialist.todoapp.entity.ToDo;

@Repository
public interface ToDoRepository extends CrudRepository<ToDo, Long> {
    Page<ToDo> findAll(Pageable pageable);
    Page<ToDo> findByDone(boolean done, Pageable pageable);
    Page<ToDo> findByMid(String mid, Pageable pageable);
    Page<ToDo> findByMidAndDone(String mid, boolean done, Pageable pageable);
    List<ToDo> findByMidAndDone(String mid, boolean done);
}
