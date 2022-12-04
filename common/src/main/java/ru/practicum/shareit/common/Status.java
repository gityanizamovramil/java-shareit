package ru.practicum.shareit.common;

public enum Status {
    WAITING, // новое бронирование, ожидает одобрения
    APPROVED, // бронирование подтверждено владельцем
    REJECTED, // бронирование отклонено владельцем
    CANCELLED // бронирование отменено создателем
}
