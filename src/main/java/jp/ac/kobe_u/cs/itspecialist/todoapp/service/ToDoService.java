package jp.ac.kobe_u.cs.itspecialist.todoapp.service;

import java.util.*;

import java.util.function.BiFunction;
import java.util.function.Function;

import jp.ac.kobe_u.cs.itspecialist.todoapp.TriFunction;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.util.Pair;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import jp.ac.kobe_u.cs.itspecialist.todoapp.dto.ToDoForm;
import jp.ac.kobe_u.cs.itspecialist.todoapp.entity.ToDo;
import jp.ac.kobe_u.cs.itspecialist.todoapp.exception.ToDoAppException;
import jp.ac.kobe_u.cs.itspecialist.todoapp.repository.ToDoRepository;

@Service
public class ToDoService {
    @Autowired
    MemberService mService;
    @Autowired
    ToDoRepository tRepo;
    /**
     * ToDoを作成する (C)
     * @param mid 作成者
     * @param form フォーム
     * @return
     */
    public ToDo createToDo(String mid, ToDoForm form) {
        mService.getMember(mid); //実在メンバーか確認
        ToDo todo = form.toEntity();
        if(!todo.isValidDueDate()) {
            throw new ToDoAppException(ToDoAppException.INVALID_TODO_OPERATION,
                    todo.getDueAt() + ": should be after created at");
        }
        todo.setMid(mid);
        return tRepo.save(todo);
    }

    /**
     * ToDoを1つ取得する (R)
     * @param seq
     * @return
     */
    public ToDo getToDo(Long seq) {
        ToDo todo = tRepo.findById(seq).orElseThrow(
            () -> new ToDoAppException(ToDoAppException.NO_SUCH_TODO_EXISTS, 
            seq + ": No such ToDo exists")
        );
        return todo;
    }

    public Page<ToDo> getToDoAllList(String mid, Pageable pageable) {
        return tRepo.findByMidAndDone(mid, false, pageable);
    }

    /**
     * あるメンバーのToDoリストを取得する (R)
     * @param mid
     * @return
     */
    public Page<ToDo> getToDoList(String mid, String sortBy, String order, Pageable pageable) {
        TriFunction<String, Boolean, Pageable, Page<ToDo>> finder = midAndDoneFinder.getOrDefault(Pair.of(sortBy, order),
                (memberId, doneFlag, pageable2) -> tRepo.findByMidAndDone(memberId, doneFlag, pageable2));
        return finder.apply(mid, false, pageable);
    }

    public Page<ToDo> getDoneAllList(String mid, Pageable pageable) {
        return tRepo.findByMidAndDone(mid, true, pageable);
    }

    /**
     * あるメンバーのDoneリストを取得する (R)
     * @param mid
     * @return
     */
    public Page<ToDo> getDoneList(String mid, String sortBy, String order, Pageable pageable) {
        TriFunction<String, Boolean, Pageable, Page<ToDo>> finder = midAndDoneFinder.getOrDefault(Pair.of(sortBy, order),
                (memberId, doneFlag, pageable2) -> tRepo.findByMidAndDone(memberId, doneFlag, pageable2));
        return finder.apply(mid, true, pageable);
    }

