# SimpleSpringBootProductCatalog
Это серверное приложение на spring boot "Каталог Товаров".

Каталог состоит из разделов и продуктов.
Раздел может содержать подразделы. Товар может находиться только в одном разделе.
У приложения реализовано rest api для управления данными каталога. (Добавление / Изменение / Удаление)

Для работы с данными используется JPA
Количество разделов и их глубина вложенности может быть не ограничена.

### Инструкция по настройке
В качестве СУБД использовать PostgreSQL.

Для конфигурации среды выполнения использовать следующие параметры:

* SERVER_PORT - порт прослушки WEB сервера;
* DB_HOST - Хост БД;
* DB_PORT - Порт БД;
* DB_NAME - Имя БД;
* DB_USER - Имя пользователя БД;
* DB_PASS - Пароль пользователя БД;
* LOG_LEVEL - Уровень логирования (INFO,DEBUG,WARN,ERROR,TRACE,OFF,FATAL).

Для доступа к Swagger документации использовать URL вида http://host/swagger-ui.html#/
