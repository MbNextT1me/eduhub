package ru.gormikle.eduhub.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.util.UUID;

@Entity
@Data
@Table(name="files")
public class File {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", nullable = false, unique = true)
    private UUID id;

    @Column(name = "name", nullable = false, length = 255)
    private String name;

    @Enumerated(EnumType.STRING)
    @Column(name = "category", nullable = false, length = 20)
    private File.Category category;

    public enum Category {
        //Логи, что получаем с кластера
        CLUSTER_LOG,
        //Файлы, что студенты отправляют для автоматического запуска на удаленном сервере
        CLUSTER_SEND,
        //Тестовые файлы, что прикладывает препод, чтобы проверить решения студентов
        CLUSTER_TEST
    }
}
