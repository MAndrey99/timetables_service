# Описание api
## передаваемые объекты

### Deadline
Используется для передачи информации о дедлайне в обоих направлениях.

Пример при отправке на сервис:  
_{ "creatorId": 7, "groupId": -1, "dateTime": "04-12-2020 23:18:30", "title": "example", "description": "" }_

Пример при получении клиентом:  
_{"creatorId":7,"groupId":-1,"dateTime":"04-12-2020 23:18:30","title":"example","description":"","id":53,"creationDateTime":"04-12-2020 23:17:16"}_

Поля:

обозначение | обязательно | тип | пояснение
---|---|---|---
**creatorId** | да | int | id создавшего дедлайн пользователя
**groupId** | да | int | id группы, в которой создан дедлайн
**dateTime** | да | String | время наступления дедлайна в формате dd-MM-yyyy HH:mm:ss
**title** | да | String | заголовок дедлайна
**description** | нет | String | пояснение дедлайна
**leadTime** | нет | int | предположительное время выполнения задачи в секундах
**id** | только для отправки клиенту | int | уникальный идентификатор дедлайна
**creationDateTime** | только для отправки клиенту | String | время создания дедлайна в формате dd-MM-yyyy HH:mm:ss
**priority** | нет | short | приоритет задачи


### DeadlinesBucket
Используется для передачи клиенту информации о неком количестве дедлайнов.

Пример:  
_{"deadlines":\[{"creatorId":7,"groupId":-1,"dateTime":"04-12-2020 23:18:30","title":"t","description":"","id":53,"creationDateTime":"04-12-2020 23:17:16"},{"creatorId":7,"groupId":-1,"dateTime":"04-12-2020 23:18:30","title":"t","description":"","id":54,"creationDateTime":"04-12-2020 23:17:22"}]}_

Поля:

обозначение | обязательно | тип | пояснение
---|---|---|---
`deadlines` | да | Array | здесь все передаваемые [дедлайны](#deadline)


### DeadlinePatch
Используется для изменения существующих дедлайнов.

Поля:

обозначение | обязательно | тип | пояснение
---|---|---|---
**dateTime** | нет | String | время наступления дедлайна в формате dd-MM-yyyy HH:mm:ss
**title** | нет | String | заголовок дедлайна
**description** | нет | String | пояснение дедлайна
**leadTime** | нет | int | предположительное время выполнения задачи в секундах
**priority** | нет | int | приоритет задачи


### Schedule
Используется для передачи клиенту сформированного расписания.
Подробно о планировании можно почитать [здесь](aboutPlanning.md).

Пример:  
_{"scheduledDeadlines":\[{"creatorId":7,"groupId":17,"dateTime":"10-01-2021 23:57:25","leadTime":1050,"priority":0,"title":"tt","description":null,"id":109,"creationDateTime":"10-01-2021 22:31:00"}],"unscheduledDeadlines":\[{"creatorId":7,"groupId":17,"dateTime":"10-01-2021 23:50:00","leadTime":1050,"priority":0,"title":"tt","description":null,"id":108,"creationDateTime":"10-01-2021 22:04:05"}]}_

Поля:

обозначение | обязательно | тип | пояснение
---|---|---|---
**scheduledDeadlines** | да | Array | здесь [дедлайны](#deadline) со скорректированным временем.
**unscheduledDeadlines** | да | Array | здесь [дедлайны](#deadline), которые невозможно выполнить вовремя.