    private final Map<Pair<String, String>, TriFunction<String, Boolean, Pageable, Page<ToDo>>> midAndDoneFinder = generateMidAndDoneFinder();
    private Map<Pair<String, String>, TriFunction<String, Boolean, Pageable, Page<ToDo>>> generateMidAndDoneFinder() {
        Map<Pair<String, String>, TriFunction<String, Boolean, Pageable, Page<ToDo>>> map = new HashMap<>();
        map.put(Pair.of("seq", "asc"), (mid, done, pageable) -> tRepo.findByMidAndDoneOrderBySeqAsc(mid, done, pageable));
        map.put(Pair.of("seq", "desc"), (mid, done, pageable) -> tRepo.findByMidAndDoneOrderBySeqDesc(mid, done, pageable));
        map.put(Pair.of("title", "asc"), (mid, done, pageable) -> tRepo.findByMidAndDoneOrderByTitleAsc(mid, done, pageable));
        map.put(Pair.of("title", "desc"), (mid, done, pageable) -> tRepo.findByMidAndDoneOrderByTitleDesc(mid, done, pageable));
        map.put(Pair.of("created_at", "asc"), (mid, done, pageable) -> tRepo.findByMidAndDoneOrderByCreatedAtAsc(mid, done, pageable));
        map.put(Pair.of("created_at", "desc"), (mid, done, pageable) -> tRepo.findByMidAndDoneOrderByCreatedAtDesc(mid, done, pageable));
        map.put(Pair.of("done_at", "asc"), (mid, done, pageable) -> tRepo.findByMidAndDoneOrderByDoneAtAsc(mid, done, pageable));
        map.put(Pair.of("done_at", "desc"), (mid, done, pageable) -> tRepo.findByMidAndDoneOrderByDoneAtDesc(mid, done, pageable));
        map.put(Pair.of("due_at", "asc"), (mid, done, pageable) -> tRepo.findByMidAndDoneOrderByDueAtAsc(mid, done, pageable));
        map.put(Pair.of("due_at", "desc"), (mid, done, pageable) -> tRepo.findByMidAndDoneOrderByDueAtDesc(mid, done, pageable));
        return map;
    }

    /**
     * 全員のToDoリストを取得する (R)
     * @return
     */
    public Page<ToDo> getToDoList(String sortBy, String order, Pageable pageable) {
        BiFunction<Boolean, Pageable, Page<ToDo>> finder = doneFinder.getOrDefault(Pair.of(sortBy, order),
                (doneFlag, pageable2) -> tRepo.findByDone(doneFlag, pageable2));
        return finder.apply(false, pageable);
    }

    /**
     * 全員のDoneリストを取得する (R)
     * @return
     */
    public Page<ToDo> getDoneList(String sortBy, String order, Pageable pageable) {
        BiFunction<Boolean, Pageable, Page<ToDo>> finder = doneFinder.getOrDefault(Pair.of(sortBy, order),
                (doneFlag, pageable2) -> tRepo.findByDone(doneFlag, pageable2));
        return finder.apply(true, pageable);
    }

    /**
     * 〆切を更新する．
     * @param mid
     * @param seq
     * @param due
     */
    public ToDo updateDueDate(String mid, Long seq, Date due) {
        ToDo todo = getToDo(seq);
        if (!Objects.equals(mid, todo.getMid())) {
            throw new ToDoAppException(ToDoAppException.INVALID_TODO_OPERATION, mid
                    + ": Cannot done other's todo of " + todo.getMid());
        }
        if (due != null && due.before(todo.getCreatedAt())) {
            throw new ToDoAppException(ToDoAppException.INVALID_TODO_OPERATION,
                    due + ": should be after created at.");
        }
        todo.setDueAt(due);
        return tRepo.save(todo);
    }

    private final Map<Pair<String, String>, BiFunction<Boolean, Pageable, Page<ToDo>>> doneFinder = generateDoneFinder();
    private Map<Pair<String, String>, BiFunction<Boolean, Pageable, Page<ToDo>>> generateDoneFinder() {
        Map<Pair<String, String>, BiFunction<Boolean, Pageable, Page<ToDo>>> map = new HashMap<>();
        map.put(Pair.of("seq", "asc"), (doneFlag, pageable) -> tRepo.findByDoneOrderBySeqAsc(doneFlag, pageable));
        map.put(Pair.of("seq", "desc"), (doneFlag, pageable) -> tRepo.findByDoneOrderBySeqDesc(doneFlag, pageable));
        map.put(Pair.of("title", "asc"), (doneFlag, pageable) -> tRepo.findByDoneOrderByTitleAsc(doneFlag, pageable));
        map.put(Pair.of("title", "desc"), (doneFlag, pageable) -> tRepo.findByDoneOrderByTitleDesc(doneFlag, pageable));
        map.put(Pair.of("mid", "asc"), (doneFlag, pageable) -> tRepo.findByDoneOrderByMidAsc(doneFlag, pageable));
        map.put(Pair.of("mid", "desc"), (doneFlag, pageable) -> tRepo.findByDoneOrderByMidDesc(doneFlag, pageable));
        map.put(Pair.of("created_at", "asc"), (doneFlag, pageable) -> tRepo.findByDoneOrderByCreatedAtAsc(doneFlag, pageable));
        map.put(Pair.of("created_at", "desc"), (doneFlag, pageable) -> tRepo.findByDoneOrderByCreatedAtDesc(doneFlag, pageable));
        map.put(Pair.of("done_at", "asc"), (doneFlag, pageable) -> tRepo.findByDoneOrderByDoneAtAsc(doneFlag, pageable));
        map.put(Pair.of("done_at", "desc"), (doneFlag, pageable) -> tRepo.findByDoneOrderByDoneAtDesc(doneFlag, pageable));
        map.put(Pair.of("due_at", "asc"), (doneFlag, pageable) -> tRepo.findByDoneOrderByDueAtAsc(doneFlag, pageable));
        map.put(Pair.of("due_at", "desc"), (doneFlag, pageable) -> tRepo.findByDoneOrderByDueAtDesc(doneFlag, pageable));
        return map;
    }

