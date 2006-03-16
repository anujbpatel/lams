# Connection: ROOT LOCAL
# Host: localhost
# Saved: 2005-04-07 10:42:43
# 
INSERT INTO lams_tool
(
tool_signature,
service_name,
tool_display_name,
description,
tool_identifier,
tool_version,
learning_library_id,
default_tool_content_id,
valid_flag,
grouping_support_type_id,
supports_run_offline_flag,
learner_url,
learner_preview_url,
learner_progress_url,
author_url,
monitor_url,
define_later_url,
export_pfolio_learner_url,
export_pfolio_class_url,
contribute_url,
moderation_url,
language_file,
create_date_time
)
VALUES
(
'lafrum11',
'forumService',
'Forum',
'Forum / Message Boards',
'forum',
'1.1',
NULL,
NULL,
0,
2,
1,
'tool/lafrum11/learning/viewForum.do?mode=learner',
'tool/lafrum11/learning/viewForum.do?mode=author',
'tool/lafrum11/learning/viewForum.do?mode=teacher',
'tool/lafrum11/authoring/init.do',
'tool/lafrum11/monitoring.do',
'tool/lafrum11/defineLater.do',
'tool/lafrum11/exportPortfolio?mode=learner',
'tool/lafrum11/exportPortfolio?mode=teacher',
'tool/lafrum11/contribute.do',
'tool/lafrum11/moderate.do',
'org.lamsfoundation.lams.tool.forum.web.ApplicationResources',
NOW()
)
