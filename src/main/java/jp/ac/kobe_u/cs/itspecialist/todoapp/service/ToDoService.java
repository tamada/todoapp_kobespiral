package jp.ac.kobe_u.cs.itspecialist.todoapp.service;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiFunction;
import java.util.function.Function;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.util.Pair;
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

    /**
     * あるメンバーのToDoリストを取得する (R)
     * @param mid
     * @return
     */
    public List<ToDo> getToDoList(String mid, String sortBy, String order) {
        BiFunction<String, Boolean, List<ToDo>> finder = midAndDoneFinder.getOrDefault(Pair.of(sortBy, order),
                (memberId, doneFlag) -> tRepo.findByMidAndDone(memberId, doneFlag));
        return finder.apply(mid, false);
    }
    /**
     * あるメンバーのDoneリストを取得する (R)
     * @param mid
     * @return
     */
    public List<ToDo> getDoneList(String mid, String sortBy, String order) {
        BiFunction<String, Boolean, List<ToDo>> finder = midAndDoneFinder.getOrDefault(Pair.of(sortBy, order),
                (memberId, doneFlag) -> tRepo.findByMidAndDone(memberId, doneFlag));
        return finder.apply(mid, true);
    }

    private final Map<Pair<String, String>, BiFunction<String, Boolean, List<ToDo>>> midAndDoneFinder = generateMidAndDoneFinder();
    private Map<Pair<String, String>, BiFunction<String, Boolean, List<ToDo>>> generateMidAndDoneFinder() {
        Map<Pair<String, String>, BiFunction<String, Boolean, List<ToDo>>> map = new HashMap<>();
        map.put(Pair.of("seq", "asc"), (mid, done) -> tRepo.findByMidAndDoneOrderBySeqAsc(mid, done));
        map.put(Pair.of("seq", "desc"), (mid, done) -> tRepo.findByMidAndDoneOrderBySeqDesc(mid, done));
        map.put(Pair.of("title", "asc"), (mid, done) -> tRepo.findByMidAndDoneOrderByTitleAsc(mid, done));
        map.put(Pair.of("title", "desc"), (mid, done) -> tRepo.findByMidAndDoneOrderByTitleDesc(mid, done));
        map.put(Pair.of("created_at", "asc"), (mid, done) -> tRepo.findByMidAndDoneOrderByCreatedAtAsc(mid, done));
        map.put(Pair.of("created_at", "desc"), (mid, done) -> tRepo.findByMidAndDoneOrderByCreatedAtDesc(mid, done));
        map.put(Pair.of("done_at", "asc"), (mid, done) -> tRepo.findByMidAndDoneOrderByDoneAtAsc(mid, done));
        map.put(Pair.of("done_at", "desc"), (mid, done) -> tRepo.findByMidAndDoneOrderByDoneAtDesc(mid, done));
        return map;
    }

    /**
     * 全員のToDoリストを取得する (R)
     * @return
     */
    public List<ToDo> getToDoList(String sortBy, String order) {
        Function<Boolean, List<ToDo>> finder = doneFinder.getOrDefault(Pair.of(sortBy, order),
                (doneFlag) -> tRepo.findByDone(doneFlag));
        return finder.apply(false);
    }

    /**
     * 全員のDoneリストを取得する (R)
     * @return
     */
    public List<ToDo> getDoneList(String sortBy, String order) {
        Function<Boolean, List<ToDo>> finder = doneFinder.getOrDefault(Pair.of(sortBy, order),
                (doneFlag) -> tRepo.findByDone(doneFlag));
        return finder.apply(true);
    }


    private final Map<Pair<String, String>, Function<Boolean, List<ToDo>>> doneFinder = generateDoneFinder();
    private Map<Pair<String, String>, Function<Boolean, List<ToDo>>> generateDoneFinder() {
        Map<Pair<String, String>, Function<Boolean, List<ToDo>>> map = new HashMap<>();
        map.put(Pair.of("seq", "asc"), (doneFlag) -> tRepo.findByDoneOrderBySeqAsc(doneFlag));
        map.put(Pair.of("seq", "desc"), (doneFlag) -> tRepo.findByDoneOrderBySeqDesc(doneFlag));
        map.put(Pair.of("title", "asc"), (doneFlag) -> tRepo.findByDoneOrderByTitleAsc(doneFlag));
        map.put(Pair.of("title", "desc"), (doneFlag) -> tRepo.findByDoneOrderByTitleDesc(doneFlag));
        map.put(Pair.of("mid", "asc"), (doneFlag) -> tRepo.findByDoneOrderByMidAsc(doneFlag));
        map.put(Pair.of("mid", "desc"), (doneFlag) -> tRepo.findByDoneOrderByMidDesc(doneFlag));
        map.put(Pair.of("created_at", "asc"), (doneFlag) -> tRepo.findByDoneOrderByCreatedAtAsc(doneFlag));
        map.put(Pair.of("created_at", "desc"), (doneFlag) -> tRepo.findByDoneOrderByCreatedAtDesc(doneFlag));
        map.put(Pair.of("done_at", "asc"), (doneFlag) -> tRepo.findByDoneOrderByDoneAtAsc(doneFlag));
        map.put(Pair.of("done_at", "desc"), (doneFlag) -> tRepo.findByDoneOrderByDoneAtDesc(doneFlag));
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
