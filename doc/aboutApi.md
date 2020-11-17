# Описание api

### post /deadlines
Создаёт новый дедлайн по переданному в теле запроса json.  
Пример такого json:  
{ "creatorId": 0, "groupId": 1, "dateTime": "11-11-2020 12:00:00", "title": "tmp", "description": "tmp description" }  

### get /deadlines
Возвращает json со списком дедлайнов.  
Необязательные аргументы: `groupId` и `creatorId` для фильтрации выдачи. 
