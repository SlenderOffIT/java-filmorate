package ru.yandex.practicum.filmorate.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.Comparator;
import java.util.Set;
import java.util.TreeSet;

@Data
@NoArgsConstructor
public class Film {
    private final Set<Genre> genres = new TreeSet<>(Comparator.comparingInt(Genre::getId));//заменил,
    private final Set<Director> directors = new TreeSet<>(Comparator.comparingInt(Director::getId));
    private int id;
    private int rate;
    private int duration;
    private String name;
    private String description;
    private LocalDate releaseDate;
    @JsonIgnore
    private Set<Integer> filmLikes = new TreeSet<>();
    // в связи с изменением маппинга.
    private RatingMpa mpa = new RatingMpa();

    public Film(String name, String description, LocalDate releaseDate, int duration) {
        this.name = name;
        this.description = description;
        this.releaseDate = releaseDate;
        this.duration = duration;
    }

    public Film(String name, String description, LocalDate releaseDate, int duration, RatingMpa mpa) {
        this.name = name;
        this.description = description;
        this.releaseDate = releaseDate;
        this.duration = duration;
        this.mpa = mpa;
    }

    public void setMpa(RatingMpa mpa) {
        this.mpa = mpa;
    }

    public Set<Integer> getFilmLikes() {
        if (filmLikes == null) {
            return new TreeSet<>();
        }
        return filmLikes;
    }

    public void incrementRating() {
        rate++;
    }

    public void decrementRating() {
        rate--;
    }
}
