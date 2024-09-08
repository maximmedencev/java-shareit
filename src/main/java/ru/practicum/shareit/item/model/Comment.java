package ru.practicum.shareit.item;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.User;

@Getter
@Setter
@ToString
@Entity
@Table(name = "items", schema = "public")
public class Comment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @OneToOne(fetch = FetchType.LAZY)
    @ToString.Exclude
    @JoinColumn(name = "item_id")
    private Item item;
    @OneToOne(fetch = FetchType.LAZY)
    @ToString.Exclude
    @JoinColumn(name = "author_id")
    private User author;
    @NotNull
    @NotBlank
    @Column(name = "text")
    private String text;
}
