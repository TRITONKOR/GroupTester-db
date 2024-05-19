package com.tritonkor.domain.service.impl;

import com.tritonkor.domain.dto.TagStoreDto;
import com.tritonkor.domain.dto.TagUpdateDto;
import com.tritonkor.domain.exception.ValidationException;
import com.tritonkor.persistence.context.factory.PersistenceContext;
import com.tritonkor.persistence.context.impl.TagContext;
import com.tritonkor.persistence.entity.Tag;
import com.tritonkor.persistence.entity.filter.TagFilterDto;
import com.tritonkor.persistence.exception.EntityNotFoundException;
import com.tritonkor.persistence.repository.contract.TagRepository;
import jakarta.validation.Validator;
import java.util.Set;
import java.util.TreeSet;
import java.util.UUID;
import org.springframework.stereotype.Service;

@Service
public class TagService {

    private final TagContext tagContext;
    private final TagRepository tagRepository;
    private final Validator validator;

    public TagService(PersistenceContext persistenceContext, Validator validator) {
        this.tagContext = persistenceContext.tags;
        this.tagRepository = persistenceContext.tags.repository;
        this.validator = validator;
    }

    public Tag findById(UUID id) {
        return tagContext.repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Не вдалось знайти тег"));
    }

    public Tag findByName(String name) {
        return tagContext.repository.findByName(name)
                .orElseThrow(() -> new EntityNotFoundException("Не вдалось знайти тег"));
    }

    public Set<Tag> findAll() {
        return new TreeSet<>(tagRepository.findAll());
    }

    public Set<Tag> findAll(int offset,
            int limit,
            String sortColumn,
            boolean ascending,
            TagFilterDto tagFilterDto) {
        return new TreeSet<>(tagRepository.findAll(
                offset,
                limit,
                sortColumn,
                ascending,
                tagFilterDto));
    }

    public long count() {
        return tagRepository.count();
    }

    public Tag create(TagStoreDto tagStoreDto) {
        var violations = validator.validate(tagStoreDto);
        if (!violations.isEmpty()) {
            throw ValidationException.create("збереженні тесту", violations);
        }

        Tag tag = Tag.builder()
                .id(null)
                .name(tagStoreDto.name())
                .tests(null)
                .build();

        tagContext.registerNew(tag);
        tagContext.commit();
        return tagContext.getEntity();
    }

    public Tag update(TagUpdateDto tagUpdateDto) {
        var violations = validator.validate(tagUpdateDto);
        if (!violations.isEmpty()) {
            throw ValidationException.create("оновленні тесту", violations);
        }

        Tag oldTag = findById(tagUpdateDto.id());

        Tag tag = Tag.builder()
                .id(tagUpdateDto.id())
                .name(tagUpdateDto.name())
                .tests(null)
                .build();

        tagContext.registerModified(tag);
        tagContext.commit();
        return tagContext.getEntity();
    }

    public boolean delete(UUID id) {
        Tag tag = findById(id);

        return tagContext.repository.delete(tag.getId());
    }
}
