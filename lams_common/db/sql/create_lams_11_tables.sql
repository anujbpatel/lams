CREATE TABLE lams_gate_activity_level (
       gate_activity_level_id INT(11) NOT NULL DEFAULT 0
     , description VARCHAR(128) NOT NULL
     , PRIMARY KEY (gate_activity_level_id)
)TYPE=InnoDB;

CREATE TABLE lams_grouping_type (
       grouping_type_id INT(11) NOT NULL
     , description VARCHAR(128) NOT NULL
     , PRIMARY KEY (grouping_type_id)
)TYPE=InnoDB;

CREATE TABLE lams_learning_activity_type (
       learning_activity_type_id INT(11) NOT NULL DEFAULT 0
     , description VARCHAR(255) NOT NULL
     , PRIMARY KEY (learning_activity_type_id)
)TYPE=InnoDB;

CREATE TABLE lams_learning_library (
       learning_library_id BIGINT(20) NOT NULL AUTO_INCREMENT
     , description TEXT
     , title VARCHAR(255)
     , create_date_time DATETIME NOT NULL DEFAULT '0000-00-00 00:00:00'
     , PRIMARY KEY (learning_library_id)
)TYPE=InnoDB;

CREATE TABLE lams_organisation_type (
       organisation_type_id INT(3) NOT NULL
     , name VARCHAR(64) NOT NULL
     , description VARCHAR(255) NOT NULL
     , PRIMARY KEY (organisation_type_id)
)TYPE=InnoDB;
CREATE UNIQUE INDEX UQ_lams_organisation_type_name ON lams_organisation_type (name ASC);

CREATE TABLE lams_role (
       role_id INT(6) NOT NULL DEFAULT 0
     , name VARCHAR(64) NOT NULL
     , description TEXT
     , create_date BIGINT(20)
     , PRIMARY KEY (role_id)
)TYPE=InnoDB;
CREATE INDEX gname ON lams_role (name ASC);

CREATE TABLE lams_tool (
       tool_id BIGINT(20) NOT NULL AUTO_INCREMENT
     , tool_signature VARCHAR(64) NOT NULL
     , service_name VARCHAR(255) NOT NULL
     , tool_display_name VARCHAR(255) NOT NULL
     , description TEXT
     , default_tool_content_id BIGINT(20) NOT NULL
     , supports_grouping_flag TINYINT(1) NOT NULL DEFAULT 0
     , supports_define_later_flag TINYINT(1) NOT NULL DEFAULT 0
     , learner_url TEXT NOT NULL
     , author_url TEXT NOT NULL
     , define_later_url TEXT
     , export_portfolio_url TEXT NOT NULL
     , monitor_url TEXT NOT NULL
     , UNIQUE UQ_lams_tool_sig (tool_signature)
     , UNIQUE UQ_lams_tool_class_name (service_name)
     , PRIMARY KEY (tool_id)
)TYPE=InnoDB;

CREATE TABLE lams_tool_session_state (
       tool_session_state_id INT(3) NOT NULL
     , description VARCHAR(255) NOT NULL
     , PRIMARY KEY (tool_session_state_id)
)TYPE=InnoDB;

CREATE TABLE lams_lesson_state (
       lesson_state_id INT(3) NOT NULL
     , description VARCHAR(255) NOT NULL
     , PRIMARY KEY (lesson_state_id)
)TYPE=InnoDB;

CREATE TABLE lams_tool_session_type (
       tool_session_type_id INT(3) NOT NULL
     , description VARCHAR(255) NOT NULL
     , PRIMARY KEY (tool_session_type_id)
)TYPE=InnoDB;

CREATE TABLE lams_license (
       license_id BIGINT(20) NOT NULL
     , license_text TEXT NOT NULL
     , license_url VARCHAR(256)
     , pciture_url VARCHAR(256)
     , name VARCHAR(200) NOT NULL
     , code VARCHAR(20) NOT NULL
     , defualt_license TINYINT(1) NOT NULL DEFAULT 0
     , PRIMARY KEY (license_id)
)TYPE=InnoDB;

