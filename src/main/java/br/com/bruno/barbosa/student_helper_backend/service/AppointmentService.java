package br.com.bruno.barbosa.student_helper_backend.service;

import br.com.bruno.barbosa.student_helper_backend.domain.dto.*;
import br.com.bruno.barbosa.student_helper_backend.domain.entity.AppointmentEntity;
import br.com.bruno.barbosa.student_helper_backend.domain.entity.StudentEntity;
import br.com.bruno.barbosa.student_helper_backend.domain.enumeration.AppointmentStatusEnum;
import br.com.bruno.barbosa.student_helper_backend.domain.enumeration.RoleEnum;
import br.com.bruno.barbosa.student_helper_backend.domain.exception.AppointmentException;
import br.com.bruno.barbosa.student_helper_backend.domain.exception.AppointmentNotFoundException;
import br.com.bruno.barbosa.student_helper_backend.domain.exception.handler.NotAuthorizedException;
import br.com.bruno.barbosa.student_helper_backend.domain.request.CreateAppointmentRequest;
import br.com.bruno.barbosa.student_helper_backend.domain.response.AppointmentResponse;
import br.com.bruno.barbosa.student_helper_backend.domain.response.AppointmentsListResponse;
import br.com.bruno.barbosa.student_helper_backend.domain.response.WeekAppointmentsResponse;
import br.com.bruno.barbosa.student_helper_backend.repository.AppointmentRepository;
import br.com.bruno.barbosa.student_helper_backend.util.DateUtils;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

import static br.com.bruno.barbosa.student_helper_backend.util.DateUtils.convertTwoDigitYearToFullYear;
import static br.com.bruno.barbosa.student_helper_backend.util.DateUtils.getLocalDateFromString;

@Service
public class AppointmentService {

    @Autowired
    private AppointmentRepository appointmentRepository;

    @Autowired
    private TeacherService teacherService;

    @Autowired
    private StudentService studentService;

    @Autowired
    private UserService userService;

    @Transactional
    public void createAppointments(List<CreateAppointmentRequest> appointmentRequests) {
        TeacherDto loggedTeacher = teacherService.findLoggedTeacher();

        List<AppointmentEntity> appointments = new ArrayList<>();
        for (CreateAppointmentRequest request : appointmentRequests) {
            for (String time : request.getTimes()) {
                AppointmentEntity appointment = new AppointmentEntity();
                appointment.setDate(request.getDate());
                appointment.setTime(time);
                appointment.setStatus(AppointmentStatusEnum.AVAILABLE.name());
                appointment.setTeacherId(loggedTeacher.getId());
                appointments.add(appointment);
            }
        }

        appointmentRepository.saveAll(appointments);
    }

    public void finishAppointment(ObjectId appointmentId) {
        TeacherDto loggedTeacher = teacherService.findLoggedTeacher();
        Optional<AppointmentEntity> foundAppointment = appointmentRepository.findById(appointmentId);
        if(foundAppointment.isEmpty()) {
            throw new AppointmentNotFoundException("Appointment not found");
        }
        if(foundAppointment.get().getTeacherId() != loggedTeacher.getId()) {
            throw new AppointmentException("User can't finish this appointment");
        }
        foundAppointment.get().setStatus(AppointmentStatusEnum.COMPLETED.name());
        appointmentRepository.save(foundAppointment.get());
    }

    public void cancelAppointment(ObjectId appointmentId) {
        userService.isUserAuthenticated();
        Optional<AppointmentEntity> foundAppointment = appointmentRepository.findById(appointmentId);
        if(foundAppointment.isEmpty()) {
            throw new AppointmentNotFoundException("Agendamento não encontrado.");
        }
        foundAppointment.get().setStatus(AppointmentStatusEnum.AVAILABLE.name());
    }

