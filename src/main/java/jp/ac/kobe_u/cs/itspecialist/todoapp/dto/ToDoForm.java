package jp.ac.kobe_u.cs.itspecialist.todoapp.dto;

import java.util.Date;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

import jp.ac.kobe_u.cs.itspecialist.todoapp.entity.ToDo;
import lombok.Data;

/**
 * ToDoの入力フォーム
 */
@Data
public class ToDoForm {
    @NotBlank
    @Size(min=1, max=64)
    String title; //ToDo題目

    @NotBlank
    @Size(min=6, max=8)
    String background; // 背景色

    public ToDo toEntity() {
        ToDo t = new ToDo();
        t.setTitle(title);
        t.setCreatedAt(new Date());
        t.setDone(false);
        t.setBackground(background);
        return t;
    }
}
