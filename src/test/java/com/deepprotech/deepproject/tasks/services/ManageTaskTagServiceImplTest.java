package com.deepprotech.deepproject.tasks.services;

import com.deepprotech.deepproject.tasks.commands.AddTaskTagCommand;
import com.deepprotech.deepproject.tasks.commands.RemoveTaskTagCommand;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.jdbc.core.JdbcTemplate;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class ManageTaskTagServiceImplTest {

    @Mock JdbcTemplate jdbc;
    @InjectMocks ManageTaskTagServiceImpl service;

    @Test
    void addsTag() {
        service.handle(new AddTaskTagCommand(1L, "urgent"));
        verify(jdbc).update(eq("INSERT INTO task_tags (task_id, tag_name) VALUES (?, ?) ON CONFLICT DO NOTHING"), eq(1L), eq("urgent"));
    }

    @Test
    void removesTag() {
        service.handle(new RemoveTaskTagCommand(1L, "urgent"));
        verify(jdbc).update(eq("DELETE FROM task_tags WHERE task_id = ? AND tag_name = ?"), eq(1L), eq("urgent"));
    }
}