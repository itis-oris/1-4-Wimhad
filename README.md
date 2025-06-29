# 🚀 SkillSwap - Платформа обмена навыками и услугами

## 📋 Описание проекта

**SkillSwap** - это веб-приложение на Spring Boot, которое позволяет пользователям обмениваться навыками и услугами. Платформа предоставляет возможность создавать предложения услуг, искать исполнителей, управлять профилями и взаимодействовать с другими пользователями.

## ✨ Основные возможности

### 🌐 Веб-интерфейс
- **Регистрация и авторизация** пользователей
- **Создание и редактирование предложений** услуг
- **Поиск и фильтрация** предложений по различным критериям
- **Управление профилем** пользователя
- **Система отзывов и рейтингов**

### 🔍 Поиск и фильтрация
- Поиск по названию и описанию
- Фильтрация по навыкам
- Фильтрация по ценовому диапазону
- Фильтрация по валюте (₽, $, €)
- Комбинированные фильтры

### 💱 Мультивалютность
- Поддержка валют: RUB (₽), USD ($), EUR (€)
- Автоматическое отображение курсов валют
- Конвертация валют в реальном времени
- Интеграция с внешним API курсов валют

### 🔄 Управление предложениями
- Создание новых предложений
- Редактирование существующих предложений
- Активация/деактивация предложений
- Удаление предложений
- Просмотр детальной информации

## 🛠️ Технологический стек

### Backend
- **Java 17** - основной язык программирования
- **Spring Boot 3.2.5** - фреймворк для создания веб-приложений
- **Spring MVC** - архитектура веб-приложений
- **Spring Data JPA** - работа с базой данных
- **Spring Security** - аутентификация и авторизация
- **Hibernate** - ORM для работы с базой данных
- **PostgreSQL** - основная база данных

### Frontend
- **Thymeleaf** - серверный шаблонизатор
- **Thymeleaf Spring Security Extras** - интеграция с Spring Security
- **Bootstrap 5** - CSS фреймворк для адаптивного дизайна
- **Bootstrap Icons** - иконки
- **JavaScript** - клиентская логика
- **AJAX** - асинхронные запросы

### API и интеграции
- **REST API** - для взаимодействия с внешними системами
- **Swagger/OpenAPI 3** - документация API
- **External Currency API** - получение курсов валют
- **HTTP Client** - тестирование API

### Инструменты разработки
- **Maven** - управление зависимостями
- **Lombok** - уменьшение boilerplate кода
- **JSON** - обработка JSON данных

## 📁 Структура проекта

```
ProjectSemester/
├── pom.xml                    # Maven конфигурация
├── README.md                  # Документация проекта
├── setup_database.sql         # Скрипт настройки БД
└── src/
    ├── main/
    │   ├── java/com/skillswap/
    │   │   ├── config/           # Конфигурации Spring
    │   │   │   └── SecurityConfig.java
    │   │   ├── controller/       # Контроллеры
    │   │   │   ├── api/         # REST API контроллеры
    │   │   │   │   └── OfferRestController.java
    │   │   │   ├── OfferController.java
    │   │   │   └── UserController.java
    │   │   ├── entity/          # JPA сущности
    │   │   │   ├── Offer.java
    │   │   │   ├── Request.java
    │   │   │   ├── Review.java
    │   │   │   ├── Skill.java
    │   │   │   └── User.java
    │   │   ├── exception/       # Обработка исключений
    │   │   │   └── GlobalExceptionHandler.java
    │   │   ├── repository/      # Репозитории данных
    │   │   │   ├── OfferRepository.java
    │   │   │   ├── RequestRepository.java
    │   │   │   ├── ReviewRepository.java
    │   │   │   ├── SkillRepository.java
    │   │   │   └── UserRepository.java
    │   │   ├── service/         # Бизнес-логика
    │   │   │   ├── CurrencyService.java
    │   │   │   ├── OfferService.java
    │   │   │   ├── RequestService.java
    │   │   │   ├── ReviewService.java
    │   │   │   ├── SkillService.java
    │   │   │   └── UserService.java
    │   │   └── SkillSwapApplication.java
    │   └── resources/
    │       ├── application.properties  # Конфигурация приложения
    │       └── templates/       # Thymeleaf шаблоны
    │           ├── fragments/   # Переиспользуемые фрагменты
    │           │   └── header.html
    │           ├── offers/      # Шаблоны для предложений
    │           │   ├── create.html
    │           │   ├── edit.html
    │           │   ├── list.html
    │           │   └── view.html
    │           ├── 403.html     # Страницы ошибок
    │           ├── 404.html
    │           ├── error.html
    │           ├── home.html    # Основные страницы
    │           ├── login.html
    │           ├── profile.html
    │           └── register.html
    └── test/
        └── http/               # HTTP тесты API
            └── offer-api.http
```

