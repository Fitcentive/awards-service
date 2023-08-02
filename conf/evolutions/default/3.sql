# -- !Ups

insert into metric_categories (name, description) values ('WeightData', 'Relating to user weight data');

insert into milestones (name, milestone_category, description)
values ('ThreeDayStreak', 'WeightData', 'When a user enters logs weight for 3 consecutive days');
insert into milestones (name, milestone_category, description)
values ('OneWeekStreak', 'WeightData', 'When a user enters logs weight for 7 consecutive days');
insert into milestones (name, milestone_category, description)
values ('TenDayStreak', 'WeightData', 'When a user enters logs weight for 10 consecutive days');
insert into milestones (name, milestone_category, description)
values ('TwoWeekStreak', 'WeightData', 'When a user enters logs weight for 14 consecutive days');
insert into milestones (name, milestone_category, description)
values ('ThreeWeekStreak', 'WeightData', 'When a user enters logs weight for 21 consecutive days');
insert into milestones (name, milestone_category, description)
values ('OneMonthStreak', 'WeightData', 'When a user enters logs weight for 30 consecutive days');
insert into milestones (name, milestone_category, description)
values ('TwoMonthStreak', 'WeightData', 'When a user enters logs weight for 60 consecutive days');
insert into milestones (name, milestone_category, description)
values ('ThreeMonthStreak', 'WeightData', 'When a user enters logs weight for 90 consecutive days');
insert into milestones (name, milestone_category, description)
values ('SixMonthStreak', 'WeightData', 'When a user enters logs weight for 180 consecutive days');
insert into milestones (name, milestone_category, description)
values ('OneYearStreak', 'WeightData', 'When a user enters logs weight for 365 consecutive days');


create table user_weight_metrics (
    user_id uuid not null,
    metric_date varchar not null,
    weight_in_lbs decimal not null,
    created_at timestamp not null default now(),
    updated_at timestamp not null default now(),
    constraint unique_user_weight_metric_per_day unique (user_id, metric_date)
)

    # -- !Downs