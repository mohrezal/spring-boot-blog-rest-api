package com.github.mohrezal.api.domains.redirects.controllers;

import com.github.mohrezal.api.config.Routes;
import com.github.mohrezal.api.domains.redirects.queries.GetByCodeQuery;
import com.github.mohrezal.api.domains.redirects.queries.params.GetByCodeQueryParams;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(Routes.Redirect.BASE)
@RequiredArgsConstructor
@Tag(name = "Redirect")
public class RedirectController {

    private final GetByCodeQuery getByCodeQuery;

    @GetMapping(Routes.Redirect.BY_CODE)
    public ResponseEntity<Void> getByCode(@PathVariable String code) {
        var params = new GetByCodeQueryParams(code);
        var redirectUrl = getByCodeQuery.execute(params);
        return ResponseEntity.status(HttpStatus.FOUND)
                .header(HttpHeaders.LOCATION, redirectUrl)
                .build();
    }
}