## 🗄️ Модель данных

### Основные сущности

#### User (Пользователь)
- `id` - уникальный идентификатор
- `username` - имя пользователя
- `email` - электронная почта
- `passwordHash` - хеш пароля
- `fullName` - полное имя
- `role` - роль (USER/ADMIN)
- `skills` - навыки пользователя (Many-to-Many)

#### Skill (Навык)
- `id` - уникальный идентификатор
- `name` - название навыка
- `description` - описание
- `category` - категория

#### Offer (Предложение)
- `id` - уникальный идентификатор
- `user` - автор предложения
- `skill` - связанный навык
- `title` - заголовок
- `description` - описание
- `price` - цена
- `currency` - валюта
- `isActive` - статус активности
- `createdAt` - дата создания

#### Request (Заявка)
- `id` - уникальный идентификатор
- `user` - пользователь
- `skill` - требуемый навык
- `title` - заголовок
- `description` - описание
- `budgetMin/Max` - бюджет
- `status` - статус заявки

#### Review (Отзыв)
- `id` - уникальный идентификатор
- `reviewer` - автор отзыва
- `reviewed` - получатель отзыва
- `rating` - оценка (1-5)
- `comment` - комментарий

## 🚀 Установка и запуск

### Предварительные требования
- Java 17 или выше
- PostgreSQL 12 или выше
- Maven 3.6 или выше

### Шаг 1: Клонирование репозитория
```bash
git clone https://github.com/itis-oris/1-4-Wimhad.git
cd ProjectSemester
```

### Шаг 2: Настройка базы данных
1. Создайте базу данных PostgreSQL:
```sql
CREATE DATABASE skillswap;
```

2. Выполните скрипт настройки:
```bash
psql -U postgres -d skillswap -f setup_database.sql
```

### Шаг 3: Настройка конфигурации
Отредактируйте `src/main/resources/application.properties`:
```properties
# Укажите ваши данные для подключения к БД
spring.datasource.username=your_username
spring.datasource.password=your_password
```

### Шаг 4: Запуск приложения
```bash
# Через Maven
mvn spring-boot:run

# Или через IDE
# Запустите SkillSwapApplication.java
```

### Шаг 5: Доступ к приложению
- **Веб-интерфейс**: http://localhost:8081
- **Swagger UI**: http://localhost:8081/swagger-ui.html
- **API документация**: http://localhost:8081/api-docs

## 🔐 Аутентификация

### Тестовые пользователи
После выполнения скрипта БД доступны следующие пользователи:
- **admin** / password123 (Администратор)
- **alice_dev** / password123 (Разработчик)
- **bob_teacher** / password123 (Преподаватель)
- **maria_artist** / password123 (Фотограф)

## 📡 REST API

### Основные эндпоинты

#### Предложения (Offers)
- `GET /api/v1/offers` - получить все предложения
- `GET /api/v1/offers/{id}` - получить предложение по ID
- `POST /api/v1/offers` - создать новое предложение
- `PUT /api/v1/offers/{id}` - обновить предложение
- `DELETE /api/v1/offers/{id}` - удалить предложение
- `POST /api/v1/offers/{id}/toggle-status` - изменить статус