    /**
     * ToDoを完了する
     * @param mid 完了者
     * @param seq 完了するToDoの番号
     * @return
     */
    public ToDo done(String mid, Long seq) {
        ToDo todo = getToDo(seq);
        //Doneの認可を確認する．他人のToDoを閉めたらダメ．
        if (!mid.equals(todo.getMid())) {
            throw new ToDoAppException(ToDoAppException.INVALID_TODO_OPERATION, mid
                    + ": Cannot done other's todo of " + todo.getMid());
        }
        todo.setDone(true);
        todo.setDoneAt(new Date());
        return tRepo.save(todo);
    }

    /**
     * ToDo の完了をキャンセルする．
     * @param mid 完了者
     * @param seq 完了をキャンセルするToDoの番号
     * @return
     */
    public ToDo cancel(String mid, Long seq) {
        ToDo todo = getToDo(seq);
        // Doneの認可を確認する．他人のToDoを閉めたらダメ．
        if (!mid.equals(todo.getMid())) {
            throw new ToDoAppException(ToDoAppException.INVALID_TODO_OPERATION,
                    mid + ": Cannot cancel other's todo of " + todo.getMid());
        }
        todo.setDone(false);
        todo.setDoneAt(null);
        return tRepo.save(todo);
    }

    /**
     * ToDoを更新する
     * @param mid 更新者
     * @param seq 更新するToDo番号
     * @param form　更新フォーム
     * @return
     */
    public ToDo updateToDo(String mid, Long seq, ToDoForm form) {
        ToDo todo = getToDo(seq);
        //Doneの認可を確認する．他人のToDoを更新したらダメ．
        if (!mid.equals(todo.getMid())) {
            throw new ToDoAppException(ToDoAppException.INVALID_TODO_OPERATION, mid
                    + ": Cannot update other's todo of " + todo.getMid());
        }
        todo.setTitle(form.getTitle()); //タイトルを更新
        return tRepo.save(todo);
    }

    /**
     * 背景色を更新する．
     * @param mid 更新者
     * @param seq 更新するToDo番号
     * @param background 新しい背景色
     * @return
     */
    public ToDo updateBackground(String mid, Long seq, String background) {
        ToDo todo = getToDo(seq);
        //Doneの認可を確認する．他人のToDoを更新したらダメ．
        if (!mid.equals(todo.getMid())) {
            throw new ToDoAppException(ToDoAppException.INVALID_TODO_OPERATION, mid
                    + ": Cannot update other's todo of " + todo.getMid());
        }
        todo.setBackground(background);
        return tRepo.save(todo);
    }

    /**
     * ToDoを削除する
     * @param mid 削除者
     * @param seq 削除するToDo番号
     */
    public void deleteToDo(String mid, Long seq) {
        ToDo todo = getToDo(seq);
        //Doneの認可を確認する．他人のToDoを削除したらダメ．
        if (!mid.equals(todo.getMid())) {
            throw new ToDoAppException(ToDoAppException.INVALID_TODO_OPERATION, mid 
            + ": Cannot delete other's todo of " + todo.getMid());
        }
        tRepo.deleteById(seq);
    }



}
