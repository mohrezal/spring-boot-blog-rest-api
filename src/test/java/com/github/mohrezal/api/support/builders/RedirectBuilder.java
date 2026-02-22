package com.github.mohrezal.api.support.builders;

import com.github.mohrezal.api.domains.redirects.enums.RedirectTargetType;
import com.github.mohrezal.api.domains.redirects.models.Redirect;
import java.util.UUID;

public class RedirectBuilder {

    private UUID id;
    private String code = "abcd";
    private RedirectTargetType targetType = RedirectTargetType.POST;
    private UUID targetId = UUID.randomUUID();

    public static RedirectBuilder aRedirect() {
        return new RedirectBuilder();
    }

    public RedirectBuilder withId(UUID id) {
        this.id = id;
        return this;
    }

    public RedirectBuilder withCode(String code) {
        this.code = code;
        return this;
    }

    public RedirectBuilder withTargetType(RedirectTargetType targetType) {
        this.targetType = targetType;
        return this;
    }

    public RedirectBuilder withTargetId(UUID targetId) {
        this.targetId = targetId;
        return this;
    }

    public Redirect build() {
        Redirect redirect =
                Redirect.builder().code(code).targetType(targetType).targetId(targetId).build();
        if (id != null) {
            redirect.setId(id);
        }
        return redirect;
    }
}