CREATE TABLE lams_authentication_method_type (
       authentication_method_type_id INT(3) NOT NULL
     , description VARCHAR(64) NOT NULL
     , PRIMARY KEY (authentication_method_type_id)
)TYPE=InnoDB;

CREATE TABLE lams_authentication_method (
       authentication_method_id BIGINT(20) NOT NULL
     , authentication_method_type_id INT(3) NOT NULL DEFAULT 0
     , authentication_method_name VARCHAR(255) NOT NULL
     , UNIQUE UQ_lams_authentication_method_1 (authentication_method_name)
     , PRIMARY KEY (authentication_method_id)
     , INDEX (authentication_method_type_id)
     , CONSTRAINT FK_lams_authorization_method_1 FOREIGN KEY (authentication_method_type_id)
                  REFERENCES lams_authentication_method_type (authentication_method_type_id) ON DELETE NO ACTION ON UPDATE NO ACTION
)TYPE=InnoDB;

CREATE TABLE lams_workspace_folder (
       workspace_folder_id BIGINT(20) NOT NULL AUTO_INCREMENT
     , parent_folder_id BIGINT(20)
     , name VARCHAR(64) NOT NULL
     , workspace_id BIGINT(20) NOT NULL
     , PRIMARY KEY (workspace_folder_id)
     , INDEX (parent_folder_id)
     , CONSTRAINT FK_lams_workspace_folder_2 FOREIGN KEY (parent_folder_id)
                  REFERENCES lams_workspace_folder (workspace_folder_id) ON DELETE NO ACTION ON UPDATE NO ACTION
)TYPE=InnoDB;

CREATE TABLE lams_workspace (
       workspace_id BIGINT(20) NOT NULL AUTO_INCREMENT
     , root_folder_id BIGINT(20) NOT NULL
     , name VARCHAR(255)
     , PRIMARY KEY (workspace_id)
     , INDEX (root_folder_id)
     , CONSTRAINT FK_lams_workspace_1 FOREIGN KEY (root_folder_id)
                  REFERENCES lams_workspace_folder (workspace_folder_id) ON DELETE NO ACTION ON UPDATE NO ACTION
)TYPE=InnoDB;

CREATE TABLE lams_grouping (
       grouping_id BIGINT(20) NOT NULL AUTO_INCREMENT
     , grouping_ui_id INT(11)
     , grouping_type_id INT(11) NOT NULL
     , number_of_groups INT(11)
     , learners_per_group INT(11)
     , staff_group_id BIGINT(20) DEFAULT 0
     , max_number_of_groups INT(3)
     , id INT(11)
     , PRIMARY KEY (grouping_id)
     , INDEX (grouping_type_id)
     , CONSTRAINT FK_lams_learning_grouping_1 FOREIGN KEY (grouping_type_id)
                  REFERENCES lams_grouping_type (grouping_type_id) ON DELETE NO ACTION ON UPDATE NO ACTION
)TYPE=InnoDB;

CREATE TABLE lams_organisation (
       organisation_id BIGINT(20) NOT NULL AUTO_INCREMENT
     , name VARCHAR(250)
     , description VARCHAR(250)
     , parent_organisation_id BIGINT(20)
     , organisation_type_id INT(3) NOT NULL DEFAULT 0
     , create_date DATETIME NOT NULL DEFAULT '0000-00-00 00:00:00'
     , workspace_id BIGINT(20)
     , PRIMARY KEY (organisation_id)
     , INDEX (organisation_type_id)
     , CONSTRAINT FK_lams_organisation_1 FOREIGN KEY (organisation_type_id)
                  REFERENCES lams_organisation_type (organisation_type_id) ON DELETE NO ACTION ON UPDATE NO ACTION
     , INDEX (workspace_id)
     , CONSTRAINT FK_lams_organisation_2 FOREIGN KEY (workspace_id)
                  REFERENCES lams_workspace (workspace_id) ON DELETE NO ACTION ON UPDATE NO ACTION
     , INDEX (parent_organisation_id)
     , CONSTRAINT FK_lams_organisation_3 FOREIGN KEY (parent_organisation_id)
                  REFERENCES lams_organisation (organisation_id) ON DELETE NO ACTION ON UPDATE NO ACTION
)TYPE=InnoDB;

