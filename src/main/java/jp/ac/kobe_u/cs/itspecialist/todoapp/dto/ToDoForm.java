package jp.ac.kobe_u.cs.itspecialist.todoapp.dto;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Date;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

import jp.ac.kobe_u.cs.itspecialist.todoapp.entity.ToDo;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

/**
 * ToDoの入力フォーム
 */
@Data
public class ToDoForm {
    @NotBlank
    @Size(min=1, max=64)
    String title; //ToDo題目
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    LocalDateTime due; // 期限

    public ToDo toEntity() {
        ToDo t = new ToDo();
        t.setTitle(title);
        t.setCreatedAt(new Date());
        t.setDone(false);
        t.setDueAt(getDueDate());
        return t;
    }

    public Date getDueDate() {
        if (due == null) {
            return null;
        }
        return toDate(due);
    }

    private Date toDate(LocalDateTime ldt) {
        ZoneId id = ZoneId.systemDefault();
        ZonedDateTime zdt = ZonedDateTime.of(ldt, id);
        Instant instant = zdt.toInstant();
        return Date.from(instant);
    }
}
