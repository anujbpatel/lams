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
'lanb11',
'nbService',
'Noticeboard',
'Displays a Noticeboard',
'nb',
'1.1',
NULL,
NULL,
0,
2,
1,
'tool/lanb11/starter/learner.do?mode=learner',
'tool/lanb11/starter/learner.do?mode=author',
'tool/lanb11/starter/learner.do?mode=teacher',
'tool/lanb11/authoring.do',
'tool/lanb11/monitoring.do',
'tool/lanb11/authoring.do?defineLater=true',
'tool/lanb11/portfolioExport?mode=learner',
'tool/lanb11/portfolioExport?mode=teacher',
NULL,
NULL,
'org.lamsfoundation.lams.tool.noticeboard.ApplicationProperties',
NOW()
);
