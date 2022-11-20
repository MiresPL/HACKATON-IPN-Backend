package com.mires.hackatonipn.objects.tag;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.UUID;

@AllArgsConstructor
@Getter
public class Tag {
    private UUID uuid;
    private String name;
}
