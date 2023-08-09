package ru.yandex.practicum.filmorate.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;

import java.time.LocalDate;
import java.util.*;

@Data
@Getter
@Setter
@NoArgsConstructor
public class Film {
    private int id;
    private int rate;
    private int duration;
    private String name;
    private String description;
    private LocalDate releaseDate;
    @JsonIgnore
    private Set<Integer> filmLikes = new TreeSet<>();
    private List<Genre> genres = new ArrayList<>();
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

    @Override
    public String toString() {
        return "Film{" +
                "id=" + id +
                ", rate=" + rate +
                ", duration=" + duration +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", releaseDate=" + releaseDate +
                ", filmLikes=" + filmLikes +
                ", genres=" + genres +
                ", mpa=" + mpa +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Film film = (Film) o;
        return id == film.id && rate == film.rate && duration == film.duration &&
                Objects.equals(name, film.name) && Objects.equals(description, film.description) &&
                Objects.equals(releaseDate, film.releaseDate) && Objects.equals(filmLikes, film.filmLikes) &&
                Objects.equals(genres, film.genres) && Objects.equals(mpa, film.mpa);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, rate, duration, name, description, releaseDate, filmLikes, genres, mpa);
    }
}