#### Примеры запросов

**Получить все предложения:**
```bash
curl -X GET http://localhost:8081/api/v1/offers \
  -H "Accept: application/json"
```

**Создать предложение:**
```bash
curl -X POST http://localhost:8081/api/v1/offers \
  -H "Content-Type: application/json" \
  -d '{
    "title": "Уроки Java",
    "description": "Обучение программированию на Java",
    "price": 1500.00,
    "currency": "RUB"
  }'
```

## 🧪 Тестирование

### HTTP тесты
Используйте файл `src/test/http/offer-api.http` для тестирования API в IntelliJ IDEA.

### Swagger UI
Откройте http://localhost:8081/swagger-ui.html для интерактивного тестирования API.

## 🌟 Особенности реализации

### Безопасность
- Spring Security с аутентификацией по сессиям
- Хеширование паролей с BCrypt
- Авторизация на основе ролей
- CSRF защита
- Thymeleaf Spring Security Extras для динамической навигации

### Производительность
- Индексы в базе данных для быстрого поиска
- Ленивая загрузка связей (Lazy Loading)
- Кэширование Thymeleaf отключено для разработки

### Пользовательский интерфейс
- Адаптивный дизайн с Bootstrap 5
- Динамическая навигация в зависимости от статуса авторизации
- AJAX для обновления данных без перезагрузки страницы
- Фрагменты Thymeleaf для переиспользования кода
- Кастомные страницы ошибок (403, 404, error)

### Мультивалютность
- Интеграция с API Центрального Банка России
- Автоматическое обновление курсов валют
- Отображение конвертации в реальном времени

## 🔧 Конфигурация

### Основные настройки
```properties
# Сервер
server.port=8081

# База данных
spring.jpa.hibernate.ddl-auto=none
spring.jpa.show-sql=true

# Thymeleaf
spring.thymeleaf.cache=false

# Swagger
springdoc.api-docs.path=/api-docs
springdoc.swagger-ui.path=/swagger-ui.html

# Локализация
spring.mvc.locale=ru_RU
spring.mvc.locale-resolver=fixed

# Обработка ошибок
spring.mvc.throw-exception-if-no-handler-found=true
spring.web.resources.add-mappings=false
```

## 📝 Логирование

Настройки логирования в `application.properties`:
```properties
logging.level.org.springframework=INFO
logging.level.com.skillswap=DEBUG
```

## 🐛 Обработка ошибок

### Кастомные страницы ошибок
- `404.html` - страница не найдена
- `403.html` - доступ запрещен
- `error.html` - общая ошибка

### GlobalExceptionHandler
Централизованная обработка исключений с поддержкой:
- REST API ошибок (JSON)
- Веб-ошибок (HTML страницы)

## 📊 Мониторинг

### Swagger/OpenAPI
- Автоматическая генерация документации API
- Интерактивное тестирование эндпоинтов
- Описание схем данных

## 🚀 Развертывание

### Локальная разработка
1. Клонируйте репозиторий
2. Настройте базу данных
3. Запустите через IDE или Maven

### Продакшн
1. Соберите JAR файл: `mvn clean package`
2. Запустите: `java -jar target/skillswap-1.0.0.jar`
3. Настройте переменные окружения для БД

## 🤝 Вклад в проект

1. Форкните репозиторий
2. Создайте ветку для новой функции
3. Внесите изменения
4. Создайте Pull Request

## 📄 Лицензия

Этот проект создан в образовательных целях.

## 👨‍💻 Автор

**Виссам** - студент, разработчик проекта SkillSwap

**GitHub**: [https://github.com/itis-oris/1-4-Wimhad](https://github.com/itis-oris/1-4-Wimhad)

---

**SkillSwap** - современная платформа для обмена навыками, построенная на Spring Boot с использованием лучших практик разработки веб-приложений. 