CREATE TABLE lams_user (
       user_id BIGINT(20) NOT NULL AUTO_INCREMENT
     , login VARCHAR(20) NOT NULL
     , password VARCHAR(50) NOT NULL
     , title VARCHAR(32)
     , first_name VARCHAR(64)
     , last_name VARCHAR(128)
     , address_line_1 VARCHAR(64)
     , address_line_2 VARCHAR(64)
     , address_line_3 VARCHAR(64)
     , city VARCHAR(64)
     , state VARCHAR(64)
     , country VARCHAR(64)
     , day_phone VARCHAR(64)
     , evening_phone VARCHAR(64)
     , mobile_phone VARCHAR(64)
     , fax VARCHAR(64)
     , email VARCHAR(128)
     , disabled_flag TINYINT(1) NOT NULL DEFAULT 0
     , create_date DATETIME NOT NULL DEFAULT '0000-00-00 00:00:00'
     , authentication_method_id BIGINT(20) NOT NULL DEFAULT 0
     , workspace_id BIGINT(20)
     , user_organisation_id BIGINT(20) NOT NULL DEFAULT 0
     , base_organisation_id BIGINT(20) NOT NULL DEFAULT 0
     , PRIMARY KEY (user_id)
     , INDEX (authentication_method_id)
     , CONSTRAINT FK_lams_user_1 FOREIGN KEY (authentication_method_id)
                  REFERENCES lams_authentication_method (authentication_method_id) ON DELETE NO ACTION ON UPDATE NO ACTION
     , INDEX (workspace_id)
     , CONSTRAINT FK_lams_user_2 FOREIGN KEY (workspace_id)
                  REFERENCES lams_workspace (workspace_id) ON DELETE NO ACTION ON UPDATE NO ACTION
     , INDEX (base_organisation_id)
     , CONSTRAINT FK_lams_user_3 FOREIGN KEY (base_organisation_id)
                  REFERENCES lams_organisation (organisation_id)
)TYPE=InnoDB;
CREATE UNIQUE INDEX UQ_lams_user_login ON lams_user (login ASC);
CREATE INDEX login ON lams_user (login ASC);

CREATE TABLE lams_learning_design (
       learning_design_id BIGINT(20) NOT NULL AUTO_INCREMENT
     , id INT(11)
     , description TEXT
     , title VARCHAR(255)
     , first_activity_id BIGINT(20)
     , max_id INT(11)
     , valid_design_flag TINYINT(4) NOT NULL
     , read_only_flag TINYINT(4) NOT NULL
     , date_read_only DATETIME
     , user_id BIGINT(20) NOT NULL
     , help_text TEXT
     , lesson_copy_flag TINYINT(4) NOT NULL DEFAULT 0
     , create_date_time DATETIME NOT NULL DEFAULT '0000-00-00 00:00:00'
     , version VARCHAR(56)
     , parent_learning_design_id BIGINT(20)
     , workspace_folder_id BIGINT(20)
     , duration BIGINT(38)
     , license_id BIGINT(20)
     , license_text TEXT
     , PRIMARY KEY (learning_design_id)
     , INDEX (parent_learning_design_id)
     , CONSTRAINT FK_lams_learning_design_2 FOREIGN KEY (parent_learning_design_id)
                  REFERENCES lams_learning_design (learning_design_id) ON DELETE NO ACTION ON UPDATE NO ACTION
     , INDEX (user_id)
     , CONSTRAINT FK_lams_learning_design_3 FOREIGN KEY (user_id)
                  REFERENCES lams_user (user_id)
     , INDEX (workspace_folder_id)
     , CONSTRAINT FK_lams_learning_design_4 FOREIGN KEY (workspace_folder_id)
                  REFERENCES lams_workspace_folder (workspace_folder_id)
     , INDEX (license_id)
     , CONSTRAINT FK_lams_learning_design_5 FOREIGN KEY (license_id)
                  REFERENCES lams_license (license_id)
)TYPE=InnoDB;
CREATE INDEX idx_design_first_act ON lams_learning_design (first_activity_id ASC);

