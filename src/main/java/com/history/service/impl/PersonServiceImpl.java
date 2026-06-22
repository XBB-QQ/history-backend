package com.history.service.impl;

import com.history.dto.PersonDTO;
import com.history.entity.PersonEntity;
import com.history.repository.PersonRepository;
import com.history.service.PersonService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PersonServiceImpl implements PersonService {

    private final PersonRepository personRepository;

    @Override
    public Page<PersonDTO> findAll(Pageable pageable) {
        return personRepository.findAll(pageable).map(this::toDTO);
    }

    @Override
    public PersonDTO findById(Long id) {
        return personRepository.findById(id)
                .map(this::toDTO)
                .orElseThrow(() -> new com.history.exception.ResourceNotFoundException("人物", id));
    }

    @Override
    public PersonDTO findByUid(String uid) {
        return personRepository.findByUid(uid)
                .map(this::toDTO)
                .orElseThrow(() -> new com.history.exception.ResourceNotFoundException("人物", uid));
    }

    @Override
    public Page<PersonDTO> search(String keyword, Pageable pageable) {
        return personRepository.search(keyword, pageable).map(this::toDTO);
    }

    @Override
    public Page<PersonDTO> findByGender(String gender, Pageable pageable) {
        return personRepository.findByGender(gender, pageable).map(this::toDTO);
    }

    @Override
    public Page<PersonDTO> findByDynasty(String dynastyName, Pageable pageable) {
        return personRepository.findByDynasty_Name(dynastyName, pageable).map(this::toDTO);
    }

    @Override
    public Page<PersonDTO> findByRole(String role, Pageable pageable) {
        return personRepository.findByRolesContaining(role, pageable).map(this::toDTO);
    }

    private PersonDTO toDTO(PersonEntity entity) {
        return PersonDTO.builder()
                .id(entity.getId())
                .uid(entity.getUid())
                .name(entity.getName())
                .courtesyName(entity.getCourtesyName())
                .dynastyName(entity.getDynasty() != null ? entity.getDynasty().getName() : null)
                .years(entity.getYears())
                .yearsDisplay(entity.getYearsDisplay())
                .gender(entity.getGender())
                .roles(entity.getRoles())
                .quote(entity.getQuote())
                .bio(entity.getBio())
                .tags(entity.getTags())
                .relatedEvents(entity.getRelatedEvents())
                .relatedPersons(entity.getRelatedPersons())
                .build();
    }

    @Override
    @Transactional
    public PersonDTO createOrUpdate(PersonDTO dto) {
        PersonEntity entity;
        if (dto.getId() != null) {
            entity = personRepository.findById(dto.getId())
                .orElseThrow(() -> new com.history.exception.ResourceNotFoundException("人物", dto.getId()));
        } else {
            entity = new PersonEntity();
        }
        entity.setUid(dto.getUid());
        entity.setName(dto.getName());
        entity.setCourtesyName(dto.getCourtesyName());
        entity.setYearsDisplay(dto.getYearsDisplay());
        entity.setGender(dto.getGender());
        entity.setRoles(dto.getRoles() != null ? dto.getRoles() : java.util.List.of());
        entity.setQuote(dto.getQuote());
        entity.setBio(dto.getBio());
        entity.setTags(dto.getTags() != null ? dto.getTags() : java.util.List.of());
        entity.setRelatedEvents(dto.getRelatedEvents() != null ? dto.getRelatedEvents() : java.util.List.of());
        entity.setRelatedPersons(dto.getRelatedPersons() != null ? dto.getRelatedPersons() : java.util.List.of());
        return toDTO(personRepository.save(entity));
    }

    @Override
    @Transactional
    public void deleteById(Long id) {
        personRepository.deleteById(id);
    }

    @Override
    public java.util.List<PersonDTO> findAllOrdered() {
        return personRepository.findAll().stream()
                .map(this::toDTO)
                .toList();
    }
}
