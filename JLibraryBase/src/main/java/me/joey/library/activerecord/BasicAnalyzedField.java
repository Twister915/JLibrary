package me.joey.library.activerecord;

import lombok.*;

import java.lang.reflect.Field;

@Data
@RequiredArgsConstructor
@EqualsAndHashCode
@ToString(includeFieldNames = true)
public class BasicAnalyzedField {
    @NonNull private final String key;
    private Object value;
    @NonNull private final Field field;
}