CREATE TABLE lams_group (
       group_id BIGINT(20) NOT NULL AUTO_INCREMENT
     , grouping_id BIGINT(20) NOT NULL
     , order_id INT(6) NOT NULL DEFAULT 1
     , PRIMARY KEY (group_id)
     , INDEX (grouping_id)
     , CONSTRAINT FK_lams_learning_group_1 FOREIGN KEY (grouping_id)
                  REFERENCES lams_grouping (grouping_id) ON DELETE NO ACTION ON UPDATE NO ACTION
)TYPE=InnoDB;

CREATE TABLE lams_user_organisation (
       user_organisation_id BIGINT(20) NOT NULL AUTO_INCREMENT
     , organisation_id BIGINT(20) NOT NULL
     , user_id BIGINT(20) NOT NULL
     , PRIMARY KEY (user_organisation_id)
     , INDEX (user_id)
     , CONSTRAINT u_user_organisation_ibfk_1 FOREIGN KEY (user_id)
                  REFERENCES lams_user (user_id) ON DELETE NO ACTION ON UPDATE NO ACTION
     , INDEX (organisation_id)
     , CONSTRAINT u_user_organisation_ibfk_2 FOREIGN KEY (organisation_id)
                  REFERENCES lams_organisation (organisation_id) ON DELETE NO ACTION ON UPDATE NO ACTION
)TYPE=InnoDB;

CREATE TABLE lams_lesson (
       lesson_id BIGINT(20) NOT NULL AUTO_INCREMENT
     , learning_design_id BIGINT(20) NOT NULL
     , user_id BIGINT(20) NOT NULL
     , create_date_time DATETIME NOT NULL
     , organisation_id BIGINT(20) NOT NULL
     , lesson_state_id INT(3) NOT NULL
     , start_date_time DATETIME
     , end_date_time DATETIME
     , class_grouping_id BIGINT(20) NOT NULL
     , PRIMARY KEY (lesson_id)
     , INDEX (learning_design_id)
     , CONSTRAINT FK_lams_lesson_1_1 FOREIGN KEY (learning_design_id)
                  REFERENCES lams_learning_design (learning_design_id)
     , INDEX (user_id)
     , CONSTRAINT FK_lams_lesson_2 FOREIGN KEY (user_id)
                  REFERENCES lams_user (user_id)
     , INDEX (organisation_id)
     , CONSTRAINT FK_lams_lesson_3 FOREIGN KEY (organisation_id)
                  REFERENCES lams_organisation (organisation_id)
     , INDEX (lesson_state_id)
     , CONSTRAINT FK_lams_lesson_4 FOREIGN KEY (lesson_state_id)
                  REFERENCES lams_lesson_state (lesson_state_id)
     , INDEX (class_grouping_id)
     , CONSTRAINT FK_lams_lesson_5 FOREIGN KEY (class_grouping_id)
                  REFERENCES lams_grouping (grouping_id)
)TYPE=InnoDB;

