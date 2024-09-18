package br.com.bruno.barbosa.student_helper_backend.repository;

import br.com.bruno.barbosa.student_helper_backend.domain.entity.AppointmentEntity;
import br.com.bruno.barbosa.student_helper_backend.domain.request.AppointmentFilterRequest;
import br.com.bruno.barbosa.student_helper_backend.domain.response.AppointmentResponse;
import br.com.bruno.barbosa.student_helper_backend.domain.response.AppointmentsListResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
public class FilterRepository {

    @Autowired
    private MongoTemplate mongoTemplate;

    public List<AppointmentResponse> getAppointmentsByFilter(AppointmentFilterRequest filters) {

        Query query = new Query();

        if (filters.getTeacherId() != null && !filters.getTeacherId().isEmpty()) {
            query.addCriteria(Criteria.where("teacherId").is(filters.getTeacherId()));
        }

        if (filters.getDate() != null) {
            LocalDate startDate = filters.getDate();

            LocalDateTime startOfDay = startDate.atStartOfDay();

            LocalDateTime endOfDay = startDate.atTime(LocalTime.MAX);

            Date startDateTime = Date.from(startOfDay.atZone(ZoneId.systemDefault()).toInstant());
            Date endDateTime = Date.from(endOfDay.atZone(ZoneId.systemDefault()).toInstant());

            query.addCriteria(Criteria.where("date").gte(startDateTime).lt(endDateTime));
        }

        if (filters.getTime() != null && !filters.getTime().isEmpty()) {
            query.addCriteria(Criteria.where("time").in(filters.getTime()));
        }

        if (filters.getStatus() != null && !filters.getStatus().isEmpty()) {
            query.addCriteria(Criteria.where("status").in(filters.getStatus()));
        }

        List<AppointmentEntity> appointmentEntities = mongoTemplate.find(query, AppointmentEntity.class);

        return appointmentEntities.stream().map(AppointmentResponse::new).toList();
    }

}
