package net.gearz.jlibrary.base.activerecord;

import lombok.*;

import java.lang.reflect.Field;

@Data
@RequiredArgsConstructor
@EqualsAndHashCode
@ToString(includeFieldNames = true)
public class BasicAnalyzedField {
    @NonNull
    private final String key;
    @NonNull
    private final Field field;
    private Object value;
}
