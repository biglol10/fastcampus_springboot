package com.fastcampus.programming.dmaker.service;

import com.fastcampus.programming.dmaker.code.StatusCode;
import com.fastcampus.programming.dmaker.dto.*;
import com.fastcampus.programming.dmaker.entity.Developer;
import com.fastcampus.programming.dmaker.entity.RetiredDeveloper;
import com.fastcampus.programming.dmaker.exception.DMakerErrorCode;
import com.fastcampus.programming.dmaker.exception.DMakerException;
import com.fastcampus.programming.dmaker.exception.DMakerException2;
import com.fastcampus.programming.dmaker.repository.DeveloperRepository;
import com.fastcampus.programming.dmaker.repository.RetiredDeveloperRepository;
import com.fastcampus.programming.dmaker.type.DeveloperLevel;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static com.fastcampus.programming.dmaker.exception.DMakerErrorCode.*;

/**
 * @author Snow
 */
@Slf4j
@Service
@RequiredArgsConstructor // service단에선 이걸 추가해주는게 좋음. final인건 무조건 있어야 함
public class DMakerService {
    // 그래야 자동으로 DeveloperRepository를 여기에 인젝션 해줌
    // 예전에는 @Autowired나 @Injection을 사용함.
    // 문제점은 서비스가 annotation들의 종속적으로 만들어져 있기 때문에 이 서비스를 단독으로 테스트하고 싶어도 어려웠음

    // 그래서 만들어진게 이거
    // 이것도 코드량이 많아져 복잡해짐... 그래서 @RequiredArgsConstructor private final ...
//    public DMakerService(DeveloperRepository developerRepository){
//        this.developerRepository = developerRepository;
//    }

    private final DeveloperRepository developerRepository;
    private final RetiredDeveloperRepository retiredDeveloperRepository;

    @Transactional
    public CreateDeveloper.Response createDeveloper(
            CreateDeveloper.Request request
    ) {
        validateCreateDeveloperRequest(request);

        Developer developerFromRequest = createDeveloperFromRequest(request);

        // business logic start
        return CreateDeveloper.Response.fromEntity(
                developerRepository.save(developerFromRequest)
        );
    }

    private Developer createDeveloperFromRequest(CreateDeveloper.Request request) {
        return Developer.builder()
                .developerLevel(request.getDeveloperLevel())
                .developerSkillType(request.getDeveloperSkillType())
                .experienceYears(request.getExperienceYears())
                .memberId(request.getMemberId())
                .statusCode(StatusCode.EMPLOYED)
                .name(request.getName())
                .age(request.getAge())
                .build();
    }

    private void validateCreateDeveloperRequest(
            @NonNull CreateDeveloper.Request request
    ) {
        // business validation
        request.getDeveloperLevel().validateExperienceYears(
                request.getExperienceYears()
        );

        developerRepository.findByMemberId(request.getMemberId())
                .ifPresent((developer -> {
                    throw new DMakerException(DUPLICATED_MEMBER_ID);
                }));
    }

    @Transactional(readOnly = true)
    public List<DeveloperDto> getAllEmployedDevelopers() {
        return developerRepository.findDevelopersByStatusCodeEquals(
                        StatusCode.EMPLOYED
                ).stream().map(DeveloperDto::fromEntity)
                .collect(Collectors.toList());
    }

    public List<DeveloperDto> getAllDevelopers2() {
        return developerRepository.findAll()
                .stream().map(DeveloperDto::fromEntity)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public DeveloperDetailDto getDeveloperDetail(String memberId) {
//        return DeveloperDetailDto.fromEntity(getDeveloperByMemberId(memberId));
        return developerRepository.findByMemberId(memberId)
                .map(DeveloperDetailDto::fromEntity)
                .orElseThrow(() -> new DMakerException(NO_DEVELOPER));
    }

    private Developer getDeveloperByMemberId(String memberId) {
        return developerRepository.findByMemberId(memberId)
                .orElseThrow(() -> new DMakerException(NO_DEVELOPER));
    }

    @Transactional
    public DeveloperDetailDto editDeveloper(
            String memberId, EditDeveloper.Request request
    ) {
        request.getDeveloperLevel().validateExperienceYears(
                request.getExperienceYears()
        );

        return DeveloperDetailDto.fromEntity(
                getUpdatedDeveloperFromRequest(
                        request, getDeveloperByMemberId(memberId)
                )
        );
    }

    @Transactional
    public DeveloperDetailDto editDeveloper2(String memberId, EditDeveloper.Request request) {
        request.getDeveloperLevel().validateExperienceYears(request.getExperienceYears());

        return DeveloperDetailDto.fromEntity(getUpdatedDeveloperFromRequest(request, getDeveloperByMemberId(memberId)));
    }

    private Developer getUpdatedDeveloperFromRequest(
            EditDeveloper.Request request, Developer developer
    ) {
        developer.setDeveloperLevel(request.getDeveloperLevel());
        developer.setDeveloperSkillType(request.getDeveloperSkillType());
        developer.setExperienceYears(request.getExperienceYears());

        return developer;
    }

    @Transactional
    public DeveloperDetailDto deleteDeveloper(String memberId) {
        // 1. EMPLOYED -> RETIRED
        Developer developer = developerRepository.findByMemberId(memberId)
                .orElseThrow(() -> new DMakerException(NO_DEVELOPER));
        developer.setStatusCode(StatusCode.RETIRED);

        // 2. save into RetiredDeveloper
        RetiredDeveloper retiredDeveloper = RetiredDeveloper.builder()
                .memberId(memberId)
                .name(developer.getName())
                .build();
        retiredDeveloperRepository.save(retiredDeveloper);
        return DeveloperDetailDto.fromEntity(developer);
    }

    @Transactional
    public CreateDeveloper2.Response createDeveloper2(CreateDeveloper2.Request request) {
        validateCreateDeveloperRequest2(request);

        // business login start
        Developer developer = Developer.builder()
                .developerLevel(request.getDeveloperLevel())
                .developerSkillType(request.getDeveloperSkillType())
                .experienceYears(request.getExperienceYears())
                .memberId(request.getMemberId())
                .name(request.getName())
                .age(request.getAge())
                .build();

        developerRepository.save(developer);

        return CreateDeveloper2.Response.fromEntity(developer);
    }


    private void validateCreateDeveloperRequest2(CreateDeveloper2.Request request) {
        // business validation
        if (request.getDeveloperLevel() == DeveloperLevel.SENIOR && request.getExperienceYears() < 10) {
//            throw new RuntimeException("SENIOR need 10 years experience");
            throw new DMakerException2(LEVEL_EXPERIENCE_YEARS_NOT_MATCHED);
        }
        if (request.getDeveloperLevel() == DeveloperLevel.JUNGNIOR && (request.getExperienceYears() < 4 || request.getExperienceYears() > 10)) {
            throw new DMakerException2(LEVEL_EXPERIENCE_YEARS_NOT_MATCHED);
        }

//        Optional<Developer> developer = developerRepository.findByMemberId(request.getMemberId());
//        boolean present = developer.isPresent();
//
//        if(developer.isPresent()) {
//            throw new DMakerException2(DUPLICATED_MEMBER_ID);
//        }

        developerRepository.findByMemberId(request.getMemberId())
                .ifPresent(developer1 -> {
                    throw new DMakerException2(DUPLICATED_MEMBER_ID);
                });
    }
}
