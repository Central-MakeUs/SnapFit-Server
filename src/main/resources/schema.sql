create table SnapfitUser (
    id SERIAL PRIMARY KEY,
    nick_name VARCHAR(32) NOT NULL,
    vibes VARCHAR(512) NOT NULL,
    social_id VARCHAR(256) NOT NULL,
    social_type VARCHAR(16) NOT NULL,
    created_at timestamp NOT NULL,
    login_time timestamp NOT  NULL,
    is_marketing_receive boolean NOT NULL,
    is_photographer boolean NOT NULL,
    is_noti boolean NOT null,
    is_valid boolean not null,
    profile varchar
);

create table UserDevice (
    id SERIAL PRIMARY KEY,
    login_date_time timestamp NOT NULL,
    device_type VARCHAR(8) NOT NULL,
    device_id VARCHAR(256) NOT NULL,
    user_id bigint NOT NULL,
    FOREIGN KEY (user_id)
    REFERENCES SnapfitUser(id)
    on delete cascade
);


create table refresh_token (
	id SERIAL primary key,
	refresh_token VARCHAR(512) not null,
	user_id bigint not null,
	FOREIGN KEY (user_id)
    REFERENCES SnapfitUser(id)
    on delete cascade
)