CREATE TABLE lams_learning_activity (
       activity_id BIGINT(20) NOT NULL AUTO_INCREMENT
     , activity_ui_id INT(11)
     , description TEXT
     , title VARCHAR(255)
     , xcoord INT(11)
     , ycoord INT(11)
     , parent_activity_id BIGINT(20)
     , parent_ui_id INT(11)
     , learning_activity_type_id INT(11) NOT NULL DEFAULT 0
     , grouping_id BIGINT(20)
     , grouping_ui_id INT(11)
     , order_id INT(11)
     , define_later_flag TINYINT(4) NOT NULL DEFAULT 0
     , learning_design_id BIGINT(20)
     , learning_library_id BIGINT(20)
     , create_date_time DATETIME NOT NULL DEFAULT '0000-00-00 00:00:00'
     , offline_instructions TEXT
     , max_number_of_options INT(5)
     , min_number_of_options INT(5)
     , options_instructions TEXT
     , tool_id BIGINT(20)
     , tool_content_id BIGINT(20)
     , gate_activity_level_id INT(11)
     , gate_start_time_offset BIGINT(38)
     , gate_end_time_offset BIGINT(38)
     , library_activity_ui_image VARCHAR(255)
     , create_grouping_id BIGINT(20)
     , create_grouping_ui_id INT(11)
     , library_activity_id BIGINT(20)
     , PRIMARY KEY (activity_id)
     , INDEX (learning_library_id)
     , CONSTRAINT FK_lams_learning_activity_7 FOREIGN KEY (learning_library_id)
                  REFERENCES lams_learning_library (learning_library_id) ON DELETE NO ACTION ON UPDATE NO ACTION
     , INDEX (learning_design_id)
     , CONSTRAINT FK_lams_learning_activity_6 FOREIGN KEY (learning_design_id)
                  REFERENCES lams_learning_design (learning_design_id) ON DELETE NO ACTION ON UPDATE NO ACTION
     , INDEX (parent_activity_id)
     , CONSTRAINT FK_learning_activity_2 FOREIGN KEY (parent_activity_id)
                  REFERENCES lams_learning_activity (activity_id) ON DELETE NO ACTION ON UPDATE NO ACTION
     , INDEX (learning_activity_type_id)
     , CONSTRAINT FK_learning_activity_3 FOREIGN KEY (learning_activity_type_id)
                  REFERENCES lams_learning_activity_type (learning_activity_type_id) ON DELETE NO ACTION ON UPDATE NO ACTION
     , INDEX (grouping_id)
     , CONSTRAINT FK_learning_activity_6 FOREIGN KEY (grouping_id)
                  REFERENCES lams_grouping (grouping_id) ON DELETE NO ACTION ON UPDATE NO ACTION
     , INDEX (tool_id)
     , CONSTRAINT FK_lams_learning_activity_8 FOREIGN KEY (tool_id)
                  REFERENCES lams_tool (tool_id)
     , INDEX (gate_activity_level_id)
     , CONSTRAINT FK_lams_learning_activity_10 FOREIGN KEY (gate_activity_level_id)
                  REFERENCES lams_gate_activity_level (gate_activity_level_id)
     , INDEX (create_grouping_id)
     , CONSTRAINT FK_lams_learning_activity_9 FOREIGN KEY (create_grouping_id)
                  REFERENCES lams_grouping (grouping_id)
     , INDEX (library_activity_id)
     , CONSTRAINT FK_lams_learning_activity_11 FOREIGN KEY (library_activity_id)
                  REFERENCES lams_learning_activity (activity_id)
)TYPE=InnoDB;

CREATE TABLE lams_learner_progress (
       learner_progress_id BIGINT(20) NOT NULL AUTO_INCREMENT
     , user_id BIGINT(20) NOT NULL
     , lesson_id BIGINT(20) NOT NULL
     , lesson_completed_flag TINYINT(1) NOT NULL DEFAULT 0
     , start_date_time DATETIME NOT NULL
     , finish_date_time DATETIME
     , current_activity_id BIGINT(20)
     , next_activity_id BIGINT(20)
     , PRIMARY KEY (learner_progress_id)
     , INDEX (user_id)
     , CONSTRAINT FK_lams_learner_progress_1 FOREIGN KEY (user_id)
                  REFERENCES lams_user (user_id)
     , INDEX (lesson_id)
     , CONSTRAINT FK_lams_learner_progress_2 FOREIGN KEY (lesson_id)
                  REFERENCES lams_lesson (lesson_id)
     , INDEX (current_activity_id)
     , CONSTRAINT FK_lams_learner_progress_3 FOREIGN KEY (current_activity_id)
                  REFERENCES lams_learning_activity (activity_id)
     , INDEX (next_activity_id)
     , CONSTRAINT FK_lams_learner_progress_4 FOREIGN KEY (next_activity_id)
                  REFERENCES lams_learning_activity (activity_id)
)TYPE=InnoDB;

