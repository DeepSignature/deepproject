create table if not exists sprints
(
    name         varchar(200)                                                 not null,
    description  text,
    status       varchar(50)              default 'PLANNED'::character varying not null,
    start_date   timestamp with time zone,
    end_date     timestamp with time zone,
    created_at   timestamp with time zone default CURRENT_TIMESTAMP           not null,
    updated_at   timestamp with time zone default CURRENT_TIMESTAMP           not null,
    created_by   varchar(100)             default 'system'::character varying,
    updated_by   varchar(100)             default 'system'::character varying,
    version      bigint                   default 0                           not null,
    id           uuid                     default gen_random_uuid()           not null
        primary key,
    active    boolean                  default false                       not null ,
    project_id uuid                                                           not null
        references projects
            on delete cascade
);

alter table sprints
    owner to postgres;

create index idx_sprints_status
    on sprints (status);

create index idx_sprints_project
    on sprints (project_id);

-- Enable audit
SELECT fn_enable_audit('sprints');
