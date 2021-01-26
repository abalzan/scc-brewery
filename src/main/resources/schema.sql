-- get this table from https://docs.spring.io/spring-security/site/docs/5.0.5.RELEASE/reference/htmlsingle/#remember-me-persistent-token
create table persistent_logins (username varchar(64) not null,
                                series varchar(64) primary key,
                                token varchar(64) not null,
                                last_used timestamp not null);