CREATE TABLE lams_user_organisation_role (
       user_organisation_role_id BIGINT(20) NOT NULL AUTO_INCREMENT
     , user_organisation_id BIGINT(20) NOT NULL
     , role_id INT(6) NOT NULL
     , PRIMARY KEY (user_organisation_role_id)
     , INDEX (role_id)
     , CONSTRAINT FK_lams_user_organisation_role_2 FOREIGN KEY (role_id)
                  REFERENCES lams_role (role_id) ON DELETE NO ACTION ON UPDATE NO ACTION
     , INDEX (user_organisation_id)
     , CONSTRAINT FK_lams_user_organisation_role_3 FOREIGN KEY (user_organisation_id)
                  REFERENCES lams_user_organisation (user_organisation_id) ON DELETE NO ACTION ON UPDATE NO ACTION
)TYPE=InnoDB;

CREATE TABLE lams_tool_session (
       tool_session_id BIGINT(20) NOT NULL AUTO_INCREMENT
     , tool_session_type_id INT(3) NOT NULL
     , lesson_id BIGINT(20) NOT NULL
     , activity_id BIGINT(20) NOT NULL
     , tool_session_state_id INT(3) NOT NULL
     , create_date_time DATETIME NOT NULL
     , group_id BIGINT(20)
     , user_id BIGINT(20)
     , unique_key VARCHAR(128) NOT NULL
     , UNIQUE UQ_lams_tool_session_1 (unique_key)
     , PRIMARY KEY (tool_session_id)
     , INDEX (group_id)
     , CONSTRAINT FK_lams_tool_session_1 FOREIGN KEY (group_id)
                  REFERENCES lams_group (group_id)
     , INDEX (tool_session_state_id)
     , CONSTRAINT FK_lams_tool_session_4 FOREIGN KEY (tool_session_state_id)
                  REFERENCES lams_tool_session_state (tool_session_state_id)
     , INDEX (group_id)
     , CONSTRAINT FK_lams_tool_session_3 FOREIGN KEY (group_id)
                  REFERENCES lams_group (group_id)
     , INDEX (user_id)
     , CONSTRAINT FK_lams_tool_session_5 FOREIGN KEY (user_id)
                  REFERENCES lams_user (user_id)
     , INDEX (tool_session_type_id)
     , CONSTRAINT FK_lams_tool_session_7 FOREIGN KEY (tool_session_type_id)
                  REFERENCES lams_tool_session_type (tool_session_type_id)
     , INDEX (activity_id)
     , CONSTRAINT FK_lams_tool_session_8 FOREIGN KEY (activity_id)
                  REFERENCES lams_learning_activity (activity_id)
)TYPE=InnoDB;

CREATE TABLE lams_progress_completed (
       learner_progress_id BIGINT(20) NOT NULL
     , activity_id BIGINT(20) NOT NULL
     , PRIMARY KEY (learner_progress_id, activity_id)
     , INDEX (learner_progress_id)
     , CONSTRAINT FK_lams_progress_completed_1 FOREIGN KEY (learner_progress_id)
                  REFERENCES lams_learner_progress (learner_progress_id)
     , INDEX (activity_id)
     , CONSTRAINT FK_lams_progress_completed_2 FOREIGN KEY (activity_id)
                  REFERENCES lams_learning_activity (activity_id)
)TYPE=InnoDB;

