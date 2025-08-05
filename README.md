
# Конфигурации
Вся конфигурация находится внутри файла application.yml. Файла application.properties не используется.
Для тестовой демонстрации все ключи (AES, секретный код для JWT, админский JWT) находятся в файле конфигурации.

В реальных проектах все ключи хранились бы в защищенных хранилищах.


# swagger http://localhost:8081/swagger-ui/index.html


# Шифрование
Для шифрования использовалась алгоритм AES. Он наиболее подходит для быстрого простого шифрования. RSA был бы избыточен.
Поскольку ключи никуда не передаются а хранятся на сервере - риск перехвата отсутствует.
Все номера карт защищены от администраторов на уровне БД и пользователя, благодаря шифрованию согласно ТЗ.


# База данных
Используется контейнер PostgreSQL. Запуск по docker-compose. Liquibase настроен.


# Тестирование
Админ jwt ключ, действующий 70 дней с 05.08:
eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiJVc2VyIGRldGFpbHMiLCJ1c2VybmFtZSI6IkFkbWluIiwicGFzc3dvcmQiOiJBZG1pbiIsImlkIjoxNCwiUk9MRSI6IkFETUlOIiwiaWF0IjoxNzU0Mzg3ODA3LCJpc3MiOiJUT0RPIiwiZXhwIjoxNzYwMzg3ODA3fQ.GmRqht3E2kfooHYG7dOwi5vMkEmvzGqJygLjMiQPQsc

Создавать аккаунт пользователя может только администратор. Если БД пустая - то единственный способ тестирования это применения этого ключа для создания администраторов и пользователей.


# staging
Дополнительно для тестирования есть staging (vps-сервис) по адресу: ovz1.j71456524.wmekm.vps.myjino.ru:49228

Пример запроса: ovz1.j71456524.wmekm.vps.myjino.ru:49228/user/getAllUsers

Header: Authorization:Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiJVc2VyIGRldGFpbHMiLCJ1c2VybmFtZSI6IkFkbWluIiwicGFzc3dvcmQiOiJBZG1pbiIsImlkIjoxNCwiUk9MRSI6IkFETUlOIiwiaWF0IjoxNzU0Mzg3ODA3LCJpc3MiOiJUT0RPIiwiZXhwIjoxNzYwMzg3ODA3fQ.GmRqht3E2kfooHYG7dOwi5vMkEmvzGqJygLjMiQPQsc


# Архитектурные комментарии
Поскольку это банковская система в нынешней архитектуре действуют такие подходы:
- Admins != Users. Администраторы не могут иметь карты, их роль - администрировать карты и пользователей.
- Создание аккаунта/карты производится администраторами по письменной заявке в банке. Иными словами пользователь не может создать собственный аккаунт или карту без подтверждения личности (которой в тз
  не требовалось) или более сложной системы, вроде номеров телефона или почты. В реальном же проекте я бы уточнил эти детали у тимлида или иных старших разработчиков.


# Доп.комментарий.
В реальных же проектах я бы использовал микросервисную архитектуру (Spring Cloud). Также я бы создал новую роль - HIGH, которой могут пользоваться только сами сервисы, например для API между друг другом.
