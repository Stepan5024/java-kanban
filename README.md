# java-kanban
![preview.png](..%2Fpreview.png)
Проект, в рамках курса Практикума.

# Бизнес смысл программы
1. Создание задач To-Do
2. Создание группы задач (Эпиков) Epic
3. Создание подзадач в эпике Subtask

Каждая задача имеет состояние (NEW, IN_PROGRESS, DONE).

# Модель данных

## Родительский класс Task
### Поля:
* title
* description
* id
* status

## Дочерний класс Epic наследуется от Task
### Поля:
* список subtasks

## Дочерний класс Subtask наследуется от Task
### Поля:
* epicId

# Управление данными

## класс TaskManager
### Поля:
* taskId

# Функциональность программы TaskManager
* Вся канбан доска храниться в списке ```ArrayList<Object> listOfAllTasks ```
* Поле ```taskId``` служит увеличивающимся номером при создании любого из объектов классов Task, Subtask,Epic
### Методы
1. ``Task createNewTask(String title, String description, String status)`` - ТЗ пункт 2. D Создание Задачи
2. ``Epic createNewEpic(String title, String description)`` - ТЗ пункт 2. D Создание Эпика
3. ``Subtask createNewSubtask(String title, String description, String status, long epicId)`` - ТЗ пункт 2. D Создание Подзадачи
4. ``void actualizationEpicStatus(Subtask subtask)`` - Обновляем эпик с id на один из статусов DONE, IN_PROGRESS, DONE
5. ``Object getEntityById(long id)`` - ТЗ 2.C Получение по идентификатору задачи, эписка, подзадачи
6. ``int removeEntityFromKanban(Class<?> aClass)`` - ТЗ пункт 2.B Удаление всех эпиков, подзадач, тасков. Возвращаемое значение - кол-во удаленных элементов
7. ``ArrayList<Object> getAllEntitiesByClass(Class<?> aClass)`` - ТЗ 2.A Получение списка всех задач, подзадач, эпиков
8. ``int removeTaskById(long taskId)`` - ТЗ пункт 2.F Удаление по идентификатору. Возвращаемое значение - кол-во удаленных элементов
9. ``ArrayList<Subtask> getListOfSubtaskByEpicId(long epicId)`` - ТЗ пункт 3.А Получение списка всех подзадач определённого эпика.
10. ``Object updateTask(Object newTask, long taskId)`` - ТЗ 2. E Обновление объекта новой версией.
