package com.mires.hackatonipn.objects.user;

import com.mires.hackatonipn.objects.exam.Exam;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
public class User {
    private final UUID uuid;
    private final String name;
    private final String surname;
    private final String email;
    private final String password;
    private List<UUID> exams;
}