CREATE TABLE lams_progress_attempted (
       learner_progress_id BIGINT(20) NOT NULL
     , activity_id BIGINT(20) NOT NULL
     , PRIMARY KEY (learner_progress_id, activity_id)
     , INDEX (learner_progress_id)
     , CONSTRAINT FK_lams_progress_current_1 FOREIGN KEY (learner_progress_id)
                  REFERENCES lams_learner_progress (learner_progress_id)
     , INDEX (activity_id)
     , CONSTRAINT FK_lams_progress_current_2 FOREIGN KEY (activity_id)
                  REFERENCES lams_learning_activity (activity_id)
)TYPE=InnoDB;

CREATE TABLE lams_user_group (
       user_id BIGINT(20) NOT NULL
     , group_id BIGINT(20) NOT NULL
     , PRIMARY KEY (user_id, group_id)
     , INDEX (user_id)
     , CONSTRAINT FK_lams_user_group_1 FOREIGN KEY (user_id)
                  REFERENCES lams_user (user_id)
     , INDEX (group_id)
     , CONSTRAINT FK_lams_user_group_2 FOREIGN KEY (group_id)
                  REFERENCES lams_group (group_id)
)TYPE=InnoDB;

CREATE TABLE lams_tool_content (
       tool_content_id BIGINT(20) NOT NULL AUTO_INCREMENT
     , tool_id BIGINT(20) NOT NULL
     , PRIMARY KEY (tool_content_id)
     , INDEX (tool_id)
     , CONSTRAINT FK_lams_tool_content_1 FOREIGN KEY (tool_id)
                  REFERENCES lams_tool (tool_id)
)TYPE=InnoDB;

CREATE TABLE lams_activity_learners (
       user_id BIGINT(20) NOT NULL DEFAULT 0
     , activity_id BIGINT(20) NOT NULL DEFAULT 0
     , INDEX (user_id)
     , CONSTRAINT FK_TABLE_32_1 FOREIGN KEY (user_id)
                  REFERENCES lams_user (user_id)
     , INDEX (activity_id)
     , CONSTRAINT FK_TABLE_32_2 FOREIGN KEY (activity_id)
                  REFERENCES lams_learning_activity (activity_id)
)TYPE=InnoDB;

CREATE TABLE lams_lesson_learner (
       lesson_id BIGINT(20) NOT NULL
     , user_id BIGINT(20) NOT NULL
     , INDEX (lesson_id)
     , CONSTRAINT FK_lams_lesson_learner_1 FOREIGN KEY (lesson_id)
                  REFERENCES lams_lesson (lesson_id)
     , INDEX (user_id)
     , CONSTRAINT FK_lams_lesson_learner_2 FOREIGN KEY (user_id)
                  REFERENCES lams_user (user_id)
)TYPE=InnoDB;

CREATE TABLE lams_learning_transition (
       transition_id BIGINT(20) NOT NULL AUTO_INCREMENT
     , transition_ui_id INT(11)
     , description TEXT
     , title VARCHAR(255)
     , to_activity_id BIGINT(20) NOT NULL
     , from_activity_id BIGINT(20) NOT NULL
     , learning_design_id BIGINT(20) NOT NULL DEFAULT 0
     , create_date_time DATETIME NOT NULL DEFAULT '0000-00-00 00:00:00'
     , to_ui_id INT(11) NOT NULL
     , from_ui_id INT(11) NOT NULL
     , PRIMARY KEY (transition_id)
     , INDEX (from_activity_id)
     , CONSTRAINT FK_learning_transition_3 FOREIGN KEY (from_activity_id)
                  REFERENCES lams_learning_activity (activity_id) ON DELETE NO ACTION ON UPDATE NO ACTION
     , INDEX (to_activity_id)
     , CONSTRAINT FK_learning_transition_2 FOREIGN KEY (to_activity_id)
                  REFERENCES lams_learning_activity (activity_id) ON DELETE NO ACTION ON UPDATE NO ACTION
     , INDEX (learning_design_id)
     , CONSTRAINT lddefn_transition_ibfk_1 FOREIGN KEY (learning_design_id)
                  REFERENCES lams_learning_design (learning_design_id) ON DELETE NO ACTION ON UPDATE NO ACTION
)TYPE=InnoDB;

