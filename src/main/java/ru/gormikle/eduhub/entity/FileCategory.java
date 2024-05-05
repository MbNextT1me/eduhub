package ru.gormikle.eduhub.entity;

public enum FileCategory {
    CLUSTER_LOG(0),
    //Файлы, что студенты отправляют для автоматического запуска на удаленном сервере
    CLUSTER_SEND(1),
    //Тестовые файлы, что прикладывает препод, чтобы проверить решения студентов
    CLUSTER_TEST(2);
    private final int code;
    FileCategory(int code) {
        this.code = code;
    }

}