    public void bookAppointment(ObjectId appointmentId) {
        StudentDto loggedStudent = studentService.findLoggedStudent();
        Optional<AppointmentEntity> foundAppointment = appointmentRepository.findById(appointmentId);
        if(foundAppointment.isEmpty()) {
            throw new AppointmentNotFoundException("Agendamento não encontrado.");
        }
        foundAppointment.get().setStatus(AppointmentStatusEnum.BOOKED.name());
        foundAppointment.get().setStudentId(loggedStudent.getId());
        appointmentRepository.save(foundAppointment.get());
    }

    public AppointmentsListResponse getTeachersAppointments(String month, String year) {
        TeacherDto loggedTeacher = teacherService.findLoggedTeacher();

        List<WeekAppointmentsResponse> weekAppointmentsResponses = new ArrayList<>();

        int yearInt = convertTwoDigitYearToFullYear(Integer.parseInt(year));
        int monthInt = Integer.parseInt(month);

        YearMonth yearMonthObj = YearMonth.of(yearInt, monthInt);
        LocalDate firstDayOfMonth = yearMonthObj.atDay(1);
        LocalDate lastDayOfMonth = yearMonthObj.atEndOfMonth();

        // Ajusta para começar na semana (domingo anterior, se necessário)
        LocalDate startDate = firstDayOfMonth.minusDays(firstDayOfMonth.getDayOfWeek().getValue() % 7);

        // Último dia da busca pode ser o final da semana em que o último dia do mês está ou o final do próximo mês.
        LocalDate endDate = lastDayOfMonth.plusDays(6 - lastDayOfMonth.getDayOfWeek().getValue());

        // Busca todas as consultas no intervalo ajustado
        List<AppointmentEntity> foundAppointments = appointmentRepository.findAllByTeacherIdAndDateBetweenOrderByDateAsc(
                loggedTeacher.getId(), startDate, endDate);

        List<AppointmentInfoDto> appointmentInfos = foundAppointments.stream().map(AppointmentInfoDto::new).toList();
        appointmentInfos.forEach(appointment -> {
            if(appointment.getStudentId() != null) {
                studentService.getStudent(appointment.getStudentId()).ifPresent(studentEntity -> appointment.setStudentName(studentEntity.getName()));
            }
        });

        List<WeekInfoDto> weeksInMonth = DateUtils.getWeeksInMonth(yearInt, monthInt);

        for (WeekInfoDto weekInfoDto : weeksInMonth) {
            WeekAppointmentsResponse weekResponse = new WeekAppointmentsResponse();
            weekResponse.setStartDay(weekInfoDto.getStartDate());
            weekResponse.setEndDay(weekInfoDto.getEndDate());

            List<AppointmentResponse> weekAppointments = new ArrayList<>();

            // Gerar todos os horários para o intervalo de datas
            for (LocalDate date = weekInfoDto.getStartDate(); !date.isAfter(weekInfoDto.getEndDate()); date = date.plusDays(1)) {
                for (int hour = 8; hour <= 22; hour++) {
                    LocalTime time = LocalTime.of(hour, 0);

                    // Verifique se o atendimento já existe no banco de dados
                    AppointmentEntity existingAppointment = appointmentRepository.findByTeacherIdAndDateAndTime(
                            loggedTeacher.getId(), date, time.toString());

                    if (existingAppointment != null) {
                        weekAppointments.add(new AppointmentResponse(new AppointmentInfoDto(existingAppointment)));
                    } else {
                        // Adicionar o horário como CLOSED se não existir
                        AppointmentResponse closedAppointment = new AppointmentResponse();
                        closedAppointment.setTeacherId(loggedTeacher.getId());
                        closedAppointment.setDay(date);
                        closedAppointment.setTime(time.toString());
                        closedAppointment.setStatus(AppointmentStatusEnum.CLOSED.name());

                        weekAppointments.add(closedAppointment);
                    }
                }
            }

            weekResponse.setAppointments(weekAppointments);
            weekAppointmentsResponses.add(weekResponse);
        }

        return new AppointmentsListResponse(weekAppointmentsResponses);
    }

