# Локальный запуск
### с помощью docker
> docker-compose up

### из среды разработки
Первым делом требуется в настройках запуска прописать переменные из `.env` файла.  
Затем поднять базу:
> docker-compose up db

После этих шагов сервис готов к запуску из среды разработки.


# Запуск на heroku
### Настройки heroku
> heroku stack:set container  
> heroku labs:enable runtime-dyno-metadata  
> heroku addons:create heroku-postgresql:hobby-dev

### настройки бд на heroku
> heroku config:get DATABASE_URL  

выведет:  
> postgres://username:password@address  

звтем:
> heroku config:set JDBC_DATABASE_URL=jdbc:postgresql://address  
> heroku config:set JDBC_DATABASE_USERNAME=username  
> heroku config:set JDBC_DATABASE_PASSWORD=password  
