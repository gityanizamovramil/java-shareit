package ru.practicum.shareit.item.model;

import lombok.*;
import ru.practicum.shareit.requests.ItemRequest;
import ru.practicum.shareit.user.User;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Entity
@Table(name = "items")
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Item {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name")
    @NotBlank
    private String name;

    @Column(name = "description")
    @NotBlank
    private String description;

    @Column(name = "is_available")
    @NotNull
    private Boolean available;

    @ManyToOne
    @JoinColumn(name = "owner_id")
    private User owner;

    @Transient
    @ManyToOne
    @JoinTable(
            name = "item_request",
            joinColumns = @JoinColumn(name = "item_id"),
            inverseJoinColumns = @JoinColumn(name = "request_id"))
    private ItemRequest request;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Item)) return false;
        return id != null && id.equals(((Item) o).getId());
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
