# Описание api

### post /deadlines
Создаёт новый дедлайн по переданному в теле запроса json.  
Пример такого json:  
{ "creatorId": 0, "groupId": -1, "dateTime": "11-11-2020 12:00", "title": "tmp", "description": "tmp description" }  
Указывать `groupId` не обязательно, по умолчанию null.
