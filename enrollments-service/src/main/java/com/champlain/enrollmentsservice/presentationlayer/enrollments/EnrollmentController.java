package com.champlain.enrollmentsservice.presentationlayer.enrollments;

import com.champlain.enrollmentsservice.businesslayer.enrollments.EnrollmentService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("api/v1/enrollments")
public class EnrollmentController {

    private final EnrollmentService enrollmentService;

    public EnrollmentController(EnrollmentService enrollmentService) {
        this.enrollmentService = enrollmentService;

    }

    @PostMapping()
    public Mono<ResponseEntity<EnrollmentResponseModel>> addEnrollment(@RequestBody Mono<EnrollmentRequestModel> enrollmentRequestModel) {
        return enrollmentRequestModel
                .as(enrollmentService::addEnrollment)
                .map(e->ResponseEntity.status(HttpStatus.CREATED).body(e));

    }
}
