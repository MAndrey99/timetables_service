# timetables_service

пока ни чего не умеет, но это только начало...

### Настройки heroku
> heroku stack:set container  
> heroku labs:enable runtime-dyno-metadata  
> heroku addons:create heroku-postgresql:hobby-dev

#### настройки бд
> heroku config:get DATABASE_URL  

выведет:  
> postgres://username:password@address  

звтем:
> heroku config:set JDBC_DATABASE_URL=jdbc:postgresql://address  
> heroku config:set JDBC_DATABASE_USERNAME=username  
> heroku config:set JDBC_DATABASE_PASSWORD=password  
