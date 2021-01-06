# Локальный запуск
### с помощью docker
> docker-compose up

### из среды разработки
Первым делом требуется в настройках запуска прописать переменные из `.env` файла.  
У переменной JDBC_DATABASE_URL требуется заменить _db_ на _localhost:5432_.  

Затем поднять базу:
> docker-compose up db

После этих шагов сервис готов к запуску из среды разработки.


# Запуск на heroku
## Настройка окружения heroku
> heroku stack:set container  
> heroku labs:enable runtime-dyno-metadata  
> heroku addons:create heroku-postgresql:hobby-dev

### настройка бд
их можно выполнить как скриптом, так и руками

#### ручная настройка
при выполнении перечисленных команд в качестве дополнительного параметра 
необходимо передать название приложения(`-a appname`)

> heroku config:get DATABASE_URL  

выведет:  
> postgres://username:password@address  

затем:
> heroku config:set JDBC_DATABASE_URL=jdbc:postgresql://address  
> heroku config:set JDBC_DATABASE_USERNAME=username  
> heroku config:set JDBC_DATABASE_PASSWORD=password

#### автоматическая настройка
выполнить скрипт `utils/init_heroku_database.py`  
в качестве параметра необходимо передать название приложения
