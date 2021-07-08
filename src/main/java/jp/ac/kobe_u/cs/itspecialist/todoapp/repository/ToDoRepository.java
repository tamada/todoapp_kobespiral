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

    // ソート機能を追加する．
    Page<ToDo> findByDoneOrderBySeqAsc(boolean done, Pageable pageable);
    Page<ToDo> findByDoneOrderBySeqDesc(boolean done, Pageable pageable);
    Page<ToDo> findByDoneOrderByTitleAsc(boolean done, Pageable pageable);
    Page<ToDo> findByDoneOrderByTitleDesc(boolean done, Pageable pageable);
    Page<ToDo> findByDoneOrderByMidAsc(boolean done, Pageable pageable);
    Page<ToDo> findByDoneOrderByMidDesc(boolean done, Pageable pageable);
    Page<ToDo> findByDoneOrderByCreatedAtAsc(boolean done, Pageable pageable);
    Page<ToDo> findByDoneOrderByCreatedAtDesc(boolean done, Pageable pageable);
    Page<ToDo> findByDoneOrderByDoneAtAsc(boolean done, Pageable pageable);
    Page<ToDo> findByDoneOrderByDoneAtDesc(boolean done, Pageable pageable);
    Page<ToDo> findByDoneOrderByDueAtAsc(boolean done, Pageable pageable);
    Page<ToDo> findByDoneOrderByDueAtDesc(boolean done, Pageable pageable);

    Page<ToDo> findByMidAndDoneOrderBySeqAsc(String mid, boolean done, Pageable pageable);
    Page<ToDo> findByMidAndDoneOrderBySeqDesc(String mid, boolean done, Pageable pageable);
    Page<ToDo> findByMidAndDoneOrderByTitleAsc(String mid, boolean done, Pageable pageable);
    Page<ToDo> findByMidAndDoneOrderByTitleDesc(String mid, boolean done, Pageable pageable);
    Page<ToDo> findByMidAndDoneOrderByCreatedAtAsc(String mid, boolean done, Pageable pageable);
    Page<ToDo> findByMidAndDoneOrderByCreatedAtDesc(String mid, boolean done, Pageable pageable);
    Page<ToDo> findByMidAndDoneOrderByDoneAtAsc(String mid, boolean done, Pageable pageable);
    Page<ToDo> findByMidAndDoneOrderByDoneAtDesc(String mid, boolean done, Pageable pageable);
    Page<ToDo> findByMidAndDoneOrderByDueAtAsc(String mid, boolean done, Pageable pageable);
    Page<ToDo> findByMidAndDoneOrderByDueAtDesc(String mid, boolean done, Pageable pageable);
}
