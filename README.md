# java-kanban
Проект, в рамках курса Практикума.

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