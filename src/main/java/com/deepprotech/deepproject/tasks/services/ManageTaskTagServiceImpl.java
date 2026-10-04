package com.deepprotech.deepproject.tasks.services;

import com.deepprotech.deepproject.core.TaskTag;
import com.deepprotech.deepproject.tasks.api.ManageTaskTagService;
import com.deepprotech.deepproject.tasks.commands.AddTaskTagCommand;
import com.deepprotech.deepproject.tasks.commands.RemoveTaskTagCommand;
import com.deepprotech.deepproject.tasks.repository.TaskTagRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class ManageTaskTagServiceImpl implements ManageTaskTagService {

    private final TaskTagRepository taskTagRepository;

    @Override
    @Transactional
    public void handle(AddTaskTagCommand command) {
        if (taskTagRepository.findByTaskIdAndTagName(command.taskId(), command.tagName()).isEmpty()) {
            TaskTag tag = TaskTag.builder()
                    .taskId(command.taskId())
                    .tagName(command.tagName())
                    .build();
            taskTagRepository.save(tag);
        }
        log.info("task_tag_added taskId={} tag={}", command.taskId(), command.tagName());
    }

    @Override
    @Transactional
    public void handle(RemoveTaskTagCommand command) {
        taskTagRepository.deleteByTaskIdAndTagName(command.taskId(), command.tagName());
        log.info("task_tag_removed taskId={} tag={}", command.taskId(), command.tagName());
    }
}
