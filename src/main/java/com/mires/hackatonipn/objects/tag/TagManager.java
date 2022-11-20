package com.mires.hackatonipn.objects.tag;

import com.google.common.collect.ImmutableSet;
import com.mires.hackatonipn.HackatonIpnBackendApplication;

import java.util.Map;
import java.util.UUID;

public class TagManager {
    private final Map<UUID, Tag> tagMap;

    public TagManager() {
        this.tagMap = HackatonIpnBackendApplication.getMysqlManager().loadAllTags();
    }

    public Tag findTag(final UUID uuid) {
        return tagMap.get(uuid);
    }

    public Tag getTag(final String name) {
        for (final Tag tag : tagMap.values()) {
            if (tag.getName().equalsIgnoreCase(name)) {
                return tag;
            }
        }
        return null;
    }

    public ImmutableSet<Tag> getTags() {
        return ImmutableSet.copyOf(tagMap.values());
    }
}
