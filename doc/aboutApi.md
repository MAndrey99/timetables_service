# Описание api

### post /deadlines
Создаёт новый дедлайн по переданному в теле запроса json.  
Пример такого json:  
_{ "creatorId": 0, "groupId": 1, "dateTime": "11-11-2020 12:00:00", "title": "tmp", "description": "tmp description" }_


### get /deadlines
Возвращает json со списком дедлайнов.  
Необязательные аргументы: `relevant`, `groupId` и `creatorId` для фильтрации выдачи.  
Пример возвращаемого json:  
_{'deadlines': \[{'creatorId': 0, 'groupId': 1, 'dateTime': '11-11-2020 12:00:00', 'title': 'tmp', 'description': 'tmp description', 'id': 1, 'creationDateTime': '20-11-2020 23:22:26'}, {'creatorId': -1, 'groupId': 2, 'dateTime': '11-11-2020 12:00:00', 'title': 'tmp', 'description': 'tmp2 description', 'id': 2, 'creationDateTime': '20-11-2020 23:23:00'}]}_

`relevant`(boolean default _true_) - указывает требуеся ли из выдачи убирать неактуальные(прошедшие) дедлайны.

В качестве аргументов `groupId` и `creatorId` можно передать значение, которому должно соответствовать данное поле в выдаче.


### delete /deadlines
Удаляет дедлайн по `id`.
