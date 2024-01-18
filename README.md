# java-filmorate
Схема таблиц базы данных
![Схема базы данных](./resources/dataBase.svg)

## Приложение представляет из себя
##### Filmorate - социальная сеть, в которой возможно обмениваться мнениями о фильмах.
## В ходе проекта реализованы следующие функциональности

### Добавление режиссеров в фильмы
#### API:
- GET /films/director/{directorId}?sortBy=[year,likes] — Возвращает список фильмов режиссера отсортированных по количеству лайков или году выпуска.
- GET /directors — Список всех режиссёров.
- GET /directors/{id} — Получение режиссёра по id.
- POST /directors — Создание режиссёра.
- PUT /directors — Изменение режиссёра
- DELETE /directors/{id} — Удаление режиссёра

### Поиск фильмов по названию и режиссерам
#### API:
- GET /films/search?query=[query]&by=[director,title] — Возвращает список фильмов, отсортированных по популярности.

### Добавление отзывов
#### API:
- POST /reviews — Добавление нового отзыва.
- PUT /reviews — Редактирование уже имеющегося отзыва.
- DELETE /reviews/{id} — Удаление уже имеющегося отзыва.
- GET /reviews/{id} — Получение отзыва по идентификатору.
- GET /reviews?filmId={filmId}&count={count} — Получение всех отзывов по идентификатору фильма, если фильм не указан то все. Если кол-во не указано то 10.
- PUT /reviews/{id}/like/{userId} — пользователь ставит лайк отзыву.
- PUT /reviews/{id}/dislike/{userId} — пользователь ставит дизлайк отзыву.
- DELETE /reviews/{id}/like/{userId} — с пользователь удаляет лайк/дизлайк отзыву.
- DELETE /reviews/{id}/dislike/{userId} — пользователь удаляет дизлайк отзыву.

### Удаление пользователей и фильмов
#### API:
- DELETE /users/{userId} — Удаляет пользователя по идентификатору.
- DELETE /films/{filmId} — Удаляет фильм по идентификатору.

### Популярные фильмы по кол-ву лайков
#### API:
- GET /films/popular?count={limit}&genreId={genreId}&year={year} — Возвращает список самых популярных фильмов указанного жанра за нужный год.

### Рекомендации фильмов для пользавотелей
#### API:
- GET /users/{id}/recommendations — Возвращает рекомендации по фильмам для просмотра.

### Общие фильмы пользователя и его друга
#### API:
- GET /films/common?userId={userId}&friendId={friendId} — Возвращает список фильмов, отсортированных по популярности.

### Лента событий, позваляющая просматривать последние события на платформе
#### API:
- GET /users/{id}/feed — Возвращает ленту событий пользователя.