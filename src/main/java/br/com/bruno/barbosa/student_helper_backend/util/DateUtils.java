package br.com.bruno.barbosa.student_helper_backend.util;

import br.com.bruno.barbosa.student_helper_backend.domain.dto.WeekInfoDto;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.List;

public class DateUtils {

    public static List<WeekInfoDto> getWeeksInMonth(int year, int monthNumber) {
        List<WeekInfoDto> weekInfos = new ArrayList<>();
        YearMonth yearMonth = YearMonth.of(year, monthNumber);
        LocalDate firstDayOfMonth = yearMonth.atDay(1);
        LocalDate lastDayOfMonth = yearMonth.atEndOfMonth();

        // Ajusta para começar no domingo anterior, se necessário
        LocalDate currentStart = firstDayOfMonth;
        if (currentStart.getDayOfWeek() != DayOfWeek.SUNDAY) {
            currentStart = currentStart.minusDays(currentStart.getDayOfWeek().getValue() % 7);
        }

        while (true) {
            LocalDate currentEnd = currentStart.plusDays(6);

            // Se currentEnd ultrapassar o final do mês corrente, ajusta para o final do mês corrente
            if (currentEnd.isAfter(lastDayOfMonth)) {
                currentEnd = lastDayOfMonth;
            }

            // Adiciona a semana com o início e o fim ajustados
            weekInfos.add(new WeekInfoDto(currentStart, currentEnd));

            // Se currentEnd é o final do mês corrente, para o loop
            if (currentEnd.equals(lastDayOfMonth)) {
                break;
            }

            // Atualiza currentStart para o próximo domingo após a semana atual
            currentStart = currentEnd.plusDays(1);
        }

        return weekInfos;
    }


    public static int convertTwoDigitYearToFullYear(int twoDigitYear) {
        int currentYear = LocalDate.now().getYear();
        int currentCentury = currentYear / 100;
        int fullYear = (currentCentury * 100) + twoDigitYear;

        // Se o ano resultante estiver no futuro, retroceda um século
        if (fullYear > currentYear) {
            fullYear -= 100;
        }

        return fullYear;
    }

    public static LocalDate getLocalDateFromString(String dateString, int year, int month) {
        int day = Integer.parseInt(dateString);
        return YearMonth.of(year, month).atDay(day);
    }

}
