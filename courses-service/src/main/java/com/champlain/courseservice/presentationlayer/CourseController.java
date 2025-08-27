package com.champlain.courseservice.presentationlayer;


import com.champlain.courseservice.businesslayer.CourseService;
import com.champlain.courseservice.exceptionhandling.ApplicationExceptions;
import com.champlain.courseservice.validation.RequestValidator;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("api/v1/courses")
@Slf4j
public class CourseController {

    private final CourseService courseService;

    public CourseController(CourseService courseService) {
        this.courseService = courseService;
    }

    @GetMapping(
            value = "",
            produces = MediaType.TEXT_EVENT_STREAM_VALUE
            // produces a stream not a .JSON
    )
    public Flux<CourseResponseModel> getAllCourses() {
        return courseService.getAllCourses();
    }

    @GetMapping("{courseId}")
    public Mono<ResponseEntity<CourseResponseModel>> getCourseByCourseId(@PathVariable String courseId) {
        // check courseId length
        return Mono.just(courseId)
                .filter(id -> id.length() == 36)
                .switchIfEmpty(ApplicationExceptions.invalidCourseId(courseId))
                .flatMap(courseService::getCourseByCourseId)
                // .map won't work since were mapping the mono of a mono
                .map(ResponseEntity::ok)
                .switchIfEmpty(ApplicationExceptions.courseNotFound(courseId));

    }

    @PostMapping()
    public Mono<ResponseEntity<CourseResponseModel>> addCourse(@RequestBody Mono<CourseRequestModel> courseRequestModel) {
        return courseRequestModel.transform(RequestValidator.validateBody())
                .as(courseService::addCourse)
                // lambda expression to get the response entity's created status, passing the responsemodel in the body
                .map(c -> ResponseEntity.status(HttpStatus.CREATED).body(c));

    }

    @PutMapping("{courseId}")
    public Mono<ResponseEntity<CourseResponseModel>> updateCourse(@PathVariable String courseId,
                                                                  @RequestBody Mono<CourseRequestModel> courseRequestModel) {
        return Mono.just(courseId)
                .filter(id -> id.length() == 36)
                .switchIfEmpty(ApplicationExceptions.invalidCourseId(courseId))
                .thenReturn(courseRequestModel.transform(RequestValidator.validateBody()))
                .flatMap(validReq -> courseService.updateCourse(validReq, courseId))
                .map(ResponseEntity::ok)
                .switchIfEmpty(ApplicationExceptions.courseNotFound(courseId));

    }

    @DeleteMapping("{courseId}")
    public Mono<ResponseEntity<CourseResponseModel>> deleteCourse(@PathVariable String courseId) {
        // check courseId length
        return Mono.just(courseId)
                .filter(id -> id.length() == 36)
                .switchIfEmpty(ApplicationExceptions.invalidCourseId(courseId))
                .flatMap(courseService::deleteCourseByCourseId)
                .map(ResponseEntity::ok)
                .switchIfEmpty(ApplicationExceptions.courseNotFound(courseId));
    }

}
