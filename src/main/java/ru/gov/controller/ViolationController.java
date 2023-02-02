package ru.gov.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.SneakyThrows;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@RestController
@CrossOrigin("*")
@RequestMapping(value = "api/v1/violation")
public class ViolationController {
    private static final List<Violation> violations = new ArrayList<>(
            Arrays.asList(
                    Violation.builder().id(1L).deptCode(1).department("Республика Адыгея").informResource("Майкоп").correction("Устранение в источнике").criterion("Не заполнено обязательное поле").violationStatus("Актуально").violationStartDate(LocalDate.now()).violationUUID(UUID.randomUUID().toString()).build(),
                    Violation.builder().id(2L).deptCode(2).department("Республика Башкортостан").informResource("Уфа").correction("Автоматическое устранение").criterion("Нарушена ссылочная целостность").violationStatus("Неактуально").violationStartDate(LocalDate.of(1999, 3, 1)).violationUUID(UUID.randomUUID().toString()).build(),
                    Violation.builder().id(3L).deptCode(3).department("Республика Бурятия").informResource("Улан-Удэ").correction("Устранение в источнике").criterion("Превышена длина значения").violationStatus("Рассматривается").violationStartDate(LocalDate.of(1999, 4, 1)).violationUUID(UUID.randomUUID().toString()).build(),
                    Violation.builder().id(4L).deptCode(4).department("Республика Алтай").informResource("Горно-Алтайск").correction("Автоматическое устранение").criterion("Некорректная дата").violationStatus("Неактуально").violationStartDate(LocalDate.of(1999, 7, 1)).violationUUID(UUID.randomUUID().toString()).build(),
                    Violation.builder().id(5L).deptCode(5).department("Республика Дагестан").informResource("Махачкала").correction("Устранение в источнике").criterion("Не заполнено обязательное поле").violationStatus("Актуально").violationStartDate(LocalDate.of(1999, 4, 1)).violationUUID(UUID.randomUUID().toString()).build(),
                    Violation.builder().id(6L).deptCode(6).department("Республика Ингушетия").informResource("Магас").correction("Автоматическое устранение").criterion("Нарушена ссылочная целостность").violationStatus("Неактуально").violationStartDate(LocalDate.of(1999, 3, 1)).violationUUID(UUID.randomUUID().toString()).build(),
                    Violation.builder().id(7L).deptCode(7).department("Кабардино-Балкарская Республика").informResource("Нальчик").correction("Устранение в источнике").criterion("Некорректная дата").violationStatus("Актуально").violationStartDate(LocalDate.of(1666, 2, 1)).violationUUID(UUID.randomUUID().toString()).build()
            )
    );

    @SneakyThrows
    @GetMapping("/filter")
    public ResponseEntity<List<Violation>> filter(@RequestParam String filter) {
        ObjectMapper objectMapper = new ObjectMapper();
        FilterDto filterDto = objectMapper.readValue(filter, FilterDto.class);

        List<Violation> result = violations.stream().filter(violation -> {
            if (!filterDto.getDepartment().isEmpty()) {
                if (violation.getDepartment() == null) {
                    return false;
                }
                return violation.getDepartment().toLowerCase(Locale.ROOT).contains(filterDto.getDepartment().toLowerCase(Locale.ROOT));
            }
            if (filterDto.getDeptCode() != null) {
                return violation.getDeptCode().equals(filterDto.getDeptCode());
            }
            return true;
        }).collect(Collectors.toList());

        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Violation> details(@PathVariable("id") Long id) {
        Optional<Violation> resultOptional = violations.stream().filter(violation -> violation.getId().equals(id)).findFirst();
        return resultOptional.map(violation -> new ResponseEntity<>(violation, HttpStatus.OK)).orElseGet(() -> new ResponseEntity<>(null, HttpStatus.NOT_FOUND));
    }

    @Data
    @Builder
    @AllArgsConstructor
    public static class Violation {
        private Long id;
        private String department;
        private Integer deptCode;
        // Информационный ресурс
        private String informResource;
        private LocalDate violationStartDate;
        // Статус нарушения
        private String violationStatus;
        // Имя критерия
        private String criterion;
        // Вариант устранения
        private String correction;
        // Идентификация нарушения
        private String violationUUID;
    }

    @Data
    private static class FilterDto {
        private String department;
        private Integer deptCode;
    }
}

