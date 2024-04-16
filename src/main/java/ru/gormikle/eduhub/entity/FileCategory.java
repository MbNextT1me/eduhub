package ru.gormikle.eduhub.entity;

public enum FileCategory {
    CLUSTER_LOG(1),
    //Файлы, что студенты отправляют для автоматического запуска на удаленном сервере
    CLUSTER_SEND(2),
    //Тестовые файлы, что прикладывает препод, чтобы проверить решения студентов
    CLUSTER_TEST(3);
    private final int code;
    FileCategory(int code) {
        this.code = code;
    }

}
