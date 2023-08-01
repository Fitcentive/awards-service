# -- !Ups

insert into metric_categories (name, description) values ('DiaryEntryData', 'Relating to diary entry data');
insert into metric_categories (name, description) values ('ActivityData', 'Relating to user activity data');

insert into milestones (name, milestone_category, description)
values ('TenEntries', 'DiaryEntryData', 'When a user amasses 10 diary entries in their lifetime');
insert into milestones (name, milestone_category, description)
values ('FiftyEntries', 'DiaryEntryData', 'When a user amasses 50 diary entries in their lifetime');
insert into milestones (name, milestone_category, description)
values ('HundredEntries', 'DiaryEntryData', 'When a user amasses 100 diary entries in their lifetime');
insert into milestones (name, milestone_category, description)
values ('TwoHundredFiftyEntries', 'DiaryEntryData', 'When a user amasses 250 diary entries in their lifetime');
insert into milestones (name, milestone_category, description)
values ('FiveHundredEntries', 'DiaryEntryData', 'When a user amasses 500 diary entries in their lifetime');
insert into milestones (name, milestone_category, description)
values ('ThousandEntries', 'DiaryEntryData', 'When a user amasses 1000 diary entries in their lifetime');
insert into milestones (name, milestone_category, description)
values ('TwoThousandEntries', 'DiaryEntryData', 'When a user amasses 2000 diary entries in their lifetime');
insert into milestones (name, milestone_category, description)
values ('FiveThousandEntries', 'DiaryEntryData', 'When a user amasses 5000 diary entries in their lifetime');
insert into milestones (name, milestone_category, description)
values ('TenThousandEntries', 'DiaryEntryData', 'When a user amasses 10,000 diary entries in their lifetime');
insert into milestones (name, milestone_category, description)
values ('TwentyFiveThousandEntries', 'DiaryEntryData', 'When a user amasses 25,000 diary entries in their lifetime');


insert into milestones (name, milestone_category, description)
values ('OneHour', 'ActivityData', 'When a user amasses 60 minutes of activity in their lifetime');
insert into milestones (name, milestone_category, description)
values ('TwoHours', 'ActivityData', 'When a user amasses 2 hours of activity in their lifetime');
insert into milestones (name, milestone_category, description)
values ('FiveHours', 'ActivityData', 'When a user amasses 5 hours of activity in their lifetime');
insert into milestones (name, milestone_category, description)
values ('TenHours', 'ActivityData', 'When a user amasses 10 hours of activity in their lifetime');
insert into milestones (name, milestone_category, description)
values ('TwentyFiveHours', 'ActivityData', 'When a user amasses 25 hours of activity in their lifetime');
insert into milestones (name, milestone_category, description)
values ('FiftyHours', 'ActivityData', 'When a user amasses 50 hours of activity in their lifetime');
insert into milestones (name, milestone_category, description)
values ('HundredHours', 'ActivityData', 'When a user amasses 100 hours of activity in their lifetime');
insert into milestones (name, milestone_category, description)
values ('TwoHundredFiftyHours', 'ActivityData', 'When a user amasses 250 hours of activity in their lifetime');
insert into milestones (name, milestone_category, description)
values ('FiveHundredHours', 'ActivityData', 'When a user amasses 500 hours of activity in their lifetime');
insert into milestones (name, milestone_category, description)
values ('ThousandHours', 'ActivityData', 'When a user amasses 1000 hours of activity in their lifetime');


create table user_diary_metrics (
    user_id uuid not null,
    metric_date varchar not null,
    activity_minutes int,
    created_at timestamp not null default now(),
    updated_at timestamp not null default now()
)

# -- !Downs