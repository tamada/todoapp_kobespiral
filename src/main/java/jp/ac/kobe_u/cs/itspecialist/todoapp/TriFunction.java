package jp.ac.kobe_u.cs.itspecialist.todoapp;

@FunctionalInterface
public interface TriFunction<A1, A2, A3, R> {
    R apply(A1 a1, A2 a2, A3 a3);
}
