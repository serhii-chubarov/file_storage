package net.proselyte.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.FieldDefaults;

import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "files")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@ToString
public class File {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Integer id;

    @ToString.Include
    @EqualsAndHashCode.Include
    String name;

    @Column(name = "file_path")
    @EqualsAndHashCode.Include
    String filePath;

    @OneToMany(mappedBy = "file")
    @ToString.Exclude
    Set<Event> events = new HashSet<>();

    public File(String name, String filePath) {
        this.name = name;
        this.filePath = filePath;
    }

    public void addEvent(Event event) {
        events.add(event);
        event.setFile(this);
    }

    public void removeEvent(Event event) {
        events.remove(event);
        event.setFile(null);
    }
}
