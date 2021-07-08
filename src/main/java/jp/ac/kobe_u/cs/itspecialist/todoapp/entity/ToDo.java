package jp.ac.kobe_u.cs.itspecialist.todoapp.entity;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import jp.ac.kobe_u.cs.itspecialist.todoapp.dto.ToDoForm;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * ToDo エンティティ
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class ToDo {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long seq;         //通し番号
    String title;     //題目
    String mid;       //作成者
    boolean done;     //完了フラグ
    @Temporal(TemporalType.TIMESTAMP)
    Date createdAt;   //作成日時
    @Temporal(TemporalType.TIMESTAMP)
    Date doneAt;      //完了日時
    @Temporal(TemporalType.TIMESTAMP)
    Date dueAt;       //期限
    String background; //背景色

    public boolean isValidDueDate() {
        if (dueAt == null) { // dueが設定されていなければ無視する．
            return true;
        }
        if (done) { // 完了していれば，更新不可．
            return false;
        }
        // 期限は作成日よりも後でなければならない．
        return dueAt.after(createdAt);
    }

    private static final DateFormat FORMATTER = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");

    public String getDueString() {
        if(dueAt == null) {
            return "";
        }
        return FORMATTER.format(dueAt);
    }
}
