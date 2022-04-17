create schema if not exists qw_dta;

create table qw_dta."user"(
                              id SERIAL,
                              username varchar(100) not null unique,
                              "password" varchar(100),
                              email varchar(200),
                              phone_number varchar(25),
                              "role" varchar(30),
                              CONSTRAINT user_pk PRIMARY KEY (id)
);


create table qw_dta.address(
                               id SERIAL,
                               street varchar(200) not null,
                               street_number numeric not null,
                               province varchar(200) not null,
                               zip_code numeric not null,
                               country varchar(200),
                               user_id integer not null,
                               CONSTRAINT address_pk PRIMARY KEY (id)
);

create table qw_dta.ad(
                          id SERIAL,
                          title varchar(200),
                          "content" varchar not null,
                          created_date timestamp not null default current_timestamp,
                          valid_until timestamp not null default current_timestamp + interval '7 days',
                          user_id integer,
                          county_id integer,
                          constraint ad_pk PRIMARY KEY (id)
);

create table qw_dta.review(
                              id SERIAL,
                              title varchar(200),
                              "content" varchar not null,
                              created_date timestamp default current_timestamp,
                              rating integer not null,
                              "role" varchar(30) not null,
                              user_id integer,
                              constraint review_pk PRIMARY KEY (id)
);

create table qw_dta.cfg_county(
                                  id SERIAL,
                                  code varchar(5),
                                  "name" varchar(200) not null,
                                  constraint county_pk PRIMARY KEY (id)
);

create table qw_dta.message(
                               id SERIAL,
                               message varchar(500),
                               created_date timestamp default current_timestamp,
                               user1_id integer,
                               user2_id integer,
                               ad_id integer,
                               constraint message_pk PRIMARY KEY (id)
);

create table qw_dta.profile_picture(
                               id SERIAL,
                               name varchar (200),
                               "type" varchar (200),
                               encoded_picture bytea,
                               user_id integer,
                               constraint profile_picture_pk PRIMARY KEY (id)
);

ALTER TABLE qw_dta.ad
    ADD CONSTRAINT user_fk FOREIGN KEY (user_id) REFERENCES qw_dta."user" (id);
ALTER TABLE qw_dta.ad
    ADD CONSTRAINT county_fk FOREIGN KEY (county_id) REFERENCES qw_dta.cfg_county (id);
ALTER TABLE qw_dta.address
    ADD CONSTRAINT user_fk FOREIGN KEY (user_id) REFERENCES qw_dta."user" (id);
ALTER TABLE qw_dta.review
    ADD CONSTRAINT user_fk FOREIGN KEY (user_id) REFERENCES qw_dta."user" (id);
ALTER TABLE qw_dta.message
    ADD CONSTRAINT user1_fk FOREIGN KEY (user1_id) REFERENCES qw_dta."user" (id);
ALTER TABLE qw_dta.message
    ADD CONSTRAINT user2_fk FOREIGN KEY (user2_id) REFERENCES qw_dta."user" (id);
ALTER TABLE qw_dta.message
    ADD CONSTRAINT ad_fk FOREIGN KEY (ad_id) REFERENCES qw_dta.ad (id);
ALTER TABLE qw_dta.profile_picture
    ADD CONSTRAINT profile_picture_fk FOREIGN KEY (user_id) REFERENCES qw_dta."user" (id);


insert into qw_dta.cfg_county("name") values ('Zagrebačka županija');
insert into qw_dta.cfg_county("name") values ('Krapinsko-zagorska županija');
insert into qw_dta.cfg_county("name") values ('Sisačko-moslavačka županija');
insert into qw_dta.cfg_county("name") values ('Karlovačka županija');
insert into qw_dta.cfg_county("name") values ('Varaždinska županija');
insert into qw_dta.cfg_county("name") values ('Koprivničko-križevačka županija');
insert into qw_dta.cfg_county("name") values ('Bjelovarsko-bilogorska županija');
insert into qw_dta.cfg_county("name") values ('Primorsko-goranska županija');
insert into qw_dta.cfg_county("name") values ('Ličko-senjska županija');
insert into qw_dta.cfg_county("name") values ('Virovitičko-podravska županija');
insert into qw_dta.cfg_county("name") values ('Požeško-slavonska županija');
insert into qw_dta.cfg_county("name") values ('Brodsko-posavska županija');
insert into qw_dta.cfg_county("name") values ('Zadarska županija');
insert into qw_dta.cfg_county("name") values ('Osječko-baranjska županija');
insert into qw_dta.cfg_county("name") values ('Šibensko-kninska županija');
insert into qw_dta.cfg_county("name") values ('Vukovarsko-srijemska županija');
insert into qw_dta.cfg_county("name") values ('Splitsko-dalmatinska županija');
insert into qw_dta.cfg_county("name") values ('Istarska županija');
insert into qw_dta.cfg_county("name") values ('Dubrovačko-neretvanska županija');
insert into qw_dta.cfg_county("name") values ('Međimurska županija');
insert into qw_dta.cfg_county("name") values ('Grad Zagreb');