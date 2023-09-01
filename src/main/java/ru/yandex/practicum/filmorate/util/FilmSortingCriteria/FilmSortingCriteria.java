package ru.yandex.practicum.filmorate.util.FilmSortingCriteria;

public enum FilmSortingCriteria {
    YEAR("ORDER BY EXTRACT(YEAR FROM f.release_date), f.id, id_genre"),
    LIKES("ORDER BY rate, f.id, id_genre");

    private final String sqlPart;

    FilmSortingCriteria(String sqlPart) {
        this.sqlPart = sqlPart;
    }

    public String getSqlPart() {
        return sqlPart;
    }
}