    public AppointmentResponse openAppointment(LocalDate date, String time) {
        TeacherDto loggedTeacher = teacherService.findLoggedTeacher();

        AppointmentEntity foundAppointment = appointmentRepository.findByTeacherIdAndDateAndTime(
                loggedTeacher.getId(), date, time);
        if(Objects.isNull(foundAppointment)) {
            foundAppointment = new AppointmentEntity();
        }
        foundAppointment.setTeacherId(loggedTeacher.getId());
        foundAppointment.setDate(date);
        foundAppointment.setTime(time);
        foundAppointment.setStudentId(null);
        foundAppointment.setStatus(AppointmentStatusEnum.AVAILABLE.name());
        AppointmentEntity savedAppointment = appointmentRepository.save(foundAppointment);
        return new AppointmentResponse(savedAppointment);
    }

    public void closeAppointment(ObjectId appointmentId) {
        TeacherDto loggedTeacher = teacherService.findLoggedTeacher();
        Optional<AppointmentEntity> foundAppointment = appointmentRepository.findById(appointmentId);
        if(foundAppointment.isEmpty()) {
            throw new AppointmentNotFoundException("Agendamento não encontrado.");
        }
        if(!foundAppointment.get().getTeacherId().equals(loggedTeacher.getId())) {
            throw new NotAuthorizedException("Apenas o professor do atendimento pode alterar o atendimento.");
        }
        foundAppointment.get().setStatus(AppointmentStatusEnum.CLOSED.name());
        appointmentRepository.save(foundAppointment.get());
    }

    public void addLinkToAppointment(ObjectId appointmentId, String url) {
        TeacherDto loggedTeacher = teacherService.findLoggedTeacher();
        Optional<AppointmentEntity> foundAppointment = appointmentRepository.findById(appointmentId);
        if(foundAppointment.isEmpty()) {
            throw new AppointmentNotFoundException("Agendamento não encontrado.");
        }
        if(!foundAppointment.get().getTeacherId().equals(loggedTeacher.getId())) {
            throw new NotAuthorizedException("Apenas o professor do atendimento pode alterar o atendimento.");
        }
        foundAppointment.get().setLessonUrl(url);
        appointmentRepository.save(foundAppointment.get());
    }

    public List<AppointmentResponse> getTeacherTodaysAppointment() {
        TeacherDto loggedTeacher = teacherService.findLoggedTeacher();
        LocalDateTime today = LocalDateTime.now();

        // Busca os agendamentos existentes no banco
        List<AppointmentEntity> existingAppointments = appointmentRepository.findByTeacherIdAndDate(loggedTeacher.getId(), today);

        // Extrai os horários existentes em uma lista para fácil comparação
        Set<String> existingTimes = existingAppointments.stream()
                .map(AppointmentEntity::getTime)
                .collect(Collectors.toSet());

        // Formata para criar novos horários
        DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm");

        // Lista final de respostas
        List<AppointmentResponse> appointmentResponses = new ArrayList<>();

        // Gera a lista completa de horários do dia, das 08:00 às 22:00
        LocalTime startTime = LocalTime.of(8, 0);
        LocalTime endTime = LocalTime.of(22, 0);

        for (LocalTime time = startTime; !time.isAfter(endTime); time = time.plusHours(1)) {
            String formattedTime = time.format(timeFormatter);

            // Verifica se já existe um agendamento para o horário
            if (existingTimes.contains(formattedTime)) {
                // Adiciona os agendamentos já existentes
                AppointmentEntity existingAppointment = existingAppointments.stream()
                        .filter(appointment -> appointment.getTime().equals(formattedTime))
                        .findFirst()
                        .orElseThrow(); // Deve sempre existir, pois foi filtrado antes

                appointmentResponses.add(new AppointmentResponse(existingAppointment));
            } else {
                AppointmentResponse closedAppointment = new AppointmentResponse();
                closedAppointment.setTeacherId(loggedTeacher.getId());
                closedAppointment.setDay(LocalDate.now());
                closedAppointment.setTime(time.toString());
                closedAppointment.setStatus(AppointmentStatusEnum.CLOSED.name());

                appointmentResponses.add(closedAppointment);
            }
        }

        return appointmentResponses;
    }

}
