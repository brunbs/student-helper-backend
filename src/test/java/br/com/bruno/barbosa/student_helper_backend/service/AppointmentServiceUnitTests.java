package br.com.bruno.barbosa.student_helper_backend.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.anyString;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import br.com.bruno.barbosa.student_helper_backend.domain.dto.StudentDto;
import br.com.bruno.barbosa.student_helper_backend.domain.dto.TeacherDto;
import br.com.bruno.barbosa.student_helper_backend.domain.entity.AppointmentEntity;
import br.com.bruno.barbosa.student_helper_backend.domain.enumeration.AppointmentStatusEnum;
import br.com.bruno.barbosa.student_helper_backend.domain.exception.AppointmentException;
import br.com.bruno.barbosa.student_helper_backend.domain.exception.AppointmentNotFoundException;
import br.com.bruno.barbosa.student_helper_backend.domain.exception.handler.NotAuthorizedException;
import br.com.bruno.barbosa.student_helper_backend.domain.request.CreateAppointmentRequest;
import br.com.bruno.barbosa.student_helper_backend.domain.response.AppointmentResponse;
import br.com.bruno.barbosa.student_helper_backend.domain.response.AppointmentsListResponse;
import br.com.bruno.barbosa.student_helper_backend.repository.AppointmentRepository;
import br.com.bruno.barbosa.student_helper_backend.repository.FilterRepository;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;
import org.bson.types.ObjectId;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class AppointmentServiceUnitTests {

  @Mock
  private AppointmentRepository appointmentRepository;

  @Mock
  private TeacherService teacherService;

  @Mock
  private StudentService studentService;

  @Mock
  private UserService userService;

  @Mock
  private FilterRepository filterRepository;

  @InjectMocks
  private AppointmentService appointmentService;

  @Test
  void testCreateAppointments() {
    TeacherDto teacherDto = new TeacherDto();
    teacherDto.setId(new ObjectId());
    when(teacherService.findLoggedTeacher()).thenReturn(teacherDto);

    CreateAppointmentRequest request = new CreateAppointmentRequest();
    request.setDate(LocalDate.now());
    request.setTimes(List.of("08:00", "09:00"));

    appointmentService.createAppointments(List.of(request));

    verify(appointmentRepository, times(1)).saveAll(any());
  }

  @Test
  void testFinishAppointment_Success() {
    TeacherDto teacherDto = new TeacherDto();
    teacherDto.setId(new ObjectId());
    when(teacherService.findLoggedTeacher()).thenReturn(teacherDto);

    AppointmentEntity appointmentEntity = new AppointmentEntity();
    appointmentEntity.setTeacherId(teacherDto.getId());
    when(appointmentRepository.findById(any(ObjectId.class))).thenReturn(
        Optional.of(appointmentEntity));

    appointmentService.finishAppointment(new ObjectId());

    verify(appointmentRepository, times(1)).save(appointmentEntity);
  }

  @Test
  void testFinishAppointment_NotFound() {
    when(appointmentRepository.findById(any(ObjectId.class))).thenReturn(Optional.empty());

    assertThrows(AppointmentNotFoundException.class,
        () -> appointmentService.finishAppointment(new ObjectId()));
  }

  @Test
  void testFinishAppointment_NotAuthorized() {
    TeacherDto teacherDto = new TeacherDto();
    teacherDto.setId(new ObjectId());
    when(teacherService.findLoggedTeacher()).thenReturn(teacherDto);

    AppointmentEntity appointmentEntity = new AppointmentEntity();
    appointmentEntity.setTeacherId(new ObjectId());
    when(appointmentRepository.findById(any(ObjectId.class))).thenReturn(
        Optional.of(appointmentEntity));

    assertThrows(AppointmentException.class,
        () -> appointmentService.finishAppointment(new ObjectId()));
  }

  @Test
  void testCancelAppointment_Success() {
//        when(userService.isUserAuthenticated()).thenReturn(true);

    AppointmentEntity appointmentEntity = new AppointmentEntity();
    when(appointmentRepository.findById(any(ObjectId.class))).thenReturn(
        Optional.of(appointmentEntity));

    appointmentService.cancelAppointment(new ObjectId());

    assertEquals(AppointmentStatusEnum.AVAILABLE.name(), appointmentEntity.getStatus());
    verify(appointmentRepository, times(1)).save(appointmentEntity);
  }

  @Test
  void testCancelAppointment_NotFound() {
//        when(userService.isUserAuthenticated()).thenReturn(true);
    when(appointmentRepository.findById(any(ObjectId.class))).thenReturn(Optional.empty());

    assertThrows(AppointmentNotFoundException.class,
        () -> appointmentService.cancelAppointment(new ObjectId()));
  }

  @Test
  void testBookAppointment_Success() {
    StudentDto studentDto = new StudentDto();
    studentDto.setId(new ObjectId());
    when(studentService.findLoggedStudent()).thenReturn(studentDto);

    AppointmentEntity appointmentEntity = new AppointmentEntity();
    when(appointmentRepository.findById(any(ObjectId.class))).thenReturn(
        Optional.of(appointmentEntity));

    appointmentService.bookAppointment(new ObjectId());

    assertEquals(AppointmentStatusEnum.BOOKED.name(), appointmentEntity.getStatus());
    assertEquals(studentDto.getId(), appointmentEntity.getStudentId());
    verify(appointmentRepository, times(1)).save(appointmentEntity);
  }

  @Test
  void testBookAppointment_NotFound() {
    when(studentService.findLoggedStudent()).thenReturn(new StudentDto());
    when(appointmentRepository.findById(any(ObjectId.class))).thenReturn(Optional.empty());

    assertThrows(AppointmentNotFoundException.class,
        () -> appointmentService.bookAppointment(new ObjectId()));
  }

  @Test
  void testGetTeachersAppointments() {
    TeacherDto teacherDto = new TeacherDto();
    teacherDto.setId(new ObjectId());
    when(teacherService.findLoggedTeacher()).thenReturn(teacherDto);

    when(appointmentRepository.findAllByTeacherIdAndDateBetweenOrderByDateAsc(any(), any(), any()))
        .thenReturn(List.of());

    AppointmentsListResponse response = appointmentService.getTeachersAppointments("09", "24");

    assertNotNull(response);
    verify(appointmentRepository, times(1))
        .findAllByTeacherIdAndDateBetweenOrderByDateAsc(any(), any(), any());
  }

  @Test
  void testOpenAppointment() {
    TeacherDto teacherDto = new TeacherDto();
    teacherDto.setId(new ObjectId());
    when(teacherService.findLoggedTeacher()).thenReturn(teacherDto);

    AppointmentEntity appointmentEntity = new AppointmentEntity();
    appointmentEntity.setStatus(AppointmentStatusEnum.AVAILABLE.name());
    appointmentEntity.setId(new ObjectId());
    appointmentEntity.setTime(LocalTime.now());
    appointmentEntity.setDate(LocalDate.now());
    appointmentEntity.setLessonUrl("");
    appointmentEntity.setTeacherId(new ObjectId());
    appointmentEntity.setStudentId(new ObjectId());

    when(appointmentRepository.findByTeacherIdAndDateAndTime(any(), any(), anyString()))
        .thenReturn(new AppointmentEntity());
    when(appointmentRepository.save(any())).thenReturn(appointmentEntity);

    AppointmentResponse response = appointmentService.openAppointment(LocalDate.now(), "08:00");

    assertNotNull(response);
    assertEquals(AppointmentStatusEnum.AVAILABLE.name(), response.getStatus());
    verify(appointmentRepository, times(1)).save(any(AppointmentEntity.class));
  }

  @Test
  void testCloseAppointment_Success() {
    TeacherDto teacherDto = new TeacherDto();
    teacherDto.setId(new ObjectId());
    when(teacherService.findLoggedTeacher()).thenReturn(teacherDto);

    AppointmentEntity appointmentEntity = new AppointmentEntity();
    appointmentEntity.setTeacherId(teacherDto.getId());
    when(appointmentRepository.findById(any(ObjectId.class))).thenReturn(
        Optional.of(appointmentEntity));

    appointmentService.closeAppointment(new ObjectId());

    assertEquals(AppointmentStatusEnum.CLOSED.name(), appointmentEntity.getStatus());
    verify(appointmentRepository, times(1)).save(appointmentEntity);
  }

  @Test
  void testCloseAppointment_NotFound() {
    when(appointmentRepository.findById(any(ObjectId.class))).thenReturn(Optional.empty());

    assertThrows(AppointmentNotFoundException.class,
        () -> appointmentService.closeAppointment(new ObjectId()));
  }

  @Test
  void testCloseAppointment_NotAuthorized() {
    TeacherDto teacherDto = new TeacherDto();
    teacherDto.setId(new ObjectId());
    when(teacherService.findLoggedTeacher()).thenReturn(teacherDto);

    AppointmentEntity appointmentEntity = new AppointmentEntity();
    appointmentEntity.setTeacherId(new ObjectId());
    when(appointmentRepository.findById(any(ObjectId.class))).thenReturn(
        Optional.of(appointmentEntity));

    assertThrows(NotAuthorizedException.class,
        () -> appointmentService.closeAppointment(new ObjectId()));
  }

  @Test
  void testAddLinkToAppointment_Success() {
    TeacherDto teacherDto = new TeacherDto();
    teacherDto.setId(new ObjectId());
    when(teacherService.findLoggedTeacher()).thenReturn(teacherDto);

    AppointmentEntity appointmentEntity = new AppointmentEntity();
    appointmentEntity.setTeacherId(teacherDto.getId());
    when(appointmentRepository.findById(any(ObjectId.class))).thenReturn(
        Optional.of(appointmentEntity));

    appointmentService.addLinkToAppointment(new ObjectId(), "http://lesson-link.com");

    assertEquals("http://lesson-link.com", appointmentEntity.getLessonUrl());
    verify(appointmentRepository, times(1)).save(appointmentEntity);
  }

  @Test
  void testAddLinkToAppointment_NotFound() {
    when(appointmentRepository.findById(any(ObjectId.class))).thenReturn(Optional.empty());

    assertThrows(AppointmentNotFoundException.class,
        () -> appointmentService.addLinkToAppointment(new ObjectId(), "http://lesson-link.com"));
  }

  @Test
  void testAddLinkToAppointment_NotAuthorized() {
    TeacherDto teacherDto = new TeacherDto();
    teacherDto.setId(new ObjectId());
    when(teacherService.findLoggedTeacher()).thenReturn(teacherDto);

    AppointmentEntity appointmentEntity = new AppointmentEntity();
    appointmentEntity.setTeacherId(new ObjectId());
    when(appointmentRepository.findById(any(ObjectId.class))).thenReturn(
        Optional.of(appointmentEntity));

    assertThrows(NotAuthorizedException.class,
        () -> appointmentService.addLinkToAppointment(new ObjectId(), "http://lesson-link.com"));
  }

  @Test
  void testGetTeacherTodaysAppointments() {
    TeacherDto teacherDto = new TeacherDto();
    teacherDto.setId(new ObjectId());
    when(teacherService.findLoggedTeacher()).thenReturn(teacherDto);

    when(appointmentRepository.findByTeacherIdAndDateRange(any(), any(), any())).thenReturn(
        List.of());

    List<AppointmentResponse> responses = appointmentService.getTeacherTodaysAppointment();

    assertNotNull(responses);
    verify(appointmentRepository, times(1)).findByTeacherIdAndDateRange(any(), any(), any());
  }

  @Test
  void testGetStudentBookedAppointments() {
    StudentDto studentDto = new StudentDto();
    studentDto.setId(new ObjectId());
    when(studentService.findLoggedStudent()).thenReturn(studentDto);

    when(appointmentRepository.findByStudentIdAndDateGreaterThanEqualOrderByDateAscTimeAsc(any(),
        any()))
        .thenReturn(List.of());

    List<AppointmentResponse> responses = appointmentService.getStudentBookedAppointments();

    assertNotNull(responses);
    verify(appointmentRepository, times(1))
        .findByStudentIdAndDateGreaterThanEqualOrderByDateAscTimeAsc(any(), any());
  }

}
