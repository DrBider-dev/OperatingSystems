package HistorialChat;

import java.time.DayOfWeek;
import java.time.LocalDate;

/**
 * AnalysisResult
 * --------------
 * Objeto de transferencia (DTO) que agrupa las respuestas a las tres
 * tareas solicitadas por la guía, junto con el tiempo de ejecución
 * total de la estrategia que lo produjo. Es usado tanto por
 * TaskParallelismAnalyzer (Estrategia A) como por
 * DataParallelismAnalyzer (Estrategia B), de modo que ambos resultados
 * se puedan comparar directamente desde Main.
 */
public class AnalysisResult {

    private LocalDate longestMessageDate;
    private int longestMessageLength;

    private DayOfWeek busiestDay;
    private long busiestDayCount;

    private String mostFrequentContact;
    private long mostFrequentContactCount;

    private long executionTimeMillis;

    public LocalDate getLongestMessageDate() {
        return longestMessageDate;
    }

    public void setLongestMessageDate(LocalDate longestMessageDate) {
        this.longestMessageDate = longestMessageDate;
    }

    public int getLongestMessageLength() {
        return longestMessageLength;
    }

    public void setLongestMessageLength(int longestMessageLength) {
        this.longestMessageLength = longestMessageLength;
    }

    public DayOfWeek getBusiestDay() {
        return busiestDay;
    }

    public void setBusiestDay(DayOfWeek busiestDay) {
        this.busiestDay = busiestDay;
    }

    public long getBusiestDayCount() {
        return busiestDayCount;
    }

    public void setBusiestDayCount(long busiestDayCount) {
        this.busiestDayCount = busiestDayCount;
    }

    public String getMostFrequentContact() {
        return mostFrequentContact;
    }

    public void setMostFrequentContact(String mostFrequentContact) {
        this.mostFrequentContact = mostFrequentContact;
    }

    public long getMostFrequentContactCount() {
        return mostFrequentContactCount;
    }

    public void setMostFrequentContactCount(long mostFrequentContactCount) {
        this.mostFrequentContactCount = mostFrequentContactCount;
    }

    public long getExecutionTimeMillis() {
        return executionTimeMillis;
    }

    public void setExecutionTimeMillis(long executionTimeMillis) {
        this.executionTimeMillis = executionTimeMillis;
    }